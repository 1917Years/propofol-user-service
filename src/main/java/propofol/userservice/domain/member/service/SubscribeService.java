package propofol.userservice.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import propofol.userservice.domain.member.entity.Subscribe;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.repository.SubscribeRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubscribeService {

    private final SubscribeRepository subscribeRepository;

    public String saveFollowing(Member member, Member followingMember){
        Subscribe findSubscribe = subscribeRepository
                .findByFollowingMemberIdAndMemberId(followingMember.getId(), member.getId()).orElse(null);

        if(findSubscribe == null){
            Subscribe subscribe = Subscribe.createFollowing()
                    .followingMemberId(followingMember.getId()).build();
            subscribe.changeMember(member);

            subscribeRepository.save(subscribe);
            return "ok";
        }else{
            subscribeRepository.delete(findSubscribe);
            return "cancel";
        }

    }

    public Page<Subscribe> findMyFollowers(long memberId, int page){
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        return subscribeRepository.getFollowers(memberId, pageRequest);
    }

    public Page<Subscribe> findMyFollowings(long memberId, int page){
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        return subscribeRepository.getFollowings(memberId, pageRequest);
    }

    public boolean isExistFollowing(Member findMember, Member followingMember) {
        Subscribe findSubscribe = subscribeRepository
                .findByFollowingMemberIdAndMemberId(followingMember.getId(), findMember.getId()).orElse(null);

        return findSubscribe == null ? false : true;
    }

    public int getFollowerCount(Long memberId) {
       return subscribeRepository.getFollowerCount(memberId);
    }

    public int getFollowingCount(Long memberId) {
        return subscribeRepository.getFollowingCount(memberId);
    }

    public Set<Long> getSetMemberIds(Long memberId) {
        return subscribeRepository.getFollowerIds(memberId);
    }
}
