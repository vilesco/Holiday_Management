package ro.axon.dot.model.response;

import lombok.Data;
import ro.axon.dot.domain.enums.LeaveRequestStatus;
import ro.axon.dot.domain.enums.LeaveRequestType;

import java.time.LocalDate;

@Data
public class LeaveRequestItem {

    private LocalDate startDate;

    private LocalDate endDate;

    private LeaveRequestStatus status;

    private LeaveRequestType type;

    private String description;

    private Integer noOfDays;
}
