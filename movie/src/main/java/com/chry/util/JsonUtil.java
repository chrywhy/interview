package com.chry.util;

import org.json.JSONObject;

public class JsonUtil {
	public static String getString(JSONObject json, String key) {
		try {
			return json.getString(key);
		} catch(Exception e) {
			return "";
		}
	}
	
	public static int getInt(JSONObject json, String key) {
		try {
			return json.getInt(key);
		} catch(Exception e) {
			return 0;
		}
	}

	public static Double getDouble(JSONObject json, String key) {
		try {
			return json.getDouble(key);
		} catch(Exception e) {
			return 0D;
		}
	}
}
