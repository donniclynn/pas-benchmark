package com.pas.benchmark.cache;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.ctg.itrdc.cache.core.Configuration;

public class JedisPerformanceTest {
	private static AtomicLong	totalfailRecords	= new AtomicLong(0);	//操作失败的记录数
	private static AtomicLong	totalsuccessRecords	= new AtomicLong(0);	//操作成功的记录数
	private static AtomicLong	maxResponseRecords	= new AtomicLong(0);	//操作成功的记录数
	private static long			startTime;									//测试开始时间
	private static long			endTime;									//测试结束时间
	private static int			totalRecords		= 10000;				//操作的记录数
	private static int			threadCount			= 2;					//线程数
	private static int			testValueSize		= 32;					//测试的value大小(B)
	private static String		operationType		= "get";				//操作类型
																			
	private static String		ip					= "127.0.0.1";
	private static int			port				= 6379;
	private static String		testkeyPrefix		= "ideal-";			//测试的key字符串前缀
	private static String		testValue;									//测试的value值		
																			
	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("i", "ip", true, "redis ip default:127.0.0.1");
		options.addOption("p", "port", true, "redis port default:6379");
		options.addOption("t", "thread", true, "thread count default:2");
		options.addOption("c", "count", true, "total of the record  default:10000");
		options.addOption("o", "operate", true, "operation type (get,set)  default:get");
		options.addOption("s", "size", true, "total bytes by value  default:32 byte");
		options.addOption("h", "help", false, "help information");
		CommandLineParser parser = new PosixParser();
		HelpFormatter formatter = new HelpFormatter();
		
		final JedisPool jedisPool = getjedisPool();
		try {
			CommandLine line = parser.parse(options, args);
			if (line.hasOption("help")) {
				formatter.printHelp("test jedis", options);
				System.exit(0);
			}
			parse(line);
			
			testValue = getStringByBytes(testValueSize);
			
			startTime = System.currentTimeMillis();
			for (int i = 0; i < threadCount; i++) {
				if (i == threadCount - 1) {
					new Thread(new JedisPerformanceTest().new T(i * (totalRecords / threadCount),
						totalRecords, jedisPool)).start();
				} else {
					new Thread(new JedisPerformanceTest().new T(i * (totalRecords / threadCount),
						(i + 1) * (totalRecords / threadCount), jedisPool)).start();
				}
			}
		} catch (Exception e) {
			formatter.printHelp("test jedis", options);
			System.exit(0);
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				endTime = System.currentTimeMillis();
				
				System.out.println("finshed,total time(ms): " + (endTime - startTime));
				System.out.println("====================================================");
				System.out.println("success records count: " + totalsuccessRecords.get());
				System.out.println("fail records count: " + totalfailRecords.get());
				System.out.println("max response time for single record(ms): "
					+ maxResponseRecords.get());
				jedisPool.destroy();
			}
		});
		printOpeartion();
		System.out.println("it's running,please wait for completion !");
	}
	
	private static JedisPool getjedisPool() {
		Configuration conf = Configuration.getInstance();
		JedisPoolConfig config = new JedisPoolConfig();
		/**
		//		config.setMinIdle(1);
		config.setMaxTotal(conf.getInt(Constant.CONF_REDIS_POOL_MAX_TOTAL,
			Constant.CONF_REDIS_POOL_MAX_TOTAL_VALUE));
		config.setMaxIdle(conf.getInt(Constant.CONF_REDIS_POOL_MAX_IDLE,
			Constant.CONF_REDIS_POOL_MAX_IDLE_VALUE));
		config.setMaxWaitMillis(conf.getLong(Constant.CONF_REDIS_POOL_MAX_WAIT_MILLIS,
			Constant.CONF_REDIS_POOL_MAX_WAIT_MILLIS_VALUE));
		config.setTestOnBorrow(conf.getBoolean(Constant.CONF_REDIS_POOL_TEST_OFBORROW,
			Constant.CONF_REDIS_POOL_TEST_OFBORROW_VALUE));
			**/
		return new JedisPool(config, ip, port);
	}
	
	private static void parse(CommandLine line) {
		
		if (line.hasOption("thread")) {
			String tms = line.getOptionValue("thread");//读取的参数为字符串
			threadCount = Integer.parseInt(tms);
		}
		if (line.hasOption("count")) {
			String tms = line.getOptionValue("count");//读取的参数为字符串
			totalRecords = Integer.parseInt(tms);
		}
		if (line.hasOption("operate")) {
			operationType = line.getOptionValue("operate");//读取的参数为字符串
		}
		if (line.hasOption("size")) {
			String value = line.getOptionValue("size");//读取的参数为字符串
			testValueSize = Integer.parseInt(value);
		}
		if (line.hasOption("ip")) {
			ip = line.getOptionValue("ip");//读取的参数为字符串
		}
		if (line.hasOption("port")) {
			String value = line.getOptionValue("port");//读取的参数为字符串
			port = Integer.parseInt(value);
		}
	}
	
	public static String getStringByBytes(int length) {
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length / 2; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}
	
	private static void printOpeartion() {
		System.out.println("current redis ip: " + ip);
		System.out.println("current redis port: " + port);
		System.out.println("current total of record: " + totalRecords);
		System.out.println("current thread count: " + threadCount);
		System.out.println("current value bytes: " + testValueSize);
		System.out.println("current operation type: " + operationType);
	}
	
	class T implements Runnable {
		private int			startIndex;
		private int			endIndex;
		private boolean		isrunning	= true;
		private JedisPool	pool;
		
		public T(int start, int end, JedisPool pool) {
			this.startIndex = start;
			this.endIndex = end;
			this.pool = pool;
		}
		
		public void run() {
			while (isrunning) {
				Jedis jedis = null;
				jedis = pool.getResource();
				for (int i = startIndex; i < endIndex; i++) {
					
					try {
						long start = System.currentTimeMillis();
						if (operationType.equals("set")) {
							String result = jedis.set(testkeyPrefix + i, testValue);
							if (!"OK".equals(result))
								totalfailRecords.incrementAndGet();
							else
								totalsuccessRecords.incrementAndGet();
						} else if (operationType.equals("get")) {
							String result = jedis.get(testkeyPrefix + i);
							if (result == null)
								totalfailRecords.incrementAndGet();
							else
								totalsuccessRecords.incrementAndGet();
						}
						long end = System.currentTimeMillis();
						long offset = end - start;
						if (maxResponseRecords.get() < offset)
							maxResponseRecords.set(offset);
					} catch (Exception e) {
					}
				}
				if (jedis != null)
					jedis.close();
				
				isrunning = false;
			}
		}
	}
}
