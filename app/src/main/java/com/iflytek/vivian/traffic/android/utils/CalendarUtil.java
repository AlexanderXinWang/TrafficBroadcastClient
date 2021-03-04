package com.iflytek.vivian.traffic.android.utils;

import java.util.Calendar;

public class CalendarUtil {


    private final static String[] weekName=new String[]{"","周日","周一","周二","周三","周四","周五","周六"};
    public static String format(Calendar calendar) {
        String string = String.format("%04d-%02d-%02d %s", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),weekName[calendar.get(Calendar.DAY_OF_WEEK)]);
        return string;
    }

}
