package propofol.userservice.domain.member.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("select m from Member m where m.id in :memberIds")
    Page<Member> getMembersByMemberIdsAndPage(@Param("memberIds") Collection<Long> memberIds, Pageable pageable);

    @Query("select m from Member m left join fetch m.profile where m.id in :memberIds")
    List<Member> getMembersByMemberIds(@Param("memberIds") Collection<Long> memberIds);

    @Query("select m from Member m left join fetch m.memberTags mt where m.id = :memberId")
    Optional<Member> findMemberWithTagByMemberId(@Param("memberId") Long memberId);

    @Query("select mt.member from MemberTag mt where mt.TagId in :tagIds group by mt.member.id " +
            "order by count(mt) desc, mt.member.totalRecommend desc")
    Page<Member> getMemberTagByTagIds(@Param("tagIds") Collection<Long> tagIds, Pageable pageable);

    @Query("select m from Member m left join fetch m.timeTables where m.id = :memberId")
    Optional<Member> getMemberWithTimeTableByMemberId(@Param("memberId") Long memberId);

    @Query("select mt.member from MemberTag mt where mt.TagId in :tagIds and mt.member.id not in :memberIds " +
            "group by mt.member.id order by count(mt) desc, mt.member.totalRecommend desc")
    Page<Member> getMemberTagByTagIdsAndNoMemberIds(@Param("tagIds") Collection<Long> tagIds,
                                                    @Param("memberIds") Collection<Long> memberIds,
                                                    Pageable pageable);
}
