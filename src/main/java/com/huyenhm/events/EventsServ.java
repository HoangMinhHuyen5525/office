package com.huyenhm.events;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huyenhm.common.Consts;
import com.huyenhm.common.JsonConverter;
import com.huyenhm.common.UtilFunction;
import com.huyenhm.data.CallApi;
import com.huyenhm.exception.ResourceNotFoundException;
import com.huyenhm.exception.UnauthorizedException;
import com.huyenhm.person.PersonRepo;

@Service
public class EventsServ {

	@Autowired
	private EventsRepo eventsRepo;

	@Autowired
	private PersonRepo personRepo;

	public List<Events> getEventsByEmployeeNo(String employeeNo) {
		List<Events> events = eventsRepo.findByEmployeeNo(employeeNo);
		if (events.isEmpty()) {
			throw new ResourceNotFoundException("No events found for employee with employeeNo: " + employeeNo);
		}
		return events;
	}

	public Events getAscEvents(String ip, String port, String username, String password, String startTime,
			String endTime) {
		Events events = new Events();
		String method = "POST";

		long totalMatch = totalMatch(ip, port, method, username, password, startTime, endTime);

		if (totalMatch == 0) {
			throw new UnauthorizedException(method);
		}
		for (int i = 1; i <= totalMatch; i += 24) {
			String body = "{\"AcsEventCond\": { \"searchID\": \"1\",\"searchResultPosition\": " + i
					+ ",\"maxResults\": 24,\"major\": 0,\"minor\": 0," + "\"startTime\": \"" + startTime
					+ "T00:00:00+07:00\",\"endTime\": \"" + endTime + "T23:59:59+07:00\"}}";

			try {
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
								events = saveEvents(event, e);
							} else {
								Events event = new Events();
								events = saveEvents(event, e);
							}
						}
					}
				} else {
					throw new UnauthorizedException(result.get(1));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return events;
	}

	private long totalMatch(String ip, String port, String method, String username, String password, String startTime,
			String endTime) {
		int searchResultPosition = 1;
		String body = "{\"AcsEventCond\": { \"searchID\": \"1\",\"searchResultPosition\": " + searchResultPosition
				+ ",\"maxResults\": 24,\"major\": 0,\"minor\": 0," + "\"startTime\": \"" + startTime
				+ "T00:00:00+07:00\",\"endTime\": \"" + endTime + "T23:59:59+07:00\"}}";

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
			e.printStackTrace();
		}
		return totalMatch;
	}

	private Events saveEvents(Events events, Map<String, Object> result) {
		events.setCardNo(result.get("cardNo") == null || result.get("cardNo").equals("") ? null : result.get("cardNo").toString());
		events.setSerialNo(Long.parseLong(result.get("serialNo").toString()));
		events.setCardType(result.get("cardType") == null || result.get("cardType").equals("") ? null : result.get("cardType").toString());
		events.setCurrentVerifyMode(result.get("currentVerifyMode") == null || result.get("currentVerifyMode").equals("") ? null : result.get("currentVerifyMode").toString());
		events.setDoorNo(result.get("cardNo") == null || result.get("cardNo").equals("") ? null : Long.parseLong(result.get("doorNo").toString()));
		events.setEmployeeNo(result.get("doorNo") == null || result.get("doorNo").equals("") ? null : result.get("employeeNoString").toString());
		events.setPerson_id(personRepo.findByEmployeeNo(result.get("employeeNoString").toString()).get().getId());
		events.setName(result.get("name") == null || result.get("name").equals("") ? null : result.get("name").toString());
		events.setDate(result.get("time") == null || result.get("time").equals("") ? null : UtilFunction.getDate(result.get("time").toString()));
		events.setTime(result.get("time") == null || result.get("time").equals("") ? null : UtilFunction.getTime(result.get("time").toString()));
		events.setUserType(result.get("userType") == null || result.get("userType").equals("") ? null : result.get("userType").toString());
		events.setPictureURL(result.get("pictureURL") == null || result.get("pictureURL").equals("") ? null : result.get("pictureURL").toString());

		return eventsRepo.save(events);
	}
}
