package com.ys.practice.rabbit;

import java.util.Arrays;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ys.practice.config.KitchenUI;
import com.ys.practice.domain.Order;
import org.springframework.amqp.core.Message;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderListener {
  
  private final KitchenUI ui;
  private final MessageConverter messageConverter;

  @RabbitListener(queues = "tacocloud.order.queue", ackMode = "NONE")
  public void receiveOrder(Message message) { // Order order

    final var order = (Order)messageConverter.fromMessage(message);

    System.out.println(message.getMessageProperties().toString());

    ui.displayOrder(order);
  }
  
}
