package authenticationSystem.authenticationSystem.controller;

import authenticationSystem.authenticationSystem.dto.LoginForm;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/signIn")
public class signInController {

    RestTemplate restTemplate;
    HttpHeaders httpHeaders;

    public signInController(RestTemplate restTemplate, HttpHeaders httpHeaders) {
        this.restTemplate = restTemplate;
        this.httpHeaders = httpHeaders;
    }

    @PostMapping("/login")
    public String login(@Validated LoginForm loginForm){
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("userId",loginForm.getId());
        body.add("password",loginForm.getPassword());

        HttpEntity<MultiValueMap<String, String>> requestMessage = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<String> responseMessage = restTemplate.postForEntity("http://localhost:8081/signIn/login", requestMessage, String.class);
        System.out.println("responseMessage = " + responseMessage);
        return "redirect:/";
    }
}
