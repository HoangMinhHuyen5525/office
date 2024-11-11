package com.huyenhm.device;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepo extends JpaRepository<Device, Long> {

	public Optional<Device> findByIpOrName(String ip, String name);
	

	@Query(value = "SELECT * FROM DEVICE WHERE LOWER(CONCAT('%', DEVICE.IP, '%')) LIKE LOWER(CONCAT('%', :key, '%')) "
			+ "OR LOWER(CONCAT('%', DEVICE.NAME, '%')) LIKE LOWER(CONCAT('%', :key, '%')) "
			+ "OR LOWER(CONCAT('%', DEVICE.DEVICE_NAME, '%')) LIKE LOWER(CONCAT('%', :key, '%')) "
			+ "OR LOWER(CONCAT('%', DEVICE.SERIAL_NUMBER, '%')) LIKE LOWER(CONCAT('%', :key, '%')) "
			+ "OR LOWER(CONCAT('%', DEVICE.STATUS, '%')) LIKE LOWER(CONCAT('%', :key, '%')) ", nativeQuery = true)
	public List<Device> searchByKey(@Param("key") String key);

}
