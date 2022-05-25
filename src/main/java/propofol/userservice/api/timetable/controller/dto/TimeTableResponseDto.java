package propofol.userservice.api.timetable.controller.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TimeTableResponseDto {
    private List<TimeTableDetailResponseDto> timeTables = new ArrayList<>();
}
