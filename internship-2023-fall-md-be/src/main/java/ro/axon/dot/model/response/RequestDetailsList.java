package ro.axon.dot.model.response;

import java.util.List;
import lombok.Data;

@Data
public class RequestDetailsList {

  private List<RequestDetailsListItem> items;

}
