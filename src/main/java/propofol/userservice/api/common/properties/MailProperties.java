package propofol.userservice.api.common.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "spring.mail")
@ConstructorBinding
public class MailProperties {
    private final String username;

    public MailProperties(String username) {
        this.username = username;
    }
}
