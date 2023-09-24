package com.ys.practice.service;

import com.ys.practice.domain.Order;

public interface OrderMessagingService {

	void sendOrder(Order order);

}
