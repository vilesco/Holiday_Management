package ro.axon.dot.model.response;

import java.time.Instant;
import java.time.LocalDate;
import lombok.Data;
import ro.axon.dot.domain.enums.Status;

@Data
public class EmployeeDetailsListItem {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String crtUsr;
    private Instant crtTms;
    private String mdfUsr;
    private Instant mdfTms;
    private String role;
    private Status status;
    private LocalDate contractStartDate;
    private int v;
    private int totalVacationDays;
    private TeamDetailsListItem teamDetails;
    private String username;

}
