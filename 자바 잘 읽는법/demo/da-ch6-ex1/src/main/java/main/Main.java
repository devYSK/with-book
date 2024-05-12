package main;

import java.util.ArrayList;
import java.util.List;

public class Main {

  public static List<Integer> list = new ArrayList<>();

  public static void main(String[] args) {
    new Producer().start();
    new Producer().start();
    new Consumer().start();
    new Consumer().start();
  }
}
