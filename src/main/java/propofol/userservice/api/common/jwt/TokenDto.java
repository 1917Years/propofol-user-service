package propofol.userservice.api.common.jwt;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TokenDto {
    private String type;
    private String accessToken;
    private String refreshToken;
    private Long expirationDate;

    @Builder(builderMethodName = "createTokenDto")
    public TokenDto(String type, String accessToken, String refreshToken, Long expirationDate) {
        this.type = type;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expirationDate = expirationDate;
    }
}
