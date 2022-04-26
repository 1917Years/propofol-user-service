package propofol.userservice.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.userservice.domain.exception.NotFoundMember;
import propofol.userservice.domain.member.service.dto.UpdateMemberDto;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.repository.MemberRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;

    @Override
    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

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

    @Override
    @Transactional
    public void updateMember(UpdateMemberDto dto, Long memberId) {
        Member findMember = getMemberById(memberId).orElseThrow(() -> {
            throw new NotFoundMember("회원을 찾을 수 없습니다.");
        });

        String password = dto.getPassword();
        String nickname = dto.getNickname();
        String degree = dto.getDegree();
        String score = dto.getScore();
        String phoneNumber = dto.getPhoneNumber();

        if(password != null){
            password = encoder.encode(password);
        }

        findMember.update(nickname, degree, score, password, phoneNumber);
    }

}
