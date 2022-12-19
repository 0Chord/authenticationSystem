package authenticationSystem.authenticationSystem.controller;

import authenticationSystem.authenticationSystem.dto.JwtForm;
import authenticationSystem.authenticationSystem.dto.LoginForm;
import authenticationSystem.authenticationSystem.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
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

import java.net.URI;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/signIn")
public class SignInController {

    RestTemplate restTemplate;
    HttpHeaders httpHeaders;
    AuthService authService;

    public SignInController(RestTemplate restTemplate, HttpHeaders httpHeaders, AuthService authService) {
        this.restTemplate = restTemplate;
        this.httpHeaders = httpHeaders;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated LoginForm loginForm, HttpServletResponse httpServletResponse) {
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("userId", loginForm.getId());
        body.add("password", loginForm.getPassword());
        HttpEntity<MultiValueMap<String, String>> requestMessage = new HttpEntity<>(body, httpHeaders);
        try {
            ResponseEntity<JwtForm> response = restTemplate.postForEntity("http://localhost:8081/signIn/login", requestMessage, JwtForm.class);
            JwtForm responseBody = response.getBody();
            assert responseBody != null;

            Cookie refreshCookie = authService.setRefreshCookie(responseBody.getRefreshToken());
            Cookie accessCookie = authService.setAccessCookie(responseBody.getRefreshToken());
            httpServletResponse.addCookie(refreshCookie);
            httpServletResponse.addCookie(accessCookie);
            HttpHeaders httpHeaders1 = new HttpHeaders();
            httpHeaders1.setLocation(URI.create("/signIn/auth"));
            return new ResponseEntity<>(responseBody, httpHeaders1, HttpStatus.SEE_OTHER);
        } catch (Exception e) {
            HttpHeaders httpHeaders1 = new HttpHeaders();
            httpHeaders1.setLocation(URI.create("/Home"));
            return new ResponseEntity<>("loginError", httpHeaders1, HttpStatus.SEE_OTHER);
        }

    }

    @GetMapping("/auth")
    public String test(HttpServletRequest request, HttpServletResponse httpServletResponse, Model model) {
        Cookie[] cookies = request.getCookies();

        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        if (cookies == null) {
            return "error/accessErrorPage";
        }
        String refreshToken = authService.findAccessTokenAndRefreshToken(body, cookies);
        ResponseEntity<?> response = authService.checkAccessToken(refreshToken, httpServletResponse, body);
        String admin = authService.findAdmin(httpHeaders, restTemplate, body);
        if (Boolean.TRUE.equals(response.getBody())) {
            if (admin.equals("ROLE_ADMIN")) {
                ResponseEntity<?> members = authService.getMembers(httpHeaders, restTemplate);
                model.addAttribute("members", members.getBody());
                return "signIn/manage";
            } else {
                ResponseEntity<?> memberInfo = authService.getMemberInfo(refreshToken, httpHeaders, restTemplate);
                Object member = memberInfo.getBody();
                model.addAttribute("member", member);
                return "signIn/private";
            }
        } else {
            return "Home";
        }
    }

    @GetMapping("/confirmLogin")
    public String confirmLogin(@ModelAttribute LoginForm loginForm, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        Cookie[] cookies = request.getCookies();

        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        if (cookies == null) {
            return "error/accessErrorPage";
        }
        String refreshToken = authService.findAccessTokenAndRefreshToken(body, cookies);
        ResponseEntity<?> response = authService.checkAccessToken(refreshToken, httpServletResponse, body);
        if (Boolean.TRUE.equals(response.getBody())) {
           return "signIn/confirmLogin";
        } else {
            return "error/searchErrorPage";
        }
    }

    @PostMapping("/confirmLogin")
    public String confirm(@ModelAttribute @Validated LoginForm loginForm, Model model, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "error/searchErrorPage";
        }
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("userId", loginForm.getId());
        body.add("password", loginForm.getPassword());
        HttpEntity<MultiValueMap<String, String>> requestMessage = new HttpEntity<>(body, httpHeaders);
        try{
            ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8081/signIn/confirm", requestMessage, String.class);
            String responseBody = response.getBody();
            if(Objects.equals(responseBody, "NoSearchMember")){
                bindingResult.reject("loginError","아이디가 일치하지 않습니다.");
                return "signIn/confirmLogin";
            }else if(Objects.equals(responseBody, "DifferentPassword")){
                bindingResult.reject("loginError","비밀번호가 일치하지 않습니다.");
                return "signIn/confirmLogin";
            }else{
                model.addAttribute("userId",loginForm.getId());
                return "signIn/passwordChangePage";
            }
        }catch (Exception e){
            return "error/searchErrorPage";
        }
    }

    @PostMapping("/passwordChange")
    public String change(@Validated String userId, String password){
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("userId",userId);
        body.add("password",password);
        HttpEntity<MultiValueMap<String, String>> requestMessage = new HttpEntity<>(body, httpHeaders);
        try{
            restTemplate.postForEntity("http://localhost:8081/signIn/passwordChange",requestMessage,String.class);
            return "redirect:/";
        }catch(Exception e){
            return "error/searchErrorPage";
        }
    }
}
