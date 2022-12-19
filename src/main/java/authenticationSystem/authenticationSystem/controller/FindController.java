package authenticationSystem.authenticationSystem.controller;

import authenticationSystem.authenticationSystem.dto.FindPasswordDto;
import authenticationSystem.authenticationSystem.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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


@Slf4j
@Controller
@RequestMapping("/find")
public class FindController {
    HttpHeaders httpHeaders;
    RestTemplate restTemplate;
    AuthService authService;

    public FindController(HttpHeaders httpHeaders, RestTemplate restTemplate, AuthService authService) {
        this.httpHeaders = httpHeaders;
        this.restTemplate = restTemplate;
        this.authService = authService;
    }

    @GetMapping("/password")
    public String findPassword(@ModelAttribute FindPasswordDto findPasswordDto) {
        return "/find/password";
    }

    @PostMapping("/password")
    public String find(@ModelAttribute @Validated FindPasswordDto findPasswordDto, BindingResult bindingResult) {
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("userId", findPasswordDto.getUserId());
        body.add("name", findPasswordDto.getName());
        body.add("phone", findPasswordDto.getPhone());
        HttpEntity<MultiValueMap<String, String>> requestMessage = new HttpEntity<>(body, httpHeaders);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8081/find/password", requestMessage, String.class);
            String responseBody = response.getBody();
            if (Objects.equals(responseBody, "NoSearchMember")) {
                bindingResult.reject("searchFail", "아이디가 일치하지 않습니다.");
                return "find/password";
            } else if (Objects.equals(responseBody, "DifferentName")) {
                bindingResult.reject("searchFail", "이름이 일치하지 않습니다.");
                return "find/password";
            } else if (Objects.equals(responseBody, "DifferentPhone")) {
                bindingResult.reject(responseBody, "전화번호가 일치하지 않습니다.");
                return "find/password";
            }
        } catch (Exception e) {
            return "error/searchErrorPage";
        }
        authService.passwordAuth(httpHeaders, restTemplate, findPasswordDto.getUserId());

        return "find/successFind";
    }

    @GetMapping("/success")
    public String success() {
        return "redirect:/";
    }
}
