package com.ys.practice.jms;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.ys.practice.domain.Order;
import com.ys.practice.service.OrderMessagingService;

import jakarta.jms.Destination;

@Profile("activemq")
@Service
public class JmsOrderMessagingService implements OrderMessagingService {

	private final JmsTemplate jms;

	private final Destination orderQueue;

	public JmsOrderMessagingService(JmsTemplate jms,
		@Qualifier("tacoQueue") Destination destination) {
		this.jms = jms;
		this.orderQueue = destination;
	}

	@Override
	public void sendOrder(Order order) {
		System.out.println("lets go!");
		jms.send("taco.queue", session -> session.createObjectMessage(order));
		System.out.println(jms.getDefaultDestination());
	}

	public void sendMessage(String message) {
		System.out.println(LocalDateTime.now());
		jms.convertAndSend("myqueue", message);
	}

}

