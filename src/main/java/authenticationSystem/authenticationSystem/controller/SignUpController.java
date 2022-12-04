package authenticationSystem.authenticationSystem.controller;

import authenticationSystem.authenticationSystem.bcrypt.Bcrypt;
import authenticationSystem.authenticationSystem.domain.Member;
import authenticationSystem.authenticationSystem.dto.MemberForm;
import authenticationSystem.authenticationSystem.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

@Slf4j
@RequestMapping("/signUp")
@Controller
public class SignUpController {
    MemberService memberService;
    Bcrypt bcrypt;

    public SignUpController(MemberService memberService, Bcrypt bcrypt) {
        this.memberService = memberService;
        this.bcrypt = bcrypt;
    }

    @GetMapping("/register")
    public String register(){
        return "signUp/register";
    }

    @PostMapping("/enroll")
    public String enroll(@Validated MemberForm memberForm){
        Member member = new Member();
        if(memberForm.getPassword().length()>15){
            System.out.println("비밀번호 초과 오류!!!!");
        }
        if(Objects.equals(memberForm.getPassword(), memberForm.getAuthPassword())){
            String encryptPassword = bcrypt.encrypt(memberForm.getPassword());
            member.setUserId(memberForm.getUserId());
            member.setName(memberForm.getName());
            member.setPassword(encryptPassword);
            member.setPhone(memberForm.getPhone());
            member.setNickname(memberForm.getNickname());
            memberService.register(member);
            return "redirect:/";
        }
        System.out.println("memberForm.getUserId() = " + memberForm.getUserId());
        System.out.println("memberForm.getPassword() = " + memberForm.getPassword());
        System.out.println("memberForm.getAuthPassword() = " + memberForm.getAuthPassword());
        System.out.println("memberForm.getName() = " + memberForm.getName());
        System.out.println("memberForm.getNickname() = " + memberForm.getNickname());

        return "redirect:/";
    }
}
