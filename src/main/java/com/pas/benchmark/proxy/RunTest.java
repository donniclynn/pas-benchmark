package com.pas.benchmark.proxy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public  class RunTest<T> extends DynamicProxy {

	private static long tmp = 0L;
	private static long preOKTrans = 0L;// 上一次计算时的成功事务数
	private static long preFailTrans = 0L;// 上一次计算时的失败事务数
	private static int interval = 5000;// 计算TPS的间隔时间毫秒为单位
	private static long startTime = 0L; // 测试开始时间
	private static long endTime = 0L; // 测试结束时间，有可能是异常结束，此时取当前时间
	private static String rtStr = null;// 记录平均响应时间
	private static long runTime = 10000L; // 运行时长 单位ms
	private static boolean saveRt = false;//是否记录响应时间
	
	/**
	 * Object[] paras 格式是：
	 *  paras[0]传入变量
	 *  paras[1]传入运行时长
	 *  paras[2]是否记录响应时间标志
	 * 
	 * @param args
	 */
	public RunTest(T realObject, Object[] paras) {
		super(realObject, paras);
		runTime = Integer.valueOf((String) paras[1]);
		if(null != paras && paras.length>=3 && paras[2].equals("true")) saveRt = true;
		
	}
	/**
	 * 
	 * @param rt		
	 * @param threads	线程数
	 */
	public  void runTest(RunTest rt,int threads) {
		int j = 1;
		if(threads>1) j = threads;
		rt.countTPS(interval);
		startTime = System.currentTimeMillis();// 标记测试开始
		for (int i = 0; i < j; i++) {
			new Thread(rt).start();
		}

		// ExecutorService pool = Executors.newFixedThreadPool(5);
		// for (int i = 0; i < 1; i++) {
		// pool.execute(rt);
		// }

		// 异常中断处理 钩子程序
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				endTime = System.currentTimeMillis();
				long du = (endTime - startTime);
				System.out.println("*******************************************************");
				System.out.println("finshed,run time(s): " + (du) / 1000);
				System.out.println("success Trans count: " + DynamicProxy.getTotalsuccessRecords().get());
				System.out.println("fail Trans count: " + DynamicProxy.getTotalfailRecords().get());
				System.out.println("Avg TPS: " + DynamicProxy.getTotalsuccessRecords().get() / (du / 1000));
				System.out.println("*******************************************************");
				result(null, "Avg TPS:" + DynamicProxy.getTotalsuccessRecords().get() / (du / 1000) + ":" + rtStr
						+ System.lineSeparator());
			}
		});
	}

	/**
	 * 间隔计算TPS
	 * 
	 * @param interaval
	 */
	public String countTPS(int interaval) {
		this.interval = interaval;
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				// TODO Auto-generated method stub
				tmp = DynamicProxy.getTotalsuccessRecords().get() - preOKTrans;
				preOKTrans = DynamicProxy.getTotalsuccessRecords().get();
				preFailTrans = DynamicProxy.getTotalfailRecords().get();
				System.out.println("Current TPS:	" + tmp / (interval / 1000) + "	|| successs trans: " + preOKTrans
						+ "|| fail trans: " + preFailTrans + "|| this interval trans: " + tmp);
				while (System.currentTimeMillis() - startTime >= runTime) {
					if (saveRt)
						rtStr = art(DynamicProxy.getVts());
					System.exit(0);
				}
			}
		}, interval, interval);
		return rtStr;
	}

	public static void result(String filePathAbs, String data) {
		String filePath = filePathAbs;
		if (null == filePath) {
			filePath = System.getProperty("user.dir") + File.separatorChar + "result.log";
		}
		File file = new File(filePath);
		writeData(file, data);

	}

	public static synchronized void writeData(File file, String data) {
		FileWriter fw;
		try {
			fw = new FileWriter(file, true);
			fw.write(data);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String art(Vector vts) {
		String ARTString = null;
		if (null != vts && vts.size() > 0) {
			long min = 0L;
			long max = 0L;
			long temp = 0L;
			long sum = 0L;
			Enumeration e = vts.elements();
			min = Long.valueOf(vts.get(0).toString());
			max = min;
			while (e.hasMoreElements()) {
				temp = Long.valueOf(e.nextElement().toString());
				if (temp < min)
					min = temp;
				if (temp > max)
					max = temp;
				sum += temp;
			}
			ARTString = "Min rt:" + min + ":Max rt:" + max + ":ART:" + sum / vts.size() + ":Trans :" + vts.size();
			System.out.println("========================================================");
			System.out.println(ARTString);
			System.out.println("========================================================");
		}
		return ARTString;
	}

}
