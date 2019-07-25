package com.sam.model;

import java.io.IOException;
import java.util.Properties;

public class AuthList {
	private static Properties authList;
	
	static {
		authList = new Properties();
        try {
        	authList.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("authList.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void setAuth(String token) {
		authList.put(token, "Self regist");
	}
	
	public static boolean authCheck(String token) {
		return authList.containsKey(token);
	}
	
}
