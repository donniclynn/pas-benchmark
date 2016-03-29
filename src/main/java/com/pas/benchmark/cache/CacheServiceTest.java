package com.pas.benchmark.cache;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * 连通测试
 */
import com.ctg.itrdc.cache.common.exception.CacheConfigException;
import com.ctg.itrdc.cache.core.CacheService;

public class CacheServiceTest {

	public static void main(String args[]) throws CacheConfigException {

		String groupId = "group.test.057";
		String testValue = "kr8vy1s8mar3nhd61ft8sunf6t679b2y2c8vvjl13bueho55eqvlbjjjk2fw1c650wqmegk7ymtc6lq3t99pfh"
				+ "5utt6j1y078811nfn515u95h0p8ag6l8mw5hq09c7xn88lwpz1qwi7erc28t1rhfn38ey0zojg90gxq8o8pxe5mnkv2r2qrrt8qtpm6e59fv2v"
				+ "mum7rb9ktz5t2r6fssye6rji8zzjvg7tzg6tjvlywlylujvycmzgrqudv5i5lfqnnsor6qwz0y8utchx2rd6zhletztj37gsbh7sgfrjgbtp1d"
				+ "fyn5tzwzpafcmuq15ihvrqvd6duzg9mdbq6tsxyub3b8y0clpiifuhy6l851cbgbei65xx7k6pl2bee0v7n6estlmsmqre0i8e7zxy9hqkzoi7"
				+ "lbgrsc19geheg5jgp0022jctc21mncammpib8r2cmasg0v2dszfoyoxviuvylcnm3s9mn9lgddtu2x0157y62xuk8x1fkozxkr8vy1s8mar3nhd61ft8sunf6t679b2y2c8vvjl13bueho55eqvlbjjjk2fw1c650wqmegk7ymtc6lq3t99pfh"
				+ "5utt6j1y078811nfn515u95h0p8ag6l8mw5hq09c7xn88lwpz1qwi7erc28t1rhfn38ey0zojg90gxq8o8pxe5mnkv2r2qrrt8qtpm6e59fv2v"
				+ "mum7rb9ktz5t2r6fssye6rji8zzjvg7tzg6tjvlywlylujvycmzgrqudv5i5lfqnnsor6qwz0y8utchx2rd6zhletztj37gsbh7sgfrjgbtp1d"
				+ "fyn5tzwzpafcmuq15ihvrqvd6duzg9mdbq6tsxyub3b8y0clpiifuhy6l851cbgbei65xx7k6pl2bee0v7n6estlmsmqre0i8e7zxy9hqkzoi7"
				+ "lbgrsc19geheg5jgp0022jctc21mncammpib8r2cmasg0v2dszfoyoxviuvylcnm3s9mn9lgddtu2x0157y62xuk8x1fkozx";
		CacheService cs = new CacheService();
		//CacheTestSummaryCheckValue测试程序初使化一个Key供 Get用
		//String  result = cs.set(groupId, "81342127-9820-4877-8ed4-effbc24d4e9","81342127-9820-4877-8ed4-effbc24d4e9");
		//String  result = cs.set(groupId, "81342127-9820-4877-8ed4-effbc24d4e9",testValue);
		String  result = cs.set(groupId, "81342127-9820-4877-8ed4-effbc24d4e9",getStringByBytes(1024));
		String result2 = cs.get(groupId, "81342127-9820-4877-8ed4-effbc24d4e9");
		//CacheTestSummary测试程序初使化一个Key供Get用
		//cs.set(groupId, "81342127-9820-4877-8ed4-effbc24d4e9", testValue);
		System.out.println("======================set " +result);
		System.out.println("======================get " +result2);
	
	}

	public static String getStringByBytes(int length) {
		return RandomStringUtils.random(length, "abcdefghijklmnopqrstuvwxyz123567890");
	}
	
	
}
