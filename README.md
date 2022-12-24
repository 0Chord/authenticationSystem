# 인증시스템 웹서버
- 인증서버는 https://github.com/0Chord/authSystem 으로 분리
### 인증서버(8081포트)와 웹서버(8080포트) 분리
- API 통신을 통한 MSA 설계
- 모든 인증과 DB접근은 8081포트에서 담당
- 8080포트는 8081 포트와의 통신을 통한 기능 수행
## 역할
|서비스|역할|
|:--:|:--:|
|웹서버|-클라이언트 View 제공<br>-인증서버와의 API통신을 통한 정보제공<br>-Cookie를 통한 클라이언트 정보 조회 후 인증서버와의 통신을 통한 검증|
## 기술스택
- Java 17
- Thymeleaf
- Spring Boot
- Cookie
## 아키텍처
<img width="765" alt="image" src="https://user-images.githubusercontent.com/114129008/209424748-3220b48e-54ba-40b5-8f08-d40cedd0564b.png">

## 제공 기능
|기능|설명|
|:--:|--|
|회원가입|회원가입 폼 제공<br>회원가입 시 정규표현식을 통해 정해진 규격 제공<br>회원가입 시 메일 인증 후 오류 표시|
|정보관리|개인정보 관리와 관리자의 회원 관리 구분<br>개인정보 관리 탭에서 비밀번호 변경 기능 제공|
|비밀번호찾기|비밀번호 찾기 시 인증을 통해 임시비밀번호 제공|

## 구현
#### 1. 회원가입 
SignupContoller.Java
~~~java
@PostMapping("/register")
    public String enroll(@ModelAttribute @Validated MemberForm memberForm, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return "signup/register";
        }
        if (Objects.equals(memberForm.getPassword(), memberForm.getAuthPassword())) {
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("userId",memberForm.getUserId());
            body.add("password", memberForm.getPassword());
            body.add("authPassword", memberForm.getAuthPassword());
            body.add("name", memberForm.getName());
            body.add("nickname", memberForm.getNickname());
            body.add("phone", memberForm.getPhone());
            body.add("adminRight","USER");
            HttpEntity<MultiValueMap<String, String>> requestMessage = new HttpEntity<>(body, httpHeaders);
            ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8081/signup/enroll", requestMessage, String.class);
            if(Objects.equals(response.getBody(), "StoredMember")){
                bindingResult.reject("signupFail","이미 등록된 아이디 입니다.");
                return "signup/register";
            }else if(Objects.equals(response.getBody(),"WrongIdPattern")){
                bindingResult.reject("signupFail","아이디 형식에 맞게 가입해주세요.");
                return "signup/register";
            }else if(Objects.equals(response.getBody(),"WrongPhonePattern")){
                bindingResult.reject("signupFail","휴데폰 번호를 형식에 맞게 기입해주세요.");
                return "signup/register";
            }else if(Objects.equals(response.getBody(),"WrongNamePattern")){
                bindingResult.reject("signupFail","이름을 올바르게 기입해주세요.");
                return "signup/register";
            }else if(Objects.equals(response.getBody(),"WrongPasswordPattern")){
                bindingResult.reject("signupFail","비밀번호를 형식에 맞게 기입해주세요.");
                return "signup/register";
            }
            ResponseEntity<?> responseEntity = authService.mailAuth(httpHeaders, restTemplate, memberForm.getUserId());
            model.addAttribute("userId",memberForm.getUserId());
            return "signup/mailAuth";
        }else{
            bindingResult.reject("signupFail","비밀번호가 일치하지 않습니다.");
            return "signup/register";
        }
    }
~~~
    
- RestTemplate로 인증서버와의 API통신을 통해 회원가입 절차 진행
   - 회원가입 오류 시, BindingResult를 통해 ExceptionHandling 진행
 
#### 2.메일인증
SignupController.Java
```Java
     @PostMapping("/mailAuth")
    public String mailAuth(@Validated MailAuthForm mailAuthForm,String userId){
        String code = mailAuthForm.getMailAuth();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code",code);
        body.add("userId",userId);

        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestMessage = new HttpEntity<>(body, httpHeaders);
        try{
            ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8081/signup/confirmCode", requestMessage, String.class);
            return "redirect:/";
        }catch(Exception e){
            restTemplate.postForEntity("http://localhost:8081/signup/signupError",requestMessage,Boolean.class);
            return "error/signupErrorPage";
        }
    }
```
#### 3.인스타그램 CSS 적용
- 다음 팀프로젝트 주제인 인스타그램에 맞게 CSS 적용
##### 홈화면
<img width="366" alt="image" src="https://user-images.githubusercontent.com/114129008/209425640-6ccbc91c-bbda-4768-ab3b-1b91b1dfd007.png">

##### 회원가입 화면
<img width="354" alt="image" src="https://user-images.githubusercontent.com/114129008/209425663-b10bbd5c-154c-4be4-8346-9f62a20d1507.png">

##### 개인정보 관리화면
<img width="741" alt="image" src="https://user-images.githubusercontent.com/114129008/209425684-8a6df3d4-b166-4f91-86c3-13234c037e12.png">

##### 회원정보 관리화면
<img width="714" alt="image" src="https://user-images.githubusercontent.com/114129008/209425790-4fbb85c1-5742-4310-b6a1-e60053668355.png">

## 코드 중 확인받고 싶은 부분
```Java
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
```

- accessToken, RefreshToken 유효기간과 Cookie의 유효기간을 같게 설정했는데 이 방법이 맞는지 궁금합니다.
```Java
public ResponseEntity<?> getAccessToken(String refreshToken){
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("refreshToken",refreshToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(uri, request, String.class);
        return response;
    }
```

- 위 방법처럼 RestTemplate를 통해 8081서버와 API통신을 하고 있는데 이 구조가 MSA인지 궁금합니다.
