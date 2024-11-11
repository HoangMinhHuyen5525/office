package com.huyenhm.person;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huyenhm.common.ResponseBean;
import com.huyenhm.person.dto.PersonDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/person")
//@CrossOrigin(origins = "http://localhost:8081")
public class PersonRest {
	@Autowired
	private PersonServ userServ;

	@Operation(summary = "Asc new user", description = "Return infomation list 0f user from user if connect user successed")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@GetMapping("/asc")
	public ResponseEntity<ResponseBean> getAscUser(@RequestParam(value = "id") String id) {
		List<Person> user = userServ.getAscUser(id);
		ResponseBean response = new ResponseBean(200, "Asc success user", user);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Get all user", description = "Return a list of user")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@GetMapping("/list")
	public ResponseEntity<ResponseBean> getAllUsers() {
		List<Person> Users = userServ.getAllUsers();
		ResponseBean response = new ResponseBean(200, "Success", Users);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Get a user by id", description = "Return a user by id")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@GetMapping("/{id}")
	public ResponseEntity<ResponseBean> getUser(@PathVariable String id) {
		Person user = userServ.getUserById(id);
		ResponseBean response = new ResponseBean(200, "User found", user);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Add a new user", description = "Return a new user")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@PostMapping("/create")
	public ResponseEntity<ResponseBean> createUser(@RequestParam(value = "id") String id,
			@RequestBody PersonDTO userDTO) {
		Person addUser = userServ.addUser(id, userDTO);
		ResponseBean response = new ResponseBean(200, "User added successfully", addUser);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Update a user", description = "Return new infomation for a user")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@PostMapping("/update")
	public ResponseEntity<ResponseBean> updateUser(@RequestParam(value = "id") String id,
			@RequestBody PersonDTO userDTO) {

		Person updatedUser = userServ.updatePerson(id, userDTO);
		ResponseBean response = new ResponseBean(200, "User updated successfully", updatedUser);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Delete a user", description = "Return successed delete a user")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@DeleteMapping("/{id}")
	public ResponseEntity<ResponseBean> deleteUser(@PathVariable String id) {
		ResponseBean response = new ResponseBean(0, null, null);
		if (userServ.deleteUser(id)) {
			response = new ResponseBean(204, "User deleted successfully", null);
		}
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Search relatively a user", description = "Return a list of relatively user base on search keyword for all column")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@GetMapping("/search")
	public ResponseEntity<ResponseBean> searchUsers(@RequestParam(value = "key") String key) {
		List<Person> users = userServ.searchUsers(key);
		ResponseBean response = new ResponseBean(200, "Search completed", users);
		return ResponseEntity.ok(response);
	}
}
