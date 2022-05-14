package propofol.userservice.api.common.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "file")
@ConstructorBinding
public class ProfilePropertiees {
    private String profileDir;

    public ProfilePropertiees(String profileDir) {
        this.profileDir = profileDir;
    }
}
