package main;

import java.util.ArrayList;
import java.util.List;

public class Main {

  public static List<Integer> listA = new ArrayList<>();
  public static List<Integer> listB = new ArrayList<>();

  public static void main(String[] args) {
    new Producer("_Producer").start();
    new Consumer("_Consumer").start();
  }
}
