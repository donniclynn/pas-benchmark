package com.pas.benchmark.proxy;
/**
 * 单例实现方式，代理对象只初始化一次 ，比如OrderImpl只实例化一次
 * @author selingchen
 *
 * @param <T>
 */
public  class DynamicProxySingle<T> implements Runnable {

	private  BasePerformance realObject;//传进来的被代理 的对象实例 ，比如 new OrderImpl()
	
	private  BasePerformance service; //代理实例
	
	private Object[] paras;
	
	public DynamicProxySingle(T realObject,Object[] paras){
		this.realObject = (BasePerformance) realObject;
		this.paras = paras;
		if(null == service){
			SingleProxyInstance.getProxyService((BasePerformance) realObject);
			service = SingleProxyInstance.service;
		}
	}
	
	public void run() {
		while (true) {
			//System.out.println("Start running");
			service.runTest(paras);
		}
	}
	
}
