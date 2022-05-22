package propofol.userservice.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.userservice.domain.member.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Member findExistByEmail(String email);

    Optional<Member> findByRefreshToken(String refreshToken);

    Member findExistByNickname(String nickname);

    Optional<Member> findByNickname(String nickname);

    @Query("select distinct m from Member m join fetch m.timeTables mt where mt.week in :weeks")
    List<Member> findMemberWithTimeTable(@Param("weeks") Collection<String> weeks);

    @Query("select m from Member m left join fetch m.memberTags mt where m.id = :memberId")
    Optional<Member> findMemberWithTagByMemberId(@Param("memberId") Long memberId);
}
