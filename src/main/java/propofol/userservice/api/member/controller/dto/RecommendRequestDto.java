package propofol.userservice.api.member.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RecommendRequestDto {
    @NotNull(message = "Id Null")
    private Long id;
}
