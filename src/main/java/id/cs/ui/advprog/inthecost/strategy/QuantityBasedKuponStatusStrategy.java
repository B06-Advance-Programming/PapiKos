package id.cs.ui.advprog.inthecost.strategy;

import id.cs.ui.advprog.inthecost.enums.KuponStatus;
import id.cs.ui.advprog.inthecost.model.Kupon;

public class QuantityBasedKuponStatusStrategy implements KuponStatusStrategy{
    @Override
    public KuponStatus evaluate (Kupon kupon){
        if(kupon.getQuantity() <= 0){
            return KuponStatus.INVALID;
        }
        else{
            return KuponStatus.VALID;
        }
    }
}
