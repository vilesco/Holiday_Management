package ro.axon.dot.model.request;

import javax.validation.constraints.Size;
import lombok.Data;
import ro.axon.dot.domain.enums.LeaveRequestType;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class UpdateRequest {
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    @NotNull
    private LeaveRequestType type;
    @Size(max = 255)
    private String description;
    @NotNull
    private int v;
}