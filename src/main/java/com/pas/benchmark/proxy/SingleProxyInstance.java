package com.pas.benchmark.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public  class SingleProxyInstance{
	
	private static SingleProxyInstance proxyService;

	public static BasePerformance service ;
	
	private SingleProxyInstance(){
		
	}
	
	public  static synchronized SingleProxyInstance getProxyService(BasePerformance realObject){
		if(null == proxyService){
			proxyService = new SingleProxyInstance();
			service = proxyService.getService((BasePerformance) realObject);
			System.out.println("In SignProxyInstance ");
		}
		return proxyService;
	}
	
	private BasePerformance getService(BasePerformance realObject) {
		InvocationHandler handler = new ProxyHandler((BasePerformance) realObject);
		service = (BasePerformance) Proxy.newProxyInstance(BasePerformance.class.getClassLoader(),
				new Class<?>[] { BasePerformance.class }, handler);		
		System.out.println(service.getClass().getName());
		System.out.println("In singleProxyInstance");
		return service;
	}
	
	
}