package com.sumerge.careertrack.user_management_svc.exceptions;

public class AlreadyExistsException extends UserManagementException {

    public static final String APP_USER_ID = "User with ID \"%s\" already exists.";
    public static final String APP_USER_EMAIL = "User with email \"%s\" already exists.";
    public static final String TITLE = "Title \"%s\" in department \"%s\" already exists.";
    public static final String DEPARTMENT = "Department \"%s\" already exists.";

    public AlreadyExistsException() {
        super();
    }

    public AlreadyExistsException(String message) {
        super(message);
    }

    public AlreadyExistsException(String message, Object... args) {
        super(String.format(message, args));
    }
}
