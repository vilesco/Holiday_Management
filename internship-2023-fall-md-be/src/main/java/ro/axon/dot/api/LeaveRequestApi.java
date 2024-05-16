package ro.axon.dot.api;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.axon.dot.model.response.RequestDetailsList;
import ro.axon.dot.model.request.EmployeeLeaveRequestsList;
import ro.axon.dot.service.LeaveRequestService;
import static ro.axon.dot.constants.Constants.BASE_URL;
import static ro.axon.dot.constants.Constants.REQUESTS;
import javax.validation.constraints.NotEmpty;

@RestController
@RequiredArgsConstructor
@RequestMapping(REQUESTS)
public class LeaveRequestApi {

  private final LeaveRequestService requestService;

  @GetMapping
  public ResponseEntity<RequestDetailsList> getRequestDetailList(
      @RequestParam(value = "status", required = false) String status,
      @RequestParam(value = "search", required = false) String search,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
  ) {
    return ResponseEntity.ok(requestService.getRequestsDetails(status, search, type, startDate, endDate));
  }

  @GetMapping("/by-period")
  public  ResponseEntity<EmployeeLeaveRequestsList> getRequestDetailListByPeriod (
          @RequestParam(required = false) String teamName,
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotEmpty LocalDate startDate,
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotEmpty LocalDate endDate) {

    return ResponseEntity.ok(requestService.getTeamRequestDetailsByPeriod(teamName, startDate, endDate));
  }

}
