package com.pas.benchmark.cache;

import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import com.ctg.itrdc.cache.common.exception.CacheConfigException;
import com.ctg.itrdc.cache.core.CacheService;
/**
 * 调试程序（相比CacheTestSummary去掉了从控制台获取参数部分）
 * @author thinkpad
 *
 */
public class CacheTestSummary2 {
	private static final  Logger log  =  Logger.getLogger(CacheTestSummary2.class);
	private static AtomicLong totalfailRecords = new AtomicLong(0); // 操作失败的记录数
	private static AtomicLong totalsuccessRecords = new AtomicLong(0); // 操作成功的记录数
	private static long startTime; // 测试开始时间
	private static long endTime; // 测试结束时间
	private static int totalTime = 300; // 执行时长
	private static int threadCount = 5; // 线程数
	private static int interval = 5000;// 毫秒为单位
	private static long preOKTrans = 0L;// 上一次计算时的成功事务数
	private static long preFailTrans = 0L;// 上一次计算时的失败事务数
	private static int testValueSize = 10240; // 测试的value大小(B)
	private static String operationType = "set"; // 操作类型
	private static String testValue = "71342127-9820-4877-8ed4-effbc24d4e9"; // 测试的value值
	private static String searchKey = "71342127-9820-4877-8ed4-effbc24d4e9";//默认查找Key
	private static String groupId = "group.test.024"; // GroupId
	private static String sourceStr = "abcdefghijklmnopqrstuvwxyz123567890";
	private static String rate = "20@1@1"; // 读增删默认比例20：1：1
	private static long tmp = 0L;
	private static Vector vts = new Vector();//响应时间  格式：运行时刻 @响应时间  
	
	public static void main(String[] args) {
		testValue = getStringByBytes(testValueSize);// 生成随机Key,测试时Key变Value固定不变
		startTime = System.currentTimeMillis();
		// 间隔计算TPS
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				// TODO Auto-generated method stub
				tmp = totalsuccessRecords.get() - preOKTrans;
				long tps = tmp / (interval / 1000);
				preOKTrans = totalsuccessRecords.get();
				preFailTrans = totalfailRecords.get();
				System.out.println("Current TPS: " + tps + "|| successs trans: " 
				+ preOKTrans + "|| fail trans: " + preFailTrans
				+"|| this interval trans: "+tmp);
				while (System.currentTimeMillis() - startTime >= totalTime * 1000){
					art();
					System.exit(0);
				}
			}
		}, 5000, interval);
		//开启线程进行测试
		for (int i = 0; i < threadCount; i++) {
			new Thread(new CacheTestSummary2().new T(groupId,searchKey,rate)).start();//testValue仅在get时作为Key查询
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				endTime = System.currentTimeMillis();
				long du = (endTime - startTime);
				log.info("========================================================");
				log.info("finshed,run time(s): " + (endTime - startTime) / 1000);
				log.info("success Trans count: " + totalsuccessRecords.get());
				log.info("fail Trans count: " + totalfailRecords.get());
				log.info("Avg TPS: " + totalsuccessRecords.get() / (du / 1000));
				log.info("========================================================");
			}
		});
		printOpeartion();
		log.info("it's running,please wait for completion !");
	}

	public static String getStringByBytes(int length) {
		return RandomStringUtils.random(length, sourceStr);
	}

	public static String getKey() throws Exception {
		return UUID.randomUUID().toString();
	}

	public static void art(){
		if(null != vts && vts.size() > 0){
			long min = 0L;
			long max = 0L;
			long temp = 0L;
			long sum = 0L;
			Enumeration e = vts.elements();
			min = Long.valueOf(vts.get(0).toString());
			max = min;
			int i =0;
			while (e.hasMoreElements()) {
				temp = Long.valueOf(e.nextElement().toString());
				if(temp<min)
					min = temp;
				if(temp>max)
					max = temp;
				sum+= temp;
			}
//			log.info("========================================================");
//			log.info("Min rt: "+min+" Max rt: "+max+" ART: "+sum/vts.size());
//			log.info("Min rt: "+min+" Max rt: "+max+" ART: "+sum/vts.size());
//			log.info("========================================================");
			log.info("========================================================");
			log.info("Min rt: "+min+" Max rt: "+max+" ART: "+sum/vts.size());
			log.info("========================================================");
		}
	}
	
	private static void printOpeartion() {
		log.info("========================================================");
		log.info("current run time(s) : " + totalTime);
		log.info("current thread count: " + threadCount);
		log.info("current value bytes: " + testValueSize);
		log.info("current operation type: " + operationType);
		log.info("========================================================");
	}

	class T implements Runnable {
		private boolean isrunning = true;
		private CacheService ser;
		private String groupId;
		private int i = 1;
		private String key = "test";
		private String rate = "20@1@1";

		public T(String groupId, String key, String rate) {
			this.groupId = groupId;
			this.key = key;
			this.rate = rate;
		}

		@SuppressWarnings("unchecked")
		public void run() {
			try {
				ser = new CacheService();
				int total = totalTime * 1000;
				long threadstart = System.currentTimeMillis();
				long threadend = 0L;
				long offset = 0L;
				String[] rate = this.rate.split("@");
				String result = "0000";
				long rs = 0L;
				long t1,t2;
				while (isrunning) {
					// 混合场景
					if (operationType.equals("get")) {
						t1 = System.currentTimeMillis();
						result = ser.get(this.groupId, this.key);
						t2 = System.currentTimeMillis();
						if (null == result)
							totalfailRecords.incrementAndGet();
						else if (null != result && !this.key.equals(result)){//Key与Value一样，为了方便测试
							totalfailRecords.incrementAndGet();
							log.info("this key get error value ,key :"+this.key+" value : "+result);
						}else{
							totalsuccessRecords.incrementAndGet();
							vts.add(t2-t1);
						}
					} else if (operationType.equals("set")) {
						t1 = System.currentTimeMillis();
						//log.info(testValue);
						result = ser.set(this.groupId, getKey(), testValue);
						t2 = System.currentTimeMillis();
						if ("0".endsWith(result))
							totalsuccessRecords.incrementAndGet();
						else{
							totalfailRecords.incrementAndGet();
							log.info(result);
							vts.add(t2-t1);
						}
					} else if (null != rate && rate.length > 1 && operationType.equals("mul")) {// mul，混全场景20:1:1
						t1 = System.currentTimeMillis();
						result = ser.get(this.groupId, this.key);// 读
						t2 = System.currentTimeMillis();
						if (null == result)
							totalfailRecords.incrementAndGet();
						else if (null != result && !this.key.equals(result))
							totalfailRecords.incrementAndGet();
						else{
							totalsuccessRecords.incrementAndGet();
							vts.add(t2-t1);
						}
						if (i % (Integer.valueOf(rate[0]) / Integer.valueOf(rate[1])) == 0) {
							String tmpKey = getKey();
							t1 = System.currentTimeMillis();
							result = ser.set(this.groupId, tmpKey,testValue);// 写
							t2 = System.currentTimeMillis();
							if ("0".endsWith(result))
								totalsuccessRecords.incrementAndGet();
							else{
								totalfailRecords.incrementAndGet();
								vts.add(t2-t1);
							}
							t1 = System.currentTimeMillis();
							rs = ser.del(this.groupId, tmpKey); // 删
							t2 = System.currentTimeMillis();
							if (rs<1)
								totalfailRecords.incrementAndGet();
							else{
								totalsuccessRecords.incrementAndGet();
								vts.add(t2-t1);
							}
						}
					}
					i++;
					threadend = System.currentTimeMillis();
					offset = threadend - threadstart;
					if (offset >= total) {
						isrunning = false;
						Thread.currentThread().stop();
					}
					TimeUnit.MILLISECONDS.sleep(100L);//think time
				}
			} catch (CacheConfigException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				totalfailRecords.incrementAndGet();
			}
		}
	}
}
