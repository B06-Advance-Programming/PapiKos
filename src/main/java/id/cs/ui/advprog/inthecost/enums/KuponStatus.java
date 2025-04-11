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

    public static KuponStatus evaluate(Kupon kupon) {
        if (kupon.getMasaBerlaku().isBefore(LocalDate.now())) {
            return INVALID;
        } else {
            return VALID;
        }
    }
}
