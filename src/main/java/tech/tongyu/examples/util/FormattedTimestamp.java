package tech.tongyu.examples.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class FormattedTimestamp {

    public static final String YYYYMMDD = "yyyy-MM-dd";
    public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String YYYYMMDDHHMMSSX = "yyyy-MM-dd'T'HH:mm:ss.S";
    public static final String YYYYMMDDHHMMSSXX = "yyyy-MM-dd'T'HH:mm:ss.SS";
    public static final String YYYYMMDDHHMMSSXXX = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static Timestamp getTimestamp(String timestamp, String timezone){
        if(timestamp == null || timestamp.equals("")){
            return new Timestamp(System.currentTimeMillis());
        }
        Calendar calendar = GregorianCalendar.getInstance();
        TimeZone tz;
        //默认的时区是UTC,启动的时候设置了时区为UTC,确保jpa连接的时候与postgresql时区一致
        //如果用户输入的时区不对，比如Asia/Shanghai输成了Asia/shanghai,时区会变成GMT,原先+8，会变成+0
        if(timezone == null || timezone.equals("")){
            tz = TimeZone.getTimeZone(calendar.getTimeZone().getID());
        }else{
            tz = TimeZone.getTimeZone(timezone);
        }
        calendar.setTimeZone(tz);
        try{
            if(Pattern.matches("\\d{4}-\\d{2}-\\d{2}", timestamp)){
                calendar.setTime(new SimpleDateFormat(YYYYMMDD).parse(timestamp));
            } else if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}", timestamp)){
                calendar.setTime(new SimpleDateFormat(YYYYMMDDHHMMSS).parse(timestamp));
            } else if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d", timestamp)) {
                calendar.setTime(new SimpleDateFormat(YYYYMMDDHHMMSSX).parse(timestamp));
            } else if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{2}", timestamp)){
                calendar.setTime(new SimpleDateFormat(YYYYMMDDHHMMSSXX).parse(timestamp));
            } else if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}", timestamp)) {
                calendar.setTime(new SimpleDateFormat(YYYYMMDDHHMMSSXXX).parse(timestamp));
            } else {
                throw new CustomerException(ErrorCode.INPUT_NOT_VALID, "Wrong pattern of timestamp.");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        calendar.add(Calendar.MILLISECOND,-(calendar.get(Calendar.ZONE_OFFSET)+calendar.get(Calendar.DST_OFFSET)));
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static String formatTimestampToIso(Timestamp timestamp){
        return timestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern(YYYYMMDDHHMMSSXXX));
    }

    public static Timestamp utcToLocal(Timestamp timestamp, String timezone) {
        if (Objects.equals(timestamp, null)){
            return new Timestamp(System.currentTimeMillis());
        }
        Calendar calendar = GregorianCalendar.getInstance();
        TimeZone tz;
        if (Objects.equals(timezone, null) || Objects.equals(timezone, "")){
            tz = TimeZone.getTimeZone(calendar.getTimeZone().getID());
        } else {
            tz = TimeZone.getTimeZone(timezone);
        }
        calendar.setTimeZone(tz);
        try{
            calendar.setTime(new SimpleDateFormat(YYYYMMDDHHMMSSXXX)
                    .parse(FormattedTimestamp.formatTimestampToIso(timestamp)));
        } catch (Exception e){
            e.printStackTrace();
        }
        calendar.add(Calendar.MILLISECOND,(calendar.get(Calendar.ZONE_OFFSET)-calendar.get(Calendar.DST_OFFSET)));
        return new Timestamp(calendar.getTimeInMillis());
    }
    
}
