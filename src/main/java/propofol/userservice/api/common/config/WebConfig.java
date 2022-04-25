package propofol.userservice.api.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import propofol.userservice.api.common.resolver.TokenResolver;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final TokenResolver tokenResolver;

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:3000")
//                .allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE");
//    }


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
       resolvers.add(tokenResolver);
    }
}
