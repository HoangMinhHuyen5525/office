package com.huyenhm.common;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.StringReader;

import com.huyenhm.exception.InvalidInputException;

public class UtilFunction {

	public static boolean isNullOrEmpty(String str) {
		return str == null || str.isEmpty();
	}

	public static String validateInput(Object value, String key, String type, Boolean required) {
		if (value == null && required == true || value.toString().isEmpty() && required == true) {
			throw new InvalidInputException(key + "is required");
		}

		String stringValue = value.toString();

		String longPattern = "^[1-9][0-9]*$";
		String booleanPattern = "^(true|false)$";
		String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
		String timePattern = "^([01]?[0-9]|2[0-3]):([0-5]?[0-9]):([0-5]?[0-9])$";
		String genderPattern = "^(?i)(male|female)$";
		String ipPattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
		String portPattern = "^([0-5]?[0-9]{1,4}|6[0-4][0-9]{2}|65[0-4][0-9]|6553[0-5])$";

		switch (type.toLowerCase()) {
		case "string":
			return stringValue;
		case "long":
			if (!Pattern.matches(longPattern, stringValue)) {
				throw new InvalidInputException(value + " is invalid " + key + " format, must be number");
			}
			return stringValue;
		case "boolean":
			if (!Pattern.matches(booleanPattern, stringValue)) {
				throw new InvalidInputException(value + " is invalid " + key + " format, must be true/false");
			}
			return stringValue;
		case "date":
			if (!Pattern.matches(datePattern, stringValue)) {
				throw new InvalidInputException(value + " is invalid " + key + " format, must be yy:mm:dd format");
			}
			return stringValue;
		case "time":
			if (!Pattern.matches(timePattern, stringValue)) {
				throw new InvalidInputException(value + " is invalid " + key + " format, must be hh:mm:ss format");
			}
			return stringValue;
		case "gender":
			if (!Pattern.matches(genderPattern, stringValue)) {
				throw new InvalidInputException(value + " is invalid " + key + " format, must be male/female format");
			}
			return stringValue;
		case "ip":
			if (!Pattern.matches(ipPattern, stringValue)) {
				throw new InvalidInputException(value + " is invalid " + key + " format, must be IPv4 format");
			}
			return stringValue;
		case "port":
			if (!Pattern.matches(portPattern, stringValue)) {
				throw new InvalidInputException(value + " is invalid " + key + " format, must be port 0-65535 format");
			}
			return stringValue;
		default:
			break;
		}
		return stringValue;

	}

	public static LocalDate getDate(String dateStr) {
		return getTimestamp(dateStr).toLocalDate();
	}

	public static LocalTime getTime(String dateStr) {
		return getTimestamp(dateStr).toLocalTime();
	}

	private static LocalDateTime getTimestamp(String dateStr) {
		if (dateStr == null || "0".equals(dateStr)) {
			return LocalDateTime.now();
		}
		DateTimeFormatter formatterWithZone = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

		DateTimeFormatter formatterWithoutZone = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		try {
			ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateStr, formatterWithZone);
			return zonedDateTime.toLocalDateTime();
		} catch (DateTimeParseException e) {
			try {
				return LocalDateTime.parse(dateStr, formatterWithoutZone);
			} catch (DateTimeParseException ex) {
				throw new IllegalArgumentException("Invalid date format: " + ex.getMessage());
			}
		}
	}

	public static String removeVietnameseAccents(String str) {
		String normalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);

		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(normalizedString).replaceAll("");
	}

	public static boolean isValidXML(String xml) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.parse(new InputSource(new StringReader(xml)));
			return true;
		} catch (SAXException | IOException | ParserConfigurationException e) {
			return false;
		}
	}

	private static final int DEFAULT_PAGE = 0;
	private static final int DEFAULT_SIZE = 20;
	private static final String DEFAULT_SORT_FIELD = "id";
	private static final Sort.Order DEFAULT_SORT_ORDER = Sort.Order.asc(DEFAULT_SORT_FIELD);

	public static Pageable createPageable(PaginationRequest paginationRequest) {
		int page = (paginationRequest.getPage() != null) ? paginationRequest.getPage() : DEFAULT_PAGE;
		int size = (paginationRequest.getSize() != null) ? paginationRequest.getSize() : DEFAULT_SIZE;

		List<String> sortFields = (paginationRequest.getSort() != null && !paginationRequest.getSort().isEmpty())
				? paginationRequest.getSort()
				: Collections.singletonList(DEFAULT_SORT_FIELD); // Default to sorting by 'id'

		List<Sort.Order> orders = sortFields.stream().map(field -> new Sort.Order(Sort.Direction.ASC, field))
				.collect(Collectors.toList());

		Sort sort = Sort.by(orders);

		return PageRequest.of(page, size, sort);
	}
}
