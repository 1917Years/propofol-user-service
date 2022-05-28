package propofol.userservice.api.subscribe.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class MemberResponseDto {
    private String nickname;
    private Set<Long> memberIds;
}
