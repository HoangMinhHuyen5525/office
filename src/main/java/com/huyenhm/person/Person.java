package com.huyenhm.person;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huyenhm.device.Device;
import com.huyenhm.events.Events;
import com.huyenhm.org.Org;
import com.huyenhm.person.dto.RightPlan;
import com.huyenhm.person.dto.PersonValid;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "person")
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "fullName")
	private String fullName;

	@Column(name = "employeeNo", unique = true)
	private String employeeNo;

	@Column(name = "userType")
	private String userType;

	@Column(name = "org_id")
	private Long org_id;

	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "person_org", joinColumns = @JoinColumn(name = "person_id"), inverseJoinColumns = @JoinColumn(name = "org_id"))
	private Set<Org> org = new HashSet<Org>();

	@Column(name = "doorRight")
	private Long doorRight;

	@Column(name = "gender")
	private String gender;

	@Column(name = "numOfCard")
	private int numOfCard;

	@Column(name = "numOfFP")
	private int numOfFP;

	@Column(name = "numofFace")
	private int numOfFace;

	@Embedded
	private PersonValid valid;

	@ElementCollection
	private List<RightPlan> rightPlan;

	@JsonIgnore
	@OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Events> events;

	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "person_device", joinColumns = @JoinColumn(name = "person_id"), inverseJoinColumns = @JoinColumn(name = "device_id"))
	private Set<Device> device = new HashSet<Device>();

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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
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

	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	public Set<Org> getOrg() {
		return org;
	}

	public void setOrg(Set<Org> org) {
		this.org = org;
	}

	public Long getDoorRight() {
		return doorRight;
	}

	public void setDoorRight(Long doorRight) {
		this.doorRight = doorRight;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getNumOfCard() {
		return numOfCard;
	}

	public void setNumOfCard(int numOfCard) {
		this.numOfCard = numOfCard;
	}

	public int getNumOfFP() {
		return numOfFP;
	}

	public void setNumOfFP(int numOfFP) {
		this.numOfFP = numOfFP;
	}

	public int getNumOfFace() {
		return numOfFace;
	}

	public void setNumOfFace(int numOfFace) {
		this.numOfFace = numOfFace;
	}

	public PersonValid getValid() {
		return valid;
	}

	public void setValid(PersonValid valid) {
		this.valid = valid;
	}

	public List<RightPlan> getRightPlan() {
		return rightPlan;
	}

	public void setRightPlan(List<RightPlan> rightPlan) {
		this.rightPlan = rightPlan;
	}

	public Set<Events> getEvents() {
		return events;
	}

	public void setEvents(Set<Events> events) {
		this.events = events;
	}

	public Set<Device> getDevice() {
		return device;
	}

	public void setDevice(Set<Device> device) {
		this.device = device;
	}

}
