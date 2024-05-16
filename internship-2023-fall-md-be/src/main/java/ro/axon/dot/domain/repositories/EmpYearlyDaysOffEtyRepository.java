package ro.axon.dot.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ro.axon.dot.domain.entities.EmpYearlyDaysOffEty;

public interface EmpYearlyDaysOffEtyRepository extends
    JpaRepository<EmpYearlyDaysOffEty, Long>,
    QuerydslPredicateExecutor<EmpYearlyDaysOffEty> {

}
