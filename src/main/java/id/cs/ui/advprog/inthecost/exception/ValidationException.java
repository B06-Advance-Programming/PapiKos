package id.cs.ui.advprog.inthecost.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private final ValidationErrorCode error;
    private final String message;

    public ValidationException(ValidationErrorCode error) {
        this.error = error;
        this.message = this.error.getException().getMessage();
    }

    public ValidationException(ValidationErrorCode error, String message) {
        this.error = error;
        this.message = message;
    }
}