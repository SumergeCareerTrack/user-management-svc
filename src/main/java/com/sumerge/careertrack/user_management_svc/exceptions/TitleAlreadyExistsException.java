package com.sumerge.careertrack.user_management_svc.exceptions;

public class TitleAlreadyExistsException extends UserManagementException {
    public TitleAlreadyExistsException(String message) {
        super(message);
    }

    public TitleAlreadyExistsException(String message, Object... args) {
        super(String.format(message, args));
    }
}
