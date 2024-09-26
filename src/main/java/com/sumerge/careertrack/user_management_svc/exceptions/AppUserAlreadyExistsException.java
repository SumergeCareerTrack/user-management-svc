package com.sumerge.careertrack.user_management_svc.exceptions;

public class AppUserAlreadyExistsException extends UserManagementException {
    public AppUserAlreadyExistsException(String message) {
        super(message);
    }

    public AppUserAlreadyExistsException(String message, Object... args) {
        super(String.format(message, args));
    }
}
