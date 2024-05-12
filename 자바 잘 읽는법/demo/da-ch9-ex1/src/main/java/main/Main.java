package main;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Main {

  private static Logger log = Logger.getLogger(Main.class.getName());

  public static List<Integer> list = new ArrayList<>();

  public static void main(String[] args) {
    try {
      Thread.sleep(10000);

      new Producer("_Producer").start();
      new Consumer("_Consumer").start();
    } catch (InterruptedException e) {
      log.severe(e.getMessage());
    }
  }
}
