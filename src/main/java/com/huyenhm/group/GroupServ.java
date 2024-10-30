package com.huyenhm.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huyenhm.exception.ResourceNotFoundException;

@Service
public class GroupServ {
	@Autowired
	private GroupRepo groupRepo;

	public Group getGroupById(Long id) {
		return groupRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + id));
	}
}
