package propofol.userservice.domain.member.service;

import propofol.userservice.domain.member.service.dto.UpdateMemberDto;
import propofol.userservice.domain.member.entity.Member;

import java.util.Optional;

public interface MemberService {
    Optional<Member> getMemberById(Long id);
    Member getMemberByEmail(String email);
    Boolean checkDuplicateByNickname(String nickname);
    Boolean checkDuplicateByEmail(String email);

    void saveMember(Member member);

    void updateMember(UpdateMemberDto dto, Long memberId);
}
