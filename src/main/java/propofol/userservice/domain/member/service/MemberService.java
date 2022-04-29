package propofol.userservice.domain.member.service;

import propofol.userservice.domain.member.service.dto.UpdateMemberDto;
import propofol.userservice.domain.member.entity.Member;

import java.util.Optional;

public interface MemberService {
    Optional<Member> getMemberById(Long id);
    Member getMemberByEmail(String email);
    Boolean isExistByEmail(String email);

    /**
     * 닉네임, 이메일 중복 체크
     */
    Boolean checkDuplicateByNickname(String nickname);
    Boolean checkDuplicateByEmail(String email);

    /**
     * 회원 저장
     */
    void saveMember(Member member);

    /**
     * 회원정보 수정
     */
    void updateMember(UpdateMemberDto dto, Long memberId);

    /**
     *  비밀변호 수정
     */
    void updatePassword(String email, String password);
}
