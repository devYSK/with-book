package com.ys.practice.web;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ys.practice.domain.Order;
import com.ys.practice.domain.Taco;
import com.ys.practice.domain.User;
import com.ys.practice.jms.JmsOrderMessagingService;
import com.ys.practice.rabbit.RabbitOrderMessagingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

	// private final JmsOrderMessagingService service;

	private final RabbitOrderMessagingService rabbit;
	//
	// @GetMapping("/test")
	// @Transactional
	// public int test() {
	// 	User user = new User("asdfs", "asdfasdf", "asdfasdf", "adsf", "adf", "asdf", "adsfa", "01012345678"); // 가정: User 클래스에는 기본 생성자가 있습니다.
	// 	// 여기서 필요한 경우 User 객체의 속성을 설정할 수 있습니다.
	//
	// 	Taco taco1 = new Taco(); // 가정: Taco 클래스에는 기본 생성자가 있습니다.
	// 	// taco1에 대한 속성 설정 (예: 이름, 재료 등)
	//
	// 	Taco taco2 = new Taco();
	// 	// taco2에 대한 속성 설정
	//
	// 	Order order = new Order();
	// 	order.setUser(user);
	// 	order.setDeliveryName("John Doe");
	// 	order.setDeliveryStreet("123 Main St");
	// 	order.setDeliveryCity("Spring City");
	// 	order.setDeliveryState("SP");
	// 	order.setDeliveryZip("12345");
	// 	order.setCcNumber("4111111111111111");
	// 	order.setCcExpiration("12/23");
	// 	order.setCcCVV("123");
	// 	order.addDesign(taco1);
	// 	order.addDesign(taco2);
	//
	// 	service.sendOrder(order);
	// 	return 1;
	// }
	// @GetMapping
	// @Transactional
	// public int test(@RequestParam("m") String message) {
	//
	// 	service.sendMessage(message);
	//
	// 	return 1;
	// }

	@GetMapping("/2")
	public int test2() {
		User user = new User("asdfs", "asdfasdf", "asdfasdf", "adsf", "adf", "asdf", "adsfa", "01012345678"); // 가정: User 클래스에는 기본 생성자가 있습니다.
		// 여기서 필요한 경우 User 객체의 속성을 설정할 수 있습니다.

		Taco taco1 = new Taco(); // 가정: Taco 클래스에는 기본 생성자가 있습니다.
		// taco1에 대한 속성 설정 (예: 이름, 재료 등)

		Taco taco2 = new Taco();
		// taco2에 대한 속성 설정

		Order order = new Order();
		order.setUser(user);
		order.setDeliveryName("John Doe");
		order.setDeliveryStreet("123 Main St");
		order.setDeliveryCity("Spring City");
		order.setDeliveryState("SP");
		order.setDeliveryZip("12345");
		order.setCcNumber("4111111111111111");
		order.setCcExpiration("12/23");
		order.setCcCVV("123");
		order.addDesign(taco1);
		order.addDesign(taco2);

		rabbit.sendOrder(order);

		return 1;
	}
}
