package com.huyenhm.person;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huyenhm.events.Events;
import com.huyenhm.group.Group;
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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Person")
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

	@Column(name = "idGroup")
	private Long idGroup;

//	@ManyToOne
//	@JoinColumn(name = "group_id", nullable = false)
//	private Group group;

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

	public Long getIdGroup() {
		return idGroup;
	}

	public void setIdGroup(Long idGroup) {
		this.idGroup = idGroup;
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

}
