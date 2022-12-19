package authenticationSystem.authenticationSystem.dto;

import lombok.Data;

@Data
public class FindPasswordDto {
    private String userId;
    private String name;
    private String phone;
}
