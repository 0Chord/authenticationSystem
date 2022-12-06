package authenticationSystem.authenticationSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    RestTemplate restTemplate(){return new RestTemplate();}
    @Bean
    HttpHeaders httpHeaders(){return new HttpHeaders();}
}
