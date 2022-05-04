package propofol.userservice.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import propofol.userservice.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Member findDuplicateByNickname(String nickname);
    Member findDuplicateByEmail(String email);

    Member findExistByEmail(String email);

    Optional<Member> findByRefreshToken(String refreshToken);
}
