package com.huyenhm.device;

import jakarta.validation.constraints.NotNull;

public class DeviceDTO {

	@NotNull(message = "name cannot be null")
	private String name;

	@NotNull(message = "IP cannot be null")
	private String ip;

	@NotNull(message = "Port cannot be null")
	private String port;

	@NotNull(message = "Username cannot be null")
	private String username;

	@NotNull(message = "Password cannot be null")
	private String password;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
