package propofol.userservice.api.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import propofol.userservice.domain.exception.NotFoundMember;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.service.MemberService;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthMemberService implements UserDetailsService {

    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member findMember = memberService.getMemberByEmail(username);

        if(findMember == null){
            throw new NotFoundMember("없는 계정 입니다.");
        }

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(findMember.getAuthority().toString());

        return new User(findMember.getEmail(), findMember.getPassword(), Collections.singleton(grantedAuthority));
    }
}
