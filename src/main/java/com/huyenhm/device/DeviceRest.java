package com.huyenhm.device;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huyenhm.common.ResponseBean;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("api/device")
public class DeviceRest {

	@Autowired
	private DeviceServ deviceServ;

	@Operation(summary = "Add new device", description = "Return infomation of device if add successed")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@PostMapping
	public ResponseEntity<ResponseBean> addDevices(@RequestBody DeviceDTO deviceDTO) {
		Device device = deviceServ.addDevice(deviceDTO);
		ResponseBean responseBean = new ResponseBean(200, "Success", device);
		return ResponseEntity.ok(responseBean);
	}

	@Operation(summary = "Get all device", description = "Return a list of device")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@GetMapping
	public ResponseEntity<ResponseBean> getAllDevices() {
		List<Device> Devices = deviceServ.getAllDevices();
		ResponseBean response = new ResponseBean(200, "Success", Devices);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Get a device by id", description = "Return a device by id")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@GetMapping("/{id}")
	public ResponseEntity<ResponseBean> getDevice(@PathVariable Long id) {
		Device device = deviceServ.getDeviceById(id);
		ResponseBean response = new ResponseBean(200, "Device found", device);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Update a device", description = "Return new infomation for a device")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@PutMapping("/{id}")
	public ResponseEntity<ResponseBean> updateDevice(@PathVariable Long id, @RequestBody String name) {
		Device updatedDevice = deviceServ.updateDevice(id, name);
		ResponseBean response = new ResponseBean(200, "Device updated successfully", updatedDevice);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Delete a device", description = "Return successed delete a device")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@DeleteMapping("/{id}")
	public ResponseEntity<ResponseBean> deleteDevice(@PathVariable Long id) {
		deviceServ.deleteDevice(id);
		ResponseBean response = new ResponseBean(204, "Device deleted successfully", null);
		return ResponseEntity.status(204).body(response);
	}

	@Operation(summary = "Search relatively a device", description = "Return a list of relatively device base on search keyword for all column")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@PostMapping("/search")
	public ResponseEntity<ResponseBean> searchDevices(@RequestBody Device deviceExample) {
		List<Device> devices = deviceServ.searchDevices(deviceExample);
		ResponseBean response = new ResponseBean(200, "Search completed", devices);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Search a device by ip or name", description = "Return a device by ip or name")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@GetMapping("/searchByIpOrName")
	public ResponseEntity<ResponseBean> searchByIpOrName(@RequestParam(required = false) String ipAddress,
			@RequestParam(required = false) String name) {
		List<Device> devices = deviceServ.searchByIpOrName(ipAddress, name);
		ResponseBean response = new ResponseBean(200, "Search completed", devices);
		return ResponseEntity.ok(response);
	}
}
