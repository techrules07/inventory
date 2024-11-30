package com.eloiacs.aapta.Inventory.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class Utils {

    public static double roundToTwoDecimalPlaces(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public static String convertUTCDateTimeToISTString(Timestamp utcTimestamp) {

        if (utcTimestamp == null) {
            return "N/A"; // Or other default value
        }
        String converted_date = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String Date = dateFormat.format(utcTimestamp);;
        try {

            DateFormat utcFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = utcFormat.parse(Date);

            DateFormat currentTFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            currentTFormat.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()));

            converted_date =  currentTFormat.format(date);
        }catch (Exception e){ e.printStackTrace();}

        return converted_date;
    }

    public static String getCurrentTimeZone(){
        TimeZone tz = Calendar.getInstance().getTimeZone();
        return tz.getID();
    }

    public static String convertDateToString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        if (date != null) {
            return dateFormat.format(date);
        }
        return null;
    }

    public static String convertDateOnlyToString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
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

    public static File convertMultipartToFile(MultipartFile file) {
        File tempFolder = new File("temp-folder");
        if (!tempFolder.exists()) {
            tempFolder.mkdir();
        }
        File convFile = new File(tempFolder + "/" + file.getOriginalFilename());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(convFile);
            fos.write( file.getBytes() );
            fos.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return convFile;
    }


    public static int generateBarcodes() {
        Random random = new Random();
        int n = 10000000 + random.nextInt(90000000);

        return n;

    }

}
