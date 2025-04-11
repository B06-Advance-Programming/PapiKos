package id.cs.ui.advprog.inthecost.Exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private final String errorCode;
    private final String errorMessage;

    public ValidationException(ValidationErrorCode errorCode) {
        this.errorMessage = errorCode.getMessage();
        this.errorCode = errorCode.getCode();
    }

    public ValidationException(ValidationErrorCode errorCode, String errorMessage) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode.getCode();
    }
}