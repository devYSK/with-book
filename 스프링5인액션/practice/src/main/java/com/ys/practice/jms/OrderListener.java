package com.ys.practice.jms;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.ys.practice.config.KitchenUI;
import com.ys.practice.domain.Order;

import lombok.RequiredArgsConstructor;

@Profile("activemq")
@Component
@RequiredArgsConstructor
public class OrderListener {

	private final KitchenUI ui;

	@JmsListener(destination = "taco.queue")
	public void receiveOrder(Order order) {
		ui.displayOrder(order);
	}

	@JmsListener(destination = "myqueue")
	public void receive(String string) {
		System.out.println(LocalDateTime.now());
		System.out.println("message : " + string);
	}

}
