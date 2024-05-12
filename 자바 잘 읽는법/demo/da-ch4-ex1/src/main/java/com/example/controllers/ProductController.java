package com.example.controllers;

import com.example.model.dtos.TotalCostResponse;
import com.example.services.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping("/total/costs")
  public TotalCostResponse totalCosts() {
    return productService.getTotalCosts();
  }

}
