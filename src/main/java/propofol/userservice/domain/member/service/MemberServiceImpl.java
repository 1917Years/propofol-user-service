package propofol.userservice.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.userservice.domain.exception.NotFoundMember;
import propofol.userservice.domain.member.entity.MemberTag;
import propofol.userservice.domain.member.service.dto.UpdateMemberDto;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.repository.MemberRepository;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;

    @Override
    public Member getMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundMember("회원을 찾을 수 없습니다.");
        });
    }

    @Override
    public Member getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> {
            throw new NotFoundMember("해당 회원을 찾을 수 없습니다.");
        });
        return member;
    }

    @Override
    public Member getMemberByNickname(String nickname) {
        return memberRepository.findByNickname(nickname).orElseThrow(() -> {
            throw new NotFoundMember("없는 회원입니다.");
        });
    }

    @Override
    public Boolean isExistByEmail(String email) {
        Member findMember = memberRepository.findExistByEmail(email);
        return findMember == null ? false : true;
    }

    @Override
    public Boolean isExistByNickname(String nickname) {
        Member findMember = memberRepository.findExistByNickname(nickname);
        return findMember == null ? false : true;
    }

    @Override
    public void saveMember(Member member) {
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void updateMember(UpdateMemberDto dto, Long memberId) {
        Member findMember = getMemberById(memberId);

        String password = dto.getPassword();
        String nickname = dto.getNickname();
        String phoneNumber = dto.getPhoneNumber();

        if(password != null){
            password = encoder.encode(password);
        }

        findMember.update(nickname, password, phoneNumber);
    }

    @Override
    @Transactional
    public void updatePassword(String email, String password) {
        Member findMember = getMemberByEmail(email);

        // TODO: 이메일 인증

        findMember.updatePassword(encoder.encode(password));
    }

    @Override
    public Member getRefreshMember(String refreshToken) {
        return memberRepository.findByRefreshToken(refreshToken).orElseThrow(() -> {
            throw new NotFoundMember("올바르지 않은 RefreshToken입니다.");
        });
    }

    @Override
    @Transactional
    public void changeRefreshToken(Member refreshMember, String refreshToken) {
        refreshMember.changeRefreshToken(refreshToken);
    }

    public Member getMemberWithTagByMemberId(Long memberId){
        return memberRepository.findMemberWithTagByMemberId(memberId)
                .orElseThrow(() -> {throw new NotFoundMember("회원을 찾을 수 없습니다.");});
    }


    @Override
    @Transactional
    public String saveMemberTags(Long memberId, List<Long> tagIds) {
        Member member = getMemberWithTagByMemberId(memberId);
        List<MemberTag> memberTags = member.getMemberTags();

        if(tagIds != null){
            memberTags.stream().filter(memberTag -> {
                if(tagIds.contains(memberTag.getTagId())){
                    tagIds.remove(memberTag.getTagId());
                    return true;
                }
                return false;
            }).forEach(memberTag -> {
                memberTag.changeCount(memberTag.getCount() + 1);
            });

            tagIds.forEach(tagId -> {
                MemberTag tag = MemberTag.createTag().tagId(tagId).count(1).build();
                tag.changeMember(member);
                memberTags.add(tag);
            });
        }

        return "ok";
    }

    @Override
    public Page<Member> getMemberWithTagId(Set<Long> tagIds, int page){
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        return memberRepository.getMemberTagByTagIds(tagIds, pageRequest);
    }

    @Override
    public Page<Member> getMembersByMemberIdsAndPage(Set<Long> memberIds, int page) {
        PageRequest pageRequest
                = PageRequest.of(page - 1, 10);
        return memberRepository.getMembersByMemberIdsAndPage(memberIds, pageRequest);
    }

    @Override
    public List<Member> getMembersByMemberIds(Set<Long> memberIds) {
        return memberRepository.getMembersByMemberIds(memberIds);
    }

    @Override
    public Member getMemberWithTimeTablesByMemberId(Long memberId) {
        return memberRepository.getMemberWithTimeTableByMemberId(memberId).orElseThrow(() -> {
            throw new NotFoundMember("회원 조회 실패");
        });
    }

    @Override
    @Transactional
    public void plusTotalRecommend(Long id) {
        Member findMember = getMemberById(id);
        findMember.plusTotalRecommend();
    }
}
