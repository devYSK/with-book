package com.ys.a.web;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

	@MessageMapping("/echo")
	@SendTo("/topic/echo")
	public String echo(String msg) {
		return "RECEIVED: " + msg;
	}

}
