package main;

import java.util.logging.Logger;

public class Consumer extends Thread {

  private Logger log = Logger.getLogger(Consumer.class.getName());

  @Override
  public void run() {
    while (true) {
      if (Main.list.size() > 0) {
        int x = Main.list.get(0);
        Main.list.remove(0);
        log.info("Consumer " + Thread.currentThread().getName() + " removed value " + x);
      }
    }
  }
}
