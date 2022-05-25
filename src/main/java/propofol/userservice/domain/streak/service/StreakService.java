package propofol.userservice.domain.streak.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.userservice.api.member.controller.dto.StreakRequestDto;
import propofol.userservice.domain.exception.NotFoundMember;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.service.MemberService;
import propofol.userservice.domain.streak.entity.Streak;
import propofol.userservice.domain.streak.repository.StreakRepository;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreakService {

    private final StreakRepository streakRepository;
    private final MemberService memberService;

    /**
     * 스트릭 가져오기
     */
    public List<Streak> getStreaksByMemberId(Long memberId, LocalDate start, LocalDate end){
        return streakRepository.findByMember_IdAndWorkingDateBetween(memberId, start, end);
    }

    /**
     * 스트릭 저장
     */
    @Transactional
    public void saveStreak(Long memberId, Streak streak) {
        Member findMember = memberService.getMemberById(memberId);

        Streak resultStreak = streakRepository
                .findByMember_IdAndAndWorkingDate(memberId, streak.getWorkingDate()).orElse(streak);

        if (streak == resultStreak) {
            resultStreak.addMember(findMember);
            streakRepository.save(resultStreak);
        }else{
            int value = resultStreak.getWorking() + 1;
            resultStreak.addWorking(value);
        }
    }

}
