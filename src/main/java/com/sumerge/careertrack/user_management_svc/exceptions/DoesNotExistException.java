package com.sumerge.careertrack.user_management_svc.exceptions;

public class DoesNotExistException extends UserManagementException {

    public static final String APP_USER_ID = "User with ID \"%d\" does not exist.";
    public static final String APP_USER_EMAIL = "User with email \"%d\" does not exist.";
    public static final String DEPARTMENT = "Department \"%s\" does not exist.";
    public static final String TITLE = "Title \"%s\" in department \"%s\" does not exist.";

    public DoesNotExistException() {
        super();
    }

    public DoesNotExistException(String message) {
        super(message);
    }

    public DoesNotExistException(String message, Object... args) {
        super(String.format(message, args));
    }
}
