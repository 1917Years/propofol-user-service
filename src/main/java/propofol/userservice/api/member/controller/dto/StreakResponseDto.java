package propofol.userservice.api.member.controller.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StreakResponseDto {
    private String year;
    private List<StreakDetailResponseDto> streaks = new ArrayList<>();
}
