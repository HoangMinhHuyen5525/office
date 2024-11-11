package com.huyenhm.events;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
			@Parameter(description = "Begindate with format yyyy-mm-dd") @RequestParam(value = "startDate", required = true) @DateTimeFormat(style = "yyyy-mm-dd") String startDate,
			@Parameter(description = "BeginTime with format hh:mm:ss") @RequestParam(value = "startTime", required = true) @DateTimeFormat(style = "hh:mm:ss") String startTime,
			@Parameter(description = "Enddate with format yyyy-mm-dd") @RequestParam(value = "endDate", required = true) @DateTimeFormat(style = "hh:mm:ss") String endDate,
			@Parameter(description = "EndTime with format hh:mm:ss") @RequestParam(value = "endTime", required = true) @DateTimeFormat(style = "hh:mm:ss") String endTime) {

		eventsServ.getAscEvents(id, startDate, startTime, endDate, endTime);
		ResponseBean response = new ResponseBean(200, "Asc success events", "Asc success");
		return ResponseEntity.ok(response);

	}

	@Operation(summary = "Get all events", description = "Return a list of events")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@GetMapping("/list")
	public ResponseEntity<ResponseBean> getAllEvents(Pageable pageable) {
		Page<Events> events = eventsServ.getEvents(pageable);
		ResponseBean response = new ResponseBean(200, "Success", events);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Get a event by id", description = "Return a event by id")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@GetMapping("/{id}")
	public ResponseEntity<ResponseBean> getEvent(@PathVariable String id) {
		Events event = eventsServ.getEventsById(id);
		ResponseBean response = new ResponseBean(200, "Events found", event);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Search a event by key", description = "Return a list events")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@GetMapping("/search")
	public ResponseEntity<ResponseBean> searchEvents(@RequestParam(required = false) List<String> searchTerm,
			@Parameter(description = "Begindate with format yyyy-mm-dd") @RequestParam(value = "startDate", required = false) @DateTimeFormat(style = "yyyy-mm-dd") String startDate,
			@Parameter(description = "BeginTime with format hh:mm:ss") @RequestParam(value = "startTime", required = false) @DateTimeFormat(style = "hh:mm:ss") String startTime,
			@Parameter(description = "Enddate with format yyyy-mm-dd") @RequestParam(value = "endDate", required = false) @DateTimeFormat(style = "hh:mm:ss") String endDate,
			@Parameter(description = "EndTime with format hh:mm:ss") @RequestParam(value = "endTime", required = false) @DateTimeFormat(style = "hh:mm:ss") String endTime) {

		List<Events> events = eventsServ.searchEvents(searchTerm, startDate, endDate, startTime, endTime);
		if (events.isEmpty()) {
			ResponseBean response = new ResponseBean(200, "No events found", events);
			return ResponseEntity.ok(response);
		} else {
			ResponseBean response = new ResponseBean(200, "Events found", events);
			return ResponseEntity.ok(response);
		}
	}
}
