package propofol.userservice.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import propofol.userservice.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
