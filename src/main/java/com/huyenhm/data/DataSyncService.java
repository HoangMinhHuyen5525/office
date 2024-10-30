package com.huyenhm.data;

import java.util.List;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.huyenhm.common.ResponseBean;
import com.huyenhm.device.Device;
import com.huyenhm.device.DeviceDTO;
import com.huyenhm.device.DeviceServ;
import com.huyenhm.person.Person;
import com.huyenhm.person.PersonServ;

@Component
public class DataSyncService {

	@Autowired
	private DeviceServ deviceServ;

	@Autowired
	private PersonServ userServ;

//	@Scheduled(cron = "0 0 * * * ?")
//	@Scheduled(fixedRate = 60000)
	public ResponseBean syncData() {
		ResponseBean responseBean = new ResponseBean(0, null, null);
		List<Object> ips = deviceServ.findAllIP();
		for (int i = 0; i <= ips.size(); i++) {
			System.out.println(ips.get(i));
//			userData(ips.get(i).toString());

		}
		return responseBean;
	}

}
