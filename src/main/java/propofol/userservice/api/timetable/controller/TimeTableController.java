package propofol.userservice.api.timetable.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import propofol.userservice.api.common.annotation.Token;
import propofol.userservice.api.member.controller.dto.ResponseDto;
import propofol.userservice.api.timetable.controller.dto.TimeTableDetailResponseDto;
import propofol.userservice.api.timetable.controller.dto.TimeTableRequestDto;
import propofol.userservice.api.timetable.controller.dto.TimeTableResponseDto;
import propofol.userservice.domain.timetable.entity.TimeTable;
import propofol.userservice.domain.timetable.service.TimeTableService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/timetables")
public class TimeTableController {

    private final TimeTableService timeTableService;

    /**
     * 자신의 시간표 조회
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto findTimeTable(@Token Long memberId){
        return new ResponseDto(HttpStatus.OK.value(), "success", "시간표 조회 성공",
                createTimeTableResponseDto(memberId));
    }

    /**
     * 시간표 저장
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto saveTimeTable(@Token Long memberId,
                                     @Validated @RequestBody TimeTableRequestDto requestDto){
        return new ResponseDto(HttpStatus.OK.value(), "success", "시간표 저장 성공",
                timeTableService.saveTimeTable(memberId, requestDto.getWeek(),
                        requestDto.getStartTime(), requestDto.getEndTime()));
    }

    /**
     * 시간표 한 개 삭제
     */
    @DeleteMapping("/{timeTableId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto deleteOneTimeTable(@PathVariable("timeTableId") Long timeTableId){
        return new ResponseDto(HttpStatus.OK.value(), "success", "시간표 한 개 삭제 성공",
                timeTableService.deleteOneTimeTable(timeTableId));
    }

    /**
     * 시간표 전부 삭제
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto deleteAllTimeTable(@Token Long memberId){
        return new ResponseDto(HttpStatus.OK.value(), "success", "시간표 삭제 성공",
                timeTableService.deleteAllTimeTable(memberId));
    }



    private TimeTableResponseDto createTimeTableResponseDto(Long memberId) {
        TimeTableResponseDto responseDto = new TimeTableResponseDto();
        List<TimeTableDetailResponseDto> responseDtoList = responseDto.getTimeTables();
        List<TimeTable> timeTables = timeTableService.findTimeTable(memberId);
        timeTables.forEach(timeTable -> {
            responseDtoList.add(new TimeTableDetailResponseDto(timeTable.getId(), timeTable.getWeek(),
                    timeTable.getStartTime().toString(), timeTable.getEndTime().toString()));
        });
        return responseDto;
    }
}
