package main;

import model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

  private static List<Product> products = new ArrayList<>();

  public static void main(String[] args) {
    Random r = new Random();
    while (true) {
      Product p =new Product();
      p.setName("Product " + r.nextInt());
      products.add(p);
    }
  }
}
