package com.huyenhm.org;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huyenhm.exception.ResourceNotFoundException;

@Service
public class OrgServ {
	@Autowired
	private OrgRepo groupRepo;

	public Org getGroupById(Long id) {
		return groupRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + id));
	}
}
