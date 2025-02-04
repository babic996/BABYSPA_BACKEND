package com.backend.babyspa.v1.utils;

public class Helper {

	public static boolean isNumeric(String text) {
		if (text == null)
			return false;
		try {
			Integer.parseInt(text);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
