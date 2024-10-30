package com.huyenhm.common;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class JsonConverter {
	public static JSONObject getJSON(String s) {
		JSONParser parser = new JSONParser();
		JSONObject response = new JSONObject();
		try {
			response = (JSONObject) parser.parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return response;
	}

	public static JSONObject XMLConverted(String xmlContent) {
		JSONObject json = new JSONObject();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));
			Document document = builder.parse(inputStream);
			document.getDocumentElement().normalize();

			String[] tags = { "deviceName", "deviceID", "model", "serialNumber", "macAddress", "firmwareVersion",
					"firmwareReleasedDate", "encoderVersion", "encoderReleasedDate", "deviceType", "telecontrolID",
					"supportBeep", "localZoneNum", "alarmOutNum", "relayNum", "electroLockNum", "RS485Num",
					"marketType" };

			for (String tag : tags) {
				NodeList nodeList = document.getElementsByTagNameNS("*", tag);
				if (nodeList.getLength() > 0) {
					String value = nodeList.item(0).getTextContent();
					json.put(tag, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
}
