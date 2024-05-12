package com.example.model.dtos;

import java.math.BigDecimal;
import java.util.Map;

public class TotalCostResponse {

  private Map<String, BigDecimal> totalCosts;

  public Map<String, BigDecimal> getTotalCosts() {
    return totalCosts;
  }

  public void setTotalCosts(Map<String, BigDecimal> totalCosts) {
    this.totalCosts = totalCosts;
  }
}
