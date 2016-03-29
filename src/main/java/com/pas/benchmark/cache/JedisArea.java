package com.pas.benchmark.cache;

import redis.clients.jedis.Jedis;
/**
 * 直接连接jedis，连通测试
 * @author thinkpad
 *
 */
public class JedisArea {
	private Jedis jedis;

	public static void main(String[] args){
		JedisArea ja = new JedisArea();
		ja.setKey();
	}
	
	
	public JedisArea() {
		jedis = new Jedis("10.142.90.26",12581);
	}


	private void setKey() {

		String str = jedis.set("test001","test001Value");
		String str02 = jedis.get("test001");
	
		System.out.println(str);
		System.out.println(str02);
	}

}
