package propofol.userservice.api.member.controller.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MemberResponseDto {
    private Long id;
    private String email;
    private String username;
    private String nickname;
    private String phoneNumber;

    private List<TagDetailDto> tagInfos = new ArrayList<>();
}
