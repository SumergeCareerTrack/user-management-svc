package com.sumerge.careertrack.user_management_svc.exceptions;

public class TitleDoesNotExistException extends UserManagementException {

    public TitleDoesNotExistException(String message) {
        super(message);
    }

    public TitleDoesNotExistException(String message, Object... args) {
        super(String.format(message, args));
    }
}
