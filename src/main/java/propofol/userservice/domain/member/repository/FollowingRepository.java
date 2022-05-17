package propofol.userservice.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.userservice.domain.member.entity.Following;

import java.util.Optional;

public interface FollowingRepository extends JpaRepository<Following, Long> {

    @Query("select count(f) from Following f where f.followingMemberId = :memberId")
    int getFollowers(@Param("memberId") Long memberId);

    Optional<Following> findByFollowingMemberIdAndMemberId(Long followingMemberId, Long MemberId);
}
