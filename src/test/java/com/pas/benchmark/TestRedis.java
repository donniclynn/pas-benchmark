package com.pas.benchmark;

import java.util.concurrent.TimeUnit;

import com.pas.benchmark.proxy.BasePerformance;
import com.pas.benchmark.proxy.SampleResult;

public class TestRedis implements BasePerformance {

	@Override
	public SampleResult runTest(Object[] args) {
		SampleResult result = new SampleResult();
		RedisService ri = new RedisImpl();
		result.setStart(true);
		try {
			TimeUnit.MILLISECONDS.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ri.getKey(args[0].toString());
		result.setEnd(true);
		//System.out.println("hahaha");
		//System.out.println(args[0]);
		return result;
	}

}
