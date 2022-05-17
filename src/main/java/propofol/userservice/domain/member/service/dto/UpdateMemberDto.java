package propofol.userservice.domain.member.service.dto;

import lombok.Data;

@Data
public class UpdateMemberDto {
    private String nickname;
    private String phoneNumber;
    private String password; // 비밀번호
}
