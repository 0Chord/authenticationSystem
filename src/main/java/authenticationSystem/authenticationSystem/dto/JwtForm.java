package authenticationSystem.authenticationSystem.dto;

import lombok.Data;

@Data
public class JwtForm {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private String userId;
}
