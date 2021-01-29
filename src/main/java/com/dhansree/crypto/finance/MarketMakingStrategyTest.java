package com.dhansree.crypto.finance;

import static com.binance.api.client.domain.account.NewOrder.limitSell;

import java.util.Date;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.NewOrderResponseType;
import com.binance.api.client.domain.market.OrderBook;

public class MarketMakingStrategyTest {
	private static final String API_KEY = "y4sldaGBCkAR7ZUB40yqzW6jDNoB4O1MFj5PJlGl5sFkPCagwyYDcdZ3Wij1J7e9";
	private static final String SECRET = "OELRZW4vD4UwJLpve3ncwSzzrpRN7Ydcaz5dPjkwyuSBTR8h8Y6l8NBcLcPqY5fd";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(API_KEY, SECRET);
		BinanceApiRestClient client = factory.newRestClient();

		// Test connectivity
		client.ping();
		
		OrderBook orderBook = client.getOrderBook("CELRBTC", 5);
		String latestAskPrice = orderBook.getAsks().get(0).getPrice();
		System.out.println(new Date() + " >>>> Latest Ask Price: "+latestAskPrice);
		//Check Balance
		//Check Account Balance and place a sell order		
		Account account = client.getAccount(60_000L, System.currentTimeMillis());
		String freeSellingAssetBalance = account.getAssetBalance("CELR").getFree();
		//rounding off to no decimal pts
		freeSellingAssetBalance = Integer.toString((int)(Double.parseDouble(freeSellingAssetBalance))); //"3084.00000000"
		System.out.println("freeSellingAssetBalance: " + freeSellingAssetBalance);
		//Check if minimum trading value
		double sellingValue = Double.parseDouble(freeSellingAssetBalance) * Double.parseDouble(latestAskPrice);
		if (sellingValue >= Double.parseDouble("0.0001")) {
			System.out.println(new Date() + ">>>>   Selling a Unit of Coin");			
		}
	}

}
