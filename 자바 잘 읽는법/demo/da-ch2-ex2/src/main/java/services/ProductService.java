package services;

import org.springframework.stereotype.Service;

@Service
public class ProductService {

  public void saveProduct(String name) {
    System.out.println("Saving product " + name);
  }

}
