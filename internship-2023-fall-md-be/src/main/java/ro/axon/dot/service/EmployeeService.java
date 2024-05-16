package ro.axon.dot.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.axon.dot.domain.enums.DaysOffChangeType;
import ro.axon.dot.domain.entities.EmpYearlyDaysOffEty;
import ro.axon.dot.domain.entities.EmpYearlyDaysOffHistEty;
import ro.axon.dot.domain.entities.EmployeeEty;
import ro.axon.dot.domain.repositories.EmployeeRepository;
import ro.axon.dot.domain.entities.LeaveRequestEty;
import ro.axon.dot.domain.enums.LeaveRequestStatus;
import ro.axon.dot.domain.enums.Status;
import ro.axon.dot.domain.entities.TeamEty;
import ro.axon.dot.domain.repositories.TeamRepository;
import ro.axon.dot.exception.BusinessErrorCode;
import ro.axon.dot.exception.BusinessException;
import ro.axon.dot.mapper.EmployeeMapper;
import ro.axon.dot.mapper.LeaveRequestMapper;
import ro.axon.dot.model.request.*;
import ro.axon.dot.model.enums.LeaveRequestReviewType;
import ro.axon.dot.model.response.RemainingDays;
import ro.axon.dot.model.response.EmployeeDetailsList;
import ro.axon.dot.model.response.EmployeeDetailsListItem;
import ro.axon.dot.model.response.RequestDetailsList;
import ro.axon.dot.model.response.RequestDetailsListItem;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private static final String INIT_DAYS_OFF_DESCRIPTION = "Initial number of days off for the current year";
    private final SecurityAccessTokenHandler securityAccessTokenHandler;
    private final EmployeeRepository employeeRepository;
    private final TeamRepository teamRepository;
    private final Clock clock;
    private final PasswordEncoder passwordEncoder;
    private final LeaveRequestHandler leaveRequestHandler;
    private final EmployeeHandler employeeHandler;

    @Transactional(readOnly = true)
    public EmployeeDetailsList getEmployees(String name) {
        EmployeeDetailsList employeeList = new EmployeeDetailsList();
        List<EmployeeDetailsListItem> employeeDetailsListItems = employeeRepository.findAll().stream()
            .filter(employeeEty -> name == null ||
                employeeEty.getFirstName().toUpperCase().contains(name.trim().toUpperCase()) ||
                employeeEty.getLastName().toUpperCase().contains(name.trim().toUpperCase()) )
            .map(employeeEty -> { // Mapping totalVacationDays manually
                EmployeeDetailsListItem employeeDetailsListItem = EmployeeMapper.INSTANCE. mapEmployeeEtyToEmployeeDto(employeeEty);
                employeeDetailsListItem.setTotalVacationDays(employeeEty.getEmpYearlyDaysOffEties()
                    .stream()
                    .filter(ety -> ety.getYear() == Year.now().getValue())
                    .map(EmpYearlyDaysOffEty::getTotalNoDays)
                    .findFirst().orElse(0));

                return employeeDetailsListItem;
            })
            .sorted(Comparator.comparing((EmployeeDetailsListItem item) ->
                    item.getStatus().getPriority())
                .thenComparing(EmployeeDetailsListItem::getLastName))
            .collect(Collectors.toList());

        employeeList.setItems(employeeDetailsListItems);
        return employeeList;
    }

    @Transactional
    public void inactivateEmployee(String employeeId) {
        EmployeeEty employee = employeeHandler.getEmployee(employeeId);

        employee.setStatus(Status.INACTIVE);
        employee.setMdfTms(clock.instant());
        employee.setMdfUsr(securityAccessTokenHandler.getEmployeeIdFromToken());

        employeeRepository.save(employee);
    }

    @Transactional
    public void createEmployee(CreateEmployeeRequest createEmployeeRequest) {
        if (employeeRepository.existsEmployeeEtyByUsernameIgnoreCase(createEmployeeRequest.getUsername()) ||
            employeeRepository.existsEmployeeEtyByEmailIgnoreCase(createEmployeeRequest.getEmail())) {
            throw BusinessException.builder()
                .error(BusinessErrorCode.USER_ALREADY_EXISTS)
                .build();
        }

        var team = teamRepository.findById(createEmployeeRequest.getTeamId()).orElseThrow(() -> BusinessException.builder()
            .error(BusinessErrorCode.TEAM_NOT_FOUND)
            .build());

        EmployeeEty employee = EmployeeMapper.INSTANCE.mapToEmployeeEty(createEmployeeRequest);
        employee.setCrtUsr(securityAccessTokenHandler.getEmployeeIdFromToken());
        employee.setCrtTms(clock.instant());
        employee.setMdfUsr(securityAccessTokenHandler.getEmployeeIdFromToken());
        employee.setMdfTms(clock.instant());
        employee.setStatus(Status.ACTIVE);

        employee.setPassword(generatePassword(createEmployeeRequest));
        employee.setTeam(team);

        employee.getEmpYearlyDaysOffEties().add(createEmpYearlyDaysOffEty(employee, createEmployeeRequest.getNoDaysOff()));
        employeeRepository.save(employee);
    }

    @Transactional
    public void updateEmployee(String employeeId, EmployeeUpdateRequest updatedEmployee) {
        EmployeeEty existingEmployee = employeeHandler.getEmployee(employeeId);

        if (updatedEmployee.getV() < existingEmployee.getV()) {
            throw new BusinessException(BusinessErrorCode.VERSION_CONFLICT, new HashMap<>());
        }

        Long teamId = updatedEmployee.getTeamId();

        existingEmployee.setFirstName(updatedEmployee.getFirstName());
        existingEmployee.setLastName(updatedEmployee.getLastName());
        existingEmployee.setUsername(updatedEmployee.getUsername());
        existingEmployee.setEmail(updatedEmployee.getEmail());
        existingEmployee.setRole(updatedEmployee.getRole());
        existingEmployee.setMdfUsr(securityAccessTokenHandler.getEmployeeIdFromToken());
        existingEmployee.setMdfTms(clock.instant());

        TeamEty team = teamRepository.findById(teamId)
                .orElseThrow(() ->
                        BusinessException.builder()
                                .error(BusinessErrorCode.TEAM_NOT_FOUND)
                                .build()
                );

        existingEmployee.setTeam(team);

        employeeRepository.save(existingEmployee);
    }

    public void validateUniqueProperties(Optional<String> username, Optional<String> email) {
        username.ifPresent(u -> {
            if (employeeRepository.existsEmployeeEtyByUsernameIgnoreCase(u)) {
                throw new BusinessException(BusinessErrorCode.USERNAME_DUPLICATE, new HashMap<>());
            }
        });

        email.ifPresent(e -> {
            if (employeeRepository.existsEmployeeEtyByEmailIgnoreCase(e)) {
                throw new BusinessException(BusinessErrorCode.EMAIL_DUPLICATE, new HashMap<>());
            }
        });
    }

    @Transactional
    public void modifyEmployeeDaysOff(ModifyEmployeeDaysOffRequest modifyEmployeeDaysOffRequest) {
        employeeRepository.findAllById(modifyEmployeeDaysOffRequest.getEmployeeIds()).forEach(employee -> {

            EmpYearlyDaysOffEty empYearlyDaysOffEty = employee.getEmpYearlyDaysOffEties()
                .stream()
                .filter(yearlyDaysOffEty -> yearlyDaysOffEty.getYear() == Year.now().getValue())
                .findFirst()
                .orElseGet(() -> { // Create a new EmpYearlyDaysOffEty if not found for the current year
                    EmpYearlyDaysOffEty newYearlyDaysOff = createEmpYearlyDaysOffEty(employee, 0);
                    employee.getEmpYearlyDaysOffEties().add(newYearlyDaysOff);
                    return newYearlyDaysOff;
                }
            );

            // Calculate and set new NoDaysOff
            if (modifyEmployeeDaysOffRequest.getType() == DaysOffChangeType.INCREASE) {
                empYearlyDaysOffEty.setTotalNoDays(empYearlyDaysOffEty.getTotalNoDays() + modifyEmployeeDaysOffRequest.getNoDays());
            }
            else if (modifyEmployeeDaysOffRequest.getType() == DaysOffChangeType.DECREASE) {
                int newTotalDaysOff = empYearlyDaysOffEty.getTotalNoDays() - modifyEmployeeDaysOffRequest.getNoDays();
                if (newTotalDaysOff < 0) {
                    throw BusinessException.builder()
                        .error(BusinessErrorCode.NEGATIVE_DAYS_OFF_BALANCE)
                        .build();
                }
                empYearlyDaysOffEty.setTotalNoDays(newTotalDaysOff);
            }

            // Create History record
            empYearlyDaysOffEty.getEmpYearlyDaysOffHistEties().add(
                createEmpYearlyDaysOffHistEty(modifyEmployeeDaysOffRequest.getDescription(),
                    modifyEmployeeDaysOffRequest.getNoDays(), modifyEmployeeDaysOffRequest.getType(),
                    empYearlyDaysOffEty)
            );

            employeeRepository.save(employee);
        });
    }

    @Transactional
    public void createLeaveRequest(String employeeId, LeaveRequest leaveRequest) {
        EmployeeEty employee = employeeHandler.getEmployee(employeeId);

        if (leaveRequest.getStartDate().isBefore(LocalDate.now()) ||
            leaveRequest.getStartDate().isAfter(leaveRequest.getEndDate())) {
            throw BusinessException.builder().error(BusinessErrorCode.LEAVE_REQUEST_CREATE_INVALID_PERIOD ).build();
        }

        if (leaveRequest.getStartDate().getYear() != leaveRequest.getEndDate().getYear()) {
            throw BusinessException.builder().error(BusinessErrorCode.LEAVE_REQUEST_CREATE_DIFFERENT_YEARS ).build();
        }

        int numberOfDaysOff = (int) leaveRequestHandler.calculateNumberOffDaysOffInAPeriod(leaveRequest.getStartDate(), leaveRequest.getEndDate());
        int remainingDaysOff = getRemainingDaysOff(employee);

        if (numberOfDaysOff > remainingDaysOff) {
            throw BusinessException.builder().error(BusinessErrorCode.INSUFFICIENT_DAYS_OFF).build();
        }

        employee.getLeaveRequestEties().add(createLeaveRequestEty(leaveRequest, numberOfDaysOff, employee));

        employeeRepository.save(employee);
    }

    private int getRemainingDaysOff(EmployeeEty employee) {
        EmpYearlyDaysOffEty empYearlyDaysOffEty = employee.getEmpYearlyDaysOffEties()
            .stream()
            .filter(yearlyDaysOffEty -> yearlyDaysOffEty.getYear() == Year.now().getValue())
            .findFirst()
            .orElseThrow(() -> BusinessException.builder()
                .error(BusinessErrorCode.NO_YEARLY_DAYS_OFF_SET)
                .build());

        return empYearlyDaysOffEty.getTotalNoDays() - getRequestedDaysOff(employee);
    }

    public int getRemainingDaysOffByEmployeeId(String employeeId) {
        EmployeeEty employee = employeeHandler.getEmployee(employeeId);

        return getRemainingDaysOff(employee);
    }

    private int getRequestedDaysOff(EmployeeEty employeeEty) {
        return employeeEty.getLeaveRequestEties()
            .stream()
            .filter(request -> request.getStartDate().getYear() == Year.now().getValue())
            .mapToInt(LeaveRequestEty::getNoOfDays)
            .sum();
    }

    private LeaveRequestEty createLeaveRequestEty(LeaveRequest leaveRequest, int numberOfDaysOff, EmployeeEty employee) {
        LeaveRequestEty leaveRequestEty = new LeaveRequestEty();
        leaveRequestEty.setCrtUsr(securityAccessTokenHandler.getEmployeeIdFromToken());
        leaveRequestEty.setCrtTms(clock.instant());
        leaveRequestEty.setMdfUsr(securityAccessTokenHandler.getEmployeeIdFromToken());
        leaveRequestEty.setMdfTms(clock.instant());
        leaveRequestEty.setStartDate(leaveRequest.getStartDate());
        leaveRequestEty.setEndDate(leaveRequest.getEndDate());
        leaveRequestEty.setType(leaveRequest.getType());
        leaveRequestEty.setDescription(leaveRequest.getDescription());
        leaveRequestEty.setStatus(LeaveRequestStatus.PENDING);
        leaveRequestEty.setNoOfDays(numberOfDaysOff);
        leaveRequestEty.setEmployeeEty(employee);
        return leaveRequestEty;
    }

    public EmpYearlyDaysOffEty createEmpYearlyDaysOffEty(EmployeeEty employee, int NoDaysOff) {
        EmpYearlyDaysOffEty empYearlyDaysOffEty = new EmpYearlyDaysOffEty();
        empYearlyDaysOffEty.setEmployee(employee);
        empYearlyDaysOffEty.setTotalNoDays(NoDaysOff);
        empYearlyDaysOffEty.setYear(Year.now().getValue());
        empYearlyDaysOffEty.setEmpYearlyDaysOffHistEties(new ArrayList<>(List.of(
            createEmpYearlyDaysOffHistEty(INIT_DAYS_OFF_DESCRIPTION,
                NoDaysOff, DaysOffChangeType.INCREASE, empYearlyDaysOffEty))));
        return empYearlyDaysOffEty;
    }

    public EmpYearlyDaysOffHistEty createEmpYearlyDaysOffHistEty(String description, int noDays, DaysOffChangeType type, EmpYearlyDaysOffEty empYearlyDaysOffEty) {
        EmpYearlyDaysOffHistEty empYearlyDaysOffHistEty = new EmpYearlyDaysOffHistEty();
        empYearlyDaysOffHistEty.setEmpYearlyDaysOff(empYearlyDaysOffEty);
        empYearlyDaysOffHistEty.setNoDays(noDays);
        empYearlyDaysOffHistEty.setDescription(description);
        empYearlyDaysOffHistEty.setType(type);
        empYearlyDaysOffHistEty.setCrtUsr(securityAccessTokenHandler.getEmployeeIdFromToken());
        empYearlyDaysOffHistEty.setCrtTms(clock.instant());
        return empYearlyDaysOffHistEty;
    }

    private String generatePassword(CreateEmployeeRequest createEmployeeRequest) {
        return passwordEncoder.encode(createEmployeeRequest.getUsername() + "23");
    }

    @Transactional
    public void reviewLeaveRequest(LeaveRequestReview leaveRequestReview, String employeeId, Long requestId) {
        EmployeeEty employee = employeeHandler.getEmployee(employeeId);

        LeaveRequestEty leaveRequestEty = employee.getLeaveRequestEties()
                .stream()
                .filter(request -> request.getId().equals(requestId))
                .findFirst()
                .orElseThrow(() -> BusinessException.builder()
                        .error(BusinessErrorCode.LEAVE_REQUEST_NOT_FOUND)
                        .build());

        if (leaveRequestReview.getV() < leaveRequestEty.getV()) {
            throw new BusinessException(BusinessErrorCode.VERSION_CONFLICT, new HashMap<>());
        }

        if (!leaveRequestEty.getStatus().equals(LeaveRequestStatus.PENDING)) {
            throw BusinessException.builder().error(BusinessErrorCode.LEAVE_REQUEST_INVALID_STATUS).build();
        }

        if (leaveRequestReview.getType() == LeaveRequestReviewType.REJECTION) {
            if (leaveRequestReview.getRejectionReason() == null) {
                throw BusinessException.builder().error(BusinessErrorCode.REJECTION_REASON_REQUIRED).build();
            }
            leaveRequestEty.setRejectReason(leaveRequestEty.getRejectReason());
            leaveRequestEty.setStatus(LeaveRequestStatus.REJECTED);
        } else if (leaveRequestReview.getType() == LeaveRequestReviewType.APPROVAL) {
            leaveRequestEty.setStatus(LeaveRequestStatus.APPROVED);
        }
        leaveRequestEty.setMdfUsr(securityAccessTokenHandler.getEmployeeIdFromToken());
        leaveRequestEty.setMdfTms(clock.instant());

        employeeRepository.save(employee);
    }

    @Transactional
        public RequestDetailsList getEmployeeRequestsByPeriod (String employeeId, LocalDate startDate, LocalDate endDate){
            var requestList = new RequestDetailsList();
        EmployeeEty employee = employeeHandler.getEmployee(employeeId);

        List<RequestDetailsListItem> requests = employee.getLeaveRequestEties().stream()
                    .filter(RequestEty ->
                            (RequestEty.getStartDate().isAfter(startDate) &&
                                RequestEty.getEndDate().isBefore(endDate)
                            )
                    )
                    .map(LeaveRequestMapper.INSTANCE::mapRequestEtyToRequestDto).toList();
            requestList.setItems(requests);
            return requestList;
        }

        @Transactional
        public RequestDetailsList getAllEmployeeRequests (String employeeId){
            var requestList = new RequestDetailsList();
            EmployeeEty employee = employeeHandler.getEmployee(employeeId);

            List<RequestDetailsListItem> requests = employee.getLeaveRequestEties().stream()
                    .map(LeaveRequestMapper.INSTANCE::mapRequestEtyToRequestDto).toList();
            requestList.setItems(requests);
            return requestList;
        }

        @Transactional
        public void deleteEmployeeLeaveRequest(String employeeId, Long requestId) {
        EmployeeEty employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> BusinessException.builder()
                        .error(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
                        .build());

        LeaveRequestEty leaveRequest = employee.getLeaveRequestEties().stream()
                .filter(leaveRequestEty -> leaveRequestEty.getId().equals(requestId))
                .findFirst()
                .orElseThrow(() -> BusinessException.builder()
                        .error(BusinessErrorCode.LEAVE_REQUEST_NOT_FOUND)
                        .build());

        if (leaveRequest.getStatus() == LeaveRequestStatus.REJECTED) {
            throw BusinessException.builder()
                    .error(BusinessErrorCode.LEAVE_REQUEST_DELETE_NOT_PERMITTED_REJECTED_STATUS)
                    .build();
        }

        if (leaveRequest.getStatus() == LeaveRequestStatus.APPROVED && isRequestInPreviousMonths(leaveRequest)) {
            throw BusinessException.builder()
                    .error(BusinessErrorCode.LEAVE_REQUEST_DELETE_NOT_PERMITTED_APPROVED_DAYS_IN_PAST)
                    .build();
        }

        employee.removeLeaveRequest(leaveRequest);
        employeeRepository.save(employee);
    }

    private boolean isRequestInPreviousMonths(LeaveRequestEty leaveRequest) {
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfCurrentMonth = now.withDayOfMonth(1);
        return leaveRequest.getStartDate().isBefore(firstDayOfCurrentMonth);
    }

    @Transactional
    public RemainingDays getRemainingDays(String employeeId) {
        RemainingDays remainingDaysDto = new RemainingDays();
        remainingDaysDto.setRemainingDays(getRemainingDaysOffByEmployeeId(employeeId));
        return remainingDaysDto;
    }

    @Transactional
    public void updateRequest(String employeeId, Long requestId, UpdateRequest updatedRequest) {

        EmployeeEty employee = employeeHandler.getEmployee(employeeId);

        LeaveRequestEty existingRequest = employee.getLeaveRequestEties().stream()
                .filter(leaveRequestEty -> leaveRequestEty.getId().equals(requestId))
                .findFirst()
                .orElseThrow(() ->
                        new BusinessException(BusinessErrorCode.LEAVE_REQUEST_NOT_FOUND, new HashMap<>())
                );

        validateRequestForUpdate(existingRequest, updatedRequest);

        updateRequestDetails(existingRequest, updatedRequest);

        employeeRepository.save(employee);

    }

    private void validateRequestForUpdate(LeaveRequestEty existingRequest, UpdateRequest updatedRequest) {

        if (updatedRequest.getV() < existingRequest.getV()) {
            throw BusinessException.builder()
                    .error(BusinessErrorCode.VERSION_CONFLICT)
                    .build();
        }

        if (!existingRequest.getStatus().equals(LeaveRequestStatus.PENDING) &&
                !existingRequest.getStatus().equals(LeaveRequestStatus.APPROVED)) {
            throw BusinessException.builder()
                    .error(BusinessErrorCode.REQUEST_CANNOT_BE_EDITED)
                    .build();
        }

        LocalDate currentDate = LocalDate.now();
        if (updatedRequest.getStartDate().isBefore(currentDate) ||
                updatedRequest.getEndDate().isBefore(currentDate)) {
            throw BusinessException.builder()
                    .error(BusinessErrorCode.REQUEST_FROM_PAST_CANNOT_BE_EDITED)
                    .build();
        }

    }

    private void updateRequestDetails(LeaveRequestEty existingRequest, UpdateRequest updatedRequest) {
        existingRequest.setType(updatedRequest.getType());
        existingRequest.setDescription(updatedRequest.getDescription());
        existingRequest.setStartDate(updatedRequest.getStartDate());
        existingRequest.setEndDate(updatedRequest.getEndDate());
        existingRequest.setNoOfDays((int) leaveRequestHandler.calculateNumberOffDaysOffInAPeriod(updatedRequest.getStartDate(), updatedRequest.getEndDate()));
        existingRequest.setStatus(LeaveRequestStatus.PENDING);

        //existingRequest.setMdfUsr(securityAccessTokenHandler.getEmployeeIdFromToken());
        existingRequest.setMdfTms(clock.instant());

    }

}
