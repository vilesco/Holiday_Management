package ro.axon.dot.model.response;

import lombok.Data;

import java.util.List;

@Data
public class EmployeeDetailsList {

    private List<EmployeeDetailsListItem> items;
}
