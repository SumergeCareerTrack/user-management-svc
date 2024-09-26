package com.sumerge.careertrack.user_management_svc.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler({TitlesDoesNotExistException.class})
    public ResponseEntity<Object> handleCourseDoesNotExistException(TitlesDoesNotExistException exception){
        return ResponseEntity.badRequest()
                    .body(exception.getMessage());
    }

    @ExceptionHandler({UserDoesNotExistException.class})
    public ResponseEntity<Object> handleCourseAlreadyExistsException(UserDoesNotExistException exception){
        return ResponseEntity.badRequest()
                    .body(exception.getMessage());
    }

    
}