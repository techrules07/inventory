package com.eloiacs.aapta.Inventory.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static String convertDateToString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        if (date != null) {
            return dateFormat.format(date);
        }
        return null;
    }
}
