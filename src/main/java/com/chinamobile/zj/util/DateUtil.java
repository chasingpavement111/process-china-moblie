package com.chinamobile.zj.util;

import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.comm.ParseDateException;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * DateFormatUtils 缓存了formatter，无需重复创建资源，无并发问题
 */
public class DateUtil {

    public final static String DATE_NANO_TIME_REGEX = "yyyy-MM-dd HH:mm:ss.sss";
    public final static String DATE_TIME_REGEX = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_MINUTE_TIME_REGEX = "yyyy-MM-dd HH:mm";
    public final static String DATE_REGEX = "yyyy-MM-dd";
    public final static String DATE_REGEX_WITHOUT_SYMBOL = "yyyyMMdd";
    public final static String MONTH_REGEX = "yyyy-MM";
    public final static String MONTH_REGEX_WITHOUT_SYMBOL = "yyyyMM";

    public static String format(Date date, String pattern) {
        return DateFormatUtils.format(date, pattern);
    }

    public static Date parse(String dateStr, String pattern) {
        try {
            return DateUtils.parseDate(dateStr, pattern);
        } catch (ParseException e) {
            throw new ParseDateException(String.format("invalid string: [%s], format should be like [%s]", dateStr, pattern), e);
        }
    }

    public static String transDateToMonthStr(String dateStr) {
        Date date = parse(dateStr, DATE_REGEX);
        return format(date, MONTH_REGEX);
    }

    /**
     * 在当月的第几周，从 0 开始
     *
     * @return {0,1,2,3,4,5} 最多六周
     */
    public static int getWeekIndexInMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_MONTH) - 1;
    }

    /**
     * 检查某天 在 某个月中
     *
     * @param month 某个月
     * @param date  某天
     */
    public static void checkDateBelongToMonth(Date month, Date date) { // Date 入参，避免string入参需检查时间格式
        ParamException.isTrue(Objects.isNull(month) || Objects.isNull(date),
                String.format("all input should not null: month=[%s], date=[%s]", month, date));
        String monthStr = format(month, MONTH_REGEX);
        String dateMonthStr = format(date, MONTH_REGEX);
        ParamException.isTrue(BooleanUtils.isNotTrue(monthStr.equals(dateMonthStr)),
                String.format("date[%s] does not belong to month[%s]", format(date, DATE_REGEX), monthStr));
    }
}
