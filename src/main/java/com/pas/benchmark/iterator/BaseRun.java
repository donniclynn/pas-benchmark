package com.pas.benchmark.iterator;

public abstract class BaseRun implements Runnable {

	public BaseRun() {}

	public void run() {
		init();
		while (true) {
			exec();
			end();
		}
	}

	public abstract void init();

	public abstract void exec();

	public abstract void end();

}
