package ro.axon.dot.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ro.axon.dot.domain.entities.LeaveRequestEty;

public interface RequestRepository extends
    JpaRepository<LeaveRequestEty, Long>,
    QuerydslPredicateExecutor<LeaveRequestEty>
{

}



