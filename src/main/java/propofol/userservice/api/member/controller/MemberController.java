package propofol.userservice.api.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import propofol.userservice.api.common.annotation.Jwt;
import propofol.userservice.api.common.annotation.Token;
import propofol.userservice.api.common.exception.SaveProfileException;
import propofol.userservice.api.feign.dto.TagsDto;
import propofol.userservice.api.feign.service.TagService;
import propofol.userservice.api.member.controller.dto.*;
import propofol.userservice.api.member.service.ProfileService;
import propofol.userservice.domain.exception.ExistFollowingException;
import propofol.userservice.domain.exception.SameMemberFollowingException;
import propofol.userservice.domain.image.entity.Profile;
import propofol.userservice.domain.member.entity.MemberTag;
import propofol.userservice.domain.member.service.FollowingService;
import propofol.userservice.domain.member.service.dto.UpdateMemberDto;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.service.MemberService;
import propofol.userservice.domain.streak.entity.Streak;
import propofol.userservice.domain.streak.service.StreakService;
import propofol.userservice.domain.timetable.entity.TimeTable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private final FollowingService followingService;

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
        Member findMember = memberService.getMemberById(memberId);

        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "회원 조회 성공!", modelMapper.map(findMember, MemberResponseDto.class));
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
    public MemberInfoDto getMemberInfo(@PathVariable("memberId") Long memberId,
                                       @Jwt String token){
        Member findMember = memberService.getMemberWithTagByMemberId(memberId);
        ProfileResponseDto profile = profileService.getProfile(memberId);
        List<TagDetailDto> tagDetailDtos = new ArrayList<>();
        Set<Long> tagIds = findMember.getMemberTags().stream().map(MemberTag::getTagId).collect(Collectors.toSet());

        TagsDto tagsDto = tagService.getTagNames(token, tagIds);
        tagsDto.getTags().forEach(tagDetailDto -> {
            tagDetailDtos.add(new TagDetailDto(tagDetailDto.getId(), tagDetailDto.getName()));
        });

        return new MemberInfoDto(findMember.getId(), findMember.getNickname(),
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
     * 회원 조회 -> Set 회원 아이디로 조회
     */
    @GetMapping("/info")
    @ResponseStatus(HttpStatus.OK)
    public PageResponseDto getMemberNickName(@RequestParam("memberId") Set<Long> memberIds,
                                             @RequestParam("page") int page,
                                             @Jwt String token){
        PageResponseDto<MemberInfoDto> responseDto = createPageDto(memberIds, page, token);

        return responseDto;
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
     * following 저장
     */
    @PostMapping("/following")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto saveFollowing(@RequestBody FollowingSaveRequestDto requestDto,
                                     @Token Long memberId){
        Member findMember = memberService.getMemberById(memberId);

        if(findMember.getNickname() == requestDto.getFollowingNickname())
            throw new SameMemberFollowingException("동일한 사용자 following 요청입니다.");

        Member followingMember = memberService.getMemberByNickname(requestDto.getFollowingNickname());

        return new ResponseDto(HttpStatus.OK.value(), "success", "follow 기능 성공!",
                followingService.saveFollowing(findMember, followingMember));
    }

    /**
     * follower 조회
     */
    @GetMapping("/follower")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getFollowers(@Token Long memberId){
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "팔로워 조회 성공", followingService.getFollowers(memberId));
    }

    /**
     * 기존 following 여부 확인
     */
    @PostMapping("/checkFollowing")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto checkFollowing(@RequestBody FollowingSaveRequestDto requestDto,
                                      @Token Long memberId) {
        Member findMember = memberService.getMemberById(memberId);

        Member followingMember = memberService.getMemberByNickname(requestDto.getFollowingNickname());

        boolean flag = followingService.isExistFollowing(findMember, followingMember);

        return new ResponseDto(HttpStatus.OK.value(), "success", "follow 조회 성공!", flag);
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
                                       @RequestParam("page") int page,
                                       @Jwt String token){
        MatchingResponseDto responseDto = createMatchingResponseDto(tagIds, page, token);

        return new ResponseDto(HttpStatus.OK.value(), "success", "추천 회원 조회 성공", responseDto);
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

    private MatchingResponseDto createMatchingResponseDto(Set<Long> tagIds, int page, String token) {
        MatchingResponseDto responseDto = new MatchingResponseDto();

        Page<Member> memberPage = memberService.getMemberWithTagId(tagIds, page);
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
                    if(tag.getId() == memberTag.getTagId()){
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

    private PageResponseDto<MemberInfoDto> createPageDto(Set<Long> memberIds, int page, String token) {
        PageResponseDto<MemberInfoDto> responseDto = new PageResponseDto<>();
        Page<Member> memberPage = memberService.getMembersByMemberIds(memberIds, page);
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

            System.out.println("me = " + member.getMemberTags().size());
            member.getMemberTags().forEach(memberTag -> {
                tagDto.getTags().forEach(tagDetailDto -> {
                    System.out.println("memberTag = " + memberTag.getTagId());
                    System.out.println("getId() = " + tagDetailDto.getId());
                    if(tagDetailDto.getId() == memberTag.getTagId()){
                        memberInfoDto.getTags().add(new TagDetailDto(tagDetailDto.getId(), tagDetailDto.getName()));
                    }
                });
            });

            responseDetailDto.add(memberInfoDto);
        });
        return responseDto;
    }
}