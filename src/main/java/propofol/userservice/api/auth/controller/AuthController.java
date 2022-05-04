package propofol.userservice.api.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import propofol.userservice.api.auth.controller.dto.LoginRequestDto;
import propofol.userservice.api.auth.service.AuthService;
import propofol.userservice.api.common.exception.dto.ErrorDetailDto;
import propofol.userservice.api.common.exception.dto.ErrorDto;
import propofol.userservice.api.common.jwt.JwtProvider;
import propofol.userservice.api.common.jwt.TokenDto;
import propofol.userservice.api.member.controller.dto.ResponseDto;
import propofol.userservice.api.member.controller.dto.SaveMemberDto;
import propofol.userservice.api.auth.controller.dto.UpdatePasswordRequestDto;
import propofol.userservice.domain.member.entity.Authority;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.service.MemberService;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
@Slf4j
public class AuthController {
    private final MemberService memberService;
    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder encoder;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto login(@Validated @RequestBody LoginRequestDto loginDto, HttpServletResponse response){
        Object result = authService.propofolLogin(loginDto, response);
        if(result instanceof ErrorDto){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "로그인 실패", result);
        }else{
            return new ResponseDto<>(HttpStatus.OK.value(), "success", "로그인 성공!", result);
        }
    }

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto saveMember(@Validated @RequestBody SaveMemberDto saveMemberDto, HttpServletResponse response){
        ErrorDto errorDto = new ErrorDto();
        checkDuplicate(saveMemberDto, errorDto);

        if(errorDto.getErrors().size() != 0){
            errorDto.setErrorMessage("중복 오류!");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "회원 저장 실패", errorDto);
        }

        String birth = saveMemberDto.getMemberBirth();
        LocalDate date = LocalDate.parse(birth, DateTimeFormatter.ISO_DATE);

        Member member = createMember(saveMemberDto, date);

        memberService.saveMember(member);

        return new ResponseDto<>(HttpStatus.CREATED.value(), "success", "회원 저장 성공!", "ok");
    }

    /**
     * 기능 : 비밀번호 변경
     */
    @PostMapping("/updatePassword")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto updatePassword(@RequestBody UpdatePasswordRequestDto requestDto){
        memberService.updatePassword(requestDto.getEmail(), requestDto.getPassword());
        return new ResponseDto<>(HttpStatus.OK.value(), "success", "패스워드 변경 성공!", "ok");
    }

    @GetMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto checkRefreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                         @RequestHeader("refresh-token") String refreshToken,
                                         HttpServletResponse response){
        Member refreshMember = memberService.getRefreshMember(refreshToken);
        // access-token 만료, refresh-token 만료X, refresh-token db와 같을 때
        if(jwtProvider.isTokenValid(token)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail",
                    "토큰 재발급 실패", "Valid access-token");
        }

        if(jwtProvider.isRefreshTokenValid(refreshToken)
                && refreshMember.getRefreshToken().equals(refreshToken)){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            TokenDto tokenDto = jwtProvider.createJwt(authentication);
            memberService.changeRefreshToken(refreshMember, tokenDto.getRefreshToken());
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "success",
                    "토큰 재발급 성공!", tokenDto);
        }

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail",
                "토큰 재발급 실패", "Please Relogin.");
    }



    private Member createMember(SaveMemberDto saveMemberDto, LocalDate date) {
        Member member = Member.createMember()
                .email(saveMemberDto.getEmail())
                .password(encoder.encode(saveMemberDto.getPassword()))
                .nickname(saveMemberDto.getNickname())
                .username(saveMemberDto.getUsername())
                .birth(date)
                .degree(saveMemberDto.getDegree())
                .phoneNumber(saveMemberDto.getPhoneNumber())
                .score(saveMemberDto.getScore())
                .authority(Authority.ROLE_USER)
                .build();
        return member;
    }

    private void checkDuplicate(SaveMemberDto saveMemberDto, ErrorDto errorDto) {
        if(memberService.checkDuplicateByEmail(saveMemberDto.getEmail())){
            errorDto.getErrors().add(new ErrorDetailDto("Email", "중복 오류"));
        }

        if(memberService.checkDuplicateByNickname(saveMemberDto.getNickname())){
            errorDto.getErrors().add(new ErrorDetailDto("Nickname", "중복 오류"));
        }
    }

}
