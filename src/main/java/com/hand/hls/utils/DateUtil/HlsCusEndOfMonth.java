package com.hand.hls.utils.DateUtil;


import com.hand.hls.exception.HlsCusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

/**
 * title:用于日期的处理类
 * description:用于日期的处理类
 * author: LiuTengfei
 * date:2019/1/16-15:57
 */
public class HlsCusEndOfMonth {
    /**
     * 日期格式
     */
    public static final SimpleDateFormat SDF=new SimpleDateFormat("yyyy-MM-dd");
    /**
     * 大月
     */
    public static final int[] BIGGER_MONTH ={1,3,5,7,8,10,12};
    /**
     * 小月
     */
    public static final int[] SMALLER_MONTH ={4,6,9,11};
    /**
     * 2月
     */
    public static final int FEBRUARY =2;

    /**
     * 对于judgeDate进行判断是否是月末，然后对currentDate做出日期矫正
     * 举例说明：1-31,2-28,3-31,4-30   --judgeDate是月末的情况
     *         1-30,2-28,3-30,4-30   --judgeDate不是月末的情况
     * @param judgeDate
     * @param currentDate
     * @return
     */
    public static Date calcEndOfMonth(Date judgeDate, Date currentDate) throws ParseException {

        //1.校验judgeDate是否是月末
        Boolean flag=isEndOfMonth(judgeDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        String lastDay=sdf.format(calendar.getTime());
        //获取currentDate的年，月
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;

        //如果是月末，那么currentDate应该获取的是该月份的月末
        if(flag){
            currentDate=sdf.parse(lastDay);
        }else{
            //如果不是月末，判断当前月份的最大day<judgeDate的day?day:judgeDate的day
            int b=calendar.getActualMaximum(Calendar.DATE);
            calendar.setTime(judgeDate);
            int a=calendar.get(Calendar.DAY_OF_MONTH);
            currentDate=b<a?currentDate:sdf.parse(year+"-"+month+"-"+a);
        }
        return currentDate;
    }

    /**
     * 使用日历函数判断当前日期是不是月末,这种方法存在一个弊端，那就是如果输入 2019-4-31 本来我想获得的是2019-4-30所以月末是30，但是使用
     * 日历函数就会得到31，因为日历函数会自动将2019-4-31转化成2019-5-1。
     * 因此需要将下面这个isEndOfMonth()算法进行优化
     * @param date
     * @return
     */
    public static Boolean isEndOfMonth(Date date){
        //矫正当前日期格式 比如说：2019-2-31 肯定是不正确的格式，那么就矫正成 2019-2-28，2019-4-31就矫正成2019-4-30

        // 获取Calendar实例
        Calendar calendar = Calendar.getInstance();
        //设置时间
        calendar.setTime(date);

        int b=calendar.get(Calendar.DAY_OF_MONTH);
        //获取本月最大日期
        int a=calendar.getActualMaximum(Calendar.DATE);
        if(a<=b){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 传入一个str格式为{yyyy-MM-dd}返回一个正确日期的字符串
     * 举例说明：2019-4-31 返回2019-4-30,如果是正常的日期 2019-4-30 那么就直接返回
     * @param str
     * @return
     */
    public static String correctDate (String str) throws Exception {
        //获取字符串的年份
        int year=cutYear(str);
        //获取字符串的月份
        int month=cutMonth(str);

        //天
        int day =cutDay(str);

        String monthStr ="";
        if(month<10){
            monthStr="0"+month;
            str=year+"-"+monthStr+"-"+day;
        }
        //如果该字符串转化成日期之后和转化之前是一致的那么就说明是正常格式的日期
        if(SDF.format(SDF.parse(str)).equalsIgnoreCase(str)){
            return str;
        }
        //判断是大月，小月或者是2月
        if(month<1 ||  month>12){
            throw new HlsCusException("月份不正确");
        }

        for(int i=0;i<BIGGER_MONTH.length;i++){
            if(month==BIGGER_MONTH[i]){
                day=31;
                break;
            }
        }
        for(int i=0;i<SMALLER_MONTH.length;i++){
            if(month==SMALLER_MONTH[i]){
                day=30;
                break;
            }
        }

        if(month==FEBRUARY){
            //如果是闰年
            if(isLeapYear(year)){
                day=29;
            }else{
                day=28;
            }
        }

        return year+"-"+month+"-"+day;
    }

    /**
     * 判断年{int}是否是闰年
     * @param year
     * @return
     */
    public static Boolean isLeapYear(int year){

        if ((year % 4 == 0) && ((year % 100 != 0) | (year % 400 == 0))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 截取 SimpleDateFormat格式【yyyy-MM-dd】的年份
     * @param str
     */
    public static int cutYear(String str){
        //截取年份
        String[] strs=str.split("-");
        //获取第0个数组就是年份
        return Integer.parseInt(strs[0]);
    }

    /**
     * 截取 SimpleDateFormat格式【yyyy-MM-dd】的月份
     * @param str
     */
    public static int cutMonth(String str){
        //截取年份
        String[] strs=str.split("-");
        //获取第0个数组就是年份
        return Integer.parseInt(strs[1]);
    }

    /**
     * 截取 SimpleDateFormat格式【yyyy-MM-dd】的天
     * @param str
     */
    public static int cutDay(String str){
        //截取年份
        String[] strs=str.split("-");
        //获取第0个数组就是年份
        return Integer.parseInt(strs[2]);
    }


    public static void main(String [] args) throws ParseException {
        SimpleDateFormat SDF=new SimpleDateFormat("M");
        Scanner sc = new Scanner(System.in);
        System.out.print("请输入年份：");
        int year = sc.nextInt();

        System.out.print("请输入月份：");

        int month = sc.nextInt();

        System.out.print("请输入日：");


       int day = sc.nextInt();
       //System.out.println(cutYear(year+"-"+month+"-"+day));
        //System.out.println(SDF.format());

        /*
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date date=sdf.parse(year+"-"+month+"-"+day);
        Boolean flag=HlsCusEndOfMonth.isEndOfMonth(date);
        System.out.println(flag);

        System.out.print("请输入第二年份：");
        int year1 = sc.nextInt();

        System.out.print("请输入第二月份：");

        int month1 = sc.nextInt();

        System.out.print("请输入第二个日：");

        int day1 = sc.nextInt();
        Date date1=sdf.parse(year1+"-"+month1+"-"+day1);

        Date date2=HlsCusEndOfMonth.calcEndOfMonth(date,date1);
        System.out.println(sdf.format(date2));*/
    }

}
