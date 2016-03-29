package com.pas.benchmark;

public class RedisImpl implements RedisService{

	@Override
	public String getKey(String key) {
		// TODO Auto-generated method stub
		System.out.println("getKey :"+key);
		return key;
	}

	@Override
	public int modifyKey(String key, String value) {
		// TODO Auto-generated method stub
		System.out.println("modifyKey :"+key);
		return 0;
	}

	@Override
	public int delKey(String key) {
		// TODO Auto-generated method stub
		System.out.println("delKey :"+key);
		return 0;
	}



}
