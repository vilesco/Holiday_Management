package ro.axon.dot.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ro.axon.dot.domain.entities.EmployeeEty;
import ro.axon.dot.domain.entities.TeamEty;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends
    JpaRepository<EmployeeEty, String>,
    QuerydslPredicateExecutor<EmployeeEty> {

    boolean existsEmployeeEtyByUsernameIgnoreCase(String username);
    boolean existsEmployeeEtyByEmailIgnoreCase(String email);

    Optional<EmployeeEty> findByUsername(String username);
    List<EmployeeEty> findAllByTeam(TeamEty teamEty);

}
