package com.ys.a.jms.frontdesk;

import javax.jms.Destination;
import javax.jms.MapMessage;

import org.springframework.jms.core.JmsTemplate;

public class JmsFrontDeskImpl implements FrontDesk {

    private JmsTemplate jmsTemplate;
    private Destination destination;

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public void sendMail(final Mail mail) {

        jmsTemplate.send(destination,
            session -> {
            MapMessage message = session.createMapMessage();
            message.setString("mailId", mail.getMailId());
            message.setString("country", mail.getCountry());
            message.setDouble("weight", mail.getWeight());
            return message;
        });
    }

}
