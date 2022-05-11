package propofol.userservice.api.auth.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EmailRequestDto {
    @NotBlank
    private String email;
}
