package propofol.userservice.api.member.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberInfoPartDto {
    private String email;
    private String username;
    private String nickname;
    private String phoneNumber;
}
