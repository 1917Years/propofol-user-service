package propofol.userservice.domain.member.service.dto;

import lombok.Data;

@Data
public class UpdateMemberDto {
    private String nickname;
    private String degree; // 학력
    private String score; // 학점
    private String phoneNumber;
    private String password; // 비밀번호
}
