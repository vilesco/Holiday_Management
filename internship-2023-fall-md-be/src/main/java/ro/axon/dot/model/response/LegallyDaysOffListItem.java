package ro.axon.dot.model.response;

import lombok.Data;

import java.time.LocalDate;
@Data
public class LegallyDaysOffListItem {
    private LocalDate date;
    private String description;

}
