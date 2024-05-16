package ro.axon.dot.model.request;

import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import ro.axon.dot.domain.enums.DaysOffChangeType;

@Data
public class ModifyEmployeeDaysOffRequest {

    @NotEmpty
    private List<String> employeeIds;
    @NotNull
    @Min(value = 0, message = "Numbers of days cannot be negative")
    private int noDays;
    @NotNull
    private DaysOffChangeType type; // It must be either INCREASE or DECREASE
    @NotEmpty
    private String description;
}
