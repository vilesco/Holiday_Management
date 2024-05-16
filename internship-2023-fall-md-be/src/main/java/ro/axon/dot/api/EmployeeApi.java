package ro.axon.dot.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.axon.dot.exception.BusinessErrorCode;
import ro.axon.dot.exception.BusinessException;
import ro.axon.dot.model.request.CreateEmployeeRequest;
import ro.axon.dot.model.request.LeaveRequest;
import ro.axon.dot.model.request.EmployeeUpdateRequest;
import ro.axon.dot.model.request.LeaveRequestReview;
import ro.axon.dot.model.request.ModifyEmployeeDaysOffRequest;
import ro.axon.dot.model.response.RemainingDays;
import ro.axon.dot.model.response.EmployeeDetailsList;
import ro.axon.dot.model.response.RequestDetailsList;
import ro.axon.dot.service.EmployeeService;
import ro.axon.dot.model.request.UpdateRequest;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;

import static ro.axon.dot.constants.Constants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(EMPLOYEES)
public class EmployeeApi {

    private final EmployeeService employeeService;

    @Data
    public static class ValidationResponse {
        private Boolean success = true;
    }

    @GetMapping
    public ResponseEntity<EmployeeDetailsList> getEmployeeList(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(employeeService.getEmployees(name));
    }

    @GetMapping(VALIDATION)
    public ResponseEntity<ValidationResponse> validateUniqueProperties(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {

        if (username == null && email == null) {
            throw BusinessException.builder()
                    .error(BusinessErrorCode.INVALID_REQUEST)
                    .build();
        }

        employeeService.validateUniqueProperties(Optional.ofNullable(username), Optional.ofNullable(email));
        ValidationResponse response = new ValidationResponse();
        return ResponseEntity.ok(response);
    }

    @PatchMapping(INACTIVATE_EMPLOYEE_ID)
    public ResponseEntity<String> inactivateEmployee(@PathVariable String employeeId) {
        employeeService.inactivateEmployee(employeeId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(EMPLOYEE_ID)
    public void updateEmployee(@PathVariable String employeeId, @Valid @RequestBody EmployeeUpdateRequest updatedEmployee) {
        employeeService.updateEmployee(employeeId, updatedEmployee);
    }

    @PatchMapping(REQUESTS_ID)
    public ResponseEntity<Void> reviewLeaveRequest(@RequestBody @Valid LeaveRequestReview leaveRequestReview,
        @PathVariable String employeeId, @PathVariable Long requestId) {
        employeeService.reviewLeaveRequest(leaveRequestReview, employeeId, requestId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public ResponseEntity<Void> createEmployee(@RequestBody @Valid CreateEmployeeRequest createEmployeeRequest) {
        employeeService.createEmployee(createEmployeeRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(DAYS_OFF)
    public ResponseEntity<Void> modifyEmployeeDaysOff(@RequestBody @Valid ModifyEmployeeDaysOffRequest modifyEmployeeDaysOffRequest) {
        employeeService.modifyEmployeeDaysOff(modifyEmployeeDaysOffRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(REQUESTS_EMPLOYEE_ID)
    public ResponseEntity<Void> createLeaveRequest(@PathVariable String employeeId, @RequestBody @Valid LeaveRequest leaveRequest) {
        employeeService.createLeaveRequest(employeeId, leaveRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping(REQUESTS_EMPLOYEE_ID)
    public ResponseEntity<RequestDetailsList> getEmployeeRequestsByPeriod(@PathVariable String employeeId,
                                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if(startDate == null || endDate == null){
            return ResponseEntity.ok(employeeService.getAllEmployeeRequests(employeeId));
        }
        return ResponseEntity.ok(employeeService.getEmployeeRequestsByPeriod(employeeId, startDate, endDate));
    }

    @GetMapping(REMAINING_DAYS_OFF)
    public ResponseEntity<RemainingDays> getRemainingDaysOff(@PathVariable String employeeId) {
        return ResponseEntity.ok(employeeService.getRemainingDays(employeeId));
    }

    @DeleteMapping(REQUESTS_ID)
    public ResponseEntity<Void> deleteEmployeeLeaveRequest(@PathVariable String employeeId, @PathVariable Long requestId) {
        employeeService.deleteEmployeeLeaveRequest(employeeId, requestId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("{employeeId}/requests/{requestId}")
    public ResponseEntity<Void> updateRequest(@PathVariable String employeeId,
                                              @PathVariable Long requestId,
                                              @Valid @RequestBody UpdateRequest updatedRequest) {
        employeeService.updateRequest(employeeId, requestId, updatedRequest);
        return ResponseEntity.noContent().build();
    }

}
