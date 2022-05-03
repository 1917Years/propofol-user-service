package propofol.userservice.api.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import propofol.userservice.api.auth.controller.dto.LoginRequestDto;
import propofol.userservice.api.auth.service.AuthService;
import propofol.userservice.api.common.exception.dto.ErrorDetailDto;
import propofol.userservice.api.common.exception.dto.ErrorDto;
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
    private final BCryptPasswordEncoder encoder;

    @PostMapping("/login")
    public Object login(@Validated @RequestBody LoginRequestDto loginDto, HttpServletResponse response){
        return authService.propofolLogin(loginDto, response);
    }

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED)
    public Object saveMember(@Validated @RequestBody SaveMemberDto saveMemberDto, HttpServletResponse response){
        ErrorDto errorDto = new ErrorDto();
        checkDuplicate(saveMemberDto, errorDto);

        if(errorDto.getErrors().size() != 0){
            errorDto.setStatus(HttpStatus.BAD_REQUEST.value());
            errorDto.setMessage("중복 오류!");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return errorDto;
        }

        String birth = saveMemberDto.getMemberBirth();
        LocalDate date = LocalDate.parse(birth, DateTimeFormatter.ISO_DATE);

        Member member = createMember(saveMemberDto, date);

        memberService.saveMember(member);

        return "ok";
    }

    /**
     * 기능 : 비밀번호 변경
     */
    @PostMapping("/updatePassword")
    public String updatePassword(@RequestBody UpdatePasswordRequestDto requestDto){
        memberService.updatePassword(requestDto.getEmail(), requestDto.getPassword());
        return "ok";
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
