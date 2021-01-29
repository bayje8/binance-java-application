package com.binance.api.examples;

public class PramidBuyBean {

	private long XOrderId1;
	private long XOrderId2;
	private long XMinus2OrderId;
	private long XMinus3OrderId1;
	private long XMinus3OrderId2;
	
	
	
	public PramidBuyBean(long xOrderId1, long xOrderId2, long xMinus2OrderId, long xMinus3OrderId1,
			long xMinus3OrderId2) {	
		XOrderId1 = xOrderId1;
		XOrderId2 = xOrderId2;
		XMinus2OrderId = xMinus2OrderId;
		XMinus3OrderId1 = xMinus3OrderId1;
		XMinus3OrderId2 = xMinus3OrderId2;
	}
	
	public PramidBuyBean() {
		// TODO Auto-generated constructor stub
	}

	public long getXOrderId1() {
		return XOrderId1;
	}
	public void setXOrderId1(long xOrderId1) {
		XOrderId1 = xOrderId1;
	}
	public long getXOrderId2() {
		return XOrderId2;
	}
	public void setXOrderId2(long xOrderId2) {
		XOrderId2 = xOrderId2;
	}
	public long getXMinus2OrderId() {
		return XMinus2OrderId;
	}
	public void setXMinus2OrderId(long xMinus2OrderId) {
		XMinus2OrderId = xMinus2OrderId;
	}
	public long getXMinus3OrderId1() {
		return XMinus3OrderId1;
	}
	public void setXMinus3OrderId1(long xMinus3OrderId1) {
		XMinus3OrderId1 = xMinus3OrderId1;
	}
	public long getXMinus3OrderId2() {
		return XMinus3OrderId2;
	}
	public void setXMinus3OrderId2(long xMinus3OrderId2) {
		XMinus3OrderId2 = xMinus3OrderId2;
	}

	@Override
	public String toString() {
		//PramidBuyBean bean = new PramidBuyBean(xOrderId1, xOrderId2, xMinus2OrderId, xMinus3OrderId1, xMinus3OrderId2);		
		return "PramidBuyBean bean = new PramidBuyBean(" + XOrderId1 + ", " + XOrderId2 + ", " + XMinus2OrderId + ", " + XMinus3OrderId1 + ", " + XMinus3OrderId2 + ");";
	}
	
	
	
}
