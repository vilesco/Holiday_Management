package ro.axon.dot.model.response;

import java.time.Instant;
import lombok.Data;
import ro.axon.dot.domain.enums.Status;

@Data
public class TeamDetailsListItem {

    private Long id;
    private String name;
    private String crtUsr;
    private Instant crtTms;
    private String mdfUsr;
    private Instant mdfTms;
    private Status status;

}
