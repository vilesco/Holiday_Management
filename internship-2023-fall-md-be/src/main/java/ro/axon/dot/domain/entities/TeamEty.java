package ro.axon.dot.domain.entities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ro.axon.dot.domain.SrgKeyEntityTml;
import ro.axon.dot.domain.enums.Status;

@Entity
@SequenceGenerator(name = "TEAM_ID_SQ", sequenceName = "TEAM_ID_SQ", allocationSize = 1)
@Getter
@Setter
@Table(name = "TEAM")
public class TeamEty extends SrgKeyEntityTml<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEAM_ID_SQ")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CRT_USR")
    private String crtUsr;

    @Column(name = "CRT_TMS")
    private Instant crtTms;

    @Column(name = "MDF_USR")
    private String mdfUsr;

    @Column(name = "MDF_TMS")
    private Instant mdfTms;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private Status status;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "TEAM_ID")
    private List<EmployeeEty> employees = new ArrayList<>();

    @Override
    protected Class<? extends SrgKeyEntityTml<Long>> entityRefClass() {
        return TeamEty.class;
    }
}
