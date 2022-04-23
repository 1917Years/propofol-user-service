package propofol.userservice.api.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import propofol.userservice.api.exception.dto.ErrorDetailDto;
import propofol.userservice.api.exception.dto.ErrorDto;
import propofol.userservice.api.member.controller.dto.FindMemberDto;
import propofol.userservice.api.member.controller.dto.SaveMemberDto;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.entity.MemberRole;
import propofol.userservice.domain.member.service.MemberService;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final ModelMapper modelMapper;

    @GetMapping("/users/{email}")
    public FindMemberDto getMemberByEmail(@PathVariable String email){
        Member findMember = memberService.getMemberByEmail(email);
        return modelMapper.map(findMember, FindMemberDto.class);
    }

    @PostMapping("/users")
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

        Member member = Member.createMember()
                .email(saveMemberDto.getEmail())
                .password(saveMemberDto.getPassword())
                .nickname(saveMemberDto.getNickname())
                .username(saveMemberDto.getUsername())
                .birth(date)
                .degree(saveMemberDto.getDegree())
                .phoneNumber(saveMemberDto.getPhoneNumber())
                .score(saveMemberDto.getScore())
                .memberRole(MemberRole.BASIC)
                .build();

        memberService.saveMember(member);

        return "회원 가입 성공!";
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
