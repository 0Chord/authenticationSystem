package authenticationSystem.authenticationSystem.controller;

import authenticationSystem.authenticationSystem.dto.LoginForm;
import authenticationSystem.authenticationSystem.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
@Slf4j
@Controller
public class HomeController {
    HttpHeaders httpHeaders;
    RestTemplate restTemplate;
    AuthService authService;

    public HomeController(HttpHeaders httpHeaders, RestTemplate restTemplate, AuthService authService) {
        this.httpHeaders = httpHeaders;
        this.restTemplate = restTemplate;
        this.authService = authService;
    }

    @GetMapping("/")
    public String home(@ModelAttribute LoginForm loginForm,HttpServletRequest request, HttpServletResponse httpServletResponse, Model model){
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return "Home";
        }
        return authService.userAuth(httpHeaders, restTemplate, httpServletResponse, request, model, cookies);
    }


}
