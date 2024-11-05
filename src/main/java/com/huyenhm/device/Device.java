package com.huyenhm.device;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huyenhm.events.Events;
import com.huyenhm.person.Person;

import jakarta.annotation.Nonnull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Device")
public class Device {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Nonnull
	@Column(name = "name")
	private String name;

	@Nonnull
	@Column(name = "ip")
	private String ip;

	@Nonnull
	@Column(name = "port")
	private String port;

	@Nonnull
	@Column(name = "username")
	private String username;

	@Nonnull
	@Column(name = "password")
	private String password;

	@Column(name = "status")
	private String status;

	@Column(name = "deviceName")
	private String deviceName;

	@Column(name = "deviceID")
	private Long deviceID;

	@Column(name = "model")
	private String model;

	@Column(name = "serialNumber")
	private String serialNumber;

	@Column(name = "macAddress")
	private String macAddress;

	@Column(name = "firmwareVersion")
	private String firmwareVersion;

	@Column(name = "encoderVersion")
	private String encoderVersion;

	@JsonIgnore
	@ManyToMany(mappedBy = "device")
	private Set<Person> person = new HashSet<Person>();

	@JsonIgnore
	@OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Events> events = new HashSet<Events>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public Long getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(Long deviceID) {
		this.deviceID = deviceID;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	public String getEncoderVersion() {
		return encoderVersion;
	}

	public void setEncoderVersion(String encoderVersion) {
		this.encoderVersion = encoderVersion;
	}

	public Set<Person> getPerson() {
		return person;
	}

	public void setPerson(Set<Person> person) {
		this.person = person;
	}

	public Set<Events> getEvents() {
		return events;
	}

	public void setEvents(Set<Events> events) {
		this.events = events;
	}

}
