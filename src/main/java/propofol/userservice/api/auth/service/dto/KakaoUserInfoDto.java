package propofol.userservice.api.auth.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KakaoUserInfoDto {
    private Long id;
    @JsonProperty("expires_in")
    private Integer expiresIn;
    @JsonProperty("app_id")
    private Integer appId;
}
