package com.huyenhm.person;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.json.simple.JSONArray;
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
import com.huyenhm.org.Org;
import com.huyenhm.org.OrgRepo;
import com.huyenhm.person.dto.RightPlan;
import com.huyenhm.person.dto.PersonDTO;
import com.huyenhm.person.dto.PersonValid;

@Service
public class PersonServ {

	@Autowired
	private PersonRepo userRepo;

	@Autowired
	private DeviceRepo deviceRepo;

	@Autowired
	private OrgRepo orgRepo;

	public List<Person> getAllUsers() {
		return userRepo.findAll();
	}

	public Person getUserById(String id) {
		long input = Long.parseLong(UtilFunction.validateInput(id, "ID", "long", true));
		return userRepo.findById(input)
				.orElseThrow(() -> new ResourceNotFoundException("Person not found with ID: " + id));
	}

	public Boolean deleteUser(String id) {
		long input = Long.parseLong(UtilFunction.validateInput(id, "ID", "long", true));
		Optional<Person> person = userRepo.findById(input);
		if (person != null) {
			if (!person.get().getEvents().isEmpty()) {
				throw new InvalidInputException("Person cannot be deleted. Its assosiated with events.");
			}
			userRepo.deleteById(input);
		} else {
			throw new ResourceNotFoundException("User not found with ID: " + id);
		}

		return true;
	}

	public List<Person> searchUsers(String key) {
		String normal = UtilFunction.removeVietnameseAccents(key);
		List<Person> persons = userRepo.searchByKey(normal);
		if (persons.isEmpty()) {
			throw new ResourceNotFoundException("No person found with key: " + key);
		}
		return persons;
	}

	public List<Person> getAscUser(String input) {
		String method = "POST";
		String body = "{\"UserInfoSearchCond\": {\"searchID\": \"1\",\"maxResults\": 40,\"searchResultPosition\": 0}}";

		List<Person> newuser = new ArrayList<Person>();
		long id = Long.parseLong(UtilFunction.validateInput(input, "ID", "long", true));
		Optional<Device> device = deviceRepo.findById(id);
		if (device.isEmpty()) {
			throw new ResourceNotFoundException("Device is not found with id: " + id);
		}
		String ip = device.get().getIp();
		String port = device.get().getPort();
		String username = device.get().getUsername();
		String password = device.get().getPassword();

		try {
			List<String> responseBean = CallApi.callApi(ip, port, Consts.SEARCH_USER, method, username, password, body);

			if (responseBean.get(0).equals("200")) {
				JSONObject response = JsonConverter.getJSON(responseBean.get(1));
				JSONObject UserInfoSearch = (JSONObject) response.get("UserInfoSearch");
				List<JSONObject> results = (List<JSONObject>) UserInfoSearch.get("UserInfo");
				for (JSONObject res : results) {
					Map<String, Object> result = res;
					Optional<Person> existUser = userRepo.findByEmployeeNo(result.get("employeeNo").toString());
					if (existUser.isPresent()) {
						Person user = existUser.get();
						newuser.add(saveUser(user, result, device));
					} else {
						Person user = new Person();
						newuser.add(saveUser(user, result, device));
					}
				}
			} else if (responseBean.get(0).equals("401")) {
				throw new UnauthorizedException("Unauthorized");
			} else {
				JSONObject response = JsonConverter.getJSON(responseBean.get(1));
				JSONObject statusString = (JSONObject) response.get("statusString");
				JSONObject subStatusCode = (JSONObject) response.get("subStatusCode");
				JSONObject errorMsg = (JSONObject) response.get("errorMsg");
				throw new InvalidInputException(statusString + " " + subStatusCode + " " + errorMsg);
			}
		} catch (Exception e) {
			throw new InvalidInputException("Invalid: " + e.getMessage());
		}
		return newuser;
	}

	private Person saveUser(Person user, Map<String, Object> result, Optional<Device> device) {
		try {
			user.setName(
					result.get("name") == null || result.get("name").equals("") ? null : result.get("name").toString());
			user.setUserType(result.get("userType") == null || result.get("userType").equals("") ? null
					: result.get("userType").toString());
			user.setGender(result.get("gender") == null || result.get("gender").equals("") ? null
					: result.get("gender").toString());
			user.setDoorRight(result.get("doorRight") == null || result.get("doorRight").equals("") ? null
					: Long.parseLong(result.get("doorRight").toString()));
			user.setNumOfCard(result.get("numOfCard") == null || result.get("numOfCard").equals("") ? null
					: Integer.parseInt(result.get("numOfCard").toString()));
			user.setNumOfFP(result.get("numOfFP") == null || result.get("numOfFP").equals("") ? null
					: Integer.parseInt(result.get("numOfFP").toString()));
			user.setNumOfFace(result.get("numOfFace") == null || result.get("numOfFace").equals("") ? null
					: Integer.parseInt(result.get("numOfFace").toString()));

			PersonValid userValid = new PersonValid();
			JSONObject enable = (JSONObject) result.get("Valid");
			userValid.setEnable(
					Boolean.parseBoolean(enable.get("enable") == null || enable.get("enable").equals("") ? null
							: enable.get("enable").toString()));

			userValid.setBeginDate(enable.get("beginTime") == null || enable.get("beginTime").equals("") ? null
					: UtilFunction.getDate(enable.get("beginTime").toString()));
			userValid.setBeginTime(enable.get("beginTime") == null || enable.get("beginTime").equals("") ? null
					: UtilFunction.getTime(enable.get("beginTime").toString()).toString());
			userValid.setEndDate(enable.get("endTime") == null || enable.get("endTime").equals("") ? null
					: UtilFunction.getDate(enable.get("endTime").toString()));
			userValid.setEndTime(enable.get("endTime") == null || enable.get("endTime").equals("") ? null
					: UtilFunction.getTime(enable.get("endTime").toString()).toString());
			userValid.setTimeType(enable.get("enable") == null || enable.get("enable").equals("") ? null
					: enable.get("timeType").toString());
			user.setValid(userValid);

			List<RightPlan> rightPlans = new ArrayList<RightPlan>();
			RightPlan rightPlan = new RightPlan();
			JSONArray jsonArray = (JSONArray) result.get("RightPlan");
			for (Object object : jsonArray) {
				JSONObject obj = (JSONObject) object;
				rightPlan.setDoorNo(obj.get("doorNo") == null || obj.get("doorNo").equals("") ? null
						: Long.parseLong(obj.get("doorNo").toString()));
				rightPlan.setPlanTemplateNo(
						obj.get("planTemplateNo") == null || obj.get("planTemplateNo").equals("") ? null
								: Long.parseLong(obj.get("planTemplateNo").toString()));
				rightPlans.add(rightPlan);
			}
			user.setRightPlan(rightPlans);

			user.setEmployeeNo(result.get("employeeNo") == null || result.get("employeeNo").equals("") ? null
					: result.get("employeeNo").toString());

			if (result.get("belongGroup") == null || result.get("belongGroup").equals("")) {
				user.setOrg_id(null);
			} else {
				Optional<Org> existOrg = orgRepo.findById(Long.parseLong(result.get("belongGroup").toString()));
				if (existOrg.isPresent()) {
					user.setOrg_id(Long.parseLong(result.get("belongGroup").toString()));
					Org org = existOrg.get();
					user.getOrg().add(org);
					org.getPerson().add(user);
				} else {
					user.setOrg_id(null);
				}
			}

			user.getDevice().add(device.get());
			device.get().getPerson().add(user);
		} catch (Exception e) {
			throw new InvalidInputException("Invalid format: " + e.getMessage());
		}
		return userRepo.save(user);
	}

	public Person addUser(String id, PersonDTO userDTO) {
		Person newUser = new Person();
		long input = Long.parseLong(UtilFunction.validateInput(id, "ID", "long", true));
		Optional<Device> device = deviceRepo.findById(input);
		if (device.isEmpty()) {
			throw new ResourceNotFoundException("Device is not found with id: " + id);
		}
		try {
			String ip = device.get().getIp();
			String port = device.get().getPort();
			String username = device.get().getUsername();
			String password = device.get().getPassword();
			String name = UtilFunction.validateInput(userDTO.getName(), "Name", "string", true);
			String fullname = UtilFunction.validateInput(userDTO.getFullName(), "FullName", "string", false);
			String employeeNo = UtilFunction.validateInput(userDTO.getEmployeeNo(), "employeeNo", "string", true);
			Optional<Person> existPerson = userRepo.findByEmployeeNo(employeeNo);
			if (existPerson.isPresent()) {
				throw new InvalidInputException("Person have existed with this employeeNo: " + employeeNo);
			}
			Long org_id = Long.parseLong(UtilFunction.validateInput(userDTO.getOrg_id(), "Org id", "long", true));
			Optional<Org> existOrg = orgRepo.findById(org_id);
			if (existOrg.isEmpty()) {
				throw new ResourceNotFoundException("Org is not found with id: " + org_id);
			}
			String userType = "normal";
			Long doorRight = Long
					.parseLong(UtilFunction.validateInput(userDTO.getDoorRight(), "DoorRight", "long", true));
			String gender = UtilFunction.validateInput(userDTO.getGender(), "Gender", "gender", true);
			PersonValid personValid = userDTO.getValid();
			Boolean enable = Boolean
					.parseBoolean(UtilFunction.validateInput(personValid.getEnable(), "Enable", "boolean", false));
			LocalDate beginDate = LocalDate
					.parse(UtilFunction.validateInput(personValid.getBeginDate(), "Begindate", "date", true));
			String beginTime = UtilFunction.validateInput(personValid.getBeginTime(), "Begintime", "time", true);
			LocalDate endDate = LocalDate
					.parse(UtilFunction.validateInput(personValid.getEndDate(), "Enddate", "date", true));
			String endTime = UtilFunction.validateInput(personValid.getBeginTime(), "End time", "time", true);

			if (endDate.isBefore(beginDate)
					|| endDate.isEqual(beginDate) && LocalTime.parse(endTime).isBefore(LocalTime.parse(beginTime))) {
				throw new InvalidInputException("Enddate/endtime must after BeginDate/BeginTime");
			}

			String timeType = "local";
			Long doorNo = null;
			Long planTemplateNo = null;
			List<RightPlan> inputRightPlans = userDTO.getRightPlan();
			for (RightPlan rightPlan : inputRightPlans) {
				doorNo = Long.parseLong(UtilFunction.validateInput(rightPlan.getDoorNo(), "Door No", "long", true));
				planTemplateNo = Long.parseLong(
						UtilFunction.validateInput(rightPlan.getPlanTemplateNo(), "Plan Template No", "long", true));
			}

			String method = "POST";
			String body = "{\"UserInfo\": " + "{\"employeeNo\": \"" + employeeNo + "\", " + "\"name\": \"" + name
					+ "\", " + "\"userType\": \"" + userType + "\", " + "\"closeDelayEnabled\": false, "
					+ "\"Valid\": {" + "\"enable\": " + enable + "," + "\"beginTime\": \"" + beginDate + "T" + beginTime
					+ "\", " + "\"endTime\": \"" + endDate + "T" + endTime + "\", " + "\"timeType\": \"local\""
					+ "}, \"belongGroup\": \"" + org_id + "\", " + "\"doorRight\": \"" + doorRight + "\", "
					+ "\"RightPlan\": [{ " + "\"doorNo\": " + doorNo + "," + "\"planTemplateNo\": \"" + planTemplateNo
					+ "\" " + "}],\"gender\": \"" + gender + "\" }}";

			List<String> responseBean = CallApi.callApi(ip, port, Consts.ADD_USER, method, username, password, body);
			if (responseBean.get(0).equals("200")) {
				Person user = new Person();
				user.setName(name);
				user.setFullName(fullname);
				user.setEmployeeNo(employeeNo);
				user.setUserType(userType);
				user.setDoorRight(doorRight);
				user.setGender(gender);

				PersonValid userValid = userDTO.getValid();
				userValid.setEnable(enable);
				userValid.setBeginDate(beginDate);
				userValid.setBeginTime(beginTime);
				userValid.setEndDate(endDate);
				userValid.setEndTime(endTime);
				userValid.setTimeType(timeType);

				user.setValid(userValid);

				List<RightPlan> rightPlans = userDTO.getRightPlan();
				for (RightPlan rightPlan : rightPlans) {
					rightPlan.setDoorNo(doorNo);
					rightPlan.setPlanTemplateNo(planTemplateNo);
					rightPlans.add(rightPlan);
				}
				user.setRightPlan(rightPlans);

				user.setOrg_id(org_id);
				Org org = existOrg.get();
				user.getOrg().add(org);
				org.getPerson().add(user);

				user.getDevice().add(device.get());
				device.get().getPerson().add(user);

				newUser = userRepo.save(user);
			}
		} catch (Exception e) {
			throw new InvalidInputException(e.getMessage());
		}

		return newUser;
	}

	public Person updatePerson(String id, PersonDTO userDTO) {
		Person newUser = null;
		long input = Long.parseLong(UtilFunction.validateInput(id, "ID", "long", true));
		Optional<Device> device = deviceRepo.findById(input);
		if (device.isEmpty()) {
			throw new ResourceNotFoundException("Device is not found with id: " + id);
		}
		try {
			String ip = device.get().getIp();
			String port = device.get().getPort();
			String username = device.get().getUsername();
			String password = device.get().getPassword();
			String name = UtilFunction.validateInput(userDTO.getName(), "Name", "string", true);
			String fullname = UtilFunction.validateInput(userDTO.getFullName(), "FullName", "string", false);
			String employeeNo = UtilFunction.validateInput(userDTO.getEmployeeNo(), "employeeNo", "string", true);
			Optional<Person> person = userRepo.findByEmployeeNo(employeeNo);
			if (person.isEmpty()) {
				throw new ResourceNotFoundException("Person is not found with employeeNo: " + employeeNo);
			}
			Long org_id = Long.parseLong(UtilFunction.validateInput(userDTO.getOrg_id(), "Org id", "long", true));
			Optional<Org> existOrg = orgRepo.findById(org_id);
			if (existOrg.isEmpty()) {
				throw new ResourceNotFoundException("Org is not found with id: " + org_id);
			}
			String userType = "normal";
			Long doorRight = Long
					.parseLong(UtilFunction.validateInput(userDTO.getDoorRight(), "DoorRight", "long", true));
			String gender = UtilFunction.validateInput(userDTO.getGender(), "Gender", "gender", true);
			PersonValid personValid = userDTO.getValid();
			Boolean enable = Boolean
					.parseBoolean(UtilFunction.validateInput(personValid.getEnable(), "Enable", "boolean", false));
			LocalDate beginDate = LocalDate
					.parse(UtilFunction.validateInput(personValid.getBeginDate(), "Begindate", "date", true));
			String beginTime = UtilFunction.validateInput(personValid.getBeginTime(), "Begintime", "time", true);
			LocalDate endDate = LocalDate
					.parse(UtilFunction.validateInput(personValid.getEndDate(), "Enddate", "date", true));
			String endTime = UtilFunction.validateInput(personValid.getBeginTime(), "End time", "time", true);

			if (endDate.isBefore(beginDate)
					|| endDate.isEqual(beginDate) && LocalTime.parse(endTime).isBefore(LocalTime.parse(beginTime))) {
				throw new InvalidInputException("Enddate/endtime must after BeginDate/BeginTime");
			}
			String timeType = "local";
			Long doorNo = null;
			Long planTemplateNo = null;
			List<RightPlan> inputRightPlans = userDTO.getRightPlan();
			for (RightPlan rightPlan : inputRightPlans) {
				doorNo = Long.parseLong(UtilFunction.validateInput(rightPlan.getDoorNo(), "Door No", "long", true));
				planTemplateNo = Long.parseLong(
						UtilFunction.validateInput(rightPlan.getPlanTemplateNo(), "Plan Template No", "long", true));
			}

			String method = "PUT";
			String body = "{\"UserInfo\": " + "{\"employeeNo\": \"" + employeeNo + "\", " + "\"name\": \"" + name
					+ "\", " + "\"userType\": \"" + userType + "\", " + "\"closeDelayEnabled\": false, "
					+ "\"Valid\": {" + "\"enable\": " + enable + "," + "\"beginTime\": \"" + beginDate + "T" + beginTime
					+ "\", " + "\"endTime\": \"" + endDate + "T" + endTime + "\", " + "\"timeType\": \"local\""
					+ "}, \"belongGroup\": \"" + org_id + "\", " + "\"doorRight\": \"" + doorRight + "\", "
					+ "\"RightPlan\": [{ " + "\"doorNo\": " + doorNo + "," + "\"planTemplateNo\": \"" + planTemplateNo
					+ "\" " + "}],\"gender\": \"" + gender + "\" }}";

			List<String> responseBean = CallApi.callApi(ip, port, Consts.EDIT_USER, method, username, password, body);
			if (responseBean.get(0).equals("200")) {
				Person user = person.get();
				user.setName(name);
				user.setFullName(fullname);
				user.setEmployeeNo(employeeNo);
				user.setUserType(userType);
				user.setOrg_id(org_id);
				user.setDoorRight(doorRight);
				user.setGender(gender);

				PersonValid userValid = new PersonValid();
				userValid.setEnable(enable);
				userValid.setBeginDate(beginDate);
				userValid.setBeginTime(beginTime);
				userValid.setEndDate(endDate);
				userValid.setEndTime(endTime);
				userValid.setTimeType(timeType);

				user.setValid(userValid);

				List<RightPlan> rightPlans = new ArrayList<RightPlan>();
				for (RightPlan rightPlan : rightPlans) {
					rightPlan.setDoorNo(doorNo);
					rightPlan.setPlanTemplateNo(planTemplateNo);
					rightPlans.add(rightPlan);
				}
				user.setRightPlan(rightPlans);

				user.setOrg_id(org_id);
				Org org = existOrg.get();
				user.getOrg().add(org);
				org.getPerson().add(user);

				user.getDevice().add(device.get());
				device.get().getPerson().add(user);
				newUser = userRepo.save(user);

			}
		} catch (Exception e) {
			throw new InvalidInputException("Invalid: " + e.getMessage());
		}

		return newUser;
	}
}
