package com.zgy.debugtool.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
	private static String TIME_DATE_TIME_STRING_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static long dateTimeStringToLong(String dateTime) {
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_DATE_TIME_STRING_FORMAT);
		Date dt2;
		try {
			dt2 = sdf.parse(dateTime);
			return dt2.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static String longToDateTimeString(long dateTimeMillis) {
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_DATE_TIME_STRING_FORMAT);
		Date dt = new Date(dateTimeMillis);
		return sdf.format(dt);
	}
}
