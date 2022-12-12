package authenticationSystem.authenticationSystem.controller;

import authenticationSystem.authenticationSystem.dto.JwtForm;
import authenticationSystem.authenticationSystem.dto.LoginForm;
import authenticationSystem.authenticationSystem.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Objects;

@Controller
@RequestMapping("/signIn")
public class signInController {

    RestTemplate restTemplate;
    HttpHeaders httpHeaders;
    AuthService authService;

    public signInController(RestTemplate restTemplate, HttpHeaders httpHeaders, AuthService authService) {
        this.restTemplate = restTemplate;
        this.httpHeaders = httpHeaders;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated LoginForm loginForm, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("userId", loginForm.getId());
        body.add("password", loginForm.getPassword());
        HttpEntity<MultiValueMap<String, String>> requestMessage = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<JwtForm> response = restTemplate.postForEntity("http://localhost:8081/signIn/login", requestMessage, JwtForm.class);
        JwtForm responseBody = response.getBody();
        assert responseBody != null;

        Cookie refreshCookie = authService.setRefreshCookie(responseBody.getRefreshToken());
        Cookie accessCookie = authService.setAccessCookie(responseBody.getRefreshToken());

        httpServletResponse.addCookie(refreshCookie);
        httpServletResponse.addCookie(accessCookie);
        HttpHeaders httpHeaders1 = new HttpHeaders();
        httpHeaders1.setLocation(URI.create("/test"));
        return new ResponseEntity<>(responseBody, httpHeaders1, HttpStatus.SEE_OTHER);
    }

}
