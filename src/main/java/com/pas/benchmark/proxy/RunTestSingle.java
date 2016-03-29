package com.pas.benchmark.proxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunTestSingle extends DynamicProxySingle{
	
	public RunTestSingle(OrderImpl orderImpl, Object[] paras) {
		super(orderImpl, paras);
	}

	public static void main(String[] args) {
		ExecutorService pool = Executors.newFixedThreadPool(5);
		for (int i = 0; i < 2; i++) {
			pool.execute(new RunTestSingle(new OrderImpl(),new String[]{"test input parameter Single"}));
		}
	}

}
