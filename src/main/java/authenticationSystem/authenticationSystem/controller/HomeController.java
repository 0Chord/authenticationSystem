package authenticationSystem.authenticationSystem.controller;

import authenticationSystem.authenticationSystem.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
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
    public String home(){
        return "Home";
    }

    @GetMapping("/test")
    public String test(HttpServletRequest request, HttpServletResponse httpServletResponse,Model model){
        Cookie[] cookies = request.getCookies();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        if(cookies == null){
            return "error/accessErrorPage";
        }
        String refreshToken = "";
        for(Cookie cookie:cookies){
            if(Objects.equals(cookie.getName(), "accessToken")){
                body.add("accessToken",cookie.getValue());
            }
            if(Objects.equals(cookie.getName(), "refreshToken")){
                refreshToken = cookie.getValue();
            }
        }

        if(body.isEmpty()){
            ResponseEntity<?> accessTokenObj = authService.getAccessToken(refreshToken);
            String accessToken = Objects.requireNonNull(accessTokenObj.getBody()).toString();
            Cookie cookie = authService.setAccessCookie(accessToken);
            httpServletResponse.addCookie(cookie);
            body.add("accessToken",accessToken);
        }
        HttpEntity<MultiValueMap<String, String>> requestMessage = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<Boolean> response = restTemplate.postForEntity("http://localhost:8081/signIn/auth", requestMessage, Boolean.class);
        if(Boolean.TRUE.equals(response.getBody())){
            model.addAttribute("cookies",cookies);
            return "test";
        }else{
            return "Home";
        }
    }

}
