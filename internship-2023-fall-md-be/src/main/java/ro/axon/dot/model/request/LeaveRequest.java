package ro.axon.dot.model.request;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import ro.axon.dot.domain.enums.LeaveRequestType;

@Data
public class LeaveRequest {

    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    @NotNull
    private LeaveRequestType type;
    @Size(max = 255)
    private String description;
}
