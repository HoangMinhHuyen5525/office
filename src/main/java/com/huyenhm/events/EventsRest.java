package com.huyenhm.events;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huyenhm.common.ResponseBean;
import com.huyenhm.device.Device;
import com.huyenhm.device.DeviceServ;

@RestController
@RequestMapping("/api/events")
public class EventsRest {

	@Autowired
	private EventsServ eventsServ;
	@Autowired
	DeviceServ deviceServ;

	@PostMapping
	public ResponseEntity<ResponseBean> getAscUser(@RequestParam(value = "ip") String ip,
			@RequestParam(value = "startTime") String startTime, @RequestParam(value = "endTime") String endTime) {
		Device device = deviceServ.findByIp(ip);
		String port = device.getPort();
		String username = device.getUsername();
		String password = device.getPassword();
		Events events = eventsServ.getAscEvents(ip, port, username, password, startTime, endTime);
		ResponseBean response = new ResponseBean(200, "Asc success user", "Success");
		return ResponseEntity.ok(response);

	}

	@GetMapping("/{employeeNo}")
	public ResponseEntity<ResponseBean> getEventsByEmployeeNo(@PathVariable String employeeNo) {
		List<Events> events = eventsServ.getEventsByEmployeeNo(employeeNo);
		ResponseBean response = new ResponseBean(200, "Success", events);
		return ResponseEntity.ok(response);
	}
}
