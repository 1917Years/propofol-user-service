package propofol.userservice.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import propofol.userservice.domain.exception.NotFoundMember;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

    @Override
    public Member getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> {
            throw new NotFoundMember("해당 회원을 찾을 수 없습니다.");
        });
        return member;
    }

    @Override
    public Boolean checkDuplicateByNickname(String nickname) {
        Member findMember = memberRepository.findDuplicateByNickname(nickname);
        if(findMember == null) return false;
        return true;
    }

    @Override
    public Boolean checkDuplicateByEmail(String email) {
        Member findMember = memberRepository.findDuplicateByEmail(email);
        if(findMember == null) return false;
        return true;
    }

    @Override
    public void saveMember(Member member) {
        memberRepository.save(member);
    }
}
