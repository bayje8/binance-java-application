package com.binance.api.examples;

import java.util.Date;
import java.util.List;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.market.OrderBook;

public class BuyAtSellAtSpecificPrice {

	private static final String API_KEY = "y4sldaGBCkAR7ZUB40yqzW6jDNoB4O1MFj5PJlGl5sFkPCagwyYDcdZ3Wij1J7e9";
	private static final String SECRET = "OELRZW4vD4UwJLpve3ncwSzzrpRN7Ydcaz5dPjkwyuSBTR8h8Y6l8NBcLcPqY5fd";
	BinanceApiClientFactory factory;
	BinanceApiRestClient client;	
	
	long orderId1 = 0;
	long orderId2 = 38070116;
	
	public static void main(String[] args) {
		BuyAtSellAtSpecificPrice myTest = new BuyAtSellAtSpecificPrice();
		myTest.run();
	}

	public void run() {
		factory = BinanceApiClientFactory.newInstance(API_KEY, SECRET);
		client = factory.newRestClient();

		// Test connectivity
		client.ping();

		// Check server time
		long serverTime = client.getServerTime();
		System.out.println(serverTime);
		TriAribitageBean bean;
		String asset;
		String symbol;
		try {
			while (true) {
				Account account = client.getAccount(60_000L, System.currentTimeMillis());
				
				asset = "ONE";symbol = "ONEBTC";
				if (Double.parseDouble(account.getAssetBalance(asset).getFree()) > 1) {
					Order order = MyUtil.latestFilledBuyOrder(client, symbol);
					String sellPrice = String.format("%.8f", (Double.parseDouble(order.getPrice()) + 0.00000001));
					List<Order> openSellOrders = MyUtil.filterBySide(client.getOpenOrders(new OrderRequest(symbol)),OrderSide.SELL);
					for (Order openSellorder : openSellOrders) {
						if(Double.parseDouble(openSellorder.getPrice()) > Double.parseDouble(sellPrice)) {
							MyUtil.cancelAOrderbyOrderId(client, symbol, openSellorder.getOrderId());
						}
					}
					MyUtil.checkBalanceAndSell(client, symbol, sellPrice,	account.getAssetBalance(asset).getFree());
				}
				
				
				/*
				OrderBook orderBook = client.getOrderBook("FUNBTC", 5);
				String lastBidPrice = orderBook.getBids().get(0).getPrice(); 
				
				TriAribitageBean bean = MyUtil.checkTriArbitage(client, lastBidPrice, "1", "FUNETH", "ETHBTC");
				//System.out.println("Is Profit? :"+ bean.isPnl());
				System.out.println(new Date()+ " TA: "+bean.getPercentage() + " || MM: " + MyUtil.roundOff(Double.parseDouble("0.00000001")/Double.parseDouble(lastBidPrice)*100));
					*/			
				//System.out.println(new Date()+ " TA: "+((1000000*Double.parseDouble(client.getPrice("FUNETH").getPrice()) * Double.parseDouble(client.getPrice("ETHBTC").getPrice()))) + " MM: "+"0.41");
				//bean = MyUtil.checkTriArbitage(client, "0.00000075", "CDTETH", "ETHBTC");
				//System.out.println(new Date() + "CDTBTC >>>> Is profit?: "+bean.isPnl()+" %: "+bean.getPercentage());
				//bean = MyUtil.checkTriArbitage(client, "0.00000039", "FUELETH", "ETHBTC");
				//System.out.println(new Date() + "FUELBTC >>>> Is profit?: "+bean.isPnl()+" %: "+bean.getPercentage());
				
				//MyUtil.checkBalanceAndSell(client, account, "ONEBTC", "0.00000046",	account.getAssetBalance("ONE").getFree());
				//MyUtil.checkBalanceAndSell(client, account, "FUELBTC", "0.00000035", account.getAssetBalance("FUEL").getFree());
				
				/*
				MyUtil.checkBalanceAndSell(client, account, "WPRBTC", "0.00000085",
				account.getAssetBalance("WPR").getFree());
				if (orderId1 == 0) {
					orderId1 = MyUtil.checkBalanceAndSell(client, account, "CDTBTC", "0.00000051",
							account.getAssetBalance("CDT").getFree());
				}
				if (orderId2 == 0) {
					orderId2 = MyUtil.checkBalanceAndSell(client, account, "MITHBTC", "0.00000054",
							account.getAssetBalance("MITH").getFree());
				}
				if (orderId1 != 0) {
					if (Double.parseDouble(account.getAssetBalance("CDT").getFree()) > 1) {
						MyUtil.checkBalanceAndSell(client, account, "CDTBTC", "0.00000048",
								account.getAssetBalance("CDT").getFree());
						MyUtil.cancelAOrderbyOrderId(client, "CDTBTC", orderId1);
						Thread.sleep(2 * 1000);
						MyUtil.checkBalanceAndSell(client, account, "CDTBTC", "0.00000048",
								account.getAssetBalance("CDT").getFree());
					}
				}

				if (orderId2 != 0) {
					if (Double.parseDouble(account.getAssetBalance("MITH").getFree()) > 1) {
						MyUtil.checkBalanceAndSell(client, account, "MITHBTC", "0.00000051",
								account.getAssetBalance("MITH").getFree());
						MyUtil.cancelAOrderbyOrderId(client, "MITHBTC", orderId2);
						Thread.sleep(2 * 1000);
						MyUtil.checkBalanceAndSell(client, account, "MITHBTC", "0.00000051",
								account.getAssetBalance("MITH").getFree());
					}
				}
				*/
				Thread.sleep(1 * 1000);
			}
		} catch (Exception e) {
			System.out.println("Exception occured. Restarting the method..!");
			e.printStackTrace();
			try {
				Thread.sleep(60 * 1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			run();			
		}
	}

}
