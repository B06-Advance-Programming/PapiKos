package id.cs.ui.advprog.inthecost.enums;

import id.cs.ui.advprog.inthecost.model.Kupon;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public enum KuponStatus {
    VALID("VALID"),
    INVALID("INVALID");

    private final String value;

    KuponStatus(String value) {
        this.value = value;
    }
}
