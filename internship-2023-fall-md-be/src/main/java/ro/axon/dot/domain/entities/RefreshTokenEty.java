package ro.axon.dot.domain.entities;

import java.time.Instant;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import ro.axon.dot.domain.enums.RefreshTokenStatus;

@Entity
@Getter
@Setter
@Table(name = "REFRESH_TOKEN")
public class RefreshTokenEty {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private RefreshTokenStatus status;

    @ManyToOne
    @JoinColumn(name = "AUDIENCE", referencedColumnName = "EMPLOYEE_ID")
    private EmployeeEty audience;

    @Column(name = "CRT_TMS")
    private Instant crtTms;

    @Column(name = "MDF_TMS")
    private Instant mdfTms;

    @Column(name = "EXP_TMS")
    private Instant expTms;
}
