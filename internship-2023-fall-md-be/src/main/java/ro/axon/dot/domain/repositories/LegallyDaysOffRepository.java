package ro.axon.dot.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.axon.dot.domain.entities.LegallyDaysOffEty;

import java.util.Date;

public interface LegallyDaysOffRepository extends JpaRepository<LegallyDaysOffEty, Date> {

}
