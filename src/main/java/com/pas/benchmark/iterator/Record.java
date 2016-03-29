package com.pas.benchmark.iterator;

public abstract class Record extends BaseRun {

	private Task currentTask;
	private int type;
	
	public Record(){}
	
	public Record(int type){
		this.type = type;
		System.out.println("Record type: "+type);
	}
	
	@Override
	public void init() {
		currentTask = createTask(type);
	}

	@Override
	public void exec(){
		long start = System.currentTimeMillis();
		try {
			currentTask.action();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long rt = System.currentTimeMillis()-start;
		System.out.println(Thread.currentThread().getName() +" RT: "+rt);
	}

	@Override
	public void end() {}

	public abstract Task createTask(int type);
	
	public static abstract class Task {
		protected abstract void action() throws Exception;
	}
	
}
