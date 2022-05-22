package propofol.userservice.api.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import propofol.userservice.api.auth.controller.dto.EmailRequestDto;
import propofol.userservice.api.auth.controller.dto.LoginRequestDto;
import propofol.userservice.api.auth.controller.dto.NicknameRequestDto;
import propofol.userservice.api.auth.service.AuthService;
import propofol.userservice.api.common.exception.DuplicateEmailException;
import propofol.userservice.api.common.exception.DuplicateNicknameException;
import propofol.userservice.api.common.exception.NotExpiredAccessTokenException;
import propofol.userservice.api.common.exception.ReCreateJwtException;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
@Slf4j
public class AuthController {
    private final MemberService memberService;
    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder encoder;
    
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto duplicateEmailException(DuplicateEmailException e){
        return new ResponseDto(HttpStatus.BAD_REQUEST.value(), "fail", "중복 오류", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto NotExpiredAccessTokenException(NotExpiredAccessTokenException e){
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail",
                "토큰 재발급 실패", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto ReCreateJwtException(ReCreateJwtException e){
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail",
                "토큰 재발급 실패", e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto duplicateNicknameException(DuplicateNicknameException e){
        return new ResponseDto(HttpStatus.BAD_REQUEST.value(), "fail", "중복 오류", e.getMessage());
    }

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
    public ResponseDto saveMember(@Validated @RequestBody SaveMemberDto saveMemberDto) {
//        String birth = saveMemberDto.getMemberBirth();
//        LocalDate date = LocalDate.parse(birth, DateTimeFormatter.ISO_DATE);
        Member member = createMember(saveMemberDto);
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

        if(jwtProvider.isTokenValid(token)){
           throw new NotExpiredAccessTokenException("Valid access-token");
        }

        if(jwtProvider.isRefreshTokenValid(refreshToken)
                && refreshMember.getRefreshToken().equals(refreshToken)){
            TokenDto tokenDto = jwtProvider.createReJwt(String.valueOf(refreshMember.getId()),
                    refreshMember.getAuthority().toString());
            memberService.changeRefreshToken(refreshMember, tokenDto.getRefreshToken());
            return new ResponseDto<>(HttpStatus.OK.value(), "success",
                    "토큰 재발급 성공!", tokenDto);
        }

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        throw new ReCreateJwtException("Please Re-Login.");
    }

    /**
     * email 중복 확인
     */
    @PostMapping("/email")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto checkDuplicateEmail(@Validated @RequestBody EmailRequestDto requestDto){
        if(memberService.isExistByEmail(requestDto.getEmail())){
            throw new DuplicateEmailException("이메일 중복");
        }
        return new ResponseDto(HttpStatus.OK.value(), "success", "이메일 중복 없음", "ok");
    }

    /**
     * nickname 중복 확인
     */
    @PostMapping("/nickname")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto checkDuplicateNickname(@Validated @RequestBody NicknameRequestDto requestDto){
        if(memberService.isExistByNickname(requestDto.getNickname())){
            throw new DuplicateNicknameException("닉네임 중복");
        }
        return new ResponseDto(HttpStatus.OK.value(), "success", "닉네임 중복 없음", "ok");
    }


    private Member createMember(SaveMemberDto saveMemberDto) {
        Member member = Member.createMember()
                .email(saveMemberDto.getEmail())
                .password(encoder.encode(saveMemberDto.getPassword()))
                .nickname(saveMemberDto.getNickname())
                .username(saveMemberDto.getUsername())
                .phoneNumber(saveMemberDto.getPhoneNumber())
                .authority(Authority.ROLE_USER)
                .totalRecommend(0)
                .build();
        return member;
    }
}
