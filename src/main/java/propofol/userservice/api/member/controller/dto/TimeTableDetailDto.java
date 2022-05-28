package propofol.userservice.api.member.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeTableDetailDto {
    private List<String> weeks = new ArrayList<>();
    private List<String> startTimes = new ArrayList<>();
    private List<String> endTimes = new ArrayList<>();
}
