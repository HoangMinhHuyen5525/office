package com.huyenhm.events;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huyenhm.person.Person;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "events")
public class Events {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "date")
	private LocalDate date;

	@Column(name = "time")
	private LocalTime time;

	@Column(name = "serialNo")
	private Long serialNo;

	@Column(name = "cardNo")
	private String cardNo;

	@Column(name = "cardType")
	private String cardType;

	@Column(name = "name")
	private String name;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "person_id", referencedColumnName = "id", insertable = false, updatable = false)
	private Person person;

	@Column(name = "person_id")
	private Long person_id;

	@Column(name = "doorNo")
	private Long doorNo;

	@Column(name = "employeeNo")
	private String employeeNo;

	@Column(name = "userType")
	private String userType;

	@Column(name = "currentVerifyMode")
	private String currentVerifyMode;

	@Column(name = "pictureURL")
	private String pictureURL;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public Long getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(Long serialNo) {
		this.serialNo = serialNo;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Long getDoorNo() {
		return doorNo;
	}

	public void setDoorNo(Long doorNo) {
		this.doorNo = doorNo;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getCurrentVerifyMode() {
		return currentVerifyMode;
	}

	public void setCurrentVerifyMode(String currentVerifyMode) {
		this.currentVerifyMode = currentVerifyMode;
	}

	public String getPictureURL() {
		return pictureURL;
	}

	public void setPictureURL(String pictureURL) {
		this.pictureURL = pictureURL;
	}

	public Long getPerson_id() {
		return person_id;
	}

	public void setPerson_id(Long person_id) {
		this.person_id = person_id;
	}

}
