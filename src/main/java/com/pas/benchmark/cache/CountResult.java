package com.pas.benchmark.cache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CountResult {

	public static void main(String[] args) {
		String filePath = System.getProperty("user.dir") + File.separatorChar + "result.log";
		if (null != args && args.length >= 1) {
			filePath = args[0];
		}
		File file = new File(filePath);
		BufferedReader reader = null;
		List<String> list = new ArrayList<String>();
		try {
			reader = new BufferedReader(new FileReader(file));
			String temp = null;
			while ((temp = reader.readLine()) != null) {
				list.add(temp);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		StringBuffer result = new StringBuffer();
		if(null != list && list.size() >= 1){
			result.append(countTPS(list));	
		}else{
			result.append("Counting error! Please check result.log");
		}
		System.out.println(result);
	}
	
	public static String countTPS(List<String> list){
		// Avg TPS:2288:Min rt:0:Max rt:64:ART:1:Trans :22875
		long ttps = 0L;//各进程运行的TPS 之和
		long tart = 0L;//各进程运行的ART 之和
		long ttrans = 0L;//总的事务数
		int i = 0;
		for(String str:list){
			ttps = ttps + Long.parseLong(str.split(":")[1]);
			tart = tart + Long.parseLong(str.split(":")[7]);
			ttrans = ttrans + Long.parseLong(str.split(":")[9]);
			i++;
		}
		return "总TPS："+ttps+"	平均响应时间： "+tart/i + "毫秒	 运行总事务数："+ttrans;
	}

}
