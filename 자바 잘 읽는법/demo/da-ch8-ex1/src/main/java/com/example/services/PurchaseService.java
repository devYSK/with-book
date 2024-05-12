package com.example.services;

import com.example.model.Product;
import com.example.model.Purchase;
import com.example.repositories.ProductRepository;
import com.example.repositories.PurchaseRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PurchaseService {

  private final ProductRepository productRepository;
  private final PurchaseRepository purchaseRepository;

  public PurchaseService(ProductRepository productRepository,
                         PurchaseRepository purchaseRepository) {
    this.productRepository = productRepository;
    this.purchaseRepository = purchaseRepository;
  }

  public Set<String> getProductNamesForPurchases() {
    try {
      Set<String> productNames = new HashSet<>();
      List<Purchase> purchases = purchaseRepository.findAll();
      for (Purchase p : purchases) {
        Product product = productRepository.findProduct(p.getProduct());
        productNames.add(product.getName());
      }
      return productNames;
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return Set.of();
  }
}
