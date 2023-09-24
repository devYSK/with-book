package com.ys.practice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ys.practice.domain.Order;
import com.ys.practice.service.OrderMessagingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaOrderMessagingService implements OrderMessagingService {
  
  private final KafkaTemplate<String, Order> kafkaTemplate;

  @Override
  public void sendOrder(Order order) {
    kafkaTemplate.send("tacocloud.orders.topic", order);
  }
  
}
