package authenticationSystem.authenticationSystem.controller;

import authenticationSystem.authenticationSystem.dto.MemberForm;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

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
        if (Objects.equals(memberForm.getPassword(), memberForm.getAuthPassword())) {
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("userId",memberForm.getUserId());
            body.add("password", memberForm.getPassword());
            body.add("authPassword", memberForm.getAuthPassword());
            body.add("name", memberForm.getName());
            body.add("nickname", memberForm.getNickname());
            body.add("phone", memberForm.getPhone());
            HttpEntity<MultiValueMap<String, String>> requestMessage = new HttpEntity<>(body, httpHeaders);
            ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8081/signup/enroll", requestMessage, String.class);
            System.out.println("response = " + response);
            return "redirect:/";
        }//비밀번호 오류 내용 추가 예정
        return "signup/register";

    }
}
