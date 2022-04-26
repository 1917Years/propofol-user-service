package propofol.userservice.api.auth.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KakaoTokenResponseDto {

    @JsonProperty("token_type")
    private String tokenType; // 토큰 타입, bearer로 고정
    @JsonProperty("access_token")
    private String accessToken; // 사용자 액세스 토큰 값
    @JsonProperty("expires_in")
    private String expiresIn; // 액세스 토큰과 ID 토큰의 만료 시간(초)
    @JsonProperty("refresh_token")
    private String refreshToken; // 사용자 리프레시 토큰 값
    @JsonProperty("refresh_token_expires_in")
    private String refreshTokenExpiresIn; // 리프레시 토큰 만료 시간(초)
}
