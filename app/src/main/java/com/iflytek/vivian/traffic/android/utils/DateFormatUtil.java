

package com.iflytek.vivian.traffic.android.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {
    private static final String DEFAULT_FORMATTER="yyyy-MM-dd HH:mm:ss";


    public static String format(String formatter, Date date){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(formatter);
        return simpleDateFormat.format(date);
    }
    public static Date parse(String formatter, String string){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(formatter);
        try {
            return simpleDateFormat.parse(string);
        } catch (Exception e){
            return null;
        }
    }

    public static String format( Date date){
        return format(DEFAULT_FORMATTER,date);
    }
    public static Date parse( String string){
        return parse(DEFAULT_FORMATTER,string);
    }

}
