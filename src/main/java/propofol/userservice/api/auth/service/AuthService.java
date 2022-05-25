package propofol.userservice.api.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.userservice.api.auth.controller.dto.LoginRequestDto;
import propofol.userservice.api.common.exception.dto.ErrorDto;
import propofol.userservice.api.common.jwt.JwtProvider;
import propofol.userservice.api.common.jwt.TokenDto;
import propofol.userservice.domain.exception.NotFoundMember;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.service.MemberService;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final JwtProvider jwtHandler;
    private final AuthenticationManager authenticationManager;
    private final MemberService memberService;

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

    private void saveRefreshToken(Authentication authenticate, TokenDto tokenDto) {
        String refreshToken = tokenDto.getRefreshToken();
        Long id = Long.valueOf(authenticate.getName());
        memberService.getMemberById(id).changeRefreshToken(refreshToken);
    }
}
