package com.dhansree.crypto.finance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.NewOrderResponseType;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.CancelOrderResponse;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.exception.BinanceApiException;

import static com.binance.api.client.domain.account.NewOrder.limitBuy;
import static com.binance.api.client.domain.account.NewOrder.limitSell;

public class MarketMakingStrategy {

	private String pivot;
	private String pivotMinusOne;
	private String pivotMinusTwo;

	private List<Order> sellingList;
	private List<Order> buyingList;
	private List<Order> pivotMinusOneList;
	private List<Order> pivotMinusTwoList;

	private static final String API_KEY = "y4sldaGBCkAR7ZUB40yqzW6jDNoB4O1MFj5PJlGl5sFkPCagwyYDcdZ3Wij1J7e9";
	private static final String SECRET = "OELRZW4vD4UwJLpve3ncwSzzrpRN7Ydcaz5dPjkwyuSBTR8h8Y6l8NBcLcPqY5fd";
	BinanceApiClientFactory factory;
	BinanceApiRestClient client;
	
	private static int buyCount = 1;
	private static int pivotMinusOneBuyCount = 1;
	private static int pivotMinusTwoBuyCount = 2;
	private static int buyintervals = 24;
	
	private String unitQty = "2000";
	private String symbol = "HOTBTC";
	private String sellingSymbol = "HOT";
	private String buyingSymbol = "BTC";
	private String minTradingValue = "0.0001";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(new Date() + ">>>>   ");
		MarketMakingStrategy strategy = new MarketMakingStrategy();
		strategy.marketMaker();
	}

	public void marketMaker() {
		// login to my account
		login();
		// initialize prices from order Book
		OrderBook orderBook = client.getOrderBook(symbol, 5);
		pivot = orderBook.getBids().get(0).getPrice();
		pivotMinusOne = orderBook.getBids().get(1).getPrice();
		pivotMinusTwo = orderBook.getBids().get(2).getPrice();

		System.out.println(new Date() + ">>>>   " + "Current Pivot (Last Bid Price): " + pivot);
		System.out.println(new Date() + ">>>>   " + "Pivot Minus One (Second Last Bid Price): " + pivotMinusOne);
		System.out.println(new Date() + ">>>>   " + "Pivot Minus Two (Third Last Bid Price): " + pivotMinusTwo);

		// initialize list to keep order IDs
		sellingList = new ArrayList<Order>();
		buyingList = new ArrayList<Order>();
		pivotMinusOneList = new ArrayList<Order>();
		pivotMinusTwoList = new ArrayList<Order>();
		
		
		//This loop runs for every 5 minutes
		try {
			while (true) {
				//clear and intialize the lists
				initOrderLists();
				//Market price will change periodically and it is necessary to check against the pivot price
				orderBook = client.getOrderBook(symbol, 5);
				String latestBidPrice = orderBook.getBids().get(0).getPrice();
				String latestAskPrice = orderBook.getAsks().get(0).getPrice();				

				//WE ARE DECIDING THAT MARKET IS CHANGED WHEN FIRST BID PRICE IS CHANGED IN THE ORDER BOOK  
				if(!latestBidPrice.equals(pivot)) {				
					if(Double.parseDouble(latestBidPrice) < Double.parseDouble(pivot)) {
						//Market Went Down
						
						//Cancel orders from Buying Queue
						for (Order order : buyingList) {
							CancelAOrder(order.getOrderId());
						}
						
						//Cancel orders from Selling Queue
						for (Order order : sellingList) {
							CancelAOrder(order.getOrderId());
						}
						
						//cancel the last order from the pivot minus two queue
						if(pivotMinusTwoList.get(0).getTime() > pivotMinusTwoList.get(1).getTime()) {
							CancelAOrder(pivotMinusTwoList.get(0).getOrderId());
						} else {
							CancelAOrder(pivotMinusTwoList.get(1).getOrderId());
						}
						
						//
					} else if (Double.parseDouble(latestBidPrice) > Double.parseDouble(pivot)) {
						//Market Went up
						
						//Cancel orders from Selling Queue
						for (Order order : sellingList) {
							CancelAOrder(order.getOrderId());
						}
						
						//cancel all but one orders from the buying queue (leave the first order)						
						for (int i = 0; i < (buyingList.size()-1); i++) {
							if(buyingList.get(i).getTime() > buyingList.get(i+1).getTime()) {
								CancelAOrder(buyingList.get(i).getOrderId());
							}
						}
						
					}
				}
				
				//Check Account Balance and place a sell order				
				checkAndSellACoin(latestAskPrice);
				
				//clear and re-intialize the lists
				initOrderLists();
				
				/*
				 * We will be placing a buy order for an unit of coins, if the below conditions are satisfied
				 * 
				 * 1. Enough balance should be there in the account, in the denomination of buying price (ex: BTC)
				 * 		Buying price should be one unit Qty of buying coin (HOT) plus trading fee 0.1%
				 * 		Example -> one Unit Buying Price = one HOT price (i.e., price) (0.00000006 BTC) * Unit Quantity (2000) * 1.001
				 * 
				 * 2. Should not have more than the planned number of units (ie., buycount)
				 * 
				 * 3. Should not buy if the last buy order is placed within the buy intervals from the current date and time
				 * 		
				 */
				//Put Buy Order for Pivot priced units
				checkAndBuyACoin(buyingList, buyingSymbol, pivot, buyCount);
				//Put Buy order for 'Pivot Minus One' Priced units
				checkAndBuyACoin(pivotMinusOneList, buyingSymbol, pivotMinusOne, pivotMinusOneBuyCount);
				//Put Buy order for 'Pivot Minus Two' Priced units
				checkAndBuyACoin(pivotMinusTwoList, buyingSymbol, pivotMinusTwo, pivotMinusTwoBuyCount);								
								
				Thread.sleep(5 * 60 * 1000);
			}
		} catch (InterruptedException e) {
			// TODO: handle exception
		}
				
	}

	private int latestTimeSinceLastBuyOrder() {
		int timeSinceTheLastBuyOrder = 99999999;
		int diff;
		Date currentDate = new Date();
		if (buyingList.size() == 0) {
			timeSinceTheLastBuyOrder = 0;
			return timeSinceTheLastBuyOrder;
		} else {
			for (Order order : buyingList) {
				diff = (int) TimeUnit.HOURS.convert((Math.abs(currentDate.getTime() - order.getTime())),
						TimeUnit.MILLISECONDS);
				if (timeSinceTheLastBuyOrder > diff) {
					timeSinceTheLastBuyOrder = diff;
				}
			}
		}
		return timeSinceTheLastBuyOrder;
	}
	
	private void initOrderLists() {
		// Clearing all lists
		sellingList.clear();
		buyingList.clear();
		pivotMinusOneList.clear();
		pivotMinusTwoList.clear();
		
		//Get the latest open orders list from the Server
		List<Order> myOpenOrders = getMyOpenOrders();
		for (Order order : myOpenOrders) {
			//classify the orders and add those to appropriate lists
			if(order.getSide().equals(OrderSide.SELL)) {
				sellingList.add(order);
			} else if(order.getSide().equals(OrderSide.BUY)) {
				if(order.getPrice().equals(pivot)) {
					buyingList.add(order);
				} else if(order.getPrice().equals(pivotMinusOne)) {
					pivotMinusOneList.add(order);
				} else if (order.getPrice().equals(pivotMinusTwo)) {
					pivotMinusTwoList.add(order);
				}
			}
		}
	}

	private void checkAndBuyACoin(List<Order> buyingList, String buyingSymbol, String price, int buyCount) {

		// buy a unit of coin if the buying list contains less than buycount (5)
		if (buyingList.size() < buyCount) {
			// difference between the latest buy order and current date is more than the buyinterval (1 day = 24 hours)
			if (latestTimeSinceLastBuyOrder() <= buyintervals) {
				// Get My Account Details
				Account account = client.getAccount(60_000L, System.currentTimeMillis());
				double freeBuyingAssetBalance = Double.parseDouble(account.getAssetBalance(buyingSymbol).getFree());
				double oneUnitBuyingPrice = Double.parseDouble(price) * Double.parseDouble(unitQty) * 1.001;
				// buying price should surpass the minimum trading value
				if ((Double.parseDouble(price) * Double.parseDouble(unitQty)) > Double.parseDouble(minTradingValue)) {
					if (freeBuyingAssetBalance >= oneUnitBuyingPrice) {
						buyAUnitCoin(price);
					} else {
						System.out.println(
								new Date() + ">>>>   Account Balance is low to buy a unit of Coin. Avaiable Balance is "
										+ freeBuyingAssetBalance + buyingSymbol + " And Required Balance is "
										+ oneUnitBuyingPrice + buyingSymbol);
					}
				} else {
					System.out.println(new Date()
							+ ">>>>    Buying order can not be placed. Minimum trading value is 0.0001 BTC. And you are trying to place a bid for "
							+ (Double.parseDouble(price) * Double.parseDouble(unitQty)));
				}
			}
		}

	}
	
	private void buyAUnitCoin(String price) {
		System.out.println(new Date() + ">>>>   Buying a Unit of Coin");
		NewOrderResponse newOrderResponse = client.newOrder(limitBuy(symbol, TimeInForce.GTC, unitQty, price).newOrderRespType(NewOrderResponseType.FULL));
        System.out.println(newOrderResponse);
	}
	
	private void checkAndSellACoin(String price) {
		//Check Account Balance and place a sell order		
		Account account = client.getAccount(60_000L, System.currentTimeMillis());
		String freeSellingAssetBalance = account.getAssetBalance(sellingSymbol).getFree();
		//rounding off to no decimal pts
		freeSellingAssetBalance = Integer.toString((int)(Double.parseDouble(freeSellingAssetBalance)));
		//Check if minimum trading value
		double sellingValue = Double.parseDouble(freeSellingAssetBalance) * Double.parseDouble(price);
		if (sellingValue >= Double.parseDouble(minTradingValue)) {
			sellAUnitCoin(price, freeSellingAssetBalance); 
		}
	}
	
	private void sellAUnitCoin(String price, String qty) {
		System.out.println(new Date() + ">>>>   Selling a Unit of Coin");
		NewOrderResponse newOrderResponse = client.newOrder(limitSell(symbol, TimeInForce.GTC, qty, price).newOrderRespType(NewOrderResponseType.FULL));
        System.out.println(newOrderResponse);
	}
	
	private void CancelAOrder(long orderId) {
		System.out.println(new Date() + ">>>>   Cancelling a Unit of Coin");
		try {
	          CancelOrderResponse cancelOrderResponse = client.cancelOrder(new CancelOrderRequest(symbol, orderId));
	          System.out.println(cancelOrderResponse);
	        } catch (BinanceApiException e) {
	          System.out.println(e.getError().getMsg());
	        }
	}

	private List<Order> getMyOpenOrders() {
		return client.getOpenOrders(new OrderRequest(symbol));
	}
	
	public void login() {
		factory = BinanceApiClientFactory.newInstance(API_KEY, SECRET);
		client = factory.newRestClient();
	}

}
