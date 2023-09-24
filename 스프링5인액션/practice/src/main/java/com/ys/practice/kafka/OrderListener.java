package com.ys.practice.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import com.ys.practice.config.KitchenUI;
import com.ys.practice.domain.Order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// @Profile("kafka-listener")
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderListener {

	private final KitchenUI ui;

	@KafkaListener(topics = "tacocloud.orders.topic")
	public void handle(Order order, ConsumerRecord<String, Order> record) {
		log.info("Received from partition {} with timestamp {}",
			record.partition(), record.timestamp());

		ui.displayOrder(order);
	}

	//
	// Alternate implementation
	//
	@KafkaListener(topics = "tacocloud.orders.topic")
	public void handle(Order order, Message<Order> message) {
		MessageHeaders headers = message.getHeaders();
		log.info("Received from partition {} with timestamp {}",
			headers.get(KafkaHeaders.RECEIVED_PARTITION),
			headers.get(KafkaHeaders.RECEIVED_TIMESTAMP));
		ui.displayOrder(order);
	}

}
