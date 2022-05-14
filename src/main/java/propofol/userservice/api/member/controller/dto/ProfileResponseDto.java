package propofol.userservice.api.member.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponseDto {
    private Long memberId;
    private byte[] profileBytes;
    private String profileType;
}
