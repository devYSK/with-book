package main;

import java.util.logging.Logger;

public class Consumer extends Thread {

  private Logger log = Logger.getLogger(Consumer.class.getName());

  public Consumer(String name) {
    super(name);
  }

  @Override
  public void run() {
    while (true) {
      synchronized (Main.listA) {

        synchronized (Main.listB) {
          work();
        }
      }
    }
  }

  private void work() {
    if (Main.listA.size() > 0) {
      int x = Main.listA.get(0);
      Main.listA.remove(0);
      log.info("Consumer " + Thread.currentThread().getName() + " removed value " + x + " from list A");
    } else {
      int x = Main.listB.get(0);
      Main.listB.remove(0);
      log.info("Consumer " + Thread.currentThread().getName() + " removed value " + x + " from list B");
    }
  }
}
