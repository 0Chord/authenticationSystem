package authenticationSystem.authenticationSystem.repository;

import authenticationSystem.authenticationSystem.domain.Member;
import jakarta.persistence.EntityManager;

public class MemberJpaRepository implements MemberRepository {

    EntityManager em;


    public MemberJpaRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Member save(Member member) {
        em.persist(member);
        return member;
    }
}
