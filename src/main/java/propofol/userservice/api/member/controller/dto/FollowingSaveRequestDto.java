package propofol.userservice.api.member.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FollowingSaveRequestDto {
    @NotBlank
    private String followingNickname;
}
