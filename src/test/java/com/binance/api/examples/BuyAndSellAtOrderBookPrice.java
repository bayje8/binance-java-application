package com.binance.api.examples;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.constant.Util;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.NewOrderResponseType;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.TradeHistoryItem;
import com.binance.api.client.domain.account.request.AllOrdersRequest;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.CancelOrderResponse;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.account.request.OrderStatusRequest;
import com.binance.api.client.domain.general.Asset;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.client.domain.general.SymbolInfo;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.binance.api.client.domain.market.TickerStatistics;
import com.binance.api.client.exception.BinanceApiException;

import static com.binance.api.client.domain.account.NewOrder.limitBuy;
import static com.binance.api.client.domain.account.NewOrder.limitSell;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Examples on how to use the general endpoints.
 */
public class BuyAndSellAtOrderBookPrice {

	private static final String API_KEY = "y4sldaGBCkAR7ZUB40yqzW6jDNoB4O1MFj5PJlGl5sFkPCagwyYDcdZ3Wij1J7e9";
	private static final String SECRET = "OELRZW4vD4UwJLpve3ncwSzzrpRN7Ydcaz5dPjkwyuSBTR8h8Y6l8NBcLcPqY5fd";
	BinanceApiClientFactory factory;
	BinanceApiRestClient client;	
	
	public static void main(String[] args) {
		BuyAndSellAtOrderBookPrice myTest = new BuyAndSellAtOrderBookPrice();
		myTest.justTest();
	}

	public void justTest() {
		factory = BinanceApiClientFactory.newInstance(API_KEY, SECRET);
		client = factory.newRestClient();

		// Test connectivity
		client.ping();

		// Check server time
		long serverTime = client.getServerTime();
		System.out.println(serverTime);

		try {
			while (true) {
				Account account = client.getAccount(60_000L, System.currentTimeMillis());
				
				//CELRBTC Market
				checkAndSell(account, "CELRBTC", "CELR");
				
				checkAndBuy("CELRBTC");
				
				cancelOutterOrders("CELRBTC");
				
				//DOGEBTC Market				
				checkAndSell(account, "DOGEBTC", "DOGE");				
				
				cancelOutterOrders("DOGEBTC");
				
				checkAndBuy("DOGEBTC");
				
				//ANKRBTC Market				
				checkAndSell(account, "ANKRBTC", "ANKR");
				
				checkAndBuy("ANKRBTC");										
				
				cancelOutterOrders("ANKRBTC");
								
				
				Thread.sleep(5 * 1000);
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
			justTest();			
		}
	}

	public String checkAndSell(Account account, String market, String sellingAsset) throws Exception {
		OrderBook orderBook = client.getOrderBook(market, 5);
		String latestAskPrice = orderBook.getAsks().get(0).getPrice();		
		// Check Balance
		// Check Account Balance and place a sell order
		String freeSellingAssetBalance = account.getAssetBalance(sellingAsset).getFree();
		// rounding off to no decimal pts
		freeSellingAssetBalance = Integer.toString((int) (Double.parseDouble(freeSellingAssetBalance)));
		//System.out.println(new Date() + " " + sellingAsset + ">>>> Latest Ask Price: " + latestAskPrice
		//		+ " Asset Balance: " + freeSellingAssetBalance);
		// Check if minimum trading value
		double sellingValue = Double.parseDouble(freeSellingAssetBalance) * Double.parseDouble(latestAskPrice);
		if (sellingValue >= Double.parseDouble("0.0001")) {
			System.out.println(new Date() + ">>>>   Selling a Unit of Coin in "+market);
			NewOrderResponse newOrderResponse = client
					.newOrder(limitSell(market, TimeInForce.GTC, freeSellingAssetBalance, latestAskPrice)
							.newOrderRespType(NewOrderResponseType.FULL));
			System.out.println(newOrderResponse);
			return Long.toString(newOrderResponse.getOrderId());
		}
		return "";
	}
	
	public void checkAndBuy(String market) throws Exception {
			List<Order> openOrders = client.getOpenOrders(new OrderRequest(market));
			int openOrdersCount = (openOrders != null)? openOrders.size():0;			
			
			if(openOrdersCount < 1) {
				OrderBook orderBook = client.getOrderBook(market, 5);
				String latestBidPrice = orderBook.getBids().get(0).getPrice();
				// Get My Account Details
				Account account = client.getAccount(60_000L, System.currentTimeMillis());
				double freeBuyingAssetBalance = Double.parseDouble(account.getAssetBalance("BTC").getFree());
				int qty = (int)(freeBuyingAssetBalance/(Double.parseDouble(latestBidPrice) * 1.001));
				if ((Double.parseDouble(latestBidPrice) * qty) > Double.parseDouble("0.0001")) {
						System.out.println(new Date() + ">>>>   Buying in " + market);
						NewOrderResponse newOrderResponse = client
								.newOrder(limitBuy(market, TimeInForce.GTC, Integer.toString(qty), latestBidPrice)
										.newOrderRespType(NewOrderResponseType.FULL));
						System.out.println(newOrderResponse);
				} else {
					System.out.println(new Date()
							+ ">>>>    Buying order can not be placed. Minimum trading value is 0.0001 BTC. And you are trying to place a bid for "
							+ (Double.parseDouble(latestBidPrice) * qty));
				}
			}						
	}
	
	public void cancelOutterOrders(String market) throws Exception {
		OrderBook orderBook = client.getOrderBook(market, 5);
		String latestBidPrice = orderBook.getBids().get(0).getPrice();
		String latestAskPrice = orderBook.getAsks().get(0).getPrice();

		List<Order> openOrders = client.getOpenOrders(new OrderRequest(market));
		for (Order order : openOrders) {
			if (order.getSide().equals(OrderSide.BUY)) {
				if (!order.getPrice().equals(latestBidPrice)) {
					System.out.println(new Date() + ">>>>   Cancelling a Unit of Coin in "+market);
					try {
						CancelOrderResponse cancelOrderResponse = client
								.cancelOrder(new CancelOrderRequest(market, order.getOrderId()));
						System.out.println(cancelOrderResponse);
					} catch (BinanceApiException e) {
						System.out.println(e.getError().getMsg());
					}
				}
			}

			if (order.getSide().equals(OrderSide.SELL)) {
				if (!order.getPrice().equals(latestAskPrice)) {
					System.out.println(new Date() + ">>>>   Cancelling a Unit of Coin in "+market);
					try {
						CancelOrderResponse cancelOrderResponse = client
								.cancelOrder(new CancelOrderRequest(market, order.getOrderId()));
						System.out.println(cancelOrderResponse);
					} catch (BinanceApiException e) {
						System.out.println(e.getError().getMsg());
					}
				}
			}
		}
	}		
		
    /*    // Get account balances
        Account account = client.getAccount(60_000L, System.currentTimeMillis());
        System.out.println("Account Balance in Integer: "+ Double.parseDouble(account.getAssetBalance("HOT").getFree()));
if(Double.parseDouble(account.getAssetBalance("HOT").getFree()) >= Double.parseDouble("0.5")) {
	System.out.println("Sell it....");
}

OrderBook orderBook = client.getOrderBook("HOTBTC", 5);
String latestBidPrice = orderBook.getBids().get(0).getPrice();
System.out.println("Latest Bid Price: "+Double.parseDouble(latestBidPrice));
if(Double.parseDouble("0.00000010") < Double.parseDouble("0.00000009")) {
	System.out.println("true");
}
        // Get total account balance in BTC (spot only)
        MyTest accountBalance = new MyTest();
        accountBalance.getTotalAccountBalance(client,account);  
        /*   
        //check all orders
        List<Order> allOrders = client.getAllOrders(new AllOrdersRequest("HOTBTC"));
        for (Order order : allOrders) {
			System.out.println(order.toString());
		}
        */ 
        
         
      /*   // REAL TESTED CODE - NOT TO PLAY!!!!!!!!!!!!!!!!!!!!!!!! 
        
        //check open orders
        long orderId = 0;
        System.out.println("-----All Open Orders--------");
        List<Order> allOpenOrders = client.getOpenOrders(new OrderRequest("HOTBTC"));
        System.out.println("Open Orders size: "+allOpenOrders.size());
        for (Order order : allOpenOrders) {
			System.out.println(order.toString());
			orderId = order.getOrderId();
			long diffInMillies = Math.abs(new Date().getTime() - order.getTime());
		    int diff = (int) TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
		    System.out.println(diff);
		}
        System.out.println("Will you reach me?");
        
        System.out.println("Conversion: "+Integer.toString((int)(Double.parseDouble("1499.0001"))));
         int i = 0;
		try {
			while (i < 1000000) {
				orderBook = client.getOrderBook("HOTBTC", 5);
				latestBidPrice = orderBook.getBids().get(0).getPrice();
				System.out.println("Latest Bid Price: " + Double.parseDouble(latestBidPrice));

				account = client.getAccount(60_000L, System.currentTimeMillis());
		        System.out.println("Account Balance in Integer: "+ Double.parseDouble(account.getAssetBalance("HOT").getFree()));
		        
				Thread.sleep(30 * 1000);
				i++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		 * String freeBalance = account.getAssetBalance("HOT").getFree(); freeBalance =
		 * Integer.toString((int)(Double.parseDouble(freeBalance))); NewOrderResponse
		 * newOrderResponse = client.newOrder(limitSell("HOTBTC", TimeInForce.GTC,
		 * freeBalance, latestBidPrice).newOrderRespType(NewOrderResponseType.FULL));
		 * System.out.println(newOrderResponse);
		 */
        
		/*
        //check particular order based on orderId
        System.out.println("-------check status of particular order------------");
        Order order = client.getOrderStatus(new OrderStatusRequest("HOTBTC", orderId));
        System.out.println(order.toString());
     
        // Canceling an order based on orderId
        System.out.println("----------Try Cancelling an order----------");
        try {
          CancelOrderResponse cancelOrderResponse = client.cancelOrder(new CancelOrderRequest("HOTBTC", orderId));
          System.out.println(cancelOrderResponse);
        } catch (BinanceApiException e) {
          System.out.println(e.getError().getMsg());
        }
      
      //check particular order based on orderId
        System.out.println("-------check status of particular order------------");
        Order orderA = client.getOrderStatus(new OrderStatusRequest("HOTBTC", orderId));
        System.out.println(orderA.toString());
     
        
        
        
        // Placing a real LIMIT order for the latest bid price and be added in the queue
        
        OrderBook orderBook = client.getOrderBook("HOTBTC", 5);
        String latestBidPrice = orderBook.getBids().get(0).getPrice();
        System.out.println("Latest Bid Price: "+latestBidPrice);
        
        //
        System.out.println("Placing an order");
        //NewOrder order = limitBuy("HOTBTC", TimeInForce.GTC, "2000", latestBidPrice);
        //client.newOrderTest(order);
        
        NewOrderResponse newOrderResponse = client.newOrder(limitBuy("HOTBTC", TimeInForce.GTC, "2000", latestBidPrice).newOrderRespType(NewOrderResponseType.FULL));
        System.out.println(newOrderResponse);
        
        //check open orders
        long orderId = 0;
        System.out.println("-----All Open Orders--------");
        List<Order> allOpenOrders = client.getOpenOrders(new OrderRequest("HOTBTC"));
        for (Order order : allOpenOrders) {
			System.out.println(order.toString());
			orderId = order.getOrderId();
		}
		}
        */
		
	
	
	// Get total account balance in BTC (spot only)
	public void getTotalAccountBalance(BinanceApiRestClient client, Account account) {
		for (AssetBalance balance : account.getBalances()) {
			double free = Double.parseDouble(balance.getFree());
			double locked = Double.parseDouble(balance.getLocked());
			String asset = balance.getAsset();
			if (free + locked != 0) {
				System.out.println("Free: " + free + asset + " Locked: " + locked + asset);
			}
		}

	}
	
	private String getMarketMakingPercentage(BinanceApiRestClient client, String symbol) {
		OrderBook orderBook = client.getOrderBook(symbol, 5);
		String latestBid = orderBook.getBids().get(0).getPrice();
		String latestAsk = orderBook.getAsks().get(0).getPrice();
		double b = Double.parseDouble(latestBid);
		double a = Double.parseDouble(latestAsk);
		double pnL = (a-b)/b * 100;
		System.out.println(symbol+" -> latestBid:"+latestBid+" latestAsk:"+latestAsk+" Profit or Loss:"+pnL+"%");
		return null;
	}
	
	private void UnwantedCodes(BinanceApiRestClient client) {
		
		List<TradeHistoryItem> historyItems = client.getTrades("CELRBTC", 1000);
		System.out.println("Length: "+historyItems.size());
		
		OrderBook orderBook = client.getOrderBook("HOTBTC", 5);
		String latestBidQty = orderBook.getBids().get(0).getQty();
		String latestBidPrice = orderBook.getBids().get(0).getPrice();
		System.out.println("latest Bid Qty:"+Double.parseDouble(latestBidQty));
		double dLatestBid = Double.parseDouble(latestBidQty);
		
		double totQty = 0.0;
		long tradeId = 0;
		for (TradeHistoryItem tradeHistoryItem : historyItems) {
			if(latestBidPrice.equals(tradeHistoryItem.getPrice())) {
				totQty = totQty + Double.parseDouble(tradeHistoryItem.getQty());
			}
			tradeId = tradeHistoryItem.getId();
			System.out.println(tradeHistoryItem.toString());	
		}
		

		System.out.println("dLatestBid: "+dLatestBid+" totQty: "+totQty);				
		
		
		while((new BigDecimal(dLatestBid).compareTo(new BigDecimal(totQty))) >= 0) {
			System.out.println("Still not found looking backwards...");
			historyItems = client.getHistoricalTrades("HOTBTC", 1000, tradeId);
			for (TradeHistoryItem tradeHistoryItem : historyItems) {
				if(latestBidPrice.equals(tradeHistoryItem.getPrice())) {
					totQty = totQty + Double.parseDouble(tradeHistoryItem.getQty());
				}
				tradeId = tradeHistoryItem.getId();
				System.out.println(tradeHistoryItem.toString());	
			}			
		}
		
		System.out.println("Total Qty:"+totQty);
		/*
		myTest.getMarketMakingPercentage(client, "HOTBTC");
		myTest.getMarketMakingPercentage(client, "MBLBTC");
		myTest.getMarketMakingPercentage(client, "POEBTC");
		myTest.getMarketMakingPercentage(client, "TNBBTC");
		myTest.getMarketMakingPercentage(client, "ANKRBTC");
		myTest.getMarketMakingPercentage(client, "DOGEBTC");
		myTest.getMarketMakingPercentage(client, "ERDBTC");
		myTest.getMarketMakingPercentage(client, "SCBTC");
		myTest.getMarketMakingPercentage(client, "CELRBTC");
		myTest.getMarketMakingPercentage(client, "STORMBTC");
		
		
		 * List<Order> openOrders = client.getOpenOrders(new OrderRequest("ANKRBTC"));
		 * System.out.println(openOrders);
		 */

		/*
		 * OrderBook orderBook = client.getOrderBook("ANKRBTC", 100);
		 * 
		 * //24 Hr Statistics TickerStatistics tickerStatistics =
		 * client.get24HrPriceStatistics("ANKRBTC");
		 * System.out.println(tickerStatistics.toString());
		 * 
		 * 
		 * 
		 * 
		 * List<OrderBookEntry> asks = orderBook.getAsks();
		 * System.out.println("Order Book Asks Size: "+ asks.size()); OrderBookEntry
		 * firstAskEntry = asks.get(9); System.out.println(firstAskEntry.getPrice() +
		 * " / " + firstAskEntry.getQty());
		 * 
		 * List<OrderBookEntry> bids = orderBook.getBids();
		 * System.out.println("Order Book Bids Size: "+ bids.size()); OrderBookEntry
		 * firstBidEntry = bids.get(0); System.out.println(firstBidEntry.getPrice() +
		 * " / " + firstBidEntry.getQty());
		 * 
		 * 
		 * ExchangeInfo exchangeInfo = client.getExchangeInfo();
		 * System.out.println(exchangeInfo.toString());
		 * 
		 * 
		 * System.out.println(exchangeInfo.getTimezone());
		 * System.out.println(exchangeInfo.getSymbols());
		 * 
		 */ 
	}
	
	
}
