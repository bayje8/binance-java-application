package com.binance.api.examples;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.market.OrderBook;

public class LoopControl {

	private static final String API_KEY = "y4sldaGBCkAR7ZUB40yqzW6jDNoB4O1MFj5PJlGl5sFkPCagwyYDcdZ3Wij1J7e9";
	private static final String SECRET = "OELRZW4vD4UwJLpve3ncwSzzrpRN7Ydcaz5dPjkwyuSBTR8h8Y6l8NBcLcPqY5fd";
	BinanceApiClientFactory factory;
	BinanceApiRestClient client;

	long orderId1 = 0;
	long orderId2 = 38070116;
	
	List<BigDecimal> amountList = new ArrayList<BigDecimal>();

	public static void main(String[] args) {
		LoopControl myTest = new LoopControl();
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

		List<String> assetList = Arrays.asList( new String[] { "ONE", "HOT", "DOGE", "MITH", "DREP", "IOTX", "CDT", "FUN", "TROY", "CELR", "FUEL",
						"STMX", "PHB", "IOST", "ERD", "QKC", "SC", "XVG", "ANKR", "POE", "TNB", "FTM", "DNT", "CND" });
		try {
			while (true) {
				Account account = client.getAccount(60_000L, System.currentTimeMillis());

				for (String asset : assetList) {
					//sell(asset, account);
				}
				
				//checkPrice();
				
				//checkMarket();

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

	public void sell(String asset, Account account) throws Exception {
		String symbol = asset + "BTC";
		// System.out.println("asset: " + asset + " symbol: " + symbol);
		if (Double.parseDouble(account.getAssetBalance(asset).getFree()) > 1) {
			boolean orderCancelled = false;
			Order order = MyUtil.latestFilledBuyOrder(client, symbol);
			String sellPrice = String.format("%.8f", (Double.parseDouble(order.getPrice()) + 0.00000001));
			List<Order> openSellOrders = MyUtil.filterBySide(client.getOpenOrders(new OrderRequest(symbol)),
					OrderSide.SELL);
			for (Order openSellorder : openSellOrders) {
				if (Double.parseDouble(openSellorder.getPrice()) > Double.parseDouble(sellPrice)) {
					orderCancelled = true;
					MyUtil.cancelAOrderbyOrderId(client, symbol, openSellorder.getOrderId());
				}
			}
			if (orderCancelled) {
				if (!MyUtil.triangularArbitageSell(client, asset)) {
					MyUtil.checkBalanceAndSell(client, symbol, sellPrice, account.getAssetBalance(asset).getFree());
				}
			} else {
				MyUtil.checkBalanceAndSell(client, symbol, sellPrice, account.getAssetBalance(asset).getFree());
			}
		}
	}
	
	
	public void checkPrice() {
		BigDecimal pairOnePriceStr = new BigDecimal(client.getOrderBook("HOTETH", 5).getBids().get(0).getPrice());
		BigDecimal pairTwoPriceStr = new BigDecimal(client.getOrderBook("ETHBTC", 5).getBids().get(0).getPrice());
		BigDecimal quantity = new BigDecimal(12500);
		BigDecimal amount = quantity.multiply(pairOnePriceStr).multiply(pairTwoPriceStr);
		amountList.add(amount);
		
		
		Collections.sort(amountList, new Comparator<BigDecimal>() {
			@Override
			public int compare(BigDecimal o1, BigDecimal o2) {
				// TODO Auto-generated method stub
				return (o2.compareTo(o1));
			}

		});				
		System.out.println(new Date() + "Current Amount: " + amount+" All Time High: "+amountList.get(0));				
	}
	
	public void checkMarket() {
		BigDecimal bidPrc = new BigDecimal(client.getOrderBook("DREPUSDT", 5).getBids().get(0).getPrice());
		BigDecimal askPrc = new BigDecimal(client.getOrderBook("DREPUSDT", 5).getAsks().get(0).getPrice());
		BigDecimal midPrc = bidPrc.add(askPrc).divide(new BigDecimal(2));
		System.out.println(midPrc);
	}

}
