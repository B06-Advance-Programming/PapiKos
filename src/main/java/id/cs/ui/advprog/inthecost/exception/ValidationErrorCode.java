package id.cs.ui.advprog.inthecost.exception;

public enum ValidationErrorCode {
    NULL_VALUE("err_0", "Field tidak boleh nulll"),
    EMPTY_VALUE("err_1", "Field tidak boleh empty"),
    NULL_OR_EMPTY_VALUE("err_2", "Field tidak boleh null atau kosong."),
    NEGATIVE_VALUE("err_3", "Field tidak boleh bernilai negatif."),
    ZERO_VALUE("err_4", "Field tidak boleh bernilai 0."),
    ZERO_OR_NEGATIVE_VALUE("err_5", "Field harus bernilai positif"),
    INVALID_ID("err_6", "ID atau key tidak ditemukan."),
    INVALID_CODE("err_7", "Code tidak ditemukan.");

    private final String code;
    private final String message;

    ValidationErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}