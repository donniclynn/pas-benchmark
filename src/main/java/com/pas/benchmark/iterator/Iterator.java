package com.pas.benchmark.iterator;

import java.util.concurrent.TimeUnit;

public class Iterator extends Record {
	
	private String name;

	private OrderService os ;
	
	public Iterator(){
	}
	
	public Iterator(int type){
		super(type);
		os = new OrderService("OrderService init");
	}
	
	public Iterator(int type,String name){
		super(type);
		this.name = name;
	}

	public Task createTask(int type) {
		return new Task(){
			protected  void action() throws Exception{
				try {
					TimeUnit.MILLISECONDS.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("TestAction Action on");
				System.out.println(os+"---------Okey");
			}
		};
	}

	public static void main(String args[]){
		Thread thread = new Thread(new Iterator(999));
		Thread thread2 = new Thread(new Iterator(888));
		thread.setName("thread one");
		thread.start();
		thread2.setName("thread two");
		thread2.start();
	}
	
	class OrderService{
		
		private String conn;
		
		public OrderService(String str){
			this.conn = str;
		}
		
		public void getConn(){
			System.out.println("getConn");
		}
		
		public void getOrder(){
			System.out.println(os);
		}
	}
	
}
