package com.ys.javaredisclient.jedis;

import redis.clients.jedis.Jedis;

public class JedisTest {

	public static void main(String[] args) {
		Jedis jedis = new Jedis("127.0.0.1", 7001);
		jedis.set("10", "Computing Team");
		String value = jedis.get("10");

		System.out.println(value);
	}
}
