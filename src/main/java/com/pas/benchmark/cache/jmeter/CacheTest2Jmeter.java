package com.pas.benchmark.cache.jmeter;

import java.io.Serializable;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;

import com.ctg.itrdc.cache.common.exception.CacheConfigException;
import com.ctg.itrdc.cache.core.CacheService;
/**
 * 性能测试RWD
 * @author thinkpad
 *
 */
public class CacheTest2Jmeter extends AbstractJavaSamplerClient implements Serializable {

	private static final org.apache.log.Logger log = LoggingManager.getLoggerForClass();
	private static final long serialVersionUID = 1L;
	private String sourceStr = "abcdefghijklmnopqrstuvwxyz";
	CacheService cs;
	private String groupId = null;

	public void setupTest(JavaSamplerContext context) {
		log.info(getClass().getName() + ": init zookeeper !");
		String zookeeperUrl = context.getParameter("zookeeperUrl");
		groupId = context.getParameter("groupId");
		//log.error(groupId+"----------------------------" + zookeeperUrl);
		try {
			cs = new CacheService();
		} catch (CacheConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public SampleResult runTest(JavaSamplerContext arg0) {
		String key = null;
		String value = null;
		if (null != arg0.getParameter("ifAuto") && "true".equals(arg0.getParameter("ifAuto"))
				&& null != arg0.getParameter("length") && Integer.valueOf(arg0.getParameter("length")) > 1) {
			String length = arg0.getParameter("length");
			value = randStr(length, sourceStr);
			key = UUID.randomUUID().toString();
		} else {
			key = arg0.getParameter("key");
			value = arg0.getParameter("keyValue");
			//log.error(key+"----------------------------" + value);
		}
		SampleResult sr = new SampleResult();
		sr.sampleStart();
		sr.setSuccessful(true);
		String str = action(Integer.valueOf(arg0.getParameter("opType")),key,value);
		sr.sampleEnd();
		if ("fail".equals(str) || null == str){
			sr.setSuccessful(false);
			str = "fail";
		}
		sr.setResponseData(str);
		return sr;
	}

	/* Implements JavaSamplerClient.teardownTest(JavaSamplerContext) */
	@Override
	public void teardownTest(JavaSamplerContext context) {
		log.info("Close connection");
		cs.close();
	}

	/* Implements JavaSamplerClient.getDefaultParameters() */
	@Override
	public Arguments getDefaultParameters() {
		Arguments args = new Arguments();
		args.addArgument("zookeeperUrl", "192.168.5.30:2181");
		args.addArgument("groupId", "TestGroupNode");
		args.addArgument("key", "key");
		args.addArgument("keyValue", "value");
		args.addArgument("opType", "1set 2 get 3 del");
		args.addArgument("ifAuto", "false user define,true auto");
		args.addArgument("length", "ifAuto=true String length");
		return args;
	}

	public static String randStr(String length, String sourceStr) {
		return RandomStringUtils.random(Integer.valueOf(length), sourceStr);
	}
/** 
 * @param opType   1add 2get 3del
 * @param key  
 * @param value
 * @return
 */
	
	public String action(int opType,String key,String value) {
		switch (opType) {
		case 1:
			return cs.set(groupId, key, value)=="0" ?"success":"fail"; //return 0 success
		case 2:
			return cs.get(groupId, key);//success return value  null fail
		case 3:
			return cs.del(groupId, key).toString() == "0"?"fail":"success"; //return 0 fail 
		default:
			log.error("please input opType");
			return "1";
		}
	}
	
	public static void main(String arg[]){
		CacheTest2Jmeter ct = new CacheTest2Jmeter();
		Arguments args = new Arguments();
		args.addArgument("zookeeperUrl", "192.168.5.33:2181");
		args.addArgument("groupId", "TestGroupNode");
		args.addArgument("key", "test1001");
		args.addArgument("value", "test1001value");
		args.addArgument("opType","1");
		JavaSamplerContext jc = new JavaSamplerContext(args);
		ct.setupTest(jc);
		ct.runTest(jc);
	}
	
	

}
