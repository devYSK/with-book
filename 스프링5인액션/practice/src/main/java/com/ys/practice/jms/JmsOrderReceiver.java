package com.ys.practice.jms;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import com.ys.practice.domain.Order;
import com.ys.practice.service.OrderReceiver;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;

@Profile("activemq")
@Component
public class JmsOrderReceiver implements OrderReceiver {

	private final JmsTemplate jms;

	private final MessageConverter messageConverter;

	@Qualifier("tacoQueue")
	private final Destination destination;

	public JmsOrderReceiver(JmsTemplate jms, MessageConverter messageConverter, Destination destination) {
		this.jms = jms;
		this.messageConverter = messageConverter;
		this.destination = destination;
	}

	@Override
	public Order receiveOrder() {
		final var defaultDestination = jms.getDefaultDestination();
		System.out.println(defaultDestination);
		final var receive = jms.receive(destination);

		try {
			final var order = (Order)messageConverter.fromMessage(receive);
			System.out.println("LGTM");
			System.out.println(order);
			return order;
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}

}
