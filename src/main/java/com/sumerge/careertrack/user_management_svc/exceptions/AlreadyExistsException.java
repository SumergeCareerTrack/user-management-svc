package com.sumerge.careertrack.user_management_svc.exceptions;

public class AlreadyExistsException extends UserManagementException {
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
