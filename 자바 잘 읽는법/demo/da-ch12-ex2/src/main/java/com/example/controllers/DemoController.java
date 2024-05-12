package com.example.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

  @GetMapping
  public void throwException() {
    throw new RuntimeException("Oh No!");
  }
}
