package propofol.userservice.api.subscribe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.userservice.api.feign.service.AlarmService;
import propofol.userservice.domain.member.entity.Subscribe;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.service.SubscribeService;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscribeApiService {
    private final SubscribeService subscribeService;
    private final AlarmService alarmService;

    @Transactional
    public String saveWithAlarm(Member findMember, Member followingMember, String token){
        String result = subscribeService.saveFollowing(findMember, followingMember);
        if(result.equals("ok")){
            alarmService.saveAlarm(followingMember.getId(),
                    findMember.getNickname() + "님이 팔로우 하셨습니다.", token);
        }else{
            alarmService.saveAlarm(followingMember.getId(),
                    findMember.getNickname() + "님이 팔로우를 취소하셨습니다.", token);
        }

        return "ok";
    }

    public Page<Subscribe> findMyFollowers(long memberId, int page){
        return subscribeService.findMyFollowers(memberId, page);
    }

    public Page<Subscribe> findMyFollowings(long memberId, int page){
        return subscribeService.findMyFollowings(memberId, page);
    }

    public int getFollowerCount(Long memberId) {
        return subscribeService.getFollowerCount(memberId);
    }

    public int getFollowingCount(Long memberId) {
        return subscribeService.getFollowingCount(memberId);
    }

    public boolean isExistFollowing(Member findMember, Member followingMember) {
        return subscribeService.isExistFollowing(findMember, followingMember);
    }

    public Set<Long> findMyFollowersFeign(Long memberId) {
        return subscribeService.getSetMemberIds(memberId);
    }
}
