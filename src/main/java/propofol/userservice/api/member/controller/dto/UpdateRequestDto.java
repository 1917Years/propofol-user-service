package propofol.userservice.api.member.controller.dto;

import lombok.Data;

@Data
public class UpdateRequestDto {

    private String nickname;
    private String degree; // 학력
    private String score; // 학점
    private String phoneNumber;
    private String password; // 비밀번호
}
