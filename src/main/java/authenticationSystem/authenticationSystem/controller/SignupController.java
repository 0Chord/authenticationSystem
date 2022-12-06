package authenticationSystem.authenticationSystem.controller;

import authenticationSystem.authenticationSystem.dto.MemberForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/signup")
public class SignupController {

    RestTemplate restTemplate;
    HttpHeaders httpHeaders;

    public SignupController(RestTemplate restTemplate, HttpHeaders httpHeaders) {
        this.restTemplate = restTemplate;
        this.httpHeaders = httpHeaders;
    }

    @GetMapping("/register")
    public String signup(){
        return "signup/register";
    }

    @PostMapping("/enroll")
    public String enroll(@Validated MemberForm memberForm){
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.postForEntity("http://localhost:8081/signup/enroll",)
        return "redirect:/";
    }
}
