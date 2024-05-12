package com.example.services;

import com.example.entities.Product;
import com.example.entities.Purchase;
import com.example.repositories.ProductRepository;
import com.example.repositories.PurchaseRepository;
import org.springframework.stereotype.Service;

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
    Set<String> productNames = new HashSet<>();
    List<Purchase> purchases = purchaseRepository.findAll();
    for (Purchase p : purchases) {
      Product product = productRepository.findById(p.getProduct());
      productNames.add(product.getName());
    }
    return productNames;
  }
}
