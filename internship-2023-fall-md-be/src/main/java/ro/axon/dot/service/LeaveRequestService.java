package ro.axon.dot.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.axon.dot.domain.entities.EmployeeEty;
import ro.axon.dot.domain.entities.LeaveRequestEty;
import ro.axon.dot.domain.enums.LeaveRequestStatus;
import ro.axon.dot.domain.enums.LeaveRequestType;
import ro.axon.dot.domain.repositories.EmployeeRepository;
import ro.axon.dot.domain.repositories.RequestRepository;
import ro.axon.dot.domain.repositories.TeamRepository;
import ro.axon.dot.mapper.LeaveRequestMapper;
import ro.axon.dot.model.response.RequestDetailsList;
import ro.axon.dot.model.response.RequestDetailsListItem;
import ro.axon.dot.exception.BusinessErrorCode;
import ro.axon.dot.exception.BusinessException;
import ro.axon.dot.model.request.EmployeeLeaveRequestItem;
import ro.axon.dot.model.request.EmployeeLeaveRequestsList;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

  private final RequestRepository requestRepository;
  private final EmployeeHandler employeeHandler;
  private final SecurityAccessTokenHandler securityAccessTokenHandler;
  private final EmployeeRepository employeeRepository;
  private final LeaveRequestHandler leaveRequestHandler;
  private final TeamRepository teamRepository;

  @Transactional
  public RequestDetailsList getRequestsDetails(String status, String search, String type, LocalDate startDate, LocalDate endDate) {
    EmployeeEty loggedEmployee = employeeHandler.getEmployee(securityAccessTokenHandler.getEmployeeIdFromToken());
    List<LeaveRequestEty> leaveRequests;

    if (loggedEmployee.getRole().equals("TEAM_LEAD")) {
      leaveRequests = getRequestsForTeamLead(loggedEmployee);
    } else {
      leaveRequests = requestRepository.findAll();
    }

    List<RequestDetailsListItem> requestDetailsListItems = leaveRequests.stream()
        .filter(requestEty ->
            (status == null || requestEty.getStatus().name().equals(status)) &&
                (startDate == null || requestEty.getStartDate().isAfter(startDate)) &&
                (endDate == null || requestEty.getEndDate().isBefore(endDate)) &&
                (type == null || requestEty.getType().name().equals(type)) &&
                (search == null ||
                    requestEty.getEmployeeEty().getFirstName().toUpperCase()
                        .contains(search.trim().toUpperCase()) ||
                    requestEty.getEmployeeEty().getLastName().toUpperCase()
                        .contains(search.trim().toUpperCase()))
        )
        .sorted(Comparator.comparing(LeaveRequestEty::getStatus)
            .thenComparing(LeaveRequestEty::getCrtTms))
        .map(LeaveRequestMapper.INSTANCE::mapRequestEtyToRequestDto)
        .collect(Collectors.toList());

    var requestDetails = new RequestDetailsList();
    requestDetails.setItems(requestDetailsListItems);
    return requestDetails;
  }

  private List<LeaveRequestEty> getRequestsForTeamLead(EmployeeEty loggedEmployee) {
    List<LeaveRequestEty> leaveRequests = new ArrayList<>();
    for (EmployeeEty employee : loggedEmployee.getTeam().getEmployees()) {
      leaveRequests.addAll(employee.getLeaveRequestEties());
    }
    return leaveRequests;
  }

  @Transactional
  public EmployeeLeaveRequestsList getTeamRequestDetailsByPeriod(String teamName, LocalDate startDate, LocalDate endDate) {
    EmployeeLeaveRequestsList employeeLeaveRequestsList = new EmployeeLeaveRequestsList();
    List<EmployeeEty> employees;
    if(teamName == null || teamName.isEmpty()){
       employees = employeeRepository.findAll().stream().toList();
    } else {
        var teamEty = teamRepository.findByNameIgnoreCase(teamName)
                .orElseThrow(() -> BusinessException.builder()
                        .error(BusinessErrorCode.TEAM_NOT_FOUND)
                        .build());

        employees = employeeRepository.findAllByTeam(teamEty).stream().toList();
        }

    employees.forEach(employeeEty -> {
          EmployeeLeaveRequestItem employeeLeaveRequestItem = new EmployeeLeaveRequestItem();
          employeeLeaveRequestItem.setFirstName(employeeEty.getFirstName());
          employeeLeaveRequestItem.setLastName(employeeEty.getLastName());
          employeeLeaveRequestItem.setLeaveRequests(employeeEty.getLeaveRequestEties().stream()
                  .filter(leaveRequestEty -> !leaveRequestEty.getStartDate().isAfter(endDate) &&
                          !leaveRequestEty.getEndDate().isBefore(startDate)
                          && leaveRequestEty.getStatus() == LeaveRequestStatus.APPROVED
                  )
                  .map(LeaveRequestMapper.INSTANCE::mapRequestEtyToLeaveRequestItem).toList());

        employeeLeaveRequestItem.setNoOfVacationDays(employeeLeaveRequestItem.getLeaveRequests().stream()
                .filter(requestDetailsListItem-> requestDetailsListItem.getType() == LeaveRequestType.VACATION)
                .mapToInt(requestDetailsListItem ->  leaveRequestHandler.calculateLeaveDaysInPeriod(startDate, endDate, requestDetailsListItem))
                .sum());

        employeeLeaveRequestItem.setNoOfVacationDays(employeeLeaveRequestItem.getLeaveRequests().stream()
                .filter(requestDetailsListItem-> requestDetailsListItem.getType() == LeaveRequestType.MEDICAL)
                .mapToInt(requestDetailsListItem ->  leaveRequestHandler.calculateLeaveDaysInPeriod(startDate, endDate, requestDetailsListItem))
                .sum());

        employeeLeaveRequestsList.getItems().add(employeeLeaveRequestItem);
    });
    return employeeLeaveRequestsList;
  }

}