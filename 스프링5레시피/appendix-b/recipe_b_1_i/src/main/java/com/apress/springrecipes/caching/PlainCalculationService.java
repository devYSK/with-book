package com.apress.springrecipes.caching;

import java.math.BigDecimal;

public class PlainCalculationService implements CalculationService {

    @Override
    public BigDecimal heavyCalculation(BigDecimal base, int power) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}
        return base.pow(power);
    }
}
