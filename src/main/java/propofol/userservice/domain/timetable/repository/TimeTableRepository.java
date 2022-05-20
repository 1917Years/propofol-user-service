package propofol.userservice.domain.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.userservice.domain.timetable.entity.TimeTable;

import java.util.List;

public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {

    List<TimeTable> findAllByMemberId(Long memberId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from TimeTable t where t.id = :timeTableId")
    void deleteOneById(@Param("timeTableId") Long timeTableId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from TimeTable t where t.member.id = :memberId")
    void deleteAllByMemberId(@Param("memberId") Long memberId);
}
