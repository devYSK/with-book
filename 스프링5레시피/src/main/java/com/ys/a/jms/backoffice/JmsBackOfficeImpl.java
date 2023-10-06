package com.ys.a.jms.backoffice;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.JmsUtils;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JmsBackOfficeImpl implements BackOffice {

	private final JmsTemplate jmsTemplate;
	private final Destination destination;

	public Mail receiveMail() {
		MapMessage message = (MapMessage)jmsTemplate.receive(destination);
		try {

			if (message == null) {
				return null;
			}

			Mail mail = new Mail();
			mail.setMailId(message.getString("mailId"));
			mail.setCountry(message.getString("country"));
			mail.setWeight(message.getDouble("weight"));
			return mail;
		} catch (JMSException e) {
			throw JmsUtils.convertJmsAccessException(e);
		}
	}

}
