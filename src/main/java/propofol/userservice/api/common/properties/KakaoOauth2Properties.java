package propofol.userservice.api.common.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "oauth2.provider.kakao")
@ConstructorBinding
public class KakaoOauth2Properties {
    private final String getTokenUri;
    private final String userInfoUri;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String grantType;

    public KakaoOauth2Properties(String getTokenUri, String userInfoUri, String clientId,
                                 String clientSecret, String redirectUri, String grantType) {
        this.getTokenUri = getTokenUri;
        this.userInfoUri = userInfoUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.grantType = grantType;
    }
}
