package com.pas.benchmark.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

public class ProxyHandler<T> implements InvocationHandler {
	// 实际代理对象
	private T realObject;
	
	public ProxyHandler(T realService) {
		this.realObject = realService;
	}

	@Override
	public Object invoke(Object object, Method method, Object[] args) throws Throwable {
		return (SampleResult) method.invoke(realObject, args);
	}

}
