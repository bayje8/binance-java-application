package com.binance.api.examples;

import static com.binance.api.client.domain.account.NewOrder.limitBuy;
import static com.binance.api.client.domain.account.NewOrder.limitSell;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.constant.Util;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.NewOrderResponseType;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.AllOrdersRequest;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.CancelOrderResponse;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.exception.BinanceApiException;

public class MyUtil {
	private static final String API_KEY = "y4sldaGBCkAR7ZUB40yqzW6jDNoB4O1MFj5PJlGl5sFkPCagwyYDcdZ3Wij1J7e9";
	private static final String SECRET = "OELRZW4vD4UwJLpve3ncwSzzrpRN7Ydcaz5dPjkwyuSBTR8h8Y6l8NBcLcPqY5fd";
	
	private static Map<String, String> triangularPairs;
	
	private static DecimalFormat a8Satoshi = new DecimalFormat("#.########");
	private static DecimalFormat noDecimal = new DecimalFormat("##########");
	private static DecimalFormat a3Satoshi = new DecimalFormat("#.###");
	private static DecimalFormat a2Satoshi = new DecimalFormat("######.##");
	
	
	static
	{
		triangularPairs = new HashMap<>();
		triangularPairs.put("HOT","HOTETH,ETHBTC");
//		triangularPairs.put("ONE","ONEUSDT,BTCUSDT");
//		triangularPairs.put("CDT","CDTETH,ETHBTC");
//		triangularPairs.put("IOST","IOSTUSDT,BTCUSDT");
//		triangularPairs.put("IOTX","IOTXETH,ETHBTC");
//		triangularPairs.put("FUN","FUNUSDT,BTCUSDT");
//		triangularPairs.put("ANKR","ANKRUSDT,BTCUSDT");
//		triangularPairs.put("CELR","CELRUSDT,BTCUSDT");
//		triangularPairs.put("PHB","PHBTUSD,BTCTUSD");
//		triangularPairs.put("TROY","TROYUSDT,BTCUSDT");
//		triangularPairs.put("SC","SCETH,ETHBTC");
//		triangularPairs.put("DREP","DREPUSDT,BTCUSDT");
	}

	public static void main(String[] args) throws Exception {
		MyUtil util = new MyUtil();
		BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(API_KEY, SECRET);
		BinanceApiRestClient client = factory.newRestClient();
		
		MyUtil.triangularArbitageSell(client, "HOT");
		/*
		String oneUnitBoughtPrice = String.format("%.4f", (Double.parseDouble(MyUtil.latestFilledBuyOrder(client, "ANKRBTC").getCummulativeQuoteQty())));
		double totalBoughtPrice = Double.parseDouble(oneUnitBoughtPrice) * 3 * 1;
		String pairTwoQuantity =  String.format("%.6f", (totalBoughtPrice));
		String pairTwoPriceStr = String.format("%.2f", ((Double.parseDouble("48.65") / (Double.parseDouble(pairTwoQuantity) * 1.001))));
		System.out.println("oneUnitBoughtPrice: "+oneUnitBoughtPrice);
		System.out.println("pairTwoQuantity: " + pairTwoQuantity);
		System.out.println("pairTwoPriceStr: " + pairTwoPriceStr);
		*/
		
				
		System.out.println(client.getOrderBook("HOTBTC", 5).getAsks().get(0).getQty());
				
				
		

	}
	
	public static boolean triangularArbitageSell(BinanceApiRestClient client, String asset) throws Exception {
		
		if (triangularPairs.containsKey(asset)) {
			String pairOne = triangularPairs.get(asset).split(",")[0].trim();
			String pairTwo = triangularPairs.get(asset).split(",")[1].trim();
			System.out.println("Pair One: " + pairOne);
			System.out.println("Pair Two: " + pairTwo);
			if (pairTwo.equals("ETHBTC")) {
				//Get the account
				Account account = client.getAccount(60_000L, System.currentTimeMillis());
				String pairOnePriceStr = client.getOrderBook(pairOne, 5).getBids().get(0).getPrice();				
				String pairOneQuantity = String.format("%.0f", (Double.parseDouble(account.getAssetBalance(asset).getFree())));
				
				try {				
				NewOrderResponse pairOneOrderResponse = client.newOrder(
						limitSell(pairOne, TimeInForce.GTC, pairOneQuantity, pairOnePriceStr).newOrderRespType(NewOrderResponseType.FULL));
				System.out.println(pairOneOrderResponse);
				}catch(Exception e) {
					pairOneQuantity = String.valueOf(Integer.parseInt(pairOneQuantity) - 1);
					NewOrderResponse pairOneOrderResponse = client.newOrder(
							limitSell(pairOne, TimeInForce.GTC, pairOneQuantity, pairOnePriceStr).newOrderRespType(NewOrderResponseType.FULL));
					System.out.println(pairOneOrderResponse);
				}
				//Get the account again
				account = client.getAccount(60_000L, System.currentTimeMillis());
				String pairTwoPriceStr = client.getOrderBook(pairTwo, 5).getBids().get(0).getPrice();
				String pairTwoQuantity = String.format("%.3f", (Double.parseDouble(account.getAssetBalance("ETH").getFree())));
				try {
				NewOrderResponse pairTwoOrderResponse = client.newOrder(
						limitSell(pairTwo, TimeInForce.GTC, pairTwoQuantity, pairTwoPriceStr).newOrderRespType(NewOrderResponseType.FULL));
				System.out.println(pairTwoOrderResponse);
				}catch(Exception e) {
					pairTwoQuantity = String.valueOf(Double.parseDouble(pairTwoQuantity) - 0.001);
					NewOrderResponse pairTwoOrderResponse = client.newOrder(
							limitSell(pairTwo, TimeInForce.GTC, pairTwoQuantity, pairTwoPriceStr).newOrderRespType(NewOrderResponseType.FULL));
					System.out.println(pairTwoOrderResponse);					
				}
			} else if (pairTwo.equals("BTCUSDT") || pairTwo.equals("BTCTUSD")) {
				String fiat = "USDT";
				switch (pairTwo) {
				case "BTCTUSD":
					fiat = "TUSD";
					break;
				default:
					fiat = "USDT";
					break;
				}
				
				//Get the account
				Account account = client.getAccount(60_000L, System.currentTimeMillis());
				String pairOnePriceStr = client.getOrderBook(pairOne, 5).getBids().get(0).getPrice();				
				String pairOneQuantity = String.format("%.0f", (Double.parseDouble(account.getAssetBalance(asset).getFree())));
				
				try {				
				NewOrderResponse pairOneOrderResponse = client.newOrder(
						limitSell(pairOne, TimeInForce.GTC, pairOneQuantity, pairOnePriceStr).newOrderRespType(NewOrderResponseType.FULL));
				System.out.println(pairOneOrderResponse);
				}catch(Exception e) {
					pairOneQuantity = String.valueOf(Integer.parseInt(pairOneQuantity) - 1);
					NewOrderResponse pairOneOrderResponse = client.newOrder(
							limitSell(pairOne, TimeInForce.GTC, pairOneQuantity, pairOnePriceStr).newOrderRespType(NewOrderResponseType.FULL));
					System.out.println(pairOneOrderResponse);
				}
				//Get the account again
				account = client.getAccount(60_000L, System.currentTimeMillis());
//				String pairTwoPriceStr = client.getOrderBook(pairTwo, 5).getAsks().get(0).getPrice();
//				String pairTwoQuantity = String.format("%.6f", ((Double.parseDouble(account.getAssetBalance("USDT").getFree()) / (Double.parseDouble(pairTwoPriceStr) * 1.001))));
				
				
				String oneUnitBoughtPrice = String.format("%.4f", (Double.parseDouble(MyUtil.latestFilledBuyOrder(client, asset+"BTC").getCummulativeQuoteQty())));
				double totalBoughtPrice = Double.parseDouble(oneUnitBoughtPrice) * 3;
				String pairTwoQuantity =  String.format("%.6f", (totalBoughtPrice));
				String pairTwoPriceStr = String.format("%.2f", ((Double.parseDouble(account.getAssetBalance(fiat).getFree()) / (Double.parseDouble(pairTwoQuantity) * 1.001))));
				
				try {
				NewOrderResponse pairTwoOrderResponse = client.newOrder(
						limitBuy(pairTwo, TimeInForce.GTC, pairTwoQuantity, pairTwoPriceStr).newOrderRespType(NewOrderResponseType.FULL));
				System.out.println(pairTwoOrderResponse);
				}catch(Exception e) {
					pairTwoQuantity = String.valueOf(Double.parseDouble(pairTwoQuantity) - 0.000001);
					NewOrderResponse pairTwoOrderResponse = client.newOrder(
							limitBuy(pairTwo, TimeInForce.GTC, pairTwoQuantity, pairTwoPriceStr).newOrderRespType(NewOrderResponseType.FULL));
					System.out.println(pairTwoOrderResponse);					
				}				
			}
			
		} else {
			System.out.println("Key isn't found");
			return false;
		}
		return true;
	}
	
	
	public void sendMail() throws IOException {
		Desktop desktop = Desktop.getDesktop();
		String message = "mailto:bala.jeevanantham@lfg.com?subject=First%20Email";
		URI uri = URI.create(message);
		desktop.mail(uri);
		
	}
	
	public static Order latestFilledBuyOrder(BinanceApiRestClient client, String symbol) throws InterruptedException {
		Thread.sleep(1 * 1000);
		// Getting list of all orders with a limit of 10
		List<Order> allOrders = client.getAllOrders(new AllOrdersRequest(symbol).limit(10));
		allOrders = filterByStatus(allOrders, OrderStatus.FILLED);
		allOrders = filterBySide(allOrders, OrderSide.BUY);
		return latestOrder(allOrders);
	}
	
	public void getAllSymbolOpenOrders() {
		BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(API_KEY, SECRET);
		BinanceApiRestClient client = factory.newRestClient();
		
		List<String> symbolList = new ArrayList<String>();
		symbolList.add("ERDBTC");
		//symbolList.add("DREPBTC");
		//symbolList.add("MITHBTC");
	//	symbolList.add("ONEBTC");
	//	symbolList.add("DOGEBTC");
		
		List<Order> totalOrders = new ArrayList<>();
		
		for (String string : symbolList) {
			List<Order> orders = client.getOpenOrders(new OrderRequest(string));
			totalOrders.addAll(orders);
		}
		//List<Order> orders = client.getOpenOrders(new OrderRequest("MITHBTC"));
		//JSONFileWrite.writeOrders(totalOrders, symbolList);
		System.out.println("|Symbol    \t|Order ID\t|Price     \t|Original Qty\t|Status\t|Side\t|");
		for (Order order : totalOrders) {
			System.out.println("|"+order.getSymbol()+"\t|"+order.getOrderId()+"\t|"+order.getPrice()+"\t|"+order.getOrigQty()+"\t|"+order.getStatus()+"\t|"+order.getSide()+"\t|");
		}
	}
	
	public static TriAribitageBean checkTriArbitage(BinanceApiRestClient client, String boughtPriceStr,
			String pairOneSymbol, String pairTwoSymbol) {

		double pairOnePrice = Double.parseDouble(client.getOrderBook(pairOneSymbol, 5).getBids().get(0).getPrice());
		double pairTwoPrice = Double.parseDouble(client.getOrderBook(pairTwoSymbol, 5).getBids().get(0).getPrice());
		System.out.println("pairOnePrice: "+pairOnePrice);
		System.out.println("pairTwoPrice: "+pairTwoPrice);
		double boughtPrice = Double.parseDouble(boughtPriceStr);
		double percentage = (((pairOnePrice * pairTwoPrice) - boughtPrice) / boughtPrice) * 100;
		return new TriAribitageBean(((pairOnePrice * pairTwoPrice) > boughtPrice), roundOff(percentage));
	}	
	
	public static void cancelAOrderbyOrderId(BinanceApiRestClient client, String symbol, long orderId) {
		System.out.println(new Date() + ">>>>   Cancelling a Unit of Coin");
		try {
	          CancelOrderResponse cancelOrderResponse = client.cancelOrder(new CancelOrderRequest(symbol, orderId));
	          System.out.println(cancelOrderResponse);
	        } catch (BinanceApiException e) {
	          System.out.println(e.getError().getMsg());
	        }
	}
	
	public static long checkBalanceAndSell(BinanceApiRestClient client, String symbol, String price,
			String unroundedQty) {
		
		if (Double.parseDouble(unroundedQty) > 1) {
			//get the quantity rounded up without decimal pts
			String qty = String.format("%.0f", (Double.parseDouble(unroundedQty)));
			//get the trade price = buying price * quantity * 1.001 - 0.1% trade fee
			BigDecimal tradeValue = new BigDecimal(Double.parseDouble(price) * Double.parseDouble(qty));
			//minimum trade value
			BigDecimal minTradeValue = new BigDecimal("0.0001");

			if (tradeValue.compareTo(minTradeValue) > 0) {
				System.out.println(new Date() + ">>>>   Selling in " + symbol + " Quantity:" + qty + " Price:" + price);
				try {
					NewOrderResponse newOrderResponse = client.newOrder(
							limitSell(symbol, TimeInForce.GTC, qty, price).newOrderRespType(NewOrderResponseType.FULL));
					System.out.println(newOrderResponse);
					return newOrderResponse.getOrderId();
				} catch (BinanceApiException e) {
					qty = String.valueOf(Integer.parseInt(qty) - 1);
					NewOrderResponse newOrderResponse = client.newOrder(
							limitSell(symbol, TimeInForce.GTC, qty, price).newOrderRespType(NewOrderResponseType.FULL));
					System.out.println(newOrderResponse);
					return newOrderResponse.getOrderId();
				}
			} else {
				System.out.println(new Date() + ">>>>    Selling order (Symbol:" + symbol + " Price:" + price + " Qty:"
						+ qty
						+ ") can not be placed. Minimum trading value is 0.0001 BTC. And you are trying to place as ask for "
						+ (Double.parseDouble(price) * Integer.parseInt(qty)));
			}
		}
		return 0;
	}
	
	public static long checkBalanceAndBuy(BinanceApiRestClient client, String symbol, String price,
			String unroundedQty) {
		//Get the account
		Account account = client.getAccount(60_000L, System.currentTimeMillis());
		//get the quantity rounded up without decimal pts
		String qty = String.format("%.0f", (Double.parseDouble(unroundedQty)));
		//get the free buying asset value
		BigDecimal freeBuyingAssetBalance = new BigDecimal(account.getAssetBalance("BTC").getFree());
		//get the trade price = buying price * quantity * 1.001 - 0.1% trade fee
		BigDecimal tradeValue = new BigDecimal(Double.parseDouble(price) * Double.parseDouble(qty) * 1.001);
		//minimum trade value
		BigDecimal minTradeValue = new BigDecimal("0.0001");
		

		if (freeBuyingAssetBalance.compareTo(tradeValue) > 0) {
			if (tradeValue.compareTo(minTradeValue) > 0) {
				System.out.println(new Date() + ">>>>   Buying in " + symbol + " Quantity:" + qty + " Price:" + price);
				NewOrderResponse newOrderResponse = client.newOrder(limitBuy(symbol, TimeInForce.GTC, qty, price).newOrderRespType(NewOrderResponseType.FULL));
				System.out.println(newOrderResponse);
				return newOrderResponse.getOrderId();
			} else {
				System.out.println(new Date() + ">>>>    Buying order (Symbol:" + symbol + " Price:" + price + " Qty:"
						+ qty
						+ ") can not be placed. Minimum trading value is 0.0001 BTC. And you are trying to place a bid for "
						+ (Double.parseDouble(price) * Integer.parseInt(qty)));
			}
		} else {
			System.out.println(new Date() + ">>>>    Buying order (Symbol:" + symbol + " Price:" + price + " Qty:" + qty
					+ ") can not be placed. Not enough Balance! And you are trying to place a bid for "
					+ (Double.parseDouble(price) * Integer.parseInt(qty)));
		}
		
		return 0;
	}
	
	/*
	 * returns asset balance in the "outAsset" denomination
	 * flag can be free/locked/both
	 */
	
	public double getAccountBalance(BinanceApiRestClient client, String inAsset, String outAsset, String flag) {
		// Get account balances
		Account account = client.getAccount(60_000L, System.currentTimeMillis());
		double totalBalance = 0;
		double free = Double.parseDouble(account.getAssetBalance(inAsset).getFree());
		double locked = Double.parseDouble(account.getAssetBalance(inAsset).getLocked());
		if(!flag.equalsIgnoreCase("locked")) {
			totalBalance += free; 
		}
		if(!flag.equalsIgnoreCase("free")) {
			totalBalance += locked; 
		}
		if(totalBalance != 0 && !inAsset.equalsIgnoreCase(outAsset)) {
			double price = Double.parseDouble(client.getPrice(inAsset+outAsset).getPrice());
			totalBalance = price * totalBalance;
		}
		System.out.println(totalBalance);
		return totalBalance;
	}
	
    // Get total account balance in BTC (spot only)
    public double getTotalAccountBalance(BinanceApiRestClient client, String flag) {
		// Get account balances
		Account account = client.getAccount(60_000L, System.currentTimeMillis());
		double totalBalance = 0;
		for (AssetBalance balance : account.getBalances()) {
			if (!flag.equalsIgnoreCase("locked")) {
				double free = Double.parseDouble(balance.getFree());
				if (balance.getAsset().equals("BTC")) {
					totalBalance = totalBalance + free;
				} else {
					String ticker = balance.getAsset() + Util.BTC_TICKER;
					String tickerReverse = Util.BTC_TICKER + balance.getAsset();
					if (free != 0) {
						if (Util.isFiatCurrency(balance.getAsset())) {
							double price = Double.parseDouble(client.getPrice(tickerReverse).getPrice());
							double amount = (free) / price;
							totalBalance += amount;
						} else {
							double price = Double.parseDouble(client.getPrice(ticker).getPrice());
							double amount = price * (free);
							totalBalance += amount;
						}

					}
				}
			}
			if (!flag.equalsIgnoreCase("free")) {
				double locked = Double.parseDouble(balance.getLocked());
				if (balance.getAsset().equals("BTC")) {
					totalBalance = totalBalance + locked;
				} else {
					String ticker = balance.getAsset() + Util.BTC_TICKER;
					String tickerReverse = Util.BTC_TICKER + balance.getAsset();
					if (locked != 0) {
						if (Util.isFiatCurrency(balance.getAsset())) {
							double price = Double.parseDouble(client.getPrice(tickerReverse).getPrice());
							double amount = (locked) / price;
							totalBalance += amount;
						} else {
							double price = Double.parseDouble(client.getPrice(ticker).getPrice());
							double amount = price * (locked);
							totalBalance += amount;
						}

					}
				}
			}
		}
        System.out.println("Account Balance: "+totalBalance+"BTC");
        return totalBalance;
    }
    
	public void CancelAOrder(BinanceApiRestClient client, String symbol, long orderId) {
		System.out.println(new Date() + ">>>>   Cancelling a Unit of Coin");
		try {
	          CancelOrderResponse cancelOrderResponse = client.cancelOrder(new CancelOrderRequest(symbol, orderId));
	          System.out.println(cancelOrderResponse);
	        } catch (BinanceApiException e) {
	          System.out.println(e.getError().getMsg());
	        }
	}
	
	//To get the latest open order
	public Order latestOpenOrder(BinanceApiRestClient client, String symbol) {
		List<Order> openOrders = client.getOpenOrders(new OrderRequest(symbol));						
		return latestOrder(openOrders);
	}
	
	//to get the latest open buy order
	public Order latestOpenBuyOrder(BinanceApiRestClient client, String symbol) {
		List<Order> openOrders = client.getOpenOrders(new OrderRequest(symbol));						
		return latestOrder(filterBySide(openOrders, OrderSide.BUY));		
	}

	//to get the latest sell order
	public Order latestOpenSellOrder(BinanceApiRestClient client, String symbol) {
		List<Order> openOrders = client.getOpenOrders(new OrderRequest(symbol));						
		return latestOrder(filterBySide(openOrders, OrderSide.SELL));		
	}
	
	//To find the latest order from the list of orders
	public static Order latestOrder(List<Order> orders) {
		Collections.sort(orders, new Comparator<Order>() {
			@Override
			public int compare(Order o1, Order o2) {
				// TODO Auto-generated method stub
				if (o1.getTime() > o1.getTime()) {
					return 1;
				} else {
					return -1;
				}
			}

		});				
		return orders.get(0);
	}

	//To filter a list of orders by side (BUY/SELL)
	public static List<Order> filterBySide(List<Order> orders, OrderSide orderSide){
		return orders.stream().filter(order -> order.getSide().equals(orderSide)).collect(Collectors.toList());
	}
	
	//To filter a list of orders by status
	public static List<Order> filterByStatus(List<Order> orders, OrderStatus orderStatus){
		return orders.stream().filter(order -> order.getStatus().equals(orderStatus)).collect(Collectors.toList());
	}

	//To filter a list of orders by status
	public static List<Order> filterBySymbol(List<Order> orders, String symbol){
		return orders.stream().filter(order -> order.getSymbol().equals(symbol)).collect(Collectors.toList());
	}
	
	//Math functions
	
	public static double roundOff(double d) {
		return Math.round(d * 100.0) / 100.0;
	}
	
	public static double roundOff8Satoshi(double d) {
		return Math.round(d * 100000000.0) / 100000000.0;
	}
	
	
}
