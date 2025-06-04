package id.cs.ui.advprog.inthecost.strategy;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class KuponStatusStrategyManagement {
    @Getter
    private static final List<KuponStatusStrategy> strategies = new ArrayList<>();

    static {
        registerStrategy(new DefaultKuponStatusStrategy());
        registerStrategy(new QuantityBasedKuponStatusStrategy());
    }

    public static void registerStrategy(KuponStatusStrategy strategy){
        strategies.add(strategy);
    }
}
