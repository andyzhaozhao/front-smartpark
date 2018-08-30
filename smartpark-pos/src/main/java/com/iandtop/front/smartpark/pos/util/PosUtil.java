package com.iandtop.front.smartpark.pos.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author andyzhao
 */
public class PosUtil {

    public static String getCurrentTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        return df.format(new Date());
    }

    /**
     * 判断source是否大于当前时间
     * @param source
     * @return
     */
    public static Boolean sourceBiggerThanCurrent(String source) {
        // String s1 = "2008-01-25 09:12:09";
        //String s2 = "2008-01-29 09:12:11";

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(source));
        } catch (java.text.ParseException e) {
            System.err.println("日期格式不正确");
        }
        int result = c1.compareTo(c2);
        if (result == 0) {
            System.out.println("source相等now");
            return false;
        } else if (result < 0) {
            System.out.println("source小于now");
            return false;
        }else{
            System.out.println("source大于now");
            return true;
        }
    }

    public static String getCurrentTimeWithoutDate(){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置日期格式
        return df.format(new Date());
    }
    public static String getCurrentDate(){
        String temp_str="";
        Date dt = new Date();
        //最后的aa表示“上午”或“下午”    HH表示24小时制    如果换成hh表示12小时制
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        temp_str=sdf.format(dt);
        return temp_str;
    }
}
