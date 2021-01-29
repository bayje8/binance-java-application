package com.binance.api.examples;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;

public class BuyAtPramid {
	
	private static final String API_KEY = "y4sldaGBCkAR7ZUB40yqzW6jDNoB4O1MFj5PJlGl5sFkPCagwyYDcdZ3Wij1J7e9";
	private static final String SECRET = "OELRZW4vD4UwJLpve3ncwSzzrpRN7Ydcaz5dPjkwyuSBTR8h8Y6l8NBcLcPqY5fd";
	BinanceApiClientFactory factory;
	BinanceApiRestClient client;		
	
	public BuyAtPramid(String apiKey, String secret) {
		factory = BinanceApiClientFactory.newInstance(apiKey, secret);
		client = factory.newRestClient();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BuyAtPramid pramid = new BuyAtPramid(API_KEY, SECRET);
		pramid.buy3("0.00000052","0.00390000","ERDBTC");
		System.exit(0);
	}
	//This Method written for the coins purchased in the range of 50 satoshi
	public PramidBuyBean buy3(String buyAtXPrc, String amount, String symbol) {
		//String buyAtXMinus2Prc = String.format("%.8f", (Double.parseDouble(buyAtXPrc) - 0.00000002));
		String buyAtXMinus3Prc = String.format("%.8f", (Double.parseDouble(buyAtXPrc) - 0.00000003));

		System.out.println("buyAtXPrc: " + buyAtXPrc);
		//System.out.println("buyAtXMinus2Prc: " + buyAtXMinus2Prc);
		System.out.println("buyAtXMinus3Prc: " + buyAtXMinus3Prc);

		double priceX = Double.parseDouble(amount) / 3;
		System.out.println("priceX: " + priceX);

		String buyAtXQty = String.format("%.0f", (priceX / (Double.parseDouble(buyAtXPrc))));
		//String buyAtXMinus2Qty = String.format("%.0f", (priceX / (Double.parseDouble(buyAtXMinus2Prc))));
		String buyAtXMinus3Qty = String.format("%.0f", (priceX / (Double.parseDouble(buyAtXMinus3Prc))));

		System.out.println("buyAtXQty: " + buyAtXQty);
		//System.out.println("buyAtXMinus2Qty: " + buyAtXMinus2Qty);
		System.out.println("buyAtXMinus3Qty: " + buyAtXMinus3Qty);
		
		PramidBuyBean bean = new PramidBuyBean();
		
		bean.setXOrderId1(MyUtil.checkBalanceAndBuy(client, symbol, buyAtXPrc, buyAtXQty));
		
		//bean.setXOrderId2(MyUtil.checkBalanceAndBuy(client, symbol, buyAtXPrc, buyAtXQty));
		
		//bean.setXMinus2OrderId(MyUtil.checkBalanceAndBuy(client, symbol, buyAtXMinus2Prc, buyAtXMinus2Qty));
		
		bean.setXMinus3OrderId1(MyUtil.checkBalanceAndBuy(client, symbol, buyAtXMinus3Prc, buyAtXMinus3Qty));
		
		bean.setXMinus3OrderId2(MyUtil.checkBalanceAndBuy(client, symbol, buyAtXMinus3Prc, buyAtXMinus3Qty));
		
		System.out.println("Completed buying orders");

		System.out.println(bean.toString());
		
		return bean;
	}
	
	//This Method written for the coins purchased in the range of 50 satoshi
	public PramidBuyBean buy5(String buyAtXPrc, String amount, String symbol) {
		String buyAtXMinus2Prc = String.format("%.8f", (Double.parseDouble(buyAtXPrc) - 0.00000002));
		String buyAtXMinus3Prc = String.format("%.8f", (Double.parseDouble(buyAtXPrc) - 0.00000003));

		System.out.println("buyAtXPrc: " + buyAtXPrc);
		System.out.println("buyAtXMinus2Prc: " + buyAtXMinus2Prc);
		System.out.println("buyAtXMinus3Prc: " + buyAtXMinus3Prc);

		double priceX = Double.parseDouble(amount) / 3;
		System.out.println("priceX: " + priceX);

		String buyAtXQty = String.format("%.0f", (priceX / (Double.parseDouble(buyAtXPrc))));
		String buyAtXMinus2Qty = String.format("%.0f", (priceX / (Double.parseDouble(buyAtXMinus2Prc))));
		String buyAtXMinus3Qty = String.format("%.0f", (priceX / (Double.parseDouble(buyAtXMinus3Prc))));

		System.out.println("buyAtXQty: " + buyAtXQty);
		System.out.println("buyAtXMinus2Qty: " + buyAtXMinus2Qty);
		System.out.println("buyAtXMinus3Qty: " + buyAtXMinus3Qty);
		
		PramidBuyBean bean = new PramidBuyBean();
		
		bean.setXOrderId1(MyUtil.checkBalanceAndBuy(client, symbol, buyAtXPrc, buyAtXQty));
		
		bean.setXOrderId2(MyUtil.checkBalanceAndBuy(client, symbol, buyAtXPrc, buyAtXQty));
		
		bean.setXMinus2OrderId(MyUtil.checkBalanceAndBuy(client, symbol, buyAtXMinus2Prc, buyAtXMinus2Qty));
		
		bean.setXMinus3OrderId1(MyUtil.checkBalanceAndBuy(client, symbol, buyAtXMinus3Prc, buyAtXMinus3Qty));
		
		bean.setXMinus3OrderId2(MyUtil.checkBalanceAndBuy(client, symbol, buyAtXMinus3Prc, buyAtXMinus3Qty));
		
		System.out.println("Completed buying orders");

		System.out.println(bean.toString());
		
		return bean;
	}

}
