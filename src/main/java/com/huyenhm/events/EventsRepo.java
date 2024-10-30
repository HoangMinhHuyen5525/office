package com.huyenhm.events;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventsRepo extends JpaRepository<Events, Long> {

	public Optional<Events> findBySerialNo(Long serialNo);;

	public List<Events> findByEmployeeNo(String employeeNo);
}
