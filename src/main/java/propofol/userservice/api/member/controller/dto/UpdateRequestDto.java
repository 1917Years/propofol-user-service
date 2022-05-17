package propofol.userservice.api.member.controller.dto;

import lombok.Data;

@Data
public class UpdateRequestDto {

    private String nickname;
    private String phoneNumber;
    private String password; // 비밀번호
}
