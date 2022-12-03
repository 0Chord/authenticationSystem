package authenticationSystem.authenticationSystem.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name="user_id")
    private String userId;
    @Column(name="user_password")
    private String password;
    @Column(name="user_nickname")
    private String nickname;
    @Column(name="user_tell")
    private String phone;
    @Column(name="user_name")
    private String name;
}
