package com.ys.practice.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import com.ys.practice.domain.Order;
import com.ys.practice.service.OrderReceiver;

@Profile("rabbit")
@Component
public class RabbitOrderReceiver implements OrderReceiver {

	private RabbitTemplate rabbit;

	public RabbitOrderReceiver(RabbitTemplate rabbit) {
		this.rabbit = rabbit;
	}

	public Order receiveOrder() {
		return rabbit.receiveAndConvert("tacocloud.order.queue", 30000,
			new ParameterizedTypeReference<Order>() {
			});
	}

}
