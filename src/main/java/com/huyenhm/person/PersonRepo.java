package com.huyenhm.person;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepo extends JpaRepository<Person, Long> {

	public Optional<Person> findByEmployeeNo(String employeeNo);
}
