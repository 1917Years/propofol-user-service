package propofol.userservice.api.member.controller.dto;

import lombok.Data;
import propofol.userservice.api.timetable.controller.dto.TimeTableDetailResponseDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class MemberDetailInfoAllResponseDto {
    private String email;
    private String username; // 사용자 이름(성명)
    private String nickname; // 별명
    private String phoneNumber;
    private LocalDate birth;
    private String degree; // 학력
    private String score; // 학점

    List<TimeTableDetailResponseDto> timeTables = new ArrayList<>();
}
