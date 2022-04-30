package propofol.userservice.api.member.controller.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StreakRequestDto {
    private LocalDate date;
    private Boolean working;
}
