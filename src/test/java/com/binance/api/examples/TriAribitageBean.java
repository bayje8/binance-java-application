package com.binance.api.examples;

public class TriAribitageBean {
	private boolean pnl;
	private double percentage;

	public TriAribitageBean(boolean pnl, double percentage) {		
		this.pnl = pnl;
		this.percentage = percentage;
	}

	public boolean isPnl() {
		return pnl;
	}

	public void setPnl(boolean pnl) {
		this.pnl = pnl;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
}
