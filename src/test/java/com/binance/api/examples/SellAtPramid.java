package com.binance.api.examples;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.OrderStatusRequest;

public class SellAtPramid {
	
	private static final String API_KEY = "y4sldaGBCkAR7ZUB40yqzW6jDNoB4O1MFj5PJlGl5sFkPCagwyYDcdZ3Wij1J7e9";
	private static final String SECRET = "OELRZW4vD4UwJLpve3ncwSzzrpRN7Ydcaz5dPjkwyuSBTR8h8Y6l8NBcLcPqY5fd";
	BinanceApiClientFactory factory;
	BinanceApiRestClient client;		
	
	public SellAtPramid(String apiKey, String secret) {
		factory = BinanceApiClientFactory.newInstance(apiKey, secret);
		client = factory.newRestClient();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SellAtPramid pramid = new SellAtPramid(API_KEY, SECRET);
		PramidBuyBean buyBean = new PramidBuyBean(1809943, 1809944, 1809945, 1809946, 1809947);
		PramidSellBean sellBean = new PramidSellBean(0, 0, 0, 0, 0);
		pramid.sell(buyBean, sellBean, "DREPBTC", "0.00000050");
	}
	
	public void sell(PramidBuyBean buyBean, PramidSellBean sellBean, String symbol, String boughtPrc) {
		String boughtPrcPlus1 = String.format("%.8f", (Double.parseDouble(boughtPrc) + 0.00000001));
		String boughtPrcPlus2 = String.format("%.8f", (Double.parseDouble(boughtPrc) + 0.00000002));
		String boughtPrcMinus1 = String.format("%.8f", (Double.parseDouble(boughtPrc) - 0.00000001));
		String boughtPrcMinus2 = String.format("%.8f", (Double.parseDouble(boughtPrc) - 0.00000002));
		Order order;
		
		//check order status of buy first order at X Price		
		order = client.getOrderStatus(new OrderStatusRequest(symbol, buyBean.getXOrderId1()));
		
		if (order.getStatus().equals(OrderStatus.FILLED) && sellBean.getBoughtPrcPlus1OrderId() != 0) {
			sellBean.setBoughtPrcPlus1OrderId(
					MyUtil.checkBalanceAndSell(client, symbol, boughtPrcPlus1, order.getExecutedQty()));
		}
		
		//check order status of buy second order at X Price		
		order = client.getOrderStatus(new OrderStatusRequest(symbol, buyBean.getXOrderId2()));
		
		if (order.getStatus().equals(OrderStatus.FILLED) && sellBean.getBoughtPrcPlus2OrderId() != 0) {
			sellBean.setBoughtPrcPlus2OrderId(MyUtil.checkBalanceAndSell(client, symbol, boughtPrcPlus2, order.getExecutedQty()));
		}
		
		
		//check order status of buy order at X-2 Price		
		order = client.getOrderStatus(new OrderStatusRequest(symbol, buyBean.getXMinus2OrderId()));
		
		if (order.getStatus().equals(OrderStatus.FILLED) && sellBean.getBoughtPrcMinus1OrderId() != 0) {
			sellBean.setBoughtPrcMinus1OrderId(MyUtil.checkBalanceAndSell(client, symbol, boughtPrcMinus1, order.getExecutedQty()));
			
			order = client.getOrderStatus(new OrderStatusRequest(symbol, sellBean.getBoughtPrcPlus2OrderId()));
			if(order.getStatus().equals(OrderStatus.NEW)) {
				MyUtil.cancelAOrderbyOrderId(client, symbol, order.getOrderId());
			}
			sellBean.setBoughtPrcPlus2OrderId(MyUtil.checkBalanceAndSell(client, symbol, boughtPrcMinus1, order.getOrigQty()));
			
			order = client.getOrderStatus(new OrderStatusRequest(symbol, sellBean.getBoughtPrcPlus1OrderId()));
			if(order.getStatus().equals(OrderStatus.NEW)) {
				MyUtil.cancelAOrderbyOrderId(client, symbol, order.getOrderId());
			}
			sellBean.setBoughtPrcPlus1OrderId(MyUtil.checkBalanceAndSell(client, symbol, boughtPrcMinus1, order.getOrigQty()));
			
		}
		
		
		//check order status of buy first order at X-3 Price		
		order = client.getOrderStatus(new OrderStatusRequest("DREPBTC", buyBean.getXMinus3OrderId1()));	
		
		if (order.getStatus().equals(OrderStatus.FILLED) && sellBean.getBoughtPrcMinus2OrderId1() != 0) {
			sellBean.setBoughtPrcMinus2OrderId1(MyUtil.checkBalanceAndSell(client, symbol, boughtPrcMinus2, order.getExecutedQty()));
			
			order = client.getOrderStatus(new OrderStatusRequest(symbol, sellBean.getBoughtPrcPlus2OrderId()));
			if(order.getStatus().equals(OrderStatus.NEW)) {
				MyUtil.cancelAOrderbyOrderId(client, symbol, order.getOrderId());
			}
			sellBean.setBoughtPrcPlus2OrderId(MyUtil.checkBalanceAndSell(client, symbol, boughtPrcMinus2, order.getOrigQty()));
			
			order = client.getOrderStatus(new OrderStatusRequest(symbol, sellBean.getBoughtPrcPlus1OrderId()));
			if(order.getStatus().equals(OrderStatus.NEW)) {
				MyUtil.cancelAOrderbyOrderId(client, symbol, order.getOrderId());
			}
			sellBean.setBoughtPrcPlus1OrderId(MyUtil.checkBalanceAndSell(client, symbol, boughtPrcMinus2, order.getOrigQty()));
			
			order = client.getOrderStatus(new OrderStatusRequest(symbol, sellBean.getBoughtPrcMinus1OrderId()));
			if(order.getStatus().equals(OrderStatus.NEW)) {
				MyUtil.cancelAOrderbyOrderId(client, symbol, order.getOrderId());
			}
			sellBean.setBoughtPrcMinus1OrderId(MyUtil.checkBalanceAndSell(client, symbol, boughtPrcMinus2, order.getOrigQty()));
		}
		
		
		//check order status of buy second order at X-3 Price		
		order = client.getOrderStatus(new OrderStatusRequest("DREPBTC", buyBean.getXMinus3OrderId2()));	
		
		if (order.getStatus().equals(OrderStatus.FILLED) && sellBean.getBoughtPrcMinus2OrderId2() != 0) {
			sellBean.setBoughtPrcMinus2OrderId2(MyUtil.checkBalanceAndSell(client, symbol, boughtPrcMinus2, order.getExecutedQty()));			
		}
		
	}

}
