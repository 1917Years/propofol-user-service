package propofol.userservice.domain.timetable.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.userservice.domain.exception.TimeSaveException;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.service.MemberService;
import propofol.userservice.domain.timetable.entity.TimeTable;
import propofol.userservice.domain.timetable.repository.TimeTableRepository;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeTableService {

    private final TimeTableRepository timeTableRepository;
    private final MemberService memberService;

    @Transactional
    public String saveTimeTable(Long memberId, String week, String startTimeString, String endTimeString) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startTime = LocalTime.parse(startTimeString, dateTimeFormatter);
        LocalTime endTime = LocalTime.parse(endTimeString, dateTimeFormatter);

        if(startTime.isAfter(endTime)){
            throw new TimeSaveException("올바른 형식이 아닙니다.");
        }

        Member findMember = memberService.getMemberById(memberId);

        TimeTable timeTable = createTimeTable(week, startTime, endTime);
        timeTable.changeMember(findMember);
        timeTableRepository.save(timeTable);

        return "ok";
    }

    public List<TimeTable> findTimeTable(Long memberId) {
        return timeTableRepository.findAllByMemberId(memberId);
    }

    @Transactional
    public String deleteOneTimeTable(Long timeTableId) {
        timeTableRepository.deleteOneById(timeTableId);

        return "ok";
    }

    @Transactional
    public String deleteAllTimeTable(Long memberId) {
        timeTableRepository.deleteAllByMemberId(memberId);

        return "ok";
    }

    private TimeTable createTimeTable(String week, LocalTime startTime, LocalTime endTime) {
        return TimeTable.createTimeTable()
                .week(week)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }


}
