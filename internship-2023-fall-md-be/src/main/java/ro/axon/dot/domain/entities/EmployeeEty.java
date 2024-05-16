package ro.axon.dot.domain.entities;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import ro.axon.dot.domain.SrgKeyEntityTml;
import ro.axon.dot.domain.enums.Status;

@Entity
@Getter
@Setter
@Table(name = "EMPLOYEE")
public class EmployeeEty extends SrgKeyEntityTml<String> {

    @Id
    @GeneratedValue(generator = "employee-uuid")
    @GenericGenerator(name = "employee-uuid", strategy = "uuid2")
    @Column(name = "EMPLOYEE_ID")
    private String id;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "CRT_USR")
    private String crtUsr;

    @Column(name = "CRT_TMS")
    private Instant crtTms;

    @Column(name = "MDF_USR")
    private String mdfUsr;

    @Column(name = "MDF_TMS")
    private Instant mdfTms;

    @Column(name = "ROLE")
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private Status status;

    @Column(name = "CONTRACT_START_DATE")
    private LocalDate contractStartDate;

    @Column(name = "CONTRACT_END_DATE")
    private LocalDate contractEndDate;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID", referencedColumnName = "ID")
    private TeamEty team;

    @OneToMany(mappedBy = "employeeEty", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LeaveRequestEty> leaveRequestEties  = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<EmpYearlyDaysOffEty> empYearlyDaysOffEties = new ArrayList<>();

    @Override
    protected Class<? extends SrgKeyEntityTml<String>> entityRefClass() {
        return EmployeeEty.class;
    }

    public void removeLeaveRequest(LeaveRequestEty leaveRequestEty) {
        leaveRequestEties.remove(leaveRequestEty);
        leaveRequestEty.setEmployeeEty(null);
    }
}
