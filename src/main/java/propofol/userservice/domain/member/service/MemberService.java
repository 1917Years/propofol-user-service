package propofol.userservice.domain.member.service;

import propofol.userservice.domain.member.service.dto.UpdateMemberDto;
import propofol.userservice.domain.member.entity.Member;

import java.util.List;

public interface MemberService {
    Member getMemberById(Long id);
    Member getMemberByEmail(String email);
    Member getMemberByNickname(String nickname);
    Boolean isExistByEmail(String email);
    Boolean isExistByNickname(String nickname);

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

    /**
     * refreshToken 가져오기
     */
    Member getRefreshMember(String refreshToken);

    void changeRefreshToken(Member refreshMember, String refreshToken);

    String saveMemberTags(Long memberId, List<Long> tagIds);

    void plusTotalRecommend(Long id);
}
