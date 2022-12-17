package authenticationSystem.authenticationSystem.controller;

import authenticationSystem.authenticationSystem.dto.LoginForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@Controller
public class ErrorController {
    @GetMapping("/Home")
    public String loginError(@ModelAttribute LoginForm loginForm, BindingResult bindingResult){
        bindingResult.reject("loginFail","아이디 떠는 비밀번호가 일치하지 않습니다.");
        return "Home";
    }
}
