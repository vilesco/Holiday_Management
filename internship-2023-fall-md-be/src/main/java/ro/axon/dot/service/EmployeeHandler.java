package ro.axon.dot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.axon.dot.domain.entities.EmployeeEty;
import ro.axon.dot.domain.repositories.EmployeeRepository;
import ro.axon.dot.exception.BusinessErrorCode;
import ro.axon.dot.exception.BusinessException;

@Component
@RequiredArgsConstructor
public class EmployeeHandler {

    private final EmployeeRepository employeeRepository;

    public EmployeeEty getEmployee(String employeeId) {
        return employeeRepository.findById(employeeId)
            .orElseThrow(() -> BusinessException.builder()
                .error(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
                .build());
    }

}
