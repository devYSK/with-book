package controllers;

import org.springframework.stereotype.Component;

import services.ProductService;

@Component
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  public void saveProduct(String name) {
    productService.saveProduct(name);
  }

}
