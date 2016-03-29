package com.pas.benchmark;

public interface RedisService {
	
	public String getKey(String key);
	
	public int modifyKey(String key,String value);
	
	public int delKey(String key);

}
