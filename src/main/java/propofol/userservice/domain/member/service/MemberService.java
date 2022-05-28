package propofol.userservice.domain.member.service;

import org.springframework.data.domain.Page;
import propofol.userservice.domain.member.service.dto.UpdateMemberDto;
import propofol.userservice.domain.member.entity.Member;

import java.util.List;
import java.util.Set;

public interface MemberService {
    Member getMemberById(Long id);
    Member getMemberByEmail(String email);
    Member getMemberByNickname(String nickname);
    Member getMemberWithTagByMemberId(Long memberId);
    Boolean isExistByEmail(String email);
    Boolean isExistByNickname(String nickname);
    Page<Member> getMemberWithTagId(Set<Long> tagIds, int page);
    Page<Member> getMembersByMemberIdsAndPage(Set<Long> memberIds, int page);
    List<Member> getMembersByMemberIds(Set<Long> memberIds);
    Member getMemberWithTimeTablesByMemberId(Long memberId);

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
