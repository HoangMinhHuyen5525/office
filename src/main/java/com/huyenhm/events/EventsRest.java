package com.huyenhm.events;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huyenhm.common.ResponseBean;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/events")
public class EventsRest {

	@Autowired
	private EventsServ eventsServ;

	@GetMapping("/asc")
	public ResponseEntity<ResponseBean> getAscEvents(@RequestParam(value = "id", required = true) String id,
			@Parameter(description = "Begindate with format yyyy-mm-dd", example = "2024-10-01") @RequestParam(value = "startDate", required = true) @DateTimeFormat(style = "yyyy-mm-dd") String startDate,
			@Parameter(description = "BeginTime with format hh:mm:ss", example = "00:00:01") @RequestParam(value = "startTime", required = true) @DateTimeFormat(style = "hh:mm:ss") String startTime,
			@Parameter(description = "Enddate with format yyyy-mm-dd", example = "2024-10-31") @RequestParam(value = "endDate", required = true) @DateTimeFormat(style = "hh:mm:ss") String endDate,
			@Parameter(description = "EndTime with format hh:mm:ss", example = "23:59:59") @RequestParam(value = "endTime", required = true) @DateTimeFormat(style = "hh:mm:ss") String endTime) {

		List<Events> events = eventsServ.getAscEvents(id, startDate, startTime, endDate, endTime);
		ResponseBean response = new ResponseBean(200, "Asc success events", events);
		return ResponseEntity.ok(response);

	}

	@Operation(summary = "Get all events", description = "Return a list of events")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@GetMapping("/list")
	public ResponseEntity<ResponseBean> getAllEvents() {
		List<Events> events = eventsServ.getEvents();
		ResponseBean response = new ResponseBean(200, "Success", events);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Get a event by id", description = "Return a event by id")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@GetMapping("/{id}")
	public ResponseEntity<ResponseBean> getEvent(@PathVariable String id) {
		Events event = eventsServ.getEventsById(id);
		ResponseBean response = new ResponseBean(200, "User found", event);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Get a first-in last-out in a month", description = "Return a list first-in last-out event in month")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@GetMapping("/firstinlastout")
	public ResponseEntity<ResponseBean> getFirstInLastOut(@RequestParam String month) {
		List<EventsDTO> events = eventsServ.getFirstInLastOut(month);
		ResponseBean response = new ResponseBean(200, "Success", events);
		return ResponseEntity.ok(response);
	}
}
