package ro.axon.dot.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ro.axon.dot.domain.enums.Status;
import ro.axon.dot.domain.entities.TeamEty;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends
    JpaRepository<TeamEty, Long>,
    QuerydslPredicateExecutor<TeamEty> {

    List<TeamEty> findByStatus(Status status);
    Optional<TeamEty> findByNameIgnoreCase(String name);

}
