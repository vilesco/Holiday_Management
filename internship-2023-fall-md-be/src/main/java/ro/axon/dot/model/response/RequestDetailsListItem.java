package ro.axon.dot.model.response;

import java.time.Instant;
import java.time.LocalDate;
import lombok.Data;
import ro.axon.dot.domain.enums.LeaveRequestStatus;
import ro.axon.dot.domain.enums.LeaveRequestType;
import ro.axon.dot.model.response.EmployeeDetails;


@Data
public class RequestDetailsListItem {
  private Long id;
  private Instant crtTms;
  private String crtUsr;
  private String mdfUsr;
  private Instant mdfTms;
  private LocalDate startDate;
  private LocalDate endDate;
  private LeaveRequestType type;
  private LeaveRequestStatus status;
  private String description;
  private String rejectReason;
  private int v;
  private int noOfDays;
  private EmployeeDetails employeeDetails;
}
