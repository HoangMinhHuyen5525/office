package com.huyenhm.data;

import java.io.IOException;
import java.net.InetAddress;

public class NetworkMonitor {

	public static String pingDevice(String ipAddress) {
		try {
			InetAddress inet = InetAddress.getByName(ipAddress);
			return inet.isReachable(1000) ? "Online" : "Offline";
		} catch (IOException e) {
			return "Offline";
		}
	}
}
