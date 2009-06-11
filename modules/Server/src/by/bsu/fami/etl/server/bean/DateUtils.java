package by.bsu.fami.etl.server.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	
	protected final static SimpleDateFormat formatter = new SimpleDateFormat(
			"HH:mm:ss dd/MM/yyyy");

	public static String format(Date date) {
		if (date != null) {
			return formatter.format(date);
		}
		return null;
	}
}
