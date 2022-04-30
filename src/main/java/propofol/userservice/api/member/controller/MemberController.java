package propofol.userservice.api.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import propofol.userservice.api.common.annotation.Token;
import propofol.userservice.api.member.controller.dto.*;
import propofol.userservice.api.member.service.MemberBoardService;
import propofol.userservice.domain.exception.NotFoundMember;
import propofol.userservice.domain.member.service.dto.UpdateMemberDto;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.service.MemberService;
import propofol.userservice.domain.streak.entity.Streak;
import propofol.userservice.domain.streak.service.StreakService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;
    private final ModelMapper modelMapper;
    private final MemberBoardService memberBoardService;
    private final StreakService streakService;

    /**
     * 회원 조회
     */
    @GetMapping
    public MemberResponseDto getMemberByMemberId(@Token Long memberId){
        Member findMember = memberService.getMemberById(memberId).orElseThrow(() -> {
            throw new NotFoundMember("회원을 찾을 수 없습니다.");
        });
        return modelMapper.map(findMember, MemberResponseDto.class);
    }

    /**
     * 회원 수정
     */
    @PostMapping("/update")
    public String updateMember(@RequestBody UpdateRequestDto dto, @Token Long memberId){
        UpdateMemberDto memberDto = modelMapper.map(dto, UpdateMemberDto.class);
        memberService.updateMember(memberDto, memberId);
        return "ok";
    }

    /**
     * 회원 게시글 가져오기
     */
    @GetMapping("/myBoards")
    public MemberBoardsResponseDto getMyBoards(@RequestParam Integer page,
                                               @RequestHeader(name = "Authorization") String token){
        return memberBoardService.getMyBoards(page, token);
    }

    /**
     * 회원 스트릭 가져오기
     */
    @GetMapping("/streak")
    public StreakResponseDto getStreaks(@Token Long memberId){
        return getStreakResponseDto(memberId);
    }

    /**
     * 스트릭 저장
     */
    @PostMapping("/streak")
    public void saveStreak(@Token Long memberId,
                           @RequestBody StreakRequestDto requestDto){
        Streak streak = createStreak(requestDto);
        streakService.saveStreak(memberId, streak);
    }

    private Streak createStreak(StreakRequestDto requestDto) {
        return Streak.createStreak()
                .workingDate(requestDto.getDate())
                .working(1)
                .build();
    }

    private StreakResponseDto getStreakResponseDto(Long memberId) {
        StreakResponseDto streakResponseDto = new StreakResponseDto();
        int year = LocalDate.now().getYear();
        streakResponseDto.setYear(String.format(year + "년"));

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        List<StreakDetailResponseDto> responseDtoStreaks = streakResponseDto.getStreaks();
        List<Streak> streaks = streakService.getStreaksByMemberId(memberId, start, end);
        streaks.forEach(streak -> {
            responseDtoStreaks.add(new StreakDetailResponseDto(streak.getWorkingDate(), streak.getWorking()));
        });
        return streakResponseDto;
    }


}
