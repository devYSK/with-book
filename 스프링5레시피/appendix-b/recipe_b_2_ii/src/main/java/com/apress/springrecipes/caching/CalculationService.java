package com.apress.springrecipes.caching;

import java.math.BigDecimal;

public interface CalculationService {

    BigDecimal heavyCalculation(BigDecimal base, int power);
}
