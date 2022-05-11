package propofol.userservice.api.auth.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class NicknameRequestDto {
    @NotBlank
    private String nickname;
}
