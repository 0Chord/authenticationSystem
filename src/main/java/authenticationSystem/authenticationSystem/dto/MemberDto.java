package authenticationSystem.authenticationSystem.dto;

import lombok.Data;

@Data
public class MemberDto {
    private String userId;
    private String password;
    private String nickname;
    private String phone;
    private String name;
}
