package propofol.userservice.api.member.controller.dto;

import lombok.Data;

import javax.persistence.Column;

@Data
public class MemberMatchingResponseDto {
    private String username; // 사용자 이름(성명)
    @Column(unique = true)
    private String nickname; // 별명
    private long totalRecommend;
}
