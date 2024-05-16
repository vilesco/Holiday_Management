package ro.axon.dot.model.request;

import lombok.Data;
import ro.axon.dot.model.response.LeaveRequestItem;


import java.util.List;

@Data
public class EmployeeLeaveRequestItem {

    private String firstName;
    private String lastName;
    private int noOfVacationDays;
    private int noOfMedicalDays;
    private List<LeaveRequestItem> leaveRequests;
}
