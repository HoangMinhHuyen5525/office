package com.huyenhm.person;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepo extends JpaRepository<Person, Long> {

	public Optional<Person> findByEmployeeNo(String employeeNo);

	@Query(value = "SELECT * FROM PERSON WHERE LOWER(CONCAT('%', PERSON.NAME, '%')) LIKE LOWER(CONCAT('%', :key, '%')) OR "
			+ "LOWER(CONCAT('%', PERSON.FULL_NAME, '%')) LIKE LOWER(CONCAT('%', :key, '%')) OR "
			+ "LOWER(CONCAT('%', PERSON.EMPLOYEE_NO, '%')) LIKE LOWER(CONCAT('%', :key, '%'))", nativeQuery = true)
	public List<Person> searchByKey(@Param("key") String key);
}
