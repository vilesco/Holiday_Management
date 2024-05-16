package ro.axon.dot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ro.axon.dot.domain.entities.EmpYearlyDaysOffEty;
import ro.axon.dot.domain.entities.EmployeeEty;
import ro.axon.dot.domain.entities.LeaveRequestEty;
import ro.axon.dot.domain.entities.TeamEty;
import ro.axon.dot.domain.enums.DaysOffChangeType;
import ro.axon.dot.domain.enums.LeaveRequestStatus;
import ro.axon.dot.domain.enums.Status;
import ro.axon.dot.domain.repositories.EmployeeRepository;
import ro.axon.dot.domain.repositories.TeamRepository;
import ro.axon.dot.exception.BusinessErrorCode;
import ro.axon.dot.exception.BusinessException;
import ro.axon.dot.model.enums.LeaveRequestReviewType;
import ro.axon.dot.model.request.CreateEmployeeRequest;
import ro.axon.dot.model.request.LeaveRequest;
import ro.axon.dot.model.request.LeaveRequestReview;
import ro.axon.dot.model.request.ModifyEmployeeDaysOffRequest;
import ro.axon.dot.model.response.EmployeeDetailsList;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private SecurityAccessTokenHandler securityAccessTokenHandler;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Clock clock;

    @Mock
    private LeaveRequestHandler leaveRequestHandler;

    @Mock
    private EmployeeHandler employeeHandler;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void testGetEmployeesWithNullName() {
        List<EmployeeEty> employees = new ArrayList<>();
        employees.add(createEmployee("John", "Doe"));
        employees.add(createEmployee("Jane", "Smith"));

        when(employeeRepository.findAll()).thenReturn(employees);

        EmployeeDetailsList employeeDetailsList = employeeService.getEmployees(null);

        assertThat(employeeDetailsList).isNotNull();
        assertThat(employeeDetailsList.getItems()).hasSize(2);

        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testGetEmployeesWithNameFilter() {
        List<EmployeeEty> employees = new ArrayList<>();
        employees.add(createEmployee("John", "Doe"));
        employees.add(createEmployee("Jane", "Smith"));

        when(employeeRepository.findAll()).thenReturn(employees);

        EmployeeDetailsList employeeDetailsList = employeeService.getEmployees("John");

        assertThat(employeeDetailsList).isNotNull();
        assertThat(employeeDetailsList.getItems()).hasSize(1);
        assertThat(employeeDetailsList.getItems().get(0).getFirstName()).isEqualTo("John");

        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testCreateEmployeeSuccess() {
        // not duplicated username
        when(employeeRepository.existsEmployeeEtyByUsernameIgnoreCase(any())).thenReturn(false);
        when(employeeRepository.existsEmployeeEtyByEmailIgnoreCase(any())).thenReturn(false);

        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest();
        createEmployeeRequest.setUsername("testUser");
        createEmployeeRequest.setTeamId(1L);

        TeamEty team = new TeamEty();
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        when(securityAccessTokenHandler.getEmployeeIdFromToken()).thenReturn("employeeId");
        when(passwordEncoder.encode(createEmployeeRequest.getUsername() + "23")).thenReturn("testUser23");

        when(clock.instant()).thenReturn(Instant.now());

        employeeService.createEmployee(createEmployeeRequest);

        verify(employeeRepository).save(any(EmployeeEty.class));
    }

    @Test
    void testCreateEmployeeDuplicateUsername() {
        // duplicated username
        when(employeeRepository.existsEmployeeEtyByUsernameIgnoreCase(any())).thenReturn(true);

        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest();
        createEmployeeRequest.setUsername("duplicateUser");

        BusinessException exception = assertThrows(BusinessException.class, () -> employeeService.createEmployee(createEmployeeRequest));

        // verify that error was thrown
        assertThat(exception.getError().getErrorCode()).isEqualTo(BusinessErrorCode.USER_ALREADY_EXISTS.getErrorCode());

        // Verify that the employee was not saved
        verify(employeeRepository, never()).save(any(EmployeeEty.class));
    }

    @Test
    void testModifyEmployeeDaysOff() {
        List<String> employeeIds = new ArrayList<>(Arrays.asList("employeeId1", "employeeId2"));

        ModifyEmployeeDaysOffRequest modifyRequest = new ModifyEmployeeDaysOffRequest();
        modifyRequest.setEmployeeIds(employeeIds);
        modifyRequest.setType(DaysOffChangeType.INCREASE);
        modifyRequest.setNoDays(5);

        when(clock.instant()).thenReturn(Instant.now());

        when(employeeRepository.findAllById(any())).thenReturn(new ArrayList<>(Arrays.asList(createEmployee("Mark1", "Mark1"), createEmployee("Mark2", "Mark2"))));

        employeeService.modifyEmployeeDaysOff(modifyRequest);

        verify(employeeRepository, times(2)).save(any(EmployeeEty.class));
    }

    @Test
    void testModifyEmployeeDaysOffNegativeBalance() {
        List<String> employeeIds = new ArrayList<>(Arrays.asList("employeeId1", "employeeId2"));

        ModifyEmployeeDaysOffRequest modifyRequest = new ModifyEmployeeDaysOffRequest();
        modifyRequest.setEmployeeIds(employeeIds);
        modifyRequest.setType(DaysOffChangeType.DECREASE);
        modifyRequest.setNoDays(22); // more than TotalNoDaysOf

        when(employeeRepository.findAllById(any())).thenReturn(new ArrayList<>(Arrays.asList(createEmployee("Mark1", "Mark1"), createEmployee("Mark2", "Mark2"))));

        BusinessException exception = assertThrows(BusinessException.class, () -> employeeService.modifyEmployeeDaysOff(modifyRequest));

        assertThat(exception.getError().getErrorCode()).isEqualTo(BusinessErrorCode.NEGATIVE_DAYS_OFF_BALANCE.getErrorCode());

        verify(employeeRepository, never()).save(any(EmployeeEty.class));
    }

    @Test
    void testCreateLeaveRequestValid() {
        EmployeeEty employee = createEmployee("Jo", "Jo");

        when(employeeHandler.getEmployee(any())).thenReturn(employee);
        when(clock.instant()).thenReturn(Instant.now());
        when(leaveRequestHandler.calculateNumberOffDaysOffInAPeriod(any(), any())).thenReturn(20L);
        when(employeeRepository.save(any(EmployeeEty.class))).thenReturn(employee);

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setStartDate(LocalDate.now().plusDays(1)); // so both to be in the future
        leaveRequest.setEndDate(LocalDate.now().plusDays(5));

        employeeService.createLeaveRequest("employeeId", leaveRequest);

        verify(employeeRepository).save(employee);
    }

    @Test
    void testReviewLeaveRequestApproval() {
        EmployeeEty employee = createEmployee("Jo", "Jo");

        LeaveRequestEty leaveRequestEty = mock(LeaveRequestEty.class);
        when(leaveRequestEty.getV()).thenReturn(0L); // does not have setter (and the main reason of using the mock)
        when(leaveRequestEty.getId()).thenReturn(1L);
        when(leaveRequestEty.getStatus()).thenReturn(LeaveRequestStatus.PENDING);
        employee.setLeaveRequestEties(new ArrayList<>(List.of(leaveRequestEty)));

        when(clock.instant()).thenReturn(Instant.now());
        when(employeeHandler.getEmployee(any())).thenReturn(employee);

        LeaveRequestReview leaveRequestReview = new LeaveRequestReview();
        leaveRequestReview.setType(LeaveRequestReviewType.APPROVAL);
        leaveRequestReview.setV(0L);

        employeeService.reviewLeaveRequest(leaveRequestReview, "employeeId", 1L);

        verify(employeeRepository).save(employee);
    }

    private EmployeeEty createEmployee(String firstName, String lastName) {
        EmployeeEty employee = new EmployeeEty();
        employee.setId("employeeId");
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setStatus(Status.ACTIVE);

        EmpYearlyDaysOffEty empYearlyDaysOffEty = new EmpYearlyDaysOffEty();
        empYearlyDaysOffEty.setEmployee(employee);
        empYearlyDaysOffEty.setTotalNoDays(21);
        empYearlyDaysOffEty.setYear(Year.now().getValue());
        employee.setEmpYearlyDaysOffEties(new ArrayList<>(List.of(empYearlyDaysOffEty)));

        // Others not needed for now
        return employee;
    }

}
