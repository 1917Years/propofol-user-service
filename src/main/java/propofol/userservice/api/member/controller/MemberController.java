package propofol.userservice.api.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import propofol.userservice.api.common.annotation.Token;
import propofol.userservice.api.common.exception.SaveProfileException;
import propofol.userservice.api.member.controller.dto.*;
import propofol.userservice.api.member.service.ProfileService;
import propofol.userservice.domain.exception.ExistFollowingException;
import propofol.userservice.domain.exception.SameMemberFollowingException;
import propofol.userservice.domain.member.service.FollowingService;
import propofol.userservice.domain.member.service.dto.UpdateMemberDto;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.service.MemberService;
import propofol.userservice.domain.streak.entity.Streak;
import propofol.userservice.domain.streak.service.StreakService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;
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
     * 매칭 게시판 회원 추천 조회
     */
    @GetMapping("/matchings")
    public MatchingResponseDto getMatchingData(){
        return null;
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

}