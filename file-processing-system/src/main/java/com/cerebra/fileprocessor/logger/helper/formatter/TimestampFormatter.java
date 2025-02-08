package com.cerebra.fileprocessor.logger.helper.formatter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampFormatter {

	private TimestampFormatter() {
	}

	public static final String yyyy_MM_dd_HH_mm_ss_SSSSSS = "yyyy_MM_dd_HH_mm_ss_SSSSSS";

	public static java.sql.Timestamp currentSQLTimestamp() {
		return new java.sql.Timestamp(new Date().getTime());
	}

	public static SimpleDateFormat getSimpleDateFormat(String format) {
		return new SimpleDateFormat(format);
	}

	public static String format(String format, Timestamp timestamp) {
		return getSimpleDateFormat(format).format(timestamp);
	}
}
