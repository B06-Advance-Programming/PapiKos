package id.cs.ui.advprog.inthecost.strategy;

import id.cs.ui.advprog.inthecost.enums.KuponStatus;
import id.cs.ui.advprog.inthecost.model.Kupon;

public interface KuponStatusStrategy {
    KuponStatus evaluate(Kupon kupon);
}
