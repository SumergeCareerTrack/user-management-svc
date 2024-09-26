package com.sumerge.careertrack.user_management_svc.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ TitleDoesNotExistException.class })
    public ResponseEntity<Object> handleCourseDoesNotExistException(TitleDoesNotExistException exception) {
        return ResponseEntity.badRequest()
                .body(exception.getMessage());
    }

    @ExceptionHandler({ AppUserDoesNotExistException.class })
    public ResponseEntity<Object> handleCourseAlreadyExistsException(AppUserDoesNotExistException exception) {
        return ResponseEntity.badRequest()
                .body(exception.getMessage());
    }

    @ExceptionHandler({ AppUserAlreadyExistsException.class })
    public ResponseEntity<Object> handleCourseAlreadyExistsException(AppUserAlreadyExistsException exception) {
        return ResponseEntity.badRequest()
                .body(exception.getMessage());
    }

    @ExceptionHandler({ TitleAlreadyExistsException.class })
    public ResponseEntity<Object> handleCourseAlreadyExistsException(TitleAlreadyExistsException exception) {
        return ResponseEntity.badRequest()
                .body(exception.getMessage());
    }

}