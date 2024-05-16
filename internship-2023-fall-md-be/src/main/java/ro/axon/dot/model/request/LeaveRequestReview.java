package ro.axon.dot.model.request;

import javax.validation.constraints.NotNull;
import lombok.Data;
import ro.axon.dot.model.enums.LeaveRequestReviewType;

@Data
public class LeaveRequestReview {

    @NotNull
    private LeaveRequestReviewType type;
    private String rejectionReason;
    @NotNull
    private Long v;
}
