package propofol.userservice.domain.streak.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import propofol.userservice.domain.streak.entity.Streak;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StreakRepository extends JpaRepository<Streak, Long> {
    List<Streak> findByMember_IdAndWorkingDateBetween(Long memberId, LocalDate start, LocalDate end);

    Optional<Streak> findByMember_IdAndAndWorkingDate(Long memberId, LocalDate workingDate);
}
