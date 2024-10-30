package com.huyenhm;

import java.io.IOException;
import java.net.InetAddress;

public class Test {
	public static boolean ping(String ipAddress) {
		try {
			InetAddress inet = InetAddress.getByName(ipAddress);
			return inet.isReachable(1000); // Thời gian chờ 1000ms
		} catch (IOException e) {
			return false;
		}
	}

	public static void main(String[] args) {
		String ipToTest = "192.168.1.112"; // Địa chỉ IP cần kiểm tra
		boolean isOnline = ping(ipToTest);
		System.out.println("IP: " + ipToTest + " is " + (isOnline ? "online" : "offline"));
	}
}
