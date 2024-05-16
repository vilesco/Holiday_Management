package ro.axon.dot.domain.entities;

import java.time.Instant;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ro.axon.dot.domain.enums.DaysOffChangeType;

@Entity
@SequenceGenerator(name = "EMP_YEARLY_DAYS_OFF_HIST_ID_SQ", sequenceName = "EMP_YEARLY_DAYS_OFF_HIST_ID_SQ", allocationSize = 1)
@Getter
@Setter
@Table(name = "EMP_YEARLY_DAYS_OFF_HIST")
public class EmpYearlyDaysOffHistEty {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMP_YEARLY_DAYS_OFF_HIST_ID_SQ")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "EMP_YEARLY_DAYS_OFF_ID", referencedColumnName = "ID")
    private EmpYearlyDaysOffEty empYearlyDaysOff;

    @Column(name = "NO_DAYS")
    private int noDays;

    @Column(name = "DESCRIPTION")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private DaysOffChangeType type;

    @Column(name = "CRT_USR")
    private String crtUsr;

    @Column(name = "CRT_TMS")
    private Instant crtTms;

}
