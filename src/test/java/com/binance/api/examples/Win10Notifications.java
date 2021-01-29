package com.binance.api.examples;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

public class Win10Notifications {
	

	    public static void main(String[] args) throws AWTException {
	        if (SystemTray.isSupported()) {
	        	Win10Notifications.displayTray("Trade Update", "Sold 1234 DREP coins");
	        } else {
	            System.err.println("System tray not supported!");
	        }
	    }

	    public static void displayTray(String title, String message) throws AWTException {
	        //Obtain only one instance of the SystemTray object
	        SystemTray tray = SystemTray.getSystemTray();

	        //If the icon is a file
	        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
	        //Alternative (if the icon is on the classpath):
	        //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

	        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
	        //Let the system resize the image if needed
	        trayIcon.setImageAutoSize(true);
	        //Set tooltip text for the tray icon
	        trayIcon.setToolTip("System tray icon demo");
	        tray.add(trayIcon);

	        trayIcon.displayMessage(title, message, MessageType.INFO);
	    }
	}
