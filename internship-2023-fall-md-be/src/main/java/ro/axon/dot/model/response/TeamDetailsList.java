package ro.axon.dot.model.response;

import lombok.Data;

import java.util.List;

@Data
public class TeamDetailsList {

    private List<TeamDetailsListItem> items;
}
