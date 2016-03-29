package com.pas.benchmark.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 代理对象一个线程实例化一次 ，比如每个线程持有一个OrderImpl实例对象
 * 
 * @author selingchen
 *
 * @param <T>
 */
public class DynamicProxy<T> implements Runnable {

	private static AtomicLong totalfailRecords = new AtomicLong(0); // 操作失败的记录数
	private static AtomicLong totalsuccessRecords = new AtomicLong(0); // 操作成功的记录数
	private static Long thk = 10L;// think time 10ms
	private static long startTime; // 测试开始时间
	private static long currentTime;// 当前线程当前运行时刻
	private static Vector  vts = new Vector();//记录响应时间，长时间运行时不建议用，会产生大对象导致内存溢出
	
	private BasePerformance realObject;// 传进来的被代理 的对象实例 ，比如 new OrderImpl()

	private BasePerformance service; // 代理实例

	private Object[] paras;

	public DynamicProxy(T realObject, Object[] paras) {
		this.realObject = (BasePerformance) realObject;
		this.paras = paras;
	}

	public void run() {
		this.getService();
		this.startTime = System.currentTimeMillis();// 设置当前线程的开始时间
		while (true) {
			SampleResult result = ((BasePerformance) service).runTest(paras);
			if (null != result && !result.isOK()) {
				totalfailRecords.incrementAndGet();
			} else {
				totalsuccessRecords.incrementAndGet();
				if(null != paras && paras.length >=3 && paras[2].equals("true"))
					vts.add(result.getEndTime() - result.getStartTime());
			}
			this.currentTime = System.currentTimeMillis();
			if (((this.currentTime - this.startTime) / 1000) >= Integer.parseInt(paras[1].toString())) // 超过预订执行时长后停止线程
				Thread.currentThread().stop();
			if (thk > 0) {
				try {
					TimeUnit.MILLISECONDS.sleep(thk);// think time
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private BasePerformance getService() {
		InvocationHandler handler = new ProxyHandler((BasePerformance) realObject);
		service = (BasePerformance) Proxy.newProxyInstance(BasePerformance.class.getClassLoader(),
				new Class<?>[] { BasePerformance.class }, handler);
		// System.out.println(service.getClass().getName());
		return service;
	}

	public static AtomicLong getTotalfailRecords() {
		return totalfailRecords;
	}

	public static AtomicLong getTotalsuccessRecords() {
		return totalsuccessRecords;
	}

	public static Long getThk() {
		return thk;
	}

	public static void setThk(Long thk) {
		DynamicProxy.thk = thk;
	}

	public static long getStartTime() {
		return startTime;
	}

	public static long getCurrentTime() {
		return currentTime;
	}
	
	public static Vector getVts() {
		return vts;
	}
}