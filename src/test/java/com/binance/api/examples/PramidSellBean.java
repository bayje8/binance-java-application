package com.binance.api.examples;

public class PramidSellBean {
	
	private long boughtPrcPlus2OrderId;
	private long boughtPrcPlus1OrderId;
	private long boughtPrcMinus1OrderId;
	private long boughtPrcMinus2OrderId1;
	private long boughtPrcMinus2OrderId2;
	
	public PramidSellBean() {
	}

	public PramidSellBean(long boughtPrcPlus2OrderId, long boughtPrcPlus1OrderId, long boughtPrcMinus1OrderId,
			long boughtPrcMinus2OrderId1, long boughtPrcMinus2OrderId2) {
		this.boughtPrcPlus2OrderId = boughtPrcPlus2OrderId;
		this.boughtPrcPlus1OrderId = boughtPrcPlus1OrderId;
		this.boughtPrcMinus1OrderId = boughtPrcMinus1OrderId;
		this.boughtPrcMinus2OrderId1 = boughtPrcMinus2OrderId1;
		this.boughtPrcMinus2OrderId2 = boughtPrcMinus2OrderId2;
	}

	public long getBoughtPrcPlus2OrderId() {
		return boughtPrcPlus2OrderId;
	}

	public void setBoughtPrcPlus2OrderId(long boughtPrcPlus2OrderId) {
		this.boughtPrcPlus2OrderId = boughtPrcPlus2OrderId;
	}

	public long getBoughtPrcPlus1OrderId() {
		return boughtPrcPlus1OrderId;
	}

	public void setBoughtPrcPlus1OrderId(long boughtPrcPlus1OrderId) {
		this.boughtPrcPlus1OrderId = boughtPrcPlus1OrderId;
	}

	public long getBoughtPrcMinus1OrderId() {
		return boughtPrcMinus1OrderId;
	}

	public void setBoughtPrcMinus1OrderId(long boughtPrcMinus1OrderId) {
		this.boughtPrcMinus1OrderId = boughtPrcMinus1OrderId;
	}

	public long getBoughtPrcMinus2OrderId1() {
		return boughtPrcMinus2OrderId1;
	}

	public void setBoughtPrcMinus2OrderId1(long boughtPrcMinus2OrderId1) {
		this.boughtPrcMinus2OrderId1 = boughtPrcMinus2OrderId1;
	}

	public long getBoughtPrcMinus2OrderId2() {
		return boughtPrcMinus2OrderId2;
	}

	public void setBoughtPrcMinus2OrderId2(long boughtPrcMinus2OrderId2) {
		this.boughtPrcMinus2OrderId2 = boughtPrcMinus2OrderId2;
	}
	
}
