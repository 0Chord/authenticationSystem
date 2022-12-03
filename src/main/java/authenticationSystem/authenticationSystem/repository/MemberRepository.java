package authenticationSystem.authenticationSystem.repository;

import authenticationSystem.authenticationSystem.domain.Member;

public interface MemberRepository {
    Member save(Member member);
}
