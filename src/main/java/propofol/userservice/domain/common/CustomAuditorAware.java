package propofol.userservice.domain.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import propofol.userservice.domain.exception.NotFoundMember;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.service.MemberService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomAuditorAware implements AuditorAware<String> {

    private final MemberService memberService;

    @Override
    public Optional<String> getCurrentAuditor() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        if(name.equals("anonymousUser")) return Optional.ofNullable("MASTER");
        else {
            Member member = memberService.getMemberById(Long.valueOf(name)).orElseThrow(() -> {
                throw new NotFoundMember("회원을 찾을 수 없습니다.");
            });
            return Optional.ofNullable(member.getEmail());
        }
    }
}
