package com.pas.benchmark;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import redis.clients.jedis.Protocol.Command;
import redis.clients.util.SafeEncoder;

import com.ctg.itrdc.cache.conn.LongConnection;
import com.ctg.itrdc.cache.conn.ParaConversion;
import com.ctg.itrdc.cache.njedis.NJedisConversion;
import com.ctg.itrdc.cache.njedis.NJedisPara;
import com.ctg.itrdc.cache.njedis.Nheader;

/**
 * 
 * @author thinkpad
 *
 */
public class Console {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ParaConversion para = new NJedisConversion();
		String host = "10.142.90.25";
		int port = 8848;
		int timeout = 10000;
		String proxyName = "proxy001";
		final String groupId = "group.test.025";
		final LongConnection conn = new LongConnection(host, port, para,
				timeout, proxyName);
		Scanner scanner = null;
		while (true) {
			scanner = new Scanner(System.in);
			String line = trimSpace(scanner.nextLine());
			String console[] = line.split(" ");
			Command command = null;
			try {
				command = Enum.valueOf(Command.class, console[0].toUpperCase());
				console = Arrays.copyOfRange(console, 1, console.length);
				byte result[] = conn.call(new NJedisPara(command, new Nheader(
						groupId), SafeEncoder.encodeMany(console)), timeout);
				System.out.println(SafeEncoder.encode(result));
			} catch (TimeoutException t) {
				System.out.println("连接超时....");
			} catch (Exception e) {
				System.out.println("命令错误!");
			}
		}
	}

	/**
	 * 替换字符串之间的多个空格为单个空格
	 * 
	 * @param str
	 * @return
	 */
	public static String trimSpace(String str) {
		// System.out.println(str.replaceAll(" +", " "));
		return str.replaceAll(" +", " ");
	}
}
