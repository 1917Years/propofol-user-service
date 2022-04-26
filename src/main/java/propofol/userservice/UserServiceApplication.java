package propofol.userservice;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan(basePackages = "propofol.userservice.api.common.properties")
//@EnableEurekaClient
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Bean
	public ModelMapper createModelMapper(){
		return new ModelMapper();
	}

	@Bean
	public BCryptPasswordEncoder createEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	public RestTemplate createRestTemplate() { return new RestTemplate();}

}
