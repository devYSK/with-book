package main;

import java.util.logging.Logger;

public class Consumer extends Thread {

  private Logger log = Logger.getLogger(Consumer.class.getName());

  public Consumer(String name) {
    super(name);
  }

  @Override
  public void run() {
    try {
      for (int i = 0; i < 1_000_000; i++) {
        synchronized (Main.list) {
          if (Main.list.size() > 0) {
            int x = Main.list.get(0);
            Main.list.remove(0);
            log.info("Consumer " + Thread.currentThread().getName() + " removed value " + x);
            Main.list.notifyAll();
          } else {
            Main.list.wait();
          }
        }
      }
    } catch (InterruptedException e) {
      log.severe(e.getMessage());
    }
  }
}
