package ro.axon.dot.model.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EmployeeLeaveRequestsList {
   private List<EmployeeLeaveRequestItem> items = new ArrayList<>();
}
