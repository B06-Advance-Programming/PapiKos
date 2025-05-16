package id.cs.ui.advprog.inthecost.exception;
import lombok.Getter;

import java.util.NoSuchElementException;

@Getter
public enum ValidationErrorCode {
    NULL_VALUE("err_0", new NullPointerException("Field tidak boleh null")),
    EMPTY_VALUE("err_1", new IllegalArgumentException("Field tidak boleh empty")),
    NEGATIVE_VALUE("err_2", new IllegalArgumentException("Field tidak boleh bernilai nol")),
    ZERO_VALUE("err_3", new IllegalArgumentException("Field tidak boleh bernilai nol")),
    INVALID_ID("err_4", new NoSuchElementException("ID atau key tidak ditemukan"));

    private final String code;
    private final RuntimeException exception;

    ValidationErrorCode(String code, RuntimeException exception) {
        this.code = code;
        this.exception = exception;
    }}
