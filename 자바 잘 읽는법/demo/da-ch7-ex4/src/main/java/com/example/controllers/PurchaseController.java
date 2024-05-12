package com.example.controllers;

import com.example.services.PurchaseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class PurchaseController {

  private final PurchaseService purchaseService;

  public PurchaseController(PurchaseService purchaseService) {
    this.purchaseService = purchaseService;
  }

  @GetMapping("/products")
  public Set<String> findPurchasedProductNames() {
    return purchaseService.getProductNamesForPurchases();
  }
}
