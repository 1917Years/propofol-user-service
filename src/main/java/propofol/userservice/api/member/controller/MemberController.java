package propofol.userservice.api.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import propofol.userservice.api.common.annotation.Jwt;
import propofol.userservice.api.common.annotation.Token;
import propofol.userservice.api.common.exception.SaveProfileException;
import propofol.userservice.api.feign.dto.TagsDto;
import propofol.userservice.api.feign.service.MatchingService;
import propofol.userservice.api.feign.service.TagService;
import propofol.userservice.api.member.controller.dto.*;
import propofol.userservice.api.member.service.ProfileService;
import propofol.userservice.domain.exception.ExistFollowingException;
import propofol.userservice.domain.image.entity.Profile;
import propofol.userservice.domain.member.entity.MemberTag;
import propofol.userservice.domain.member.service.dto.UpdateMemberDto;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.service.MemberService;
import propofol.userservice.domain.streak.entity.Streak;
import propofol.userservice.domain.streak.service.StreakService;
import propofol.userservice.domain.timetable.entity.TimeTable;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;
    private final TagService tagService;
    private final ProfileService profileService;
    private final ModelMapper modelMapper;
    private final StreakService streakService;
    private final MatchingService matchingService;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto saveProfileException(SaveProfileException e){
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "프로필 저장 실패", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto existFollowingException(ExistFollowingException e){
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "팔로잉 실패", e.getMessage());
    }

    /**
     * 회원 조회
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getMemberByMemberId(@Token Long memberId){
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "회원 조회 성공!", modelMapper.map(memberService.getMemberById(memberId), MemberResponseDto.class));
    }

    /**
     * 회원 조회 -> 닉네임 반환
     */
    @GetMapping("/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public String getMemberNickname(@PathVariable("memberId") Long memberId){
        return memberService.getMemberById(memberId).getNickname();
    }

    /**
     * 회원 조회 -> 닉네임, Profile, 태그
     */
    @GetMapping("/info/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public MemberInfoResponseDto getMemberInfo(@PathVariable("memberId") Long memberId,
                                               @Jwt String token){
        Member findMember = memberService.getMemberWithTagByMemberId(memberId);
        ProfileResponseDto profile = profileService.getProfile(memberId);
        List<TagDetailDto> tagDetailDtos = new ArrayList<>();
        Set<Long> tagIds = findMember.getMemberTags().stream().map(MemberTag::getTagId).collect(Collectors.toSet());

        TagsDto tagsDto = tagService.getTagNames(token, tagIds);
        tagsDto.getTags().forEach(tagDetailDto -> {
            tagDetailDtos.add(new TagDetailDto(tagDetailDto.getId(), tagDetailDto.getName()));
        });

        return new MemberInfoResponseDto(findMember.getId(), findMember.getNickname(),
                profile.getProfileString(), profile.getProfileType(), tagDetailDtos);
    }

    /**
     * 회원 조회 -> 닉네임
     */
    @GetMapping("/info/{memberId}/nickName")
    @ResponseStatus(HttpStatus.OK)
    public String getMemberNickName(@PathVariable("memberId") Long memberId,
                                         @Jwt String token){
        return memberService.getMemberById(memberId).getNickname();
    }

    /**
     * 회원 조회 -> 닉네임, 폰번호, 이메일, 실명
     */
    @GetMapping("/info/{memberId}/part")
    @ResponseStatus(HttpStatus.OK)
    public MemberInfoPartDto getMemberInfos(@PathVariable("memberId") Long memberId){
        return modelMapper.map(memberService.getMemberById(memberId), MemberInfoPartDto.class);
    }

    /**
     * 회원 조회 -> Set 회원 아이디로 조회
     */
    @GetMapping("/info")
    @ResponseStatus(HttpStatus.OK)
    public PageResponseDto getMemberNickName(@RequestParam("memberId") Set<Long> memberIds,
                                             @RequestParam(value = "page", required = false) int page,
                                             @Jwt String token){
        PageResponseDto<MemberInfoDto> responseDto = createPageDto(memberIds, page, token);

        return responseDto;
    }

    /**
     * 회원 조회 -> Set 회원 아이디로 조회
     */
    @GetMapping("/infos")
    @ResponseStatus(HttpStatus.OK)
    public List<MemberInfoDto> getMembers(@RequestParam("memberId") Set<Long> memberIds,
                                          @Jwt String token){

        return getMembersDto(memberIds, token);
    }

    /**
     * 회원 프로필 수정
     */
    @PostMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto saveMemberProfile(@RequestParam("profile") MultipartFile file,
                                         @Token Long memberId) throws Exception {
        return new ResponseDto(HttpStatus.OK.value(), "success", "프로파일 저장 성공",
                profileService.saveProfile(file, memberService.getMemberById(memberId)));
    }

    /**
     * 회원 프로필 조회
     */
    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getMemberProfile(@Token Long memberId){
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "프로필 조회 성공", profileService.getProfile(memberId));
    }

    /**
     * 댓글 작성한 유저의 프로필 조회 추가
     */
    @PostMapping("/commentProfile")
    @ResponseStatus(HttpStatus.OK)
    public ProfileResponseDto getCommentMemberProfile(@RequestBody String nickname) {
        Member findMember = memberService.getMemberByNickname(nickname);
        return profileService.getProfile(findMember.getId());
    }

    /**
     * 회원 수정
     */
    @PostMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto updateMember(@RequestBody UpdateRequestDto dto, @Token Long memberId){
        UpdateMemberDto memberDto = modelMapper.map(dto, UpdateMemberDto.class);
        memberService.updateMember(memberDto, memberId);
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "회원 수정 성공!", "ok");
    }

    /**
     * 회원 스트릭 가져오기
     */
    @GetMapping("/streak")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getStreaks(@Token Long memberId){
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "회원 스트릭 조회 성공!", getStreakResponseDto(memberId));
    }

    /**
     * 스트릭 저장
     */
    @PostMapping("/streak")
    @ResponseStatus(HttpStatus.OK)
    public void saveStreak(@Token Long memberId,
                           @RequestBody StreakRequestDto requestDto){
        Streak streak = createStreak(requestDto);
        streakService.saveStreak(memberId, streak);
    }

    private Streak createStreak(StreakRequestDto requestDto) {
        return Streak.createStreak()
                .workingDate(requestDto.getDate())
                .working(1)
                .build();
    }


    /**
     * Member Tag 저장
     */
    @PostMapping("/tag")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto saveMemberTags(@Token Long memberId,
                                      @RequestBody TagIdRequestDto requestDto){

        return new ResponseDto(HttpStatus.OK.value(), "success", "회원 태그 저장 성공",
                memberService.saveMemberTags(memberId, requestDto.getTagIds()));
    }

    /**
     * 회원 추천 수 Plus
     */
    @PostMapping("/recommend")
    public void plusRecommend(@RequestBody RecommendRequestDto requestDto){
        memberService.plusTotalRecommend(requestDto.getId());
    }

    /**
     * 태그 + 태그 수 + 총 추천 수 회원 조회
     */
    @GetMapping("/matchings")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getMatchingData(@RequestParam(value = "tagId", required = false) Set<Long> tagIds,
                                       @RequestParam(value = "boardId", required = false) Long boardId,
                                       @RequestParam("page") int page,
                                       @Jwt String token){
        MatchingResponseDto responseDto = createMatchingResponseDto(tagIds, boardId, page, token);

        return new ResponseDto(HttpStatus.OK.value(), "success", "추천 회원 조회 성공", responseDto);
    }

    /**
     * 회원 시간표 조회
     */
    @GetMapping("/timetables")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getMemberTimeTables(@RequestParam("memberId") Long memberId){
        TimeTableDetailDto timeTableDetailDto = createTimeTableDto(memberId);

        return new ResponseDto(HttpStatus.OK.value(), "success", "시간표 조회 성공", timeTableDetailDto);
    }

    private StreakResponseDto getStreakResponseDto(Long memberId) {
        StreakResponseDto streakResponseDto = new StreakResponseDto();
        int year = LocalDate.now().getYear();

        streakResponseDto.setYear(String.valueOf(year));

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        List<StreakDetailResponseDto> responseDtoStreaks = streakResponseDto.getStreaks();
        List<Streak> streaks = streakService.getStreaksByMemberId(memberId, start, end);
        streaks.forEach(streak -> {
            responseDtoStreaks.add(new StreakDetailResponseDto(streak.getWorkingDate(), streak.getWorking()));
        });
        return streakResponseDto;
    }

    private MatchingResponseDto createMatchingResponseDto(Set<Long> tagIds, Long boardId, int page, String token) {
        MatchingResponseDto responseDto = new MatchingResponseDto();

        Page<Member> memberPage;
        if(boardId != null){
            memberPage = memberService.getMemberWithTagIdAndNoMemberId(tagIds, page,
                    matchingService.findAllBoardMember(boardId, token));
        }else{
            memberPage = memberService.getMemberWithTagId(tagIds, page);
        }

        responseDto.setTotalPageCount(memberPage.getTotalPages());
        responseDto.setTotalCount(memberPage.getTotalElements());
        Set<Long> tagIdSet = new HashSet<>();
        memberPage.forEach(member -> {
            member.getMemberTags().forEach(memberTag -> {
                tagIdSet.add(memberTag.getTagId());
            });
        });

        TagsDto tagDto = tagService.getTagNames(token, tagIdSet);

        memberPage.forEach(member -> {
            MatchingDetailResponseDto detailResponseDto = modelMapper.map(member, MatchingDetailResponseDto.class);
            List<Profile> profile = member.getProfile();

            if(profile.size() > 0) {
                detailResponseDto.setProfileString(profileService.getProfileString(profile.get(0).getStoreFileName()));
                detailResponseDto.setProfileType(profile.get(0).getContentType());
            }

            List<MemberTag> memberTags = member.getMemberTags();
            memberTags.forEach(memberTag -> {
                tagDto.getTags().forEach(tag -> {
                    if(ObjectUtils.equals(tag.getId(), memberTag.getTagId())){
                        detailResponseDto.getTagData().add(modelMapper.map(tag, TagDetailDto.class));
                    }
                });
            });

            List<TimeTable> timeTables = member.getTimeTables();
            if(timeTables.size() > 0) {
                List<String> weeks = new ArrayList<>();
                List<String> startTimes = new ArrayList<>();
                List<String> endTimes = new ArrayList<>();
                timeTables.forEach(timeTable -> {
                    weeks.add(timeTable.getWeek());
                    startTimes.add(timeTable.getStartTime().toString());
                    endTimes.add(timeTable.getEndTime().toString());
                });
                detailResponseDto.getTimetableData().add(new TimeTableDetailDto(weeks, startTimes, endTimes));
            }

            responseDto.getData().add(detailResponseDto);
        });
        return responseDto;
    }

    private List<MemberInfoDto> getMembersDto(Set<Long> memberIds, String token) {
        List<Member> findMembers = memberService.getMembersByMemberIds(memberIds);

        Set<Long> tagIds = new HashSet<>();
        findMembers.forEach(member ->
                member.getMemberTags().forEach(memberTag -> tagIds.add(memberTag.getTagId())));
        TagsDto tagDto = tagService.getTagNames(token, tagIds);

        List<MemberInfoDto> responseDetailDto = new ArrayList<>();

        findMembers.forEach(member -> {
            MemberInfoDto memberInfoDto = new MemberInfoDto();
            List<Profile> profiles = member.getProfile();
            if(profiles.size() > 0){
                Profile profile = profiles.get(0);
                memberInfoDto.setProfileString(profileService.getProfileString(profile.getStoreFileName()));
                memberInfoDto.setProfileType(profile.getContentType());
            }
            memberInfoDto.setNickName(member.getNickname());
            memberInfoDto.setId(member.getId());
            memberInfoDto.setEmail(member.getEmail());

            member.getMemberTags().forEach(memberTag -> tagDto.getTags().forEach(tagDetailDto -> {
                if(ObjectUtils.equals(memberTag.getTagId(), tagDetailDto.getId())){
                    memberInfoDto.getTags().add(new TagDetailDto(tagDetailDto.getId(), tagDetailDto.getName()));
                }
            }));

            responseDetailDto.add(memberInfoDto);
        });
        return responseDetailDto;
    }

    private PageResponseDto<MemberInfoDto> createPageDto(Set<Long> memberIds, int page, String token) {
        PageResponseDto<MemberInfoDto> responseDto = new PageResponseDto<>();
        Page<Member> memberPage = memberService.getMembersByMemberIdsAndPage(memberIds, page);
        responseDto.setTotalCount(memberPage.getTotalElements());
        responseDto.setTotalPageCount(memberPage.getTotalPages());

        Set<Long> tagIds = new HashSet<>();
        memberPage.getContent().forEach(member ->
                member.getMemberTags().forEach(memberTag -> tagIds.add(memberTag.getTagId())));
        TagsDto tagDto = tagService.getTagNames(token, tagIds);

        List<MemberInfoDto> responseDetailDto = responseDto.getData();

        memberPage.forEach(member -> {
            MemberInfoDto memberInfoDto = new MemberInfoDto();
            List<Profile> profiles = member.getProfile();
            if(profiles.size() > 0){
                Profile profile = profiles.get(0);
                memberInfoDto.setProfileString(profileService.getProfileString(profile.getStoreFileName()));
                memberInfoDto.setProfileType(profile.getContentType());
            }
            memberInfoDto.setNickName(member.getNickname());
            memberInfoDto.setId(member.getId());
            memberInfoDto.setEmail(member.getEmail());

            member.getMemberTags().forEach(memberTag -> tagDto.getTags().forEach(tagDetailDto -> {
                if(ObjectUtils.equals(memberTag.getTagId(), tagDetailDto.getId())){
                    memberInfoDto.getTags().add(new TagDetailDto(tagDetailDto.getId(), tagDetailDto.getName()));
                }
            }));

            responseDetailDto.add(memberInfoDto);
        });
        return responseDto;
    }

    private TimeTableDetailDto createTimeTableDto(Long memberId) {
        Member findMember = memberService.getMemberWithTimeTablesByMemberId(memberId);
        TimeTableDetailDto timeTableDetailDto = new TimeTableDetailDto();
        List<TimeTable> timeTables = findMember.getTimeTables();
        timeTables.forEach(timeTable -> {
            timeTableDetailDto.getWeeks().add(timeTable.getWeek());
            timeTableDetailDto.getStartTimes().add(timeTable.getStartTime().toString());
            timeTableDetailDto.getEndTimes().add(timeTable.getEndTime().toString());
        });
        return timeTableDetailDto;
    }
}