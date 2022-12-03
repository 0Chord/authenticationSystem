package authenticationSystem.authenticationSystem.dto;

import lombok.Data;

@Data
public class MemberForm {
    private String userId;
    private String password;
    private String authPassword;
    private String name;
    private String nickname;
    private String phone;
}
