package id.cs.ui.advprog.inthecost.strategy;

import id.cs.ui.advprog.inthecost.enums.KuponStatus;
import id.cs.ui.advprog.inthecost.model.Kupon;

import java.time.LocalDate;

public class DefaultKuponStatusStrategy implements KuponStatusStrategy {
    @Override
    public KuponStatus evaluate(Kupon kupon) {
        if (kupon.getMasaBerlaku().isBefore(LocalDate.now())) {
            return KuponStatus.INVALID;
        }
        return KuponStatus.VALID;
    }
}
