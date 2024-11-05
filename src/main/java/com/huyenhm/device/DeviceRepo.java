package com.huyenhm.device;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepo extends JpaRepository<Device, Long> {

	public Optional<Device> findByIp(String ip);

	@Query(value = "SELECT * FROM DEVICE WHERE DEVICE.IP LIKE CONCAT(%, :key) "
			+ "OR DEVICE.NAME LIKE CONCAT(%, : key) "
			+ "OR DEVICE.deviceName LIKE CONCAT(%, : key) "
			+ "OR DEVICE.serialNumber LIKE CONCAT(%, : key) "
			+ "OR DEVICE.status LIKE CONCAT(%, : key) ", nativeQuery = true)
	public List<Device> searchByKey(@Param("key") String key);

}
