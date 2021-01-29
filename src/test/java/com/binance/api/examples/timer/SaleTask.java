package com.binance.api.examples.timer;

import static com.binance.api.client.domain.account.NewOrder.limitSell;

import java.util.Timer;
import java.util.TimerTask;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.NewOrderResponseType;


public class SaleTask extends TimerTask {
	private static final String API_KEY = "GGPIMeOx6Ko5mYkVNrPyoyI86zufu2ZAWGkswtDXUDAUxY92YDv0W1Uog4BtbtUN";
	private static final String SECRET = "l80WN1LRajKWc3f0zHDIs5gizR536cjWHfRqfNkfkaWr8D9BoLMcr9JNrGtfEoZH";
	BinanceApiClientFactory factory;
	BinanceApiRestClient client;

	private String symbol;
	private String quantity;
	private String purchasedPrice;
	private String profitPercentage;
	private String lossPercentage;

	public SaleTask(String symbol, String quantity, String purchasedPrice, String profitPercentage,
			String lossPercentage) {
		this.symbol = symbol;
		this.quantity = quantity;
		this.purchasedPrice = purchasedPrice;
		this.profitPercentage = profitPercentage;
		this.lossPercentage = lossPercentage;
	}

	@Override
	public void run() {
		factory = BinanceApiClientFactory.newInstance(API_KEY, SECRET);
		client = factory.newRestClient();

		// Test connectivity
		client.ping();
		String ticketPriceStr = client.getPrice(symbol).getPrice();
		double tickerPrice = Double.parseDouble(ticketPriceStr);
		double lossLimit = ((100.00 - Double.parseDouble(lossPercentage)) / 100) * Double.parseDouble(purchasedPrice);
		double profitLimit = ((100.00 + Double.parseDouble(profitPercentage)) / 100)
				* Double.parseDouble(purchasedPrice);
		System.out.println(symbol + ": " + tickerPrice);

		if (tickerPrice <= lossLimit || tickerPrice >= profitLimit) {
			System.out.println("lossLimit: " + lossLimit);
			System.out.println("profitLimit: " + profitLimit);
			
			NewOrderResponse newOrderResponse = client
					.newOrder(limitSell(symbol, TimeInForce.GTC, quantity, ticketPriceStr)
							.newOrderRespType(NewOrderResponseType.FULL));
			System.out.println(newOrderResponse);
		}

	}

	public static void main(String args[]) throws Exception {
		Timer timer = new Timer(true);
		//SaleTask task1 = new SaleTask("LINKDOWNUSDT", "231.24", "0.432", "6", "2");
		//timer.scheduleAtFixedRate(task1, 0, 2 * 1000);
		SaleTask task2 = new SaleTask("DASHUSDT", "1.48285", "67.37", "6", "2");
		timer.scheduleAtFixedRate(task2, 0, 2 * 1000);

		Thread.currentThread().join();

	}
}
