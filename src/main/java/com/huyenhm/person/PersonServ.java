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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.huyenhm.common.Consts;
import com.huyenhm.common.JsonConverter;
import com.huyenhm.common.UtilFunction;
import com.huyenhm.data.CallApi;
import com.huyenhm.exception.DuplicateException;
import com.huyenhm.exception.InvalidInputException;
import com.huyenhm.exception.ResourceNotFoundException;
import com.huyenhm.exception.UnauthorizedException;
import com.huyenhm.group.GroupRepo;
import com.huyenhm.person.dto.RightPlan;
import com.huyenhm.person.dto.PersonDTO;
import com.huyenhm.person.dto.PersonValid;

@Service
public class PersonServ {

	@Autowired
	private PersonRepo userRepo;

	@Autowired
	private GroupRepo groupRepo;

	public List<Person> getAllUsers() {
		return userRepo.findAll();
	}

	public Person getUserById(Long id) {
		return userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
	}

	public void deleteUser(Long id) {
		Person User = userRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
		userRepo.delete(User);
	}

	public List<Person> searchUsers(Person User) {
		ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues()
				.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING).withIgnoreCase();

		Example<Person> example = Example.of(User, matcher);
		return userRepo.findAll(example);
	}

	public Person getAscUser(String ip, String port, String username, String password) {
		String method = "POST";
		String body = "{\"UserInfoSearchCond\": {\"searchID\": \"1\",\"maxResults\": 40,\"searchResultPosition\": 0}}";

		Person newuser = new Person();
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
						newuser = saveUser(user, result);
					} else {
						Person user = new Person();
						newuser = saveUser(user, result);
					}
				}
			} else {
				throw new UnauthorizedException(responseBean.get(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newuser;
	}

	private Person saveUser(Person user, Map<String, Object> result) {
		user.setName(
				result.get("name") == null || result.get("name").equals("") ? null : result.get("name").toString());
		user.setEmployeeNo(result.get("employeeNo") == null || result.get("employeeNo").equals("") ? null
				: result.get("employeeNo").toString());
		user.setUserType(result.get("userType") == null || result.get("userType").equals("") ? null
				: result.get("userType").toString());
		user.setIdGroup(result.get("belongGroup") == null || result.get("belongGroup").equals("") ? null
				: Long.parseLong(result.get("belongGroup").toString()));
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
		userValid.setEnable(Boolean.parseBoolean(enable.get("enable") == null || enable.get("enable").equals("") ? null
				: enable.get("enable").toString()));

		System.out.println(enable.get("beginTime") + " " + enable.get("endTime"));
		userValid.setBeginDate(enable.get("beginTime") == null || enable.get("beginTime").equals("") ? null
				: UtilFunction.getDate(enable.get("beginTime").toString()));
		userValid.setBeginTime(enable.get("beginTime") == null || enable.get("beginTime").equals("") ? null
				: enable.get("beginTime").toString());
		userValid.setEndDate(enable.get("endTime") == null || enable.get("endTime").equals("") ? null
				: UtilFunction.getDate(enable.get("endTime").toString()));
		userValid.setEndTime(enable.get("endTime") == null || enable.get("endTime").equals("") ? null
				: enable.get("endTime").toString());
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
			rightPlan.setPlanTemplateNo(obj.get("planTemplateNo") == null || obj.get("planTemplateNo").equals("") ? null
					: Long.parseLong(obj.get("planTemplateNo").toString()));
			rightPlans.add(rightPlan);
		}
		user.setRightPlan(rightPlans);

		return userRepo.save(user);
	}

	public Person addUser(String ip, String port, String username, String password, PersonDTO userDTO) {
		String name = UtilFunction.validateInput(userDTO.getName(), "Name", true, "^[a-zA-Z]+$").toString();
		String fullName = UtilFunction.validateInput(userDTO.getFullName(), "Full name", true, "^[a-zA-Z]+$")
				.toString();
		String employeeNo = UtilFunction.validateInput(userDTO.getEmployeeNo(), "Employee No", true, "^[a-zA-Z0-9]+$")
				.toString();
		String userType = "normal";
		Long idGroup = Long
				.parseLong(UtilFunction.validateInput(userDTO.getIdGroup(), "ID Group", true, "^\\d+$").toString());
		Long doorRight = Long
				.parseLong(UtilFunction.validateInput(userDTO.getDoorRight(), "DoorRight", true, "^\\d+$").toString());
		String gender = UtilFunction.validateInput(userDTO.getGender(), "Gender", true, "^[a-zA-Z]+$").toString();

		PersonValid input = userDTO.getValid();
		Boolean enable = Boolean.parseBoolean(
				UtilFunction.validateInput(input.getEnable(), "enable", true, "^(true|false)$").toString());
		LocalDate beginDate = input.getBeginDate();

		LocalTime beginTime = input.parseBeginTime();
		LocalDate endDate = input.getEndDate();
		LocalTime endTime = input.parseEndTime();
		String timeType = "local";

		Long doorNo = null;
		Long planTemplateNo = null;
		List<RightPlan> inputRightPlans = userDTO.getRightPlan();
		for (RightPlan rightPlan : inputRightPlans) {
			doorNo = Long
					.parseLong(UtilFunction.validateInput(rightPlan.getDoorNo(), "Door no", true, "^\\d+$").toString());
			planTemplateNo = Long.parseLong(UtilFunction
					.validateInput(rightPlan.getPlanTemplateNo(), "Plan Template No", true, "^\\d+$").toString());
		}

		Person newUser = new Person();

		String method = "POST";
		String body = "{\"UserInfo\": " + "{\"employeeNo\": \"" + employeeNo + "\", " + "\"name\": \"" + name + "\", "
				+ "\"userType\": \"" + userType + "\", " + "\"closeDelayEnabled\": false, " + "\"Valid\": {"
				+ "\"enable\": " + enable + "," + "\"beginTime\": \"" + beginDate + "T" + beginTime + "\", "
				+ "\"endTime\": \"" + endDate + "T" + endTime + "\", " + "\"timeType\": \"local\""
				+ "}, \"belongGroup\": \"" + idGroup + "\", " + "\"doorRight\": \"" + doorRight + "\", "
				+ "\"RightPlan\": [{ " + "\"doorNo\": " + doorNo + "," + "\"planTemplateNo\": \"" + planTemplateNo
				+ "\" " + "}],\"gender\": \"" + gender + "\" }}";

		System.out.println(body);
		try {
			List<String> responseBean = CallApi.callApi(ip, port, Consts.ADD_USER, method, username, password, body);
			if (responseBean.get(0).equals("200")) {
				Person user = new Person();
				user.setName(name);
				user.setFullName(fullName);
				user.setEmployeeNo(employeeNo);
				user.setUserType(userType);
				user.setIdGroup(idGroup);
				user.setDoorRight(doorRight);
				user.setGender(gender);

				PersonValid userValid = new PersonValid();
				userValid.setEnable(enable);
				System.out.println(beginDate);
				userValid.setBeginDate(beginDate);
				userValid.setBeginTime(beginTime.toString());
				userValid.setEndDate(endDate);
				userValid.setEndTime(endTime.toString());
				userValid.setTimeType(timeType);

				user.setValid(userValid);

				List<RightPlan> rightPlans = new ArrayList<RightPlan>();
				for (RightPlan rightPlan : rightPlans) {
					rightPlan.setDoorNo(doorNo);
					rightPlan.setPlanTemplateNo(planTemplateNo);
					rightPlans.add(rightPlan);
				}
				user.setRightPlan(rightPlans);

				newUser = userRepo.save(user);

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
			e.printStackTrace();
		}
		return newUser;
	}

	public Person updatePerson(String ip, String port, String username, String password, PersonDTO userDTO) {
		String name = UtilFunction.validateInput(userDTO.getName(), "Name", true, "^[a-zA-Z]+$").toString();
		String fullName = UtilFunction.validateInput(userDTO.getFullName(), "Full name", true, "^[a-zA-Z]+$")
				.toString();
		String employeeNo = UtilFunction.validateInput(userDTO.getEmployeeNo(), "Employee No", true, "^[a-zA-Z0-9]+$")
				.toString();
		String userType = "normal";
		Long idGroup = Long
				.parseLong(UtilFunction.validateInput(userDTO.getIdGroup(), "ID Group", true, "^\\d+$").toString());
		Long doorRight = Long
				.parseLong(UtilFunction.validateInput(userDTO.getDoorRight(), "DoorRight", true, "^\\d+$").toString());
		String gender = UtilFunction.validateInput(userDTO.getGender(), "Gender", true, "^[a-zA-Z]+$").toString();

		PersonValid input = userDTO.getValid();
		Boolean enable = Boolean.parseBoolean(
				UtilFunction.validateInput(input.getEnable(), "enable", true, "^(true|false)$").toString());
		LocalDate beginDate = input.getBeginDate();

		LocalTime beginTime = input.parseBeginTime();
		LocalDate endDate = input.getEndDate();
		LocalTime endTime = input.parseEndTime();
		String timeType = "local";

		Long doorNo = null;
		Long planTemplateNo = null;
		List<RightPlan> inputRightPlans = userDTO.getRightPlan();
		for (RightPlan rightPlan : inputRightPlans) {
			doorNo = Long
					.parseLong(UtilFunction.validateInput(rightPlan.getDoorNo(), "Door no", true, "^\\d+$").toString());
			planTemplateNo = Long.parseLong(UtilFunction
					.validateInput(rightPlan.getPlanTemplateNo(), "Plan Template No", true, "^\\d+$").toString());
		}

		Person newUser = new Person();

		String method = "PUT";
		String body = "{\"UserInfo\": " + "{\"employeeNo\": \"" + employeeNo + "\", " + "\"name\": \"" + name + "\", "
				+ "\"userType\": \"" + userType + "\", " + "\"closeDelayEnabled\": false, " + "\"Valid\": {"
				+ "\"enable\": " + enable + "," + "\"beginTime\": \"" + beginDate + "T" + beginTime + "\", "
				+ "\"endTime\": \"" + endDate + "T" + endTime + "\", " + "\"timeType\": \"local\""
				+ "}, \"belongGroup\": \"" + idGroup + "\", " + "\"doorRight\": \"" + doorRight + "\", "
				+ "\"RightPlan\": [{ " + "\"doorNo\": " + doorNo + "," + "\"planTemplateNo\": \"" + planTemplateNo
				+ "\" " + "}],\"gender\": \"" + gender + "\" }}";

		System.out.println(body);
		List<String> responseBean = null;
		if (userRepo.findByEmployeeNo(employeeNo).isEmpty()) {
			throw new ResourceNotFoundException(employeeNo + " not found");
		}
		try {
			responseBean = CallApi.callApi(ip, port, Consts.EDIT_USER, method, username, password, body);
			if (responseBean.get(0).equals("200")) {

				Person user = userRepo.findByEmployeeNo(employeeNo).get();
				user.setName(name);
				user.setFullName(fullName);
				user.setUserType(userType);
				user.setIdGroup(idGroup);
				user.setDoorRight(doorRight);
				user.setGender(gender);

				PersonValid userValid = new PersonValid();
				userValid.setEnable(enable);
				System.out.println(beginDate);
				userValid.setBeginDate(beginDate);
				userValid.setBeginTime(beginTime.toString());
				userValid.setEndDate(endDate);
				userValid.setEndTime(endTime.toString());
				userValid.setTimeType(timeType);

				user.setValid(userValid);

				List<RightPlan> rightPlans = new ArrayList<RightPlan>();
				for (RightPlan rightPlan : rightPlans) {
					rightPlan.setDoorNo(doorNo);
					rightPlan.setPlanTemplateNo(planTemplateNo);
					rightPlans.add(rightPlan);
				}
				user.setRightPlan(rightPlans);

				newUser = userRepo.save(user);

			}
			System.out.println(responseBean.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			if (responseBean.get(0).equals("401")) {
				throw new UnauthorizedException("Unauthorized");
			} else {
				System.out.println(responseBean.get(1));
//				JSONObject response = JsonConverter.getJSON(responseBean.get(1));
//				JSONObject statusString = (JSONObject) response.get("statusString");
//				JSONObject subStatusCode = (JSONObject) response.get("subStatusCode");
//				JSONObject errorMsg = (JSONObject) response.get("errorMsg");
//				throw new InvalidInputException(statusString + " " + subStatusCode + " " + errorMsg);
			}
		}
		return newUser;
	}
}
