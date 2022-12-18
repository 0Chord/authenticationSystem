package authenticationSystem.authenticationSystem.controller;

import authenticationSystem.authenticationSystem.dto.MailAuthForm;
import authenticationSystem.authenticationSystem.dto.MemberForm;
import authenticationSystem.authenticationSystem.service.AuthService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Controller
@RequestMapping("/signup")
public class SignupController {
    RestTemplate restTemplate;
    HttpHeaders httpHeaders;
    AuthService authService;

    public SignupController(RestTemplate restTemplate, HttpHeaders httpHeaders, AuthService authService) {
        this.restTemplate = restTemplate;
        this.httpHeaders = httpHeaders;
        this.authService = authService;
    }

    @GetMapping("/register")
    public String signup(@ModelAttribute MemberForm memberForm){
        return "signup/register";
    }

    @PostMapping("/register")
    public String enroll(@ModelAttribute @Validated MemberForm memberForm, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return "signup/register";
        }
        if (Objects.equals(memberForm.getPassword(), memberForm.getAuthPassword())) {
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("userId",memberForm.getUserId());
            body.add("password", memberForm.getPassword());
            body.add("authPassword", memberForm.getAuthPassword());
            body.add("name", memberForm.getName());
            body.add("nickname", memberForm.getNickname());
            body.add("phone", memberForm.getPhone());
            body.add("adminRight","USER");
            HttpEntity<MultiValueMap<String, String>> requestMessage = new HttpEntity<>(body, httpHeaders);
            ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8081/signup/enroll", requestMessage, String.class);
            if(Objects.equals(response.getBody(), "StoredMember")){
                bindingResult.reject("signupFail","이미 등록된 아이디 입니다.");
                return "signup/register";
            }else if(Objects.equals(response.getBody(),"WrongIdPattern")){
                bindingResult.reject("signupFail","아이디 형식에 맞게 가입해주세요.");
                return "signup/register";
            }else if(Objects.equals(response.getBody(),"WrongPhonePattern")){
                bindingResult.reject("signupFail","휴데폰 번호를 형식에 맞게 기입해주세요.");
                return "signup/register";
            }else if(Objects.equals(response.getBody(),"WrongNamePattern")){
                bindingResult.reject("signupFail","이름을 올바르게 기입해주세요.");
                return "signup/register";
            }else if(Objects.equals(response.getBody(),"WrongPasswordPattern")){
                bindingResult.reject("signupFail","비밀번호를 형식에 맞게 기입해주세요.");
                return "signup/register";
            }
            ResponseEntity<?> responseEntity = authService.mailAuth(httpHeaders, restTemplate, memberForm.getUserId());
            model.addAttribute("userId",memberForm.getUserId());
            return "signup/mailAuth";
        }else{
            bindingResult.reject("signupFail","비밀번호가 일치하지 않습니다.");
            return "signup/register";
        }
    }
    @PostMapping("/mailAuth")
    public String mailAuth(@Validated MailAuthForm mailAuthForm,String userId){
        String code = mailAuthForm.getMailAuth();;
        System.out.println("code = " + code);
        System.out.println("userId = " + userId);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code",code);
        body.add("userId",userId);

        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestMessage = new HttpEntity<>(body, httpHeaders);
        try{
            ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8081/signup/confirmCode", requestMessage, String.class);
            return "redirect:/";
        }catch(Exception e){
            return "error/signupErrorPage";
        }

    }
}
