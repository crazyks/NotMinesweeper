package com.crazyks.mt;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.format.Time;

public class DateHelper {
    
    private final static Date strToDate(final String dateStr) {
        if (dateStr == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (Exception e) {
            date = null;
            e.printStackTrace();
        }
        return date;
    }
    
    private final static long getMillis(Date date) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        return c.getTimeInMillis();
    } 
    
    final static int diffDate(String date, String date1) throws Exception {
        return (int) ((getMillis(strToDate(date)) - getMillis(strToDate(date1))) / (24 * 3600 * 1000));
    }
    
    final static String now() {
        Time time = new Time();
        time.set(System.currentTimeMillis());
        return String.format("%04d-%02d-%02d", time.year, (time.month + 1), time.monthDay);
    }
}
