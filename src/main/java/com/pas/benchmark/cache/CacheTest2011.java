package com.pas.benchmark.cache;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.RandomStringUtils;
import com.ctg.itrdc.cache.core.CacheService;

/**
 * 按读写删的比例测试TPS  不记录事务响应时间（响应时间记录在数组中，跑稳定性内存溢出，此类中去掉了），测试稳定性时用此类
 * 
 * @author thinkpad
 **/
public class CacheTest2011 {
	private static AtomicLong totalfailRecords = new AtomicLong(0); // 操作失败的记录数
	private static AtomicLong totalsuccessRecords = new AtomicLong(0); // 操作成功的记录数
	private static long startTime; // 测试开始时间
	private static long endTime; // 测试结束时间
	private static int totalTime = 60; // 操作的时间
	private static int threadCount = 1; // 线程数
	private static int testValueSize = 32; // 测试的value大小(B)
	private static String operationType = "get"; // 操作类型
	private static String testValue = "81342127-9820-4877-8ed4-effbc24d4e9"; // 测试的value值
	private static int len = 0; // 计数长度
	private static String groupId = "group.test.025"; // GroupId
	private static String zkUrl = "10.142.90.30:36381"; // ZkUrl
	private static String sourceStr = "abcdefghijklmnopqrstuvwxyz123567890";
	private static String rate = "20@1@1"; // 读增删默认比例20：1：1
	
	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("t", "thread", true, "thread count default:2");
		options.addOption("r", "runtime", true, "run time(s)  default:30");
		options.addOption("o", "operate", true, "operation type (get,set,mul)  default:get");
		options.addOption("s", "vsize", true, "total bytes by value  default:32 byte");
		options.addOption("h", "help", false, "help information");
		options.addOption("k", "key", true, "key info");
		options.addOption("z", "zkUrlPort", true, "zk host&port");
		options.addOption("g", "groupIdG", true, "groupId");
		options.addOption("rt", "rate", true, "rate default 20:1:1");
		CommandLineParser parser = new PosixParser();
		HelpFormatter formatter = new HelpFormatter();

		try {
			CommandLine line = parser.parse(options, args);
			if (line.hasOption("help")) {
				formatter.printHelp("bechmark", options);
				System.exit(0);
			}
			parse(line);// 读取命令行参数
			if ("set".equals(line.hasOption("operate")))
				testValue = getStringByBytes(testValueSize);// 生成随机Key
			startTime = System.currentTimeMillis();
			for (int i = 0; i < threadCount; i++) {
				new Thread(new CacheTest2011().new T(totalTime, zkUrl, groupId, testValue, rate)).start();// set
			}
			if (System.currentTimeMillis() - startTime > totalTime * 1000) {
				System.out.println("========================================================");
				System.out.println("success records count: " + totalsuccessRecords.get());
				System.out.println("fail records count: " + totalfailRecords.get());
				System.out.println("TPS: " + totalsuccessRecords.get() / (totalTime));
				System.out.println("========================================================");
				System.exit(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			formatter.printHelp("bechmark", options);
			System.exit(0);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				endTime = System.currentTimeMillis();
				long du = (endTime - startTime);
				System.out.println("finshed,total time(ms): " + (endTime - startTime));
				System.out.println("========================================================");
				System.out.println("success records count: " + totalsuccessRecords.get());
				System.out.println("fail records count: " + totalfailRecords.get());
				System.out.println("TPS: " + totalsuccessRecords.get() / (du / 1000));
			}
		});
		printOpeartion();
		System.out.println("it's running,please wait for completion !");
	}

	private static void parse(CommandLine line) {

		if (line.hasOption("thread")) {
			String tms = line.getOptionValue("thread");// 读取的参数为字符串
			threadCount = Integer.parseInt(tms);
		}
		if (line.hasOption("runtime")) {
			String tms = line.getOptionValue("runtime");// 读取的参数为字符串
			totalTime = Integer.parseInt(tms);
		}
		if (line.hasOption("operate")) {
			operationType = line.getOptionValue("operate");// 读取的参数为字符串
		}
		if (line.hasOption("vsize")) {
			String value = line.getOptionValue("vsize");// 读取的参数为字符串
			testValueSize = Integer.parseInt(value);
		}
		if (line.hasOption("key")) {
			String key = line.getOptionValue("key");// 获取查询的Key
			if (null != key && key.length() > 4)
				testValue = key;
		}
		if (line.hasOption("zkUrlPort")) {
			zkUrl = line.getOptionValue("zkUrlPort");// 获取查询的Key
		}
		if (line.hasOption("groupIdG")) {
			groupId = line.getOptionValue("groupIdG");// 获取查询的Key
		}
		if (line.hasOption("rate")) {
			rate = line.getOptionValue("rate");// 获取查询的Key
		}
	}

	public static String getStringByBytes(int length) {
		return RandomStringUtils.random(length, sourceStr);
	}

	public static String getKey() throws Exception {
		return UUID.randomUUID().toString();
	}

	private static void printOpeartion() {
		System.out.println("current run time(s) : " + totalTime);
		System.out.println("current thread count: " + threadCount);
		System.out.println("current value bytes: " + testValueSize);
		System.out.println("current operation type: " + operationType);
	}

	class T implements Runnable {
		private int runtime;
		private boolean isrunning = true;
		private CacheService ser;
		private String groupId;
		private int i = 1;
		private String zkUrl;
		private String key = "test";
		private String rate = "20@1@1";
		private Long rs = 0L;

		public T(int runtime, String zkUrl, String groupId, String key, String rate) {
			this.runtime = runtime;
			this.groupId = groupId;
			this.zkUrl = zkUrl;
			this.key = key;
			this.rate = rate;
		}

		public void run() {
			try {
				ser = new CacheService();
				int total = runtime * 1000;
				long threadstart = System.currentTimeMillis();
				String[] rate = this.rate.split("@");
				String result = "0000";
				String keyValue = getStringByBytes(testValueSize);
				while (isrunning) {
					// 混合场景时用
					if (operationType.equals("get")) {
						result = ser.get(this.groupId, this.key);
						if (result == null)
							totalfailRecords.incrementAndGet();
						else
							totalsuccessRecords.incrementAndGet();
					} else if (operationType.equals("set")) {
						result = ser.set(this.groupId, getKey(),keyValue);
						if ("0".endsWith(result))
							totalsuccessRecords.incrementAndGet();
						else
							totalfailRecords.incrementAndGet();
					} else if (null != rate && rate.length > 1 && operationType.equals("mul")) {// mul
																								// 混全场景
																								// 20:1:1
						result = ser.get(this.groupId, this.key);// 读
						if (result == null)
							totalfailRecords.incrementAndGet();
						else
							totalsuccessRecords.incrementAndGet();
						if (i % (Integer.valueOf(rate[0]) / Integer.valueOf(rate[1])) == 0) {
							String tmpKey = getKey();
							result = ser.set(this.groupId, tmpKey, getStringByBytes(testValueSize));// 写
							if ("0".endsWith(result))
								totalsuccessRecords.incrementAndGet();
							else
								totalfailRecords.incrementAndGet();
							rs = ser.del(this.groupId, tmpKey); // 删
							if (rs < 1)
								totalfailRecords.incrementAndGet();
							else
								totalsuccessRecords.incrementAndGet();
						}
					}
					i++;
					long threadend = System.currentTimeMillis();
					long offset = threadend - threadstart;
					if (offset >= total) {
						isrunning = false;
						Thread.currentThread().stop();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				totalfailRecords.incrementAndGet();
			}
		}
	}
}
