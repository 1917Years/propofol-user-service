package propofol.userservice.api.common.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "token")
@ConstructorBinding
public class JwtProperties {

    private final String type;
    private final String expirationTime;
    private final String refreshExpirationTime;
    private final String secret;

    public JwtProperties(String type, String expirationTime, String refreshExpirationTime, String secret) {
        this.type = type;
        this.expirationTime = expirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
        this.secret = secret;
    }
}
