package ro.axon.dot.model.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class UserDetailsResponse {

    private List<String> roles = new ArrayList<>();
    private String username;
    private String employeeId;
    private TeamDetails teamDetails;

    @Data
    public static class TeamDetails {
        private Long teamId;
        private String name;
    }
}

