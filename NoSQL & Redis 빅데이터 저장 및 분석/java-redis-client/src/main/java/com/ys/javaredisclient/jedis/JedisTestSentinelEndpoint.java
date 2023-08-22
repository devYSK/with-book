package com.ys.javaredisclient.jedis;

import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.exceptions.JedisException;

public class JedisTestSentinelEndpoint {
	private static final String MASTER_NAME = "mymaster";
	public static final String PASSWORD = "1234";
	private static final Set sentinels;

	static {
		sentinels = new HashSet<>();
		sentinels.add("127.0.0.1:7001");
		sentinels.add("127.0.0.1:7002");
		sentinels.add("127.0.0.1:7003");
	}

	public JedisTestSentinelEndpoint() {

	}

	public static void main(String[] args) throws InterruptedException {
		new JedisTestSentinelEndpoint().runTest();
	}

	private void runTest() throws InterruptedException {
		JedisSentinelPool pool = new JedisSentinelPool(MASTER_NAME, sentinels);
		Jedis jedis = null;
		try {
			printer("Fetching connection from pool");
			jedis = pool.getResource();

			printer("Authentication !!");
			jedis.auth(PASSWORD);

			printer("Authentication Finish...");


			printer("Connected to " + jedis.info());
			printer("Writing...");

			jedis.set("10", "Computing Team");
			printer("Reading...");
			jedis.get("1101");

			jedis.zadd("type", 0, "bentz");
			jedis.zadd("type", 0, "bmw");
			jedis.zrange("type", 0, -1);
		} catch (JedisException e) {
			printer("Connection error of some sort!");
			printer(e.getMessage());
			Thread.sleep(2 * 1_000);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	private static void printer(String str) {
		System.out.println(str);
	}
}
