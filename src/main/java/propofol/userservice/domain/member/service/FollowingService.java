package propofol.userservice.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.userservice.domain.exception.ExistFollowingException;
import propofol.userservice.domain.exception.SameMemberFollowingException;
import propofol.userservice.domain.member.entity.Following;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.repository.FollowingRepository;

@Service
@RequiredArgsConstructor
public class FollowingService {

    private final FollowingRepository followingRepository;

    public String saveFollowing(Member member, Long followingMemberId){
        if(member.getId() == followingMemberId) throw new SameMemberFollowingException("동일한 사용자 following 요청입니다.");

        Following findFollowing = followingRepository
                .findByFollowingMemberIdAndMemberId(followingMemberId, member.getId()).orElse(null);

        if(findFollowing == null){
            Following following = Following.createFollowing()
                    .followingMemberId(followingMemberId).build();
            following.changeMember(member);

            followingRepository.save(following);
            return "ok";
        }else{
            followingRepository.delete(findFollowing);
            return "팔로잉 취소";
        }

    }

    public int getFollowers(Long memberId) {
       return followingRepository.getFollowers(memberId);
    }
}
