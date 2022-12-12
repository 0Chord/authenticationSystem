package authenticationSystem.authenticationSystem.service;

import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {

   HttpHeaders httpHeaders;
   RestTemplate restTemplate;
   private final String uri = "http://localhost:8081/access";

    public AuthService(HttpHeaders httpHeaders, RestTemplate restTemplate) {
        this.httpHeaders = httpHeaders;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<?> getAccessToken(String refreshToken){
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("refreshToken",refreshToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(uri, request, String.class);
        return response;
    }

    public Cookie setAccessCookie(String accessToken){
        Cookie cookie = new Cookie("accessToken",accessToken);
        cookie.setMaxAge(30*60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

    public Cookie setRefreshCookie(String refreshToken){
        Cookie cookie = new Cookie("refreshToken",refreshToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(24*60*60);
        cookie.setPath("/");
        return cookie;
    }
}
