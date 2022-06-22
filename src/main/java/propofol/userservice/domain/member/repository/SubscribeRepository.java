package propofol.userservice.domain.member.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.userservice.domain.member.entity.Subscribe;

import java.util.Optional;
import java.util.Set;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

    @Query("select count(s) from Subscribe s where s.followingMemberId = :memberId")
    int getFollowerCount(@Param("memberId") Long memberId);

    @Query("select s from Subscribe s where s.followingMemberId = :memberId")
    Page<Subscribe> getFollowers(@Param("memberId") Long memberId, Pageable pageable);

    @Query("select count(s) from Subscribe s where s.member.id = :memberId")
    int getFollowingCount(@Param("memberId") Long memberId);

    @Query("select s from Subscribe s join s.member sm where sm.id = :memberId")
    Page<Subscribe> getFollowings(@Param("memberId") Long memberId, Pageable pageable);

    Optional<Subscribe> findByFollowingMemberIdAndMemberId(Long followingMemberId, Long MemberId);

    @Query("select s.member.id from Subscribe s where s.followingMemberId = :memberId")
    Set<Long> getFollowerIds(@Param("memberId") Long memberId);
}
