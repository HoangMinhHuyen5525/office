package com.huyenhm.events;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventsRepo extends JpaRepository<Events, Long> {

	public Optional<Events> findBySerialNo(Long serialNo);

	@Query(value = "SELECT * FROM events WHERE events.date BETWEEN :startDate AND :endDate", nativeQuery = true)
	public List<Events> findEventsByDateRange(@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);
	
	@Query(nativeQuery = true, value = "SELECT * "
			+ "FROM events  WHERE  "
			+ "    (:searchTerm IS NULL OR "
			+ "	   (LOWER(CONCAT('%', name, '%')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "    LOWER(CONCAT('%', card_no, '%')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "    LOWER(CONCAT('%', employee_no, '%')) LIKE LOWER(CONCAT('%', :searchTerm, '%')))) AND "
			+ "    ((date >= COALESCE(:startDate, CURRENT_DATE)) AND "
			+ "            (date <= COALESCE(:endDate, CURRENT_DATE))) AND "
			+ "    ((time >= COALESCE(:startTime, '00:00:00')) AND "
			+ "        (time <= COALESCE(:endTime, CURRENT_TIME))) "
			+ "ORDER BY date, time;")
	public List<Events> searchEvents(
	        @Param("searchTerm") String searchTerm,
	        @Param("startDate") LocalDate startDate,
	        @Param("endDate") LocalDate endDate,
	        @Param("startTime") LocalTime startTime,
	        @Param("endTime") LocalTime endTime);

}
