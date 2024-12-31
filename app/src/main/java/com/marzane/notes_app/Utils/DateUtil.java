package com.marzane.notes_app.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class DateUtil {

    // obtener fecha y hora actual:
    // LocalDateTime now = LocalDateTime.now();

    // formato de fecha "2024-11-29 13:31:28"
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // recibe "2024-11-29 13:31:28"
    public static LocalDateTime StringToLocalDateTime(String dateString){
        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
        return dateTime;
    }

    public static String LocalDateTimeToString(LocalDateTime localDateTime){

        String dateTimeString = localDateTime.format(formatter);	//2019-03-28 14:47:33
        return dateTimeString;
    }

    public static String customDateFormatToString(LocalDateTime localDateTime, String format) {
        String date = "";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        date = localDateTime.format(formatter);

        return date;
    }

}
