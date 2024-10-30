package com.huyenhm.person.dto;

import jakarta.persistence.Embeddable;

@Embeddable
public class RightPlan {

	private Long planTemplateNo;
	private Long doorNo;

	public Long getPlanTemplateNo() {
		return planTemplateNo;
	}

	public void setPlanTemplateNo(Long planTemplateNo) {
		this.planTemplateNo = planTemplateNo;
	}

	public Long getDoorNo() {
		return doorNo;
	}

	public void setDoorNo(Long doorNo) {
		this.doorNo = doorNo;
	}

}
