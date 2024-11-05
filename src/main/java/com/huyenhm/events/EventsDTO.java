package com.huyenhm.events;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventsDTO {
	private LocalDate date;
	private LocalTime checkin;
	private LocalTime checkout;
	private String name;
	private Long person_id;
	private String employeeNo;

	public EventsDTO(LocalDate date, LocalTime checkin, LocalTime checkout, String name, Long person_id,
			String employeeNo) {
		this.date = date;
		this.checkin = checkin;
		this.checkout = checkout;
		this.name = name;
		this.person_id = person_id;
		this.employeeNo = employeeNo;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getCheckin() {
		return checkin;
	}

	public void setCheckin(LocalTime checkin) {
		this.checkin = checkin;
	}

	public LocalTime getCheckout() {
		return checkout;
	}

	public void setCheckout(LocalTime checkout) {
		this.checkout = checkout;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getPerson_id() {
		return person_id;
	}

	public void setPerson_id(Long person_id) {
		this.person_id = person_id;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

}
