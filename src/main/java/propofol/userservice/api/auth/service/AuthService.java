package propofol.userservice.api.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import propofol.userservice.api.auth.controller.dto.LoginRequestDto;
import propofol.userservice.api.exception.dto.ErrorDto;
import propofol.userservice.api.jwt.JwtProvider;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final JwtProvider jwtHandler;
    private final AuthenticationManager authenticationManager;

    public Object propofolLogin(LoginRequestDto loginDto, HttpServletResponse response){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        try {

            Authentication authenticate = authenticationManager.authenticate(authenticationToken);
            return jwtHandler.createJwt(authenticate);

        }catch (Exception exception){
            log.info("login Error = {}", exception.getMessage());
            ErrorDto errorDto = new ErrorDto();
            if(exception instanceof AuthenticationServiceException) {
                errorDto.setMessage("회원을 찾을 수 없습니다.");
                errorDto.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }else if(exception instanceof BadCredentialsException){
                errorDto.setMessage("패스워드 오류!");
                errorDto.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            return errorDto;
        }

    }
}
