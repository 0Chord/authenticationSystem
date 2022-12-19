package authenticationSystem.authenticationSystem.service;

import authenticationSystem.authenticationSystem.dto.MemberDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

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

    public String notHaveRefreshToken(){
        return "redirect:/";
    }

    public void setAccessCookieAndSetBody(String refreshToken,MultiValueMap<String, String> body, HttpServletResponse httpServletResponse){
        ResponseEntity<?> accessTokenObj = getAccessToken(refreshToken);
        String accessToken = Objects.requireNonNull(accessTokenObj.getBody()).toString();
        Cookie cookie = setAccessCookie(accessToken);
        httpServletResponse.addCookie(cookie);
        body.add("accessToken",accessToken);
    }

    public String findAccessTokenAndRefreshToken(MultiValueMap<String, String> body, Cookie[] cookies){
        String refreshToken = "";
        for(Cookie cookie:cookies){
            if(Objects.equals(cookie.getName(), "accessToken")){
                body.add("accessToken",cookie.getValue());
            }
            if(Objects.equals(cookie.getName(), "refreshToken")){
                refreshToken = cookie.getValue();
            }
        }
        return refreshToken;
    }

    public String findAdmin(HttpHeaders httpHeaders, RestTemplate restTemplate, MultiValueMap<String,String> body){
        HttpEntity<MultiValueMap<String, String>> requestMessage = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8081/signIn/admin", requestMessage, String.class);
        return response.getBody();
    }

    public ResponseEntity<?> getMemberInfo(String refreshToken, HttpHeaders httpHeaders, RestTemplate restTemplate){
        MultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("refreshToken",refreshToken);
        HttpEntity<MultiValueMap<String, String>> requestMessage = new HttpEntity<>(body, httpHeaders);
        return restTemplate.postForEntity("http://localhost:8081/signIn/member", requestMessage, MemberDto.class);
    }

    public ResponseEntity<?> getMembers(HttpHeaders httpHeaders, RestTemplate restTemplate){
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("access","manage");
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, httpHeaders);
        return restTemplate.postForEntity("http://localhost:8081/signIn/manage",httpEntity, List.class);
    }

    public ResponseEntity<?> deleteMember(HttpHeaders httpHeaders, RestTemplate restTemplate, String userId){
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("userId",userId);
        HttpEntity<MultiValueMap<String, String>> requestMessage = new HttpEntity<>(body, httpHeaders);
        return restTemplate.postForEntity("http://localhost:8081/signIn/removeMember", requestMessage, List.class);
    }

    public ResponseEntity<?> mailAuth(HttpHeaders httpHeaders, RestTemplate restTemplate, String userId){
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("userId",userId);
        HttpEntity<MultiValueMap<String, String>> requestMessage = new HttpEntity<>(body, httpHeaders);
        return restTemplate.postForEntity("http://localhost:8081/signIn/mailAuth",requestMessage,String.class);
    }

    public ResponseEntity<?> passwordAuth(HttpHeaders httpHeaders, RestTemplate restTemplate, String userId){
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("userId",userId);
        HttpEntity<MultiValueMap<String, String>> requestMessage = new HttpEntity<>(body, httpHeaders);
        return restTemplate.postForEntity("http://localhost:8081/find/passwordAuth",requestMessage,String.class);
    }

    public ResponseEntity<?> checkAccessToken(String refreshToken, HttpServletResponse httpServletResponse, MultiValueMap<String,String> body){

        if (Objects.equals(refreshToken, "")) {
            notHaveRefreshToken();
        }

        if (body.isEmpty()) {
            setAccessCookieAndSetBody(refreshToken, body, httpServletResponse);
        }

        HttpEntity<MultiValueMap<String, String>> requestMessage = new HttpEntity<>(body, httpHeaders);
        return restTemplate.postForEntity("http://localhost:8081/signIn/auth", requestMessage, Boolean.class);
    }
}
