package com.sumerge.careertrack.user_management_svc.exceptions;

public class TitlesDoesNotExistException extends RuntimeException {
        
        public TitlesDoesNotExistException(String message) {
            super(message);
        }
        
        public TitlesDoesNotExistException(String message, Object... args) {
            super(String.format(message, args));
        }
}
