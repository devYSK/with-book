package com.example.controllers;

import com.example.proxy.DemoProxy;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DemoController {

  static Logger log = org.slf4j.LoggerFactory.getLogger(DemoController.class);

  private final DemoProxy demoProxy;

  public DemoController(DemoProxy demoProxy) {
    this.demoProxy = demoProxy;
  }

  @GetMapping("/demo")
  public void demo() {
    log.info("threadName");
    demoProxy.delay(5);

    log.info("end");
  }
}
