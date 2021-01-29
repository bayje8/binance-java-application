package com.binance.api.examples;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.binance.api.client.domain.account.Order;

public class JSONFileWrite {
	public static FileWriter file;
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static void writeOrders(List<Order> orders, List<String> symbolList) {
		try {
			JSONObject totalOrders = new JSONObject();
			for (String string : symbolList) {
				JSONArray symbolOrders = new JSONArray();
				List<Order> filteredOrders = MyUtil.filterBySymbol(orders, string);
				for (Order order : filteredOrders) {
					JSONObject jsonOrder = new JSONObject();
					jsonOrder.put("orderId", order.getOrderId());
					jsonOrder.put("price", order.getPrice());
					jsonOrder.put("quantity", order.getOrigQty());
					jsonOrder.put("Status", order.getStatus());
					jsonOrder.put("side", order.getSide());
					symbolOrders.add(jsonOrder);
				}
				totalOrders.put(string, symbolOrders);				
			}
			
			/// obj.put("Orders", orders);
            // Constructs a FileWriter given a file name, using the platform's default charset
            file = new FileWriter("C:\\Users\\bayje8\\git\\binance-java-api\\src\\test\\resources\\orders.json");
            file.write(totalOrders.toJSONString());
            System.out.println("Successfully Copied JSON Object to File...");
            System.out.println("\nJSON Object: " + totalOrders);
 
        } catch (IOException e) {
            e.printStackTrace();
 
        } finally {
 
            try {
                file.flush();
                file.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
	}

}
