package com.example.controller;

import com.example.model.Product;
import com.example.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping("/product/{name}")
  public void saveProduct(@PathVariable String name) {
    productService.saveProduct(name);
  }

  @GetMapping("/product")
  public List<Product> findProducts() {
    return productService.findAll();
  }
}
