package propofol.userservice.api.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import propofol.userservice.api.common.annotation.Token;
import propofol.userservice.api.member.controller.dto.MemberBoardsResponseDto;
import propofol.userservice.api.member.controller.dto.MemberResponseDto;
import propofol.userservice.api.member.controller.dto.UpdateRequestDto;
import propofol.userservice.api.member.service.MemberBoardService;
import propofol.userservice.domain.exception.NotFoundMember;
import propofol.userservice.domain.member.service.dto.UpdateMemberDto;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;
    private final ModelMapper modelMapper;
    private final MemberBoardService memberBoardService;

    @GetMapping("/health-check")
    public String health(){
        return "Working!!";
    }

    @GetMapping
    public MemberResponseDto getMemberByMemberId(@Token Long memberId){
        Member findMember = memberService.getMemberById(memberId).orElseThrow(() -> {
            throw new NotFoundMember("회원을 찾을 수 없습니다.");
        });
        return modelMapper.map(findMember, MemberResponseDto.class);
    }

    @PostMapping("/update")
    public String updateMember(@RequestBody UpdateRequestDto dto, @Token Long memberId){
        UpdateMemberDto memberDto = modelMapper.map(dto, UpdateMemberDto.class);
        memberService.updateMember(memberDto, memberId);
        return "ok";
    }

    @GetMapping("/myBoards")
    public MemberBoardsResponseDto getMyBoards(@RequestParam Integer page,
                                               @RequestHeader(name = "Authorization") String token){
        return memberBoardService.getMyBoards(page, token);
    }


}
