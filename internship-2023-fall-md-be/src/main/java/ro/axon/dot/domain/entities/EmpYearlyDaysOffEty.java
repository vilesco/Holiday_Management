package ro.axon.dot.domain.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@SequenceGenerator(name = "EMP_YEARLY_DAYS_OFF_ID_SQ", sequenceName = "EMP_YEARLY_DAYS_OFF_ID_SQ", allocationSize = 1)
@Getter
@Setter
@Table(name = "EMP_YEARLY_DAYS_OFF")
public class EmpYearlyDaysOffEty {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMP_YEARLY_DAYS_OFF_ID_SQ")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "EMPLOYEE_ID")
    private EmployeeEty employee;

    @Column(name = "TOTAL_NO_DAYS")
    private int totalNoDays;

    @Column(name = "YEAR")
    private int year;

    @OneToMany(mappedBy = "empYearlyDaysOff", cascade = CascadeType.ALL)
    private List<EmpYearlyDaysOffHistEty> empYearlyDaysOffHistEties = new ArrayList<>();
}
