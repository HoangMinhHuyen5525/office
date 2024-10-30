package com.huyenhm.person.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PersonDTO {
	private String name;
	private String fullName;
	private String employeeNo;

	@JsonIgnore
	private String userType;
	private Long idGroup;
	private Long doorRight;
	private String gender;
	private PersonValid valid;
	private List<RightPlan> rightPlan;

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

}
