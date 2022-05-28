package propofol.userservice.api.subscribe.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubscribeResponseDto {
    private Long id;
    private String nickname;

    private String profileString;
    private String profileType;
}
