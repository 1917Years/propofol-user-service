package propofol.userservice.api.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.userservice.api.auth.controller.dto.LoginRequestDto;
import propofol.userservice.api.common.exception.MailSendException;
import propofol.userservice.api.common.exception.dto.ErrorDto;
import propofol.userservice.api.common.jwt.JwtProvider;
import propofol.userservice.api.common.jwt.TokenDto;
import propofol.userservice.domain.member.service.MemberService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final JwtProvider jwtHandler;
    private final AuthenticationManager authenticationManager;
    private final MemberService memberService;
    private final JavaMailSender mailSender;

    @Transactional
    public Object propofolLogin(LoginRequestDto loginDto){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        try {
            TokenDto tokenDto = jwtHandler.createJwt(authenticate);
            saveRefreshToken(authenticate, tokenDto);
            return tokenDto;

        }catch (Exception exception){
            log.info("login Error = {}", exception.getMessage());
            ErrorDto errorDto = new ErrorDto();
            if(exception instanceof AuthenticationServiceException) {
                errorDto.setErrorMessage("회원을 찾을 수 없습니다.");
            }else if(exception instanceof BadCredentialsException){
                errorDto.setErrorMessage("패스워드 오류!");
            }else{
                errorDto.setErrorMessage(exception.getMessage());
            }
            return errorDto;
        }

    }

    public String sendEmail(String address) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(random.nextInt(10));
            if(i == 3) sb.append("-");
        }

        String key = sb.toString();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = null;
        try {
            mimeMessageHelper = new MimeMessageHelper(message, false, "UTF-8");
            mimeMessageHelper.setTo(address);
            mimeMessageHelper.setSubject("propofol 인증 메일");
            mimeMessageHelper.setText(key);
        } catch (MessagingException e) {
            throw new MailSendException("메일 전송 실패");
        }

        mailSender.send(message);

        return key;
    }

    private void saveRefreshToken(Authentication authenticate, TokenDto tokenDto) {
        String refreshToken = tokenDto.getRefreshToken();
        Long id = Long.valueOf(authenticate.getName());
        memberService.getMemberById(id).changeRefreshToken(refreshToken);
    }
}
