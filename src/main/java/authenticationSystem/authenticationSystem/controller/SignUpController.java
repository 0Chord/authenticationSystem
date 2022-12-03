package authenticationSystem.authenticationSystem.controller;

import authenticationSystem.authenticationSystem.dto.MemberForm;
import authenticationSystem.authenticationSystem.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping("/signUp")
@Controller
public class SignUpController {
    MemberService memberService;

    public SignUpController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/register")
    public String register(){
        return "signUp/register";
    }

    @PostMapping("/enroll")
    public String enroll(@Validated MemberForm memberForm){
        System.out.println("memberForm.getUserId() = " + memberForm.getUserId());
        System.out.println("memberForm.getPassword() = " + memberForm.getPassword());
        System.out.println("memberForm.getAuthPassword() = " + memberForm.getAuthPassword());
        System.out.println("memberForm.getName() = " + memberForm.getName());
        System.out.println("memberForm.getNickname() = " + memberForm.getNickname());
        return null;
    }
}
