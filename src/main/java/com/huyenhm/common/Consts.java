package com.huyenhm.common;

public class Consts {

	final static public String LOGIN = "/ISAPI/Security/userCheck";
	final static public String SEARCH_USER = "/ISAPI/AccessControl/UserInfo/Search?format=json";
	final static public String ADD_USER = "/ISAPI/AccessControl/UserInfo/Record?format=json";
	final static public String EDIT_USER = "/ISAPI/AccessControl/UserInfo/Modify?format=json";
	final static public String GET_TOTAL_USER = "/ISAPI/AccessControl/UserInfo/Count?format=json";
	final static public String SEARCH_EVENTS = "/ISAPI/AccessControl/AcsEvent?format=json";
	final static public String GET_DEVICE = "/ISAPI/System/DeviceInfo?format=json";
}
