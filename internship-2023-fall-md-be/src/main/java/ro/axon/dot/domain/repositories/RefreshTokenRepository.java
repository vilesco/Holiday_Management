package ro.axon.dot.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ro.axon.dot.domain.entities.RefreshTokenEty;

public interface RefreshTokenRepository extends
    JpaRepository<RefreshTokenEty, String>,
    QuerydslPredicateExecutor<RefreshTokenEty> {

}
