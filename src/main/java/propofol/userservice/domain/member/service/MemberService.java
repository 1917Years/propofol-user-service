package propofol.userservice.domain.member.service;

import propofol.userservice.domain.member.entity.Member;

public interface MemberService {
    Member getMemberByEmail(String email);
    Boolean checkDuplicateByNickname(String nickname);
    Boolean checkDuplicateByEmail(String email);

    void saveMember(Member member);
}
