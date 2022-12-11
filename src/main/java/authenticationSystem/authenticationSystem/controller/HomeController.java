package authenticationSystem.authenticationSystem.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(){
        return "Home";
    }

    @GetMapping("/test")
    public String test(HttpServletRequest req){
        return "test";
    }
}
