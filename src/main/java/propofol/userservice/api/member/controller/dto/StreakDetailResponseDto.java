package propofol.userservice.api.member.controller.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StreakDetailResponseDto {
    private LocalDate date;
    private Integer working;

    public StreakDetailResponseDto(LocalDate date, Integer working) {
        this.date = date;
        this.working = working;
    }
}
