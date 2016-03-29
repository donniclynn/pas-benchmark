package com.pas.benchmark.cache;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import com.ctg.itrdc.cache.common.exception.CacheConfigException;
import com.ctg.itrdc.cache.core.CacheService;
/**
 * 分布式缓存测试，可以指定key-Value长度,支持get,set,mul(get,set,del)的测试
 * 记录TPS ART到文件，多进程运行时由CountResult.java类来统计结果 
 * @author thinkpad
 *
 */
public class CacheTestSummary {
	private static final  Logger log  =  Logger.getLogger(CacheTestSummary.class);
	private static AtomicLong totalfailRecords = new AtomicLong(0); // 操作失败的记录数
	private static AtomicLong totalsuccessRecords = new AtomicLong(0); // 操作成功的记录数
	private static long startTime; // 测试开始时间
	private static long endTime; // 测试结束时间
	private static int totalTime = 30; // 执行时长
	private static int threadCount = 2; // 线程数
	private static int interval = 5000;// 毫秒为单位
	private static long preOKTrans = 0L;// 上一次计算时的成功事务数
	private static long preFailTrans = 0L;// 上一次计算时的失败事务数
	private static int testValueSize = 512; // 测试的value大小(B)
	private static String operationType = "get"; // 操作类型
	private static String testValue = "81342127-9820-4877-8ed4-effbc24d4e9"; // 测试的value值
	private static String searchKey = "81342127-9820-4877-8ed4-effbc24d4e9";//默认查找Key
	private static String groupId = "group.test.025"; // GroupId
	private static String sourceStr = "abcdefghijklmnopqrstuvwxyz123567890";
	private static String rate = "20@1@1"; // 读增删默认比例20：1：1
	private static long tmp = 0L;
	private static Vector  vts = new Vector();//响应时间  格式：运行时刻 @响应时间  
	private static Long thk = 1L ;//think time 10ms
	private static String rtStr = null;//记录平均响应时间
	
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
		options.addOption("i", "interval", true, "interval default 5000ms");
		options.addOption("thk", "thinkTime", true, "thinkTime default 10ms");
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
				testValue = getStringByBytes(testValueSize);// 生成随机Key,测试时Key变Value固定不变
			startTime = System.currentTimeMillis();
			// 间隔计算TPS
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					// TODO Auto-generated method stub
					tmp = totalsuccessRecords.get() - preOKTrans;
					preOKTrans = totalsuccessRecords.get();
					preFailTrans = totalfailRecords.get();
					System.out.println("Current TPS:	" + tmp / (interval / 1000) + "	|| successs trans: " + preOKTrans
							+ "|| fail trans: " + preFailTrans + "|| this interval trans: " + tmp);
					while (System.currentTimeMillis() - startTime >= totalTime * 1000){
						rtStr = art();
						System.exit(0);
					}				
				}
			}, 5000, interval);
			// 开启线程进行测试
			for (int i = 0; i < threadCount; i++) {
				new Thread(new CacheTestSummary().new T(groupId, searchKey, rate)).start();// searchKey仅在get时作为Key查询
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				endTime = System.currentTimeMillis();
				long du = (endTime - startTime);
				System.out.println("*******************************************************");
				System.out.println("finshed,run time(s): " + (endTime - startTime) / 1000);
				System.out.println("success Trans count: " + totalsuccessRecords.get());
				System.out.println("fail Trans count: " + totalfailRecords.get());
				System.out.println("Avg TPS: " + totalsuccessRecords.get() / (du / 1000));
				System.out.println("*******************************************************");
				result(null,"Avg TPS:" + totalsuccessRecords.get() / (du / 1000)+":"+rtStr+System.lineSeparator());
			}
		});
		printOpeartion();
		System.out.println("it's running,please wait for completion !");
	}

	public static String getStringByBytes(int length) {
		return RandomStringUtils.random(length, sourceStr);
	}

	public static String getKey() throws Exception {
		return UUID.randomUUID().toString();
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
				searchKey = key;
		}
		if (line.hasOption("groupIdG")) {
			groupId = line.getOptionValue("groupIdG");// get groupId
		}
		if (line.hasOption("rate")) {
			rate = line.getOptionValue("rate");// 业务比例
		}
		if (line.hasOption("interval")) {
			rate = line.getOptionValue("interval");// 显示TPS间隔
		}
		if (line.hasOption("thinkTime")) {
			thk = Long.parseLong(line.getOptionValue("thinkTime"));// 显示TPS间隔
		}
		
	}

	private static void printOpeartion() {
		System.out.println("current run time(s) : " + totalTime);
		System.out.println("current thread count: " + threadCount);
		System.out.println("current value bytes: " + testValueSize);
		System.out.println("current operation type: " + operationType);
	}
	
	public static String art(){
		String ARTString = null;
		if(null != vts && vts.size() > 0){
			long min = 0L;
			long max = 0L;
			long temp = 0L;
			long sum = 0L;
			Enumeration e = vts.elements();
			min = Long.valueOf(vts.get(0).toString());
			max = min;
			while (e.hasMoreElements()) {
				temp = Long.valueOf(e.nextElement().toString());
				if(temp<min)
					min = temp;
				if(temp>max)
					max = temp;
				sum+=temp;
			}
			ARTString = "Min rt:"+min+":Max rt:"+max+":ART:"+sum/vts.size()+":Trans :"+vts.size();
			System.out.println("========================================================");
			System.out.println(ARTString);
			System.out.println("========================================================");
		}
		return ARTString;
	}
	
	class T implements Runnable {
		private boolean isrunning = true;
		private CacheService ser;
		private String groupId;
		private int i = 1;
		private String rate = "20@1@1";
		private String key;
		
		public T(String groupId, String key, String rate) {
			this.groupId = groupId;
			this.key = searchKey;
			this.rate = rate;
		}

		public void run() {
			try {
				ser = new CacheService();
				int total = totalTime * 1000;
				long threadstart = System.currentTimeMillis();
				long threadend = 0L;
				long offset = 0L;
				String[] rate = this.rate.split("@");
				String result = "0000";
				Long rs = 0L;
				long t1,t2;
				String keyValue = getStringByBytes(testValueSize);
				log.info("testValueSize : "+testValueSize+" keyValue :"+keyValue);
				while (isrunning) {
					// only R
					if (operationType.equals("get")) {
						t1 = System.currentTimeMillis();
						result = ser.get(this.groupId, this.key);
						t2 = System.currentTimeMillis();
						if (null == result)
							totalfailRecords.incrementAndGet();
						else{
							totalsuccessRecords.incrementAndGet();
							vts.add(t2-t1);
						}
					} else if (operationType.equals("set")) {//only W
						t1 = System.currentTimeMillis();
						result = ser.set(this.groupId, getKey(), keyValue);
						t2 = System.currentTimeMillis();
						if ("0".endsWith(result)){
							totalsuccessRecords.incrementAndGet();
							vts.add(t2-t1);
						}else{
							totalfailRecords.incrementAndGet();
							System.out.println(result);
						}	
					} else if (null != rate && rate.length > 1 && operationType.equals("mul")) {// mul，混全场景20:1:1
						t1 = System.currentTimeMillis();
						result = ser.get(this.groupId, this.key);// 读
						t2 = System.currentTimeMillis();
						if (null == result)
							totalfailRecords.incrementAndGet();
						else{
							totalsuccessRecords.incrementAndGet();
							vts.add(t2-t1);
						}
						if (i % (Integer.valueOf(rate[0]) / Integer.valueOf(rate[1])) == 0) {
							String tmpKey = getKey();
							t1 = System.currentTimeMillis();
							result = ser.set(this.groupId, tmpKey, testValue);
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
					if(thk > 2){
						TimeUnit.MILLISECONDS.sleep(thk);//think time
					}
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
	
	public static void result(String filePathAbs,String data){
		String filePath = filePathAbs;
		if(null == filePath){
			filePath = System.getProperty("user.dir")+File.separatorChar+"result.log";
		}
		File file = new File(filePath);
		writeData(file, data);

	}
	
	public static synchronized void writeData(File file,String data) {
		FileWriter fw;
		try {
			fw = new FileWriter(file,true);
			fw.write(data);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
