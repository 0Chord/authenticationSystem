package authenticationSystem.authenticationSystem;


import authenticationSystem.authenticationSystem.repository.MemberJpaRepository;
import authenticationSystem.authenticationSystem.repository.MemberRepository;
import authenticationSystem.authenticationSystem.service.MemberService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    private final EntityManager em;

    @Autowired
    public AppConfig(EntityManager em) {
        this.em = em;
    }

    @Bean
    public MemberRepository memberRepository(){
        return new MemberJpaRepository(em);
    }

    @Bean
    public MemberService memberService(){
        return new MemberService(memberRepository());
    }
}
