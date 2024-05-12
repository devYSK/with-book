package main;

import java.util.Random;
import java.util.logging.Logger;

public class Producer extends Thread {

  private Logger log = Logger.getLogger(Producer.class.getName());

  @Override
  public void run() {
    Random r = new Random();
    while (true) {
      if (Main.list.size() < 100) {
        int x = r.nextInt();
        Main.list.add(x);
        log.info("Producer " + Thread.currentThread().getName() + " added value " + x);
      }
    }
  }

}
