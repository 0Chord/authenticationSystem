package authenticationSystem.authenticationSystem;

import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class AppConfig {
    @Bean
    RestTemplate restTemplate(){return new RestTemplate();}
    @Bean
    HttpHeaders httpHeaders(){return new HttpHeaders();}


}
