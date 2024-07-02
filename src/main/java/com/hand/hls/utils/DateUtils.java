package com.hand.hls.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * @author ferry
 *         日期处理工具类
 *         注意:LocalDate只包含日期,没有时间
 */

public class DateUtils {
    /**
     * LocalDate 转化 Date
     */
    public static Date format(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Date 转化 LocalDate
     */
    public static LocalDate format(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * String 转化 Date
     */
    public static Date format(CharSequence text) {
        return format(LocalDate.parse(text));
    }

    /**
     * Date 转化 String
     */
    public static String formatString(Date date) {
        return format(date).toString();
    }

    /**
     * Date 追加天数
     */
    public static Date plusDays(Date date, long daysToAdd) {
        date = format(format(date).plusDays(daysToAdd));
        return date;
    }

    /**
     * Date 追加周
     */
    public static Date plusWeeks(Date date, long weeksToAdd) {
        date = format(format(date).plusWeeks(weeksToAdd));
        return date;
    }

    /**
     * Date 追加月
     */
    public static Date plusMonths(Date date, long monthsToAdd) {
        date = format(format(date).plusMonths(monthsToAdd));
        return date;
    }

    /**
     * Date 追加年
     */
    public static Date plusYears(Date date, long yearsToAdd) {
        date = format(format(date).plusYears(yearsToAdd));
        return date;
    }

    /**
     * Date 减天数
     */
    public static Date minusDays(Date date, long daysToSubtract) {
        date = format(format(date).minusDays(daysToSubtract));
        return date;
    }

    /**
     * Date 减周
     */
    public static Date minusWeeks(Date date, long weeksToSubtract) {
        date = format(format(date).minusWeeks(weeksToSubtract));
        return date;
    }

    /**
     * Date 减月
     */
    public static Date minusMonths(Date date, long monthsToSubtract) {
        date = format(format(date).minusMonths(monthsToSubtract));
        return date;
    }

    /**
     * Date 减年
     */
    public static Date minusYears(Date date, long yearsToSubtract) {
        date = format(format(date).minusYears(yearsToSubtract));
        return date;
    }

    /**
     * 根据指定年月日获取日期
     */
    public static Date of(int year, int month, int days) {
        return format(LocalDate.of(year, month, days));
    }

    /**
     * 根据某年的第n天获取 LocalDate
     */
    public static Date ofYearDay(int year, int dayOfYear) {
        return format(LocalDate.ofYearDay(year, dayOfYear));
    }

    /**
     * 获取当前年份当前天的天数
     */
    public static int getDayOfYear(Date date) {
        return format(date).getDayOfYear();
    }

    /**
     * 获取日期的月
     */
    public static int getMonthValue(Date date) {
        return format(date).getMonthValue();
    }

    /**
     * 获取日期的年
     */
    public static int getYear(Date date) {
        return format(date).getYear();
    }

    /**
     * 获取日期的当前月份对应的天数
     */
    public static int getDayOfMonth(Date date) {
        return format(date).getDayOfMonth();
    }

    /**
     * 获取日期的星期几
     */
    public static int getDayOfWeek(Date date) {
        return format(date).getDayOfWeek().getValue();
    }

    /**
     * 替换为当月指定天
     * 若指定的为闰年二月30或者平年二月29会抛出异常
     */
    public static Date withDayOfMonth(Date date, int dayOfMonth) {
        date = format(format(date).withDayOfMonth(dayOfMonth));
        return date;
    }

    /**
     * 替换为当年指定天
     */
    public static Date withDayOfYear(Date date, int dayOfYear) {
        date = format(format(date).withDayOfYear(dayOfYear));
        return date;
    }

    /**
     * 替换为指定月
     * 若替换后的日期不合法,会自动合法化,例如:
     * sourceDate : 2000-01-31
     * 调用方法: withMonth(format(LocalDate.parse(sourceDate)), 2);
     * targetDate : 2000-02-29
     */
    public static Date withMonth(Date date, int month) {
        date = format(format(date).withMonth(month));
        return date;
    }

    /**
     * 替换为指定年
     * 若替换后的日期不合法,会自动合法化,例如:
     * sourceDate : 2000-02-29
     * 调用方法: withYear(format(LocalDate.parse(sourceDate)), 2018);
     * targetDate : 2018-02-28
     */
    public static Date withYear(Date date, int year) {
        date = format(format(date).withYear(year));
        return date;
    }

    /**
     * 是否为闰年
     */
    public static boolean isLeapYear(Date date) {
        return format(date).isLeapYear();
    }

    /**
     * 计算日期间隔
     * 若 d1 在 d2 后面,返回负数;相等返回0;否则返回正数
     */
    public static long between(Date d1, Date d2) {
        return format(d1).until(format(d2), DAYS);
    }

    /**
     * 计算日期间隔
     * 若 d1 在 d2 后面,返回负数;相等返回0;否则返回正数
     */
    public static long between(LocalDate d1, LocalDate d2) {
        return d1.until(d2, DAYS);
    }

    /**
     * 是否同一天
     */
    public static boolean isSameDay(Date d1, Date d2) {
        return format(d1).isEqual(format(d2));
    }

    /**
     * 判断 d1 是否在 d2 之前
     */
    public static boolean before(Date d1, Date d2) {
        return format(d1).isBefore(format(d2));
    }

    /**
     * 比较两个日期
     */
    public static int compare(Date d1, Date d2){
        return isSameDay(d1,d2) ? 0 : before(d1,d2) ? -1 : 1;
    }

}

