package com.pas.benchmark.proxy;

public class SampleResult {
	
	private String sampleName;
	
	private boolean isOK = true;
	
	private long startTime = 0l;
	
	private long endTime = 0l;
	
	private String responseData;
	
	private int responseCode;
	
	private long latency;
	
	private boolean start;
	
	public void setStart(boolean start) {
		this.startTime = System.currentTimeMillis();
		this.start = start;
	}

	public void setEnd(boolean end) {
		this.endTime = System.currentTimeMillis();
	}

	private boolean end;

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public boolean isOK() {
		return isOK;
	}

	public void setOK(boolean isOK) {
		this.isOK = isOK;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public String getResponseData() {
		return responseData;
	}

	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public long getLatency() {
		return latency;
	}

	public void setLatency(long latency) {
		this.latency = latency;
	}

}
