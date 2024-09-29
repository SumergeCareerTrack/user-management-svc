package com.sumerge.careertrack.user_management_svc.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ DoesNotExistException.class })
    public ResponseEntity<Object> handleCourseAlreadyExistsException(DoesNotExistException exception) {
        return ResponseEntity.badRequest()
                .body(exception.getMessage());
    }

    @ExceptionHandler({ AlreadyExistsException.class })
    public ResponseEntity<Object> handleCourseAlreadyExistsException(AlreadyExistsException exception) {
        return ResponseEntity.badRequest()
                .body(exception.getMessage());
    }

}