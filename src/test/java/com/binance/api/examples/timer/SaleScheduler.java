package com.binance.api.examples.timer;

import java.util.Timer;

public class SaleScheduler {
	public static void main(String args[]) {
		System.out.println(">>");
		SaleTask task1 = new SaleTask("LINKDOWNUSDT", "231.24", "0.432", "6", "2");
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(task1, 0, 20 * 1000);
	}
}
