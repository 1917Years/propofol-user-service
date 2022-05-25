package propofol.userservice.api.timetable.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimeTableDetailResponseDto {
    private Long id;
    private String week; // 요일
    private String startTime;
    private String endTime;
}
