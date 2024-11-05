package com.huyenhm.events;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huyenhm.common.Consts;
import com.huyenhm.common.JsonConverter;
import com.huyenhm.common.UtilFunction;
import com.huyenhm.data.CallApi;
import com.huyenhm.device.Device;
import com.huyenhm.device.DeviceRepo;
import com.huyenhm.exception.InvalidInputException;
import com.huyenhm.exception.ResourceNotFoundException;
import com.huyenhm.exception.UnauthorizedException;
import com.huyenhm.person.Person;
import com.huyenhm.person.PersonRepo;

@Service
public class EventsServ {

	@Autowired
	private EventsRepo eventsRepo;

	@Autowired
	private DeviceRepo deviceRepo;

	@Autowired
	private PersonRepo personRepo;

	public List<Events> getEvents() {
		return eventsRepo.findAll();
	}

	public Events getEventsById(String id) {
		long input = Long.parseLong(UtilFunction.validateInput(id, "ID", "long", true));
		return eventsRepo.findById(input)
				.orElseThrow(() -> new ResourceNotFoundException("Events not found with ID: " + id));
	}

	public Boolean deleteEvents(String id) {
		long input = Long.parseLong(UtilFunction.validateInput(id, "ID", "long", true));
		Optional<Events> event = eventsRepo.findById(input);
		if (event != null) {
			eventsRepo.deleteById(input);
		} else {
			throw new ResourceNotFoundException("Event not found with ID: " + id);
		}

		return true;
	}

	public List<Events> getAscEvents(String input, String startDate, String startTime, String lastDate,
			String lastTime) {
		List<Events> events = new ArrayList<Events>();

		long id = Long.parseLong(UtilFunction.validateInput(input, "ID", "long", true));
		Optional<Device> device = deviceRepo.findById(id);
		if (device.isEmpty()) {
			throw new ResourceNotFoundException("Device is not found with id: " + id);
		}
		String ip = device.get().getIp();
		String port = device.get().getPort();
		String username = device.get().getUsername();
		String password = device.get().getPassword();
		String method = "POST";

		try {
			LocalDate beginDate = LocalDate.parse(UtilFunction.validateInput(startDate, "Begindate", "date", true));
			LocalTime beginTime = LocalTime.parse(UtilFunction.validateInput(startTime, "Begintime", "time", true));
			LocalDate endDate = LocalDate.parse(UtilFunction.validateInput(lastDate, "Enddate", "date", true));
			LocalTime endTime = LocalTime.parse(UtilFunction.validateInput(lastTime, "End time", "time", true));

			if (endDate.isAfter(beginDate) || endDate.isEqual(beginDate) && endTime.isAfter(beginTime)) {
				throw new InvalidInputException("Enddate/endtime must after BeginDate/BeginTime");
			}
			long totalMatch = totalMatch(ip, port, method, username, password, beginDate.toString(),
					beginTime.toString(), endDate.toString(), endTime.toString());

			if (totalMatch == 0) {
				throw new UnauthorizedException("Unauthoried.");
			}
			for (int i = 1; i <= totalMatch; i += 24) {
				String body = "{\"AcsEventCond\": { \"searchID\": \"1\",\"searchResultPosition\": " + i
						+ ",\"maxResults\": 24,\"major\": 0,\"minor\": 0," + "\"startTime\": \"" + beginDate + "T"
						+ beginTime + "+07:00\",\"endTime\": \"" + endDate + "T" + endTime + "+07:00\"}}";

				List<String> result = CallApi.callApi(ip, port, Consts.SEARCH_EVENTS, method, username, password, body);
				if (result.get(0).equals("200")) {
					JSONObject response = JsonConverter.getJSON(result.get(1));
					JSONObject AcsEvent = (JSONObject) response.get("AcsEvent");
					List<JSONObject> InfoList = (List<JSONObject>) AcsEvent.get("InfoList");
					for (JSONObject info : InfoList) {
						if (info.get("currentVerifyMode").equals("cardOrFaceOrFp")) {
							Map<String, Object> e = info;
							Optional<Events> existEvent = eventsRepo
									.findBySerialNo(Long.parseLong(e.get("serialNo").toString()));
							if (existEvent.isPresent()) {
								Events event = existEvent.get();
								events.add(saveEvents(event, e, device));
							} else {
								Events event = new Events();
								events.add(saveEvents(event, e, device));
							}
						}
					}
				} else if (result.get(0).equals("401")) {
					throw new UnauthorizedException("Unauthorized");
				} else {
					JSONObject response = JsonConverter.getJSON(result.get(1));
					JSONObject statusString = (JSONObject) response.get("statusString");
					JSONObject subStatusCode = (JSONObject) response.get("subStatusCode");
					JSONObject errorMsg = (JSONObject) response.get("errorMsg");
					throw new InvalidInputException(statusString + " " + subStatusCode + " " + errorMsg);
				}
			}
		} catch (Exception e) {
			throw new InvalidInputException(e.getMessage());
		}

		return events;
	}

	private long totalMatch(String ip, String port, String method, String username, String password, String startDate,
			String startTime, String endDate, String endTime) {
		int searchResultPosition = 1;
		String body = "{\"AcsEventCond\": { \"searchID\": \"1\",\"searchResultPosition\": " + searchResultPosition
				+ ",\"maxResults\": 24,\"major\": 0,\"minor\": 0," + "\"startTime\": \"" + startDate + "T" + startTime
				+ "+07:00\",\"endTime\": \"" + endDate + "T" + endTime + "+07:00\"}}";

		long totalMatch = 0;

		List<String> result = null;
		try {
			result = CallApi.callApi(ip, port, Consts.SEARCH_EVENTS, method, username, password, body);
			if (result.get(0).equals("200")) {
				JSONObject response = JsonConverter.getJSON(result.get(1));
				JSONObject AcsEvent = (JSONObject) response.get("AcsEvent");
				totalMatch = (long) AcsEvent.get("totalMatches");
			}
		} catch (Exception e) {
			throw new InvalidInputException(e.getMessage());
		}
		return totalMatch;
	}

	private Events saveEvents(Events events, Map<String, Object> result, Optional<Device> device) {
		try {
			events.setCardNo(result.get("cardNo") == null || result.get("cardNo").equals("") ? null
					: result.get("cardNo").toString());
			events.setSerialNo(Long.parseLong(result.get("serialNo").toString()));
			events.setCardType(result.get("cardType") == null || result.get("cardType").equals("") ? null
					: result.get("cardType").toString());
			events.setCurrentVerifyMode(
					result.get("currentVerifyMode") == null || result.get("currentVerifyMode").equals("") ? null
							: result.get("currentVerifyMode").toString());
			events.setDoorNo(result.get("doorNo") == null || result.get("doorNo").equals("") ? null
					: Long.parseLong(result.get("doorNo").toString()));
			events.setName(
					result.get("name") == null || result.get("name").equals("") ? null : result.get("name").toString());
			events.setDate(result.get("time") == null || result.get("time").equals("") ? null
					: UtilFunction.getDate(result.get("time").toString()));
			events.setTime(result.get("time") == null || result.get("time").equals("") ? null
					: UtilFunction.getTime(result.get("time").toString()));
			events.setUserType(result.get("userType") == null || result.get("userType").equals("") ? null
					: result.get("userType").toString());
			events.setPictureURL(result.get("pictureURL") == null || result.get("pictureURL").equals("") ? null
					: result.get("pictureURL").toString());
			if (result.get("employeeNoString") == null || result.get("employeeNoString").equals("")) {
				events.setEmployeeNo(null);
				events.setPerson_id(null);
			} else {
				Optional<Person> existPerson = personRepo.findByEmployeeNo(result.get("employeeNoString").toString());
				if (existPerson.isPresent()) {
					events.setEmployeeNo(result.get("employeeNoString").toString());
					events.setPerson_id(existPerson.get().getId());
					Person person = existPerson.get();
					person.getEvents().add(events);
				} else {
					events.setEmployeeNo(null);
					events.setPerson_id(null);
				}
			}

			events.setDevice_id(device.get().getDeviceID());
			device.get().getEvents().add(events);

		} catch (Exception e) {
			throw new InvalidInputException("Invalid: " + e.getMessage());
		}
		return eventsRepo.save(events);
	}

	public List<EventsDTO> getFirstInLastOut(String month) {
		LocalDate startDate = LocalDate.parse(month + "-01");
		LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

		List<Events> events = eventsRepo.findEventsByDateRange(startDate, endDate);

		Map<LocalDate, Map<Person, List<Events>>> eventsGroupedByDayAndPerson = events.stream()
				.collect(Collectors.groupingBy(Events::getDate, Collectors.groupingBy(Events::getPerson)));

		List<EventsDTO> result = new ArrayList<>();
		for (Map.Entry<LocalDate, Map<Person, List<Events>>> dateEntry : eventsGroupedByDayAndPerson.entrySet()) {
			LocalDate date = dateEntry.getKey();
			for (Map.Entry<Person, List<Events>> personEntry : dateEntry.getValue().entrySet()) {
				Person person = personEntry.getKey();
				List<Events> personEvents = personEntry.getValue();

				LocalTime firstIn = personEvents.stream().map(Events::getTime).min(LocalTime::compareTo).orElse(null);

				LocalTime lastOut = personEvents.stream().map(Events::getTime).max(LocalTime::compareTo).orElse(null);

				EventsDTO record = new EventsDTO(date, firstIn != null ? firstIn : lastOut,
						lastOut != null ? lastOut : firstIn, person.getName(), person.getId(), person.getEmployeeNo());

				result.add(record);
			}
		}
		return result;
	}
}
