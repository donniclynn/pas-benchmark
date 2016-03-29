package com.pas.benchmark.proxy;

import java.util.concurrent.TimeUnit;
/**
 * 示例程序：需要继承BasePerformance，在runTest类中编写被测试程序
 * args为入参，支持传入多个对象
 * @author selingchen
 *
 */
public class OrderImpl implements BasePerformance{


	@Override
	public SampleResult runTest(Object[] args) {

		SampleResult result = new SampleResult();
		result.setStart(true);
		try {
			TimeUnit.MILLISECONDS.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result.setEnd(true);
		result.setOK(false);
		//System.out.println("hahaha");
		//System.out.println(args[0]);
		return result;
	}

}
