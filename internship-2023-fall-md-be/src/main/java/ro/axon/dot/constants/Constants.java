package ro.axon.dot.constants;

public  final class Constants {
    private Constants(){}
    public static final String BASE_URL = "/api/v1";
    public static final String MISC_URI= BASE_URL + "/misc";
    public static final String LEGALLY_DAY_OFF = "legally-days-off";
    public static final String ROLES = "roles";
    public static final String USER = "user";
    public static final String REFRESH = "refresh";
    public static final String LOGOUT = "logout";
    public static final String LOGIN = "login";
    public static final String EMPLOYEES = BASE_URL + "/employees";
    public static final String VALIDATION = "validation";
    public static final String REQUESTS = BASE_URL + "/requests";
    public static final String TEAMS = BASE_URL + "/teams";
    public static final String EMPLOYEE_ID = "/{employeeId}";
    public static final String INACTIVATE_EMPLOYEE_ID = EMPLOYEE_ID + "/inactivate";
    public static final String REQUESTS_EMPLOYEE_ID = EMPLOYEE_ID + "/requests";
    public static final String REQUESTS_ID =  REQUESTS_EMPLOYEE_ID + "/{requestId}";
    public static final String DAYS_OFF = "days-off";
    public static final String REMAINING_DAYS_OFF = EMPLOYEE_ID + "/remaining-days-off";


}
