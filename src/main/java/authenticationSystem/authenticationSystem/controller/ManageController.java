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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/manage")
public class ManageController {
    AuthService authService;
    HttpHeaders httpHeaders;
    RestTemplate restTemplate;

    public ManageController(AuthService authService, HttpHeaders httpHeaders, RestTemplate restTemplate) {
        this.authService = authService;
        this.httpHeaders = httpHeaders;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/remove/{userId}")
    public String removeUser(@PathVariable("userId") String userId, Model model, HttpServletRequest request, HttpServletResponse httpServletResponse){
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
            if (admin.equals("1")) {
                ResponseEntity<?> responseEntity = authService.deleteMember(httpHeaders, restTemplate, userId);
                Object members = responseEntity.getBody();
                model.addAttribute("members",members);
                return "signIn/manage";
            } else {
                ResponseEntity<?> memberInfo = authService.getMemberInfo(refreshToken, httpHeaders, restTemplate);
                Object member = memberInfo.getBody();
                model.addAttribute("member",member);
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }
    }
}
