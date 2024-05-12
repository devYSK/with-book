package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

  public static List<Cat> list = new ArrayList<>();

  public static void main(String[] args) {
    while(true) {
      list.add(new Cat(new Random().nextInt(10)));
    }
  }
}
