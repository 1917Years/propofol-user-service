package propofol.userservice.api.auth.controller.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter
@NoArgsConstructor
public class LoginRequestDto {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
