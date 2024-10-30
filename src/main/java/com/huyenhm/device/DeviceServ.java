package com.huyenhm.device;

import java.util.List;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import com.huyenhm.common.Consts;
import com.huyenhm.common.JsonConverter;
import com.huyenhm.common.UtilFunction;
import com.huyenhm.data.CallApi;
import com.huyenhm.data.NetworkMonitor;
import com.huyenhm.exception.DuplicateException;
import com.huyenhm.exception.RequiredException;
import com.huyenhm.exception.ResourceNotFoundException;
import com.huyenhm.exception.UnauthorizedException;

@Service
public class DeviceServ {

	@Autowired
	private DeviceRepo deviceRepo;

	public List<Device> getAllDevices() {
		List<Device> devices = deviceRepo.findAll();
		for (Device device : devices) {
			device.setStatus(NetworkMonitor.pingDevice(device.getIp()));
		}
		return devices;
	}

	public Device getDeviceById(Long id) {
		return deviceRepo.findById(id).map(device -> {
			String status = NetworkMonitor.pingDevice(device.getIp());
			device.setStatus(status);
			deviceRepo.save(device);
			return device;
		}).orElseThrow(() -> new ResourceNotFoundException("Device not found with ID: " + id));
	}

	public Device updateDevice(Long id, String name) {
		Device device = deviceRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Device not found with ID: " + id));
		device.setName(name);
		return deviceRepo.save(device);
	}

	public void deleteDevice(Long id) {
		Device device = deviceRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Device not found with ID: " + id));
		deviceRepo.delete(device);
	}

	public List<Device> searchDevices(Device device) {
		ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues()
				.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING).withIgnoreCase();

		Example<Device> example = Example.of(device, matcher);
		return deviceRepo.findAll(example);
	}

	public List<Device> searchByIpOrName(String ip, String name) {
		if (ip != null && !ip.isEmpty()) {
			Device deviceByIp = new Device();
			deviceByIp.setIp(ip);
			return deviceRepo.findAll(Example.of(deviceByIp));
		} else if (name != null && !name.isEmpty()) {
			Device deviceByName = new Device();
			deviceByName.setName(name);
			return deviceRepo.findAll(Example.of(deviceByName));
		}
		return List.of();
	}

	public Device findByIp(String ip) {
		return deviceRepo.findByIp(ip);
	}

	public List<Object> findAllIP() {
		return deviceRepo.findAllIP();
	}

	public Device addDevice(DeviceDTO deviceDTO) {
		if (!UtilFunction.isNullOrEmpty(deviceDTO.getName()) && !UtilFunction.isNullOrEmpty(deviceDTO.getIp())
				&& !UtilFunction.isNullOrEmpty(deviceDTO.getPort())
				&& !UtilFunction.isNullOrEmpty(deviceDTO.getUsername())
				&& !UtilFunction.isNullOrEmpty(deviceDTO.getPassword())) {
			if (deviceRepo.checkByIp(deviceDTO.getIp()) == null) {
				return saveDevice(deviceDTO);
			} else {
				throw new DuplicateException("This ip " + deviceDTO.getIp() + " have existed");
			}
		} else {
			if (UtilFunction.isNullOrEmpty(deviceDTO.getIp())) {
				throw new RequiredException("Fill ip");
			} else if (UtilFunction.isNullOrEmpty(deviceDTO.getName())) {
				throw new RequiredException("Fill name");
			} else if (UtilFunction.isNullOrEmpty(deviceDTO.getPort())) {
				throw new RequiredException("Fill port");
			} else if (UtilFunction.isNullOrEmpty(deviceDTO.getUsername())) {
				throw new RequiredException("Fill username");
			} else if (UtilFunction.isNullOrEmpty(deviceDTO.getPassword())) {
				throw new RequiredException("Fill password");
			} else {
				throw new RequiredException("Fill form");
			}
		}
	}

	public Device saveDevice(DeviceDTO deviceDTO) {

		String ip = deviceDTO.getIp();
		String port = deviceDTO.getPort();
		String username = deviceDTO.getUsername();
		String password = deviceDTO.getPassword();
		String method = "GET";

		List<String> responseList = null;
		try {
			responseList = CallApi.callApi(ip, port, Consts.GET_DEVICE, method, username, password, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String responseBean = responseList.get(1);
		if (responseList.get(0).equals("200")) {
			Device device = new Device();
			JSONObject response = JsonConverter.XMLConverted(responseBean);
			device.setName(deviceDTO.getName());
			device.setIp(deviceDTO.getIp());
			device.setPort(deviceDTO.getPort());
			device.setUsername(deviceDTO.getUsername());
			device.setPassword(deviceDTO.getPassword());
			device.setStatus(NetworkMonitor.pingDevice(deviceDTO.getIp()));
			device.setDeviceID(Long.parseLong(response.get("deviceID").toString()));
			device.setDeviceName((response.get("deviceName").toString()));
			device.setEncoderVersion(response.get("encoderVersion").toString());
			device.setFirmwareVersion(response.get("firmwareVersion").toString());
			device.setMacAddress(response.get("macAddress").toString());
			device.setModel(response.get("model").toString());
			device.setSerialNumber(response.get("serialNumber").toString());

			return deviceRepo.save(device);
		} else {
			throw new UnauthorizedException(responseBean);
		}
	}
}
