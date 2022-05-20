package propofol.userservice.api.timetable.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Data
public class TimeTableRequestDto {
    @NotBlank(message = "요일이 null입니다.")
    private String week; // 요일
    @NotNull
    private String startTime;
    @NotNull
    private String endTime;
}
