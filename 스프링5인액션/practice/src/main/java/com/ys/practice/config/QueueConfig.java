package com.ys.practice.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.util.ErrorHandler;

import com.ys.practice.domain.Order;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import lombok.RequiredArgsConstructor;

@EnableJms
@Configuration
public class QueueConfig {

	@Profile("activemq")
	@Bean("tacoQueue")
	public Destination orderQueue() {
		return new ActiveMQQueue("taco.queue");
	}

	@Profile("activemq")
	@Bean
	public MappingJackson2MessageConverter messageConverter() {
		MappingJackson2MessageConverter messageConverter =
			new MappingJackson2MessageConverter();

		messageConverter.setTypeIdPropertyName("_typeId");

		Map<String, Class<?>> typeIdMappings = new HashMap<>();

		typeIdMappings.put("order", Order.class);

		messageConverter.setTypeIdMappings(typeIdMappings);

		return messageConverter;
	}

	public static class JmsErrorHandler implements ErrorHandler {

		@Override
		public void handleError(Throwable t) {
			System.out.println("handler error " + t.getMessage());
		}

	}

	// @Bean
	// public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory, CustomMessageConverter customMessageConverter) {
	// 	JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
	// 	jmsTemplate.setMessageConverter(customMessageConverter);
	// 	return jmsTemplate;
	// }
	//
	// @Bean
	// public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory, CustomMessageConverter customMessageConverter) {
	// 	DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
	// 	factory.setConnectionFactory(connectionFactory);
	// 	factory.setMessageConverter(customMessageConverter);
	// 	return factory;
	// }



	// @RequiredArgsConstructor
	// public static class CustomerMessageConverter implements MessageConverter {
	//
	// 	private MappingJackson2MessageConverter jacksonConverter = new MappingJackson2MessageConverter();
	// 	private SimpleMessageConverter simpleConverter = new SimpleMessageConverter();
	//
	//
	//
	//
	// 	@Override
	// 	public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
	// 		return null;
	// 	}
	//
	// 	@Override
	// 	public Object fromMessage(Message message) throws JMSException, MessageConversionException {
	// 		if (isSomeSpecialCondition(message)) {
	// 			return jacksonConverter.fromMessage(message);
	// 		} else {
	// 			return simpleConverter.fromMessage(message);
	// 		}
	// 	}
	//
	// 	private boolean isSomeSpecialCondition(Message message) {
	// 		// 조건을 기반으로 메시지를 검사하여 어느 변환기를 사용할지 결정
	// 		// 예: 메시지 헤더나 특정 프로퍼티를 검사
	// 		return false; // 예시 코드
	// 	}
	// }

}
