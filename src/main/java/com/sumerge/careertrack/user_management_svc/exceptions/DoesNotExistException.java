package com.sumerge.careertrack.user_management_svc.exceptions;

public class DoesNotExistException extends UserManagementException {
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
