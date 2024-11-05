package com.huyenhm.events;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventsRepo extends JpaRepository<Events, Long> {

	public Optional<Events> findBySerialNo(Long serialNo);;

	@Query(value = "SELECT * FROM events WHERE events.date BETWEEN :startDate AND :endDate", nativeQuery = true)
	public List<Events> findEventsByDateRange(@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);
}
