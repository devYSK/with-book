package com.ys.a.jms.backoffice;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class RabbitMailListener {

    @RabbitListener(queues = "mail.queue")
    public void displayMail(Mail mail) {
        System.out.println("Received: " + mail);
    }

}
