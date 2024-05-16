package ro.axon.dot.domain.entities;

import java.time.Instant;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ro.axon.dot.domain.enums.LeaveRequestStatus;
import ro.axon.dot.domain.enums.LeaveRequestType;
import ro.axon.dot.domain.SrgKeyEntityTml;

import javax.persistence.SequenceGenerator;

@Entity
@SequenceGenerator(name = "LEAVE_REQ_ID_SQ", sequenceName = "LEAVE_REQUEST_ID_SQ", allocationSize = 1)
@Getter
@Setter
@Table(name= "LEAVE_REQUEST")
public class LeaveRequestEty extends SrgKeyEntityTml<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LEAVE_REQ_ID_SQ")
  @Column(name = "ID")
  private Long id;

  @Column(name = "CRT_USR")
  private String crtUsr;

  @Column(name = "CRT_TMS")
  private Instant crtTms;

  @Column(name = "MDF_USR")
  private String mdfUsr;

  @Column(name = "MDF_TMS")
  private Instant mdfTms;

  @Column(name = "START_DATE")
  private LocalDate startDate;

  @Column(name = "END_DATE")
  private LocalDate endDate;

  @Column(name = "TYPE")
  @Enumerated(EnumType.STRING)
  private LeaveRequestType type;

  @Column(name = "DESCRIPTION")
  private String description;

  @Column(name = "REJECT_REASON")
  private String rejectReason;

  @Column(name = "STATUS")
  @Enumerated(EnumType.STRING)
  private LeaveRequestStatus status;

  @Column(name = "NO_DAYS")
  private int noOfDays;

  @ManyToOne
  @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "EMPLOYEE_ID")
  private EmployeeEty employeeEty;

  @Override
  protected Class<? extends SrgKeyEntityTml<Long>> entityRefClass() {
    return LeaveRequestEty.class;
  }
}
