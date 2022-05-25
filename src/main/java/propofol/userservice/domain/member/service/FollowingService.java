package propofol.userservice.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.userservice.domain.member.entity.Following;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.repository.FollowingRepository;

@Service
@RequiredArgsConstructor
public class FollowingService {

    private final FollowingRepository followingRepository;

    public String saveFollowing(Member member, Member followingMember){
        Following findFollowing = followingRepository
                .findByFollowingMemberIdAndMemberId(followingMember.getId(), member.getId()).orElse(null);

        if(findFollowing == null){
            Following following = Following.createFollowing()
                    .followingMemberId(followingMember.getId()).build();
            following.changeMember(member);

            followingRepository.save(following);
            return "ok";
        }else{
            followingRepository.delete(findFollowing);
            return "팔로잉 취소";
        }

    }

    public boolean isExistFollowing(Member findMember, Member followingMember) {
        Following findFollowing = followingRepository
                .findByFollowingMemberIdAndMemberId(followingMember.getId(), findMember.getId()).orElse(null);

        return findFollowing == null ? false : true;
    }

    public int getFollowers(Long memberId) {
       return followingRepository.getFollowers(memberId);
    }
}
