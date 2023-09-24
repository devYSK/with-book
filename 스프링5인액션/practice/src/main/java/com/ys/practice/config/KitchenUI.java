package com.ys.practice.config;

import org.springframework.stereotype.Component;

import com.ys.practice.domain.Order;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KitchenUI {

  public void displayOrder(Order order) {
    log.info("RECEIVED ORDER:  " + order);
  }
  
}
