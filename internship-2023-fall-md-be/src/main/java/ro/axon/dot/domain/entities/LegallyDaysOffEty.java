package ro.axon.dot.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "LEGALLY_DAYS_OFF")
@Getter
@Setter
public class LegallyDaysOffEty {

    @Id
    @Column(name="DATE")
    private LocalDate date;

    @Column(name="DESCRIPTION")
    private String description;

}
