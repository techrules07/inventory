package com.eloiacs.aapta.Inventory.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Utils {

    public static String convertDateToString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        if (date != null) {
            return dateFormat.format(date);
        }
        return null;
    }

    public static boolean checkExpired(String unixTime){
        Date startDate = null;
        Date endDate = null;
        startDate = getExpireDateMinusTenMinutes(unixTime);
        endDate = getExpireDate(unixTime);
        boolean expired = false;
        try {
            // Get the current date
            Date currentDate = new Date();

            // Check if the current date is between start and end dates (inclusive)
            expired = (currentDate.equals(startDate) || currentDate.after(startDate)) &&
                    (currentDate.equals(endDate) || currentDate.before(endDate));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return expired;
    }

    public static Date getExpireDateMinusTenMinutes(String unixTime){
        Date date = null;
        long unixTimestamp = Long.parseLong(unixTime);

        // Convert Unix timestamp to Instant
        Instant instant = Instant.ofEpochSecond(unixTimestamp);

        // Convert Instant to ZonedDateTime with system default time zone
        ZonedDateTime dateTime = instant.atZone(ZoneId.systemDefault());

        // Reduce 10 minutes from the ZonedDateTime
        ZonedDateTime newDateTime = dateTime.minusMinutes(10);

        // Format the date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = newDateTime.format(formatter);

        // Parse the formatted date string back to a ZonedDateTime
        ZonedDateTime parsedDateTime = ZonedDateTime.parse(formattedDate, formatter.withZone(ZoneId.systemDefault()));

        // Convert ZonedDateTime to Date
        date = Date.from(parsedDateTime.toInstant());
        return date;
    }

    public static Date getExpireDate(String unixTime){
        Date date = null;
        long unixTimestamp = Long.parseLong(unixTime);

        // Convert Unix timestamp to Instant
        Instant instant = Instant.ofEpochSecond(unixTimestamp);

        // Convert Instant to ZonedDateTime with system default time zone
        ZonedDateTime dateTime = instant.atZone(ZoneId.systemDefault());

        // Format the date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateTime.format(formatter);

        // Parse the formatted date string back to a ZonedDateTime
        ZonedDateTime parsedDateTime = ZonedDateTime.parse(formattedDate, formatter.withZone(ZoneId.systemDefault()));

        // Convert ZonedDateTime to Date
        date = Date.from(parsedDateTime.toInstant());
        return date;
    }
}
