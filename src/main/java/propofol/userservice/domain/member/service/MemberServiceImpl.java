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

import javax.persistence.EntityManager;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;
    private final EntityManager em;

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
        Member findMember = getMemberById(memberId).orElseThrow(() -> {
            throw new NotFoundMember("회원을 찾을 수 없습니다.");
        });

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
}
