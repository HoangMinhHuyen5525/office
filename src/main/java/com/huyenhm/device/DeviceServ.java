package com.huyenhm.device;

import java.util.List;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.huyenhm.common.Consts;
import com.huyenhm.common.JsonConverter;
import com.huyenhm.common.UtilFunction;
import com.huyenhm.data.CallApi;
import com.huyenhm.data.NetworkMonitor;
import com.huyenhm.exception.InvalidInputException;
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

	public Device getDeviceById(String input) {
		long id = Long.parseLong(UtilFunction.validateInput(input, "ID", "long", true));
		return deviceRepo.findById(id).map(device -> {
			String status = NetworkMonitor.pingDevice(device.getIp());
			device.setStatus(status);
			deviceRepo.save(device);
			return device;
		}).orElseThrow(() -> new ResourceNotFoundException("Device not found with ID: " + id));
	}

	public Device updateDevice(String input, DeviceDTO deviceDTO) {
		long id = Long.parseLong(UtilFunction.validateInput(input, "ID", "long", true));
		Device device = deviceRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Device not found with ID: " + id));
		try {
			String name = UtilFunction.validateInput(deviceDTO.getName(), "Name", "string", true);
			String ip = UtilFunction.validateInput(deviceDTO.getIp(), "IP", "ip", true);
			String port = UtilFunction.validateInput(deviceDTO.getPort(), "Port", "port", true);
			String username = UtilFunction.validateInput(deviceDTO.getUsername(), "Username", "string", true);
			String password = UtilFunction.validateInput(deviceDTO.getPassword(), "Password", "string", true);

			device.setName(name);
			device.setIp(ip);
			device.setPort(port);
			device.setUsername(username);
			device.setPassword(password);
		} catch (Exception e) {
			throw new InvalidInputException("Invalid input format: " + e.getMessage());
		}

		return deviceRepo.save(device);
	}

	public Boolean deleteDevice(String input) {
		long id = Long.parseLong(UtilFunction.validateInput(input, "ID", "long", true));

		Optional<Device> device = deviceRepo.findById(id);
		if (device.isPresent()) {
			if (device.get().getPerson() != null) {
				throw new InvalidInputException("Device cannot be delete, its assoliated to person");
			}
			deviceRepo.deleteById(id);
		} else {
			throw new ResourceNotFoundException("Device not found with ID: " + id);
		}
		return true;
	}

	public List<Device> searchDevices(String key) {
		String normal = UtilFunction.removeVietnameseAccents(key);
		List<Device> devices = deviceRepo.searchByKey(normal);
		if (devices.isEmpty()) {
			throw new ResourceNotFoundException("No device found with key: " + key);
		} else {
			return devices;
		}
	}

	public Device addDevice(DeviceDTO deviceDTO) {
		Device newDevice = new Device();
		try {
			String name = UtilFunction.validateInput(deviceDTO.getName(), "Name", "string", true);
			String ip = UtilFunction.validateInput(deviceDTO.getIp(), "IP", "ip", true);
			String port = UtilFunction.validateInput(deviceDTO.getPort(), "Port", "port", true);
			String username = UtilFunction.validateInput(deviceDTO.getUsername(), "Username", "string", true);
			String password = UtilFunction.validateInput(deviceDTO.getPassword(), "Password", "string", true);

			Optional<Device> existDevice = deviceRepo.findByIpOrName(ip, name);
			if (existDevice.isPresent()) {
				throw new InvalidInputException("Device have existed with ip: " + ip + "or name: " + name);
			}

			if (NetworkMonitor.pingDevice(ip).equals("Offline")) {
				Device device = new Device();
				device.setName(name);
				device.setIp(ip);
				device.setPort(port);
				device.setUsername(username);
				device.setPassword(password);
				device.setStatus(NetworkMonitor.pingDevice(ip));
				newDevice = deviceRepo.save(device);
			} else {
				String method = "GET";
				List<String> responseBean = CallApi.callApi(ip, port, Consts.GET_DEVICE, method, username, password,
						null);
				if (responseBean.get(0).equals("200")) {
					Device device = new Device();
					JSONObject response = JsonConverter.XMLConverted(responseBean.get(1));
					device.setName(name);
					device.setIp(ip);
					device.setPort(port);
					device.setUsername(username);
					device.setPassword(password);
					device.setStatus(NetworkMonitor.pingDevice(ip));
					device.setDeviceIndex(Long.parseLong(response.get("deviceID").toString()));
					device.setDeviceName((response.get("deviceName").toString()));
					device.setEncoderVersion(response.get("encoderVersion").toString());
					device.setFirmwareVersion(response.get("firmwareVersion").toString());
					device.setMacAddress(response.get("macAddress").toString());
					device.setModel(response.get("model").toString());
					device.setSerialNumber(response.get("serialNumber").toString());

					newDevice = deviceRepo.save(device);
				}
			}

		} catch (Exception e) {
			throw new InvalidInputException(e.getMessage());
		}
		return newDevice;
	}
}
