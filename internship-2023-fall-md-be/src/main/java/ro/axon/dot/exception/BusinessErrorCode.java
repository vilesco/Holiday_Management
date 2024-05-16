package ro.axon.dot.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BusinessErrorCode {
    EMPLOYEE_NOT_FOUND("EDOT0001400",  "The employee with the given ID does not exist."),
    USER_ALREADY_EXISTS("EDOT0002409", "The employee with the given Username/Email already exist."),
    TEAM_NOT_FOUND("EDOT0003400", "Team with the given ID does not exist."),
    USERNAME_DUPLICATE("EDOT0004409", "The provided username is already in use."),
    EMAIL_DUPLICATE("EDOT0005409", "The provided email is already in use."),
    INVALID_REQUEST("EDOT0006400", "At least one parameter (username or email) must be provided."),
    NEGATIVE_DAYS_OFF_BALANCE("EDOT0007400", "The requested action would result in a negative balance of available days off for the employee."),
    VERSION_CONFLICT("EDOT0008409", "Version conflict."),
    INSUFFICIENT_DAYS_OFF("EDOT0009400", "Insufficient days off for leave."),
    LEAVE_REQUEST_CREATE_DIFFERENT_YEARS ("EDOT0010400", "The leave request cannot have different years."),
    LEAVE_REQUEST_CREATE_INVALID_PERIOD ("EDOT0011400", "Period invalid (startDate > endDate) or period in the past."),
    LEAVE_REQUEST_NOT_FOUND("EDOT0012400", "The leave request with the given ID does not exist."),
    LEAVE_REQUEST_INVALID_STATUS("EDOT0013400", "Leave request status must be pending."),
    REJECTION_REASON_REQUIRED("EDOT0014400", "Rejection reason required"),
    TOKEN_DETAILS_EMPLOYEE_ID_MISSING("EDOT0015400", "Employee ID is missing in the token."),
    INVALID_EMPLOYEE_ID("EDOT0016400","Empolyee ID is missing from data base"),
    NO_YEARLY_DAYS_OFF_SET("EDOT0017400","Number of days for current year is missing"),
    TOKEN_DETAILS_KEY_ID_MISSING("EDOT0018400", "Key ID is missing in the token."),
    REFRESH_TOKEN_NOT_FOUND("EDOT0019400", "Refresh token has invalid Key ID."),
    REFRESH_TOKEN_INVALID_AUDIENCE("EDOT0020400", "The Refresh Token audience does not match."),
    REFRESH_TOKEN_NOT_ACTIVE("EDOT0021400", "The Refresh Token is Not Active."),
    REFRESH_TOKEN_EXPIRED("EDOT0022400", "The Refresh Token has expired."),
    TOKEN_PARSE_EXCEPTION("EDOT0023400", "Failed to parse token."),
    TOKEN_GENERATION_EXCEPTION("EDOT0024400", "Error when signing jwt token."),
    INVALID_EMPLOYEE_CREDENTIALS_EXCEPTION("EDOT0025400", "Username or password is incorrect."),
    LEAVE_REQUEST_DELETE_NOT_PERMITTED_REJECTED_STATUS("EDOT0026400", "Leave request with given id has rejected status"),
    LEAVE_REQUEST_DELETE_NOT_PERMITTED_APPROVED_DAYS_IN_PAST("EDOT0027400", "Leave request with given id has approved days in past"),
    REQUEST_CANNOT_BE_EDITED("EDOT0028400", "Request was already refused."),
    REQUEST_FROM_PAST_CANNOT_BE_EDITED("EDOT0029400", "Request from past cannot be edited.");

    private final String errorCode;
    private final String devMsg;
    private final HttpStatus status;

    BusinessErrorCode(String errorCode, String devMsg) {
        this.errorCode = errorCode;
        this.devMsg = devMsg;
        this.status = HttpStatus.valueOf(Integer.parseInt(errorCode.substring(errorCode.length() - 3)));
    }
}
