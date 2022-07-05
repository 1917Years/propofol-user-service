package propofol.userservice.api.subscribe.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import propofol.userservice.api.common.annotation.Jwt;
import propofol.userservice.api.common.annotation.Token;
import propofol.userservice.api.member.service.ProfileService;
import propofol.userservice.api.subscribe.controller.dto.FollowingSaveRequestDto;
import propofol.userservice.api.member.controller.dto.PageResponseDto;
import propofol.userservice.api.member.controller.dto.ResponseDto;
import propofol.userservice.api.subscribe.controller.dto.MemberResponseDto;
import propofol.userservice.api.subscribe.controller.dto.SubscribeResponseDto;
import propofol.userservice.api.subscribe.service.SubscribeApiService;
import propofol.userservice.domain.exception.SameMemberFollowingException;
import propofol.userservice.domain.image.entity.Profile;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.entity.Subscribe;
import propofol.userservice.domain.member.service.MemberService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subscribe")
public class SubscribeController {

    private final MemberService memberService;
    private final ProfileService profileService;
    private final SubscribeApiService followingService;
    private final ModelMapper modelMapper;

    /**
     * following 저장
     */
    @PostMapping("/following")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto saveFollowing(@RequestBody FollowingSaveRequestDto requestDto,
                                     @Jwt String token,
                                     @Token Long memberId){
        Member findMember = memberService.getMemberById(memberId);

        if(findMember.getNickname() == requestDto.getFollowingNickname())
            throw new SameMemberFollowingException("동일한 사용자 following 요청입니다.");

        Member followingMember = memberService.getMemberByNickname(requestDto.getFollowingNickname());

        return new ResponseDto(HttpStatus.OK.value(), "success", "follow 기능 성공!",
                followingService.saveWithAlarm(findMember, followingMember, token));
    }

    /**
     * follower Count 조회
     */
    @GetMapping("/followerCount")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getFollowerCount(@Token Long memberId){
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "follower Count 조회 성공", followingService.getFollowerCount(memberId));
    }

    /**
     * following Count 조회
     */
    @GetMapping("/followingCount")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getFollowingCount(@Token Long memberId){
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "following Count 조회 성공", followingService.getFollowingCount(memberId));
    }

    /**
     * follower 목록 조회
     */
    @GetMapping("/followers")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getFollowers(@RequestParam("page") int page,
                                    @Token Long memberId){
        Page<Subscribe> subscribePage = followingService.findMyFollowers(memberId, page);

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "following Count 조회 성공", createPageResponseDtoWithPageSubscribe(subscribePage, false));
    }


    /**
     * following 목록 조회
     */
    @GetMapping("/followings")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getFollowings(@RequestParam("page") int page,
                                     @Token Long memberId){
        Page<Subscribe> subscribePage = followingService.findMyFollowings(memberId, page);

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "following Count 조회 성공", createPageResponseDtoWithPageSubscribe(subscribePage, true));
    }

    @GetMapping("/following")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto checkFollowing(@RequestParam("nickname") String nickname,
                                      @Token Long memberId) {
        Member findMember = memberService.getMemberById(memberId);

        Member followingMember = memberService.getMemberByNickname(nickname);

        boolean flag = followingService.isExistFollowing(findMember, followingMember);

        return new ResponseDto(HttpStatus.OK.value(), "success", "following 조회 성공!", flag);
    }

    @GetMapping("/feign/followers/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public MemberResponseDto getFollowersFeign(@PathVariable(name = "memberId") Long memberId){
        Set<Long> myFollowersIds = followingService.findMyFollowersFeign(memberId);
        Member findMember = memberService.getMemberById(memberId);

        return new MemberResponseDto(findMember.getNickname(), myFollowersIds);
    }

    private PageResponseDto<SubscribeResponseDto> createPageResponseDtoWithPageSubscribe(Page<Subscribe> subscribePage,
                                                                                         boolean type) {
        PageResponseDto<SubscribeResponseDto> pageResponseDto = new PageResponseDto();
        pageResponseDto.setTotalPageCount(subscribePage.getTotalPages());
        pageResponseDto.setTotalCount(subscribePage.getTotalElements());

        List<SubscribeResponseDto> responseDto = pageResponseDto.getData();
        if(!type) {
            subscribePage.getContent().forEach(subscribe -> {
                Member member = subscribe.getMember();
                SubscribeResponseDto subscribeResponseDto = createSubscribeDto(member);
                responseDto.add(subscribeResponseDto);
            });
        }else{
            Set<Long> followingIds
                    = subscribePage.getContent().stream().map(Subscribe::getFollowingMemberId).collect(Collectors.toSet());

            List<Member> followingMembers = memberService.getMembersByMemberIds(followingIds);
            followingMembers.forEach(member -> {
                SubscribeResponseDto subscribeResponseDto = createSubscribeDto(member);
                responseDto.add(subscribeResponseDto);
            });
        }
        return pageResponseDto;
    }

    private SubscribeResponseDto createSubscribeDto(Member member) {
        SubscribeResponseDto subscribeResponseDto = modelMapper.map(member, SubscribeResponseDto.class);
        List<Profile> profile = member.getProfile();
        if(profile.size() > 0){
            subscribeResponseDto.setProfileString(profileService.getProfileString(profile.get(0).getStoreFileName()));
            subscribeResponseDto.setProfileType(profile.get(0).getContentType());
        }
        return subscribeResponseDto;
    }
}
