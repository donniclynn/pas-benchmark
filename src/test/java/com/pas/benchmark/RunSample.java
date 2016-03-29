package com.pas.benchmark;

import com.pas.benchmark.proxy.RunTest;
import com.pas.benchmark.proxy.RunTestSingle;

/**
 * RedisService 是接口
 * RedisImpl是具体实现类
 * 需求是测试RedisService中的接口方法
 * 1.先开发TestRedis测试程序，必须要实现BasePerformance接口，测试结果记录在SampleResult中
 * 2.在RunSample中开始测试
 * RunTest是迭代器
 * @author selingchen
 *
 */
public class RunSample {

	/**
	 * Object[] paras 格式是：
	 *  paras[0]传入变量				:test input parameter		 入参			必填项
	 *  paras[1]传入运行时长			:100						运行100秒 		必填项
	 *  paras[2]是否记录响应时间标志		:false						不记录响应时间	可选项
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
//		RunTest rt = new RunTest(new TestRedis(),
//				new String[] { "test input parameter","10000"});
//				//new String[] { "test input parameter", 10000 + "", "true" });
//		rt.runTest(rt,10);
		
		RunTestSingle rts = new RunTestSingle(new TestRedis(),
				new String[] { "test input parameter","10000000"});
		rts.runTest(rts, 10);
	}
	
	

}
