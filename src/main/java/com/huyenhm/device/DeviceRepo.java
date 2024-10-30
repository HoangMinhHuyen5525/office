package com.huyenhm.device;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepo extends JpaRepository<Device, Long>{

	@Query(value = "SELECT * FROM DEVICES WHERE ip = :ip", nativeQuery = true)
	public Device findByIp(@Param("ip") String ip);
	
	@Query(value = "SELECT ip FROM DEVICES WHERE ip = :ip", nativeQuery = true)
	public Object checkByIp(@Param("ip") String ip);
	
	@Query(value = "SELECT IP FROM DEVICES", nativeQuery = true)
	public List<Object> findAllIP();
	
}
