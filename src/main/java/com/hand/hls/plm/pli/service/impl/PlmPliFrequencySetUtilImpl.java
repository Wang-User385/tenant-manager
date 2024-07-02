package com.hand.hls.plm.pli.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.plm.pli.dto.PlmPliFrequencySet;
import com.hand.hls.plm.pli.mapper.PlmPliFrequencySetMapper;
import com.hand.hls.plm.pli.service.PlmPliFrequencySetUtil;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class PlmPliFrequencySetUtilImpl extends BaseServiceImpl<PlmPliFrequencySet> implements PlmPliFrequencySetUtil {

    @Autowired
    private PlmPliFrequencySetMapper mapper;

    public static final String ON_SITE_INSPECT = "ON_SITE_INSPECT";
    public static final String OFF_SITE_INSPECT_PLAN = "OFF_SITE_INSPECT_PLAN";
    public static final String ON_SITE_INSPECT_PLANE = "ON_SITE_INSPECT_PLANE";
    public static final String OFF_SITE_INSPECT = "OFF_SITE_INSPECT";
    public static final String ALL = "ALL";

    public static final String INSPECTION_DATE = "INSPECTION_DATE";
    public static final String REMIND_DATE = "REMIND_DATE";


    private String FREQUENCY_TYPE;
    private String priorityInspect;
    private PlmPliFrequencySet plmPliFrequencySet = new PlmPliFrequencySet();
    private String MODEL;

    /**
     * 构造函数为工具函数作初始化，获取配置信息
     *
     * @param FREQUENCY_TYPE 规则类型
     */
    @Override
    public void createUtil(String FREQUENCY_TYPE,String MODEL) {
        this.FREQUENCY_TYPE = FREQUENCY_TYPE;
        this.plmPliFrequencySet.setFrequencyType(this.FREQUENCY_TYPE);
        this.plmPliFrequencySet.setEnableFlag("Y");
        this.plmPliFrequencySet = this.mapper.select(this.plmPliFrequencySet).get(0);
        this.priorityInspect = this.plmPliFrequencySet.getPriorityInspect();
        this.MODEL = MODEL;
    }

    /**
     * 根据标记获取现场或非现场检查日期
     *
     * @param SIGN     现场或非现场标记
     * @param rentDate 起租日期
     * @return 检查日期/提醒日
     * @throws Exception
     */
    @Override
    public Map<String, Date> getFrequencyDate(String SIGN, Date rentDate, Date appointedDate) throws Exception {
        Date oldDate = this.getDate(rentDate);
        Date remindDate = new Date();
        if (appointedDate == null) {
            appointedDate = rentDate;
        }
        appointedDate = this.getDate(appointedDate);
        Date today = this.getDate(new Date());

        GregorianCalendar rentDateGc = new GregorianCalendar();
        GregorianCalendar remindDateGc = new GregorianCalendar();
        if (ON_SITE_INSPECT.equalsIgnoreCase(SIGN)) {

            rentDateGc.setTime(oldDate);
            rentDateGc.add(GregorianCalendar.MONTH, +this.plmPliFrequencySet.getOnSiteInspect().intValue());
            oldDate = rentDateGc.getTime();

            remindDateGc.setTime(oldDate);
            remindDateGc.add(GregorianCalendar.DAY_OF_MONTH, -this.plmPliFrequencySet.getOnSiteReminderDay().intValue());
            remindDate = remindDateGc.getTime();

//            while (judgingCycleTime(SIGN,oldDate,remindDate)) {
//                rentDateGc.setTime(oldDate);
//                rentDateGc.add(GregorianCalendar.MONTH, +this.plmPliFrequencySet.getOnSiteInspect().intValue());
//                oldDate = rentDateGc.getTime();
//                remindDateGc.setTime(oldDate);
//                remindDateGc.add(GregorianCalendar.DAY_OF_MONTH, -this.plmPliFrequencySet.getOnSiteReminderDay().intValue());
//                remindDate = remindDateGc.getTime();
//            }
        } else if (OFF_SITE_INSPECT.equalsIgnoreCase(SIGN)) {

            rentDateGc.setTime(oldDate);
            rentDateGc.add(GregorianCalendar.MONTH, +this.plmPliFrequencySet.getOffSiteInspect().intValue());
            oldDate = rentDateGc.getTime();

            remindDateGc.setTime(oldDate);
            remindDateGc.add(GregorianCalendar.DAY_OF_MONTH, -this.plmPliFrequencySet.getOffSiteReminderDay().intValue());
            remindDate = remindDateGc.getTime();

//            while (judgingCycleTime(SIGN,oldDate,remindDate)) {
//                rentDateGc.setTime(oldDate);
//                rentDateGc.add(GregorianCalendar.MONTH, +this.plmPliFrequencySet.getOffSiteInspect().intValue());
//                oldDate = rentDateGc.getTime();
//                remindDateGc.setTime(oldDate);
//                remindDateGc.add(GregorianCalendar.DAY_OF_MONTH, -this.plmPliFrequencySet.getOffSiteReminderDay().intValue());
//                remindDate = remindDateGc.getTime();
//            }
        } else {
            throw new Exception("SIGN参数值非法!");
        }

        Map<String, Date> maps = new HashMap<>();
        maps.put(INSPECTION_DATE, oldDate);
        maps.put(REMIND_DATE, remindDate);
        return maps;
    }

    /**
     * 判断是否需要提醒
     *
     * @param maps
     * @return
     */
    @Override
    public boolean isRemind(Map<String, Date> maps) {
        Date today = this.getDate(new Date());
        if (DateUtils.isSameDay(maps.get(REMIND_DATE), today) || today.after(maps.get(REMIND_DATE))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取规则优先级
     *
     * @return ALL 两种检查同时 /ON_SITE_INSPECT 冲突日使用现场检查 /OFF_SITE_INSPECT 冲突日使用非现场检查
     */
    @Override
    public String getPriorityInspect() {
        return this.priorityInspect;
    }

    @Override
    public String getPriorityInspect(String defaultPriorityInspect) {
        if (ALL.equalsIgnoreCase(this.priorityInspect)){
            return defaultPriorityInspect;
        }else {
            return this.priorityInspect;
        }
    }

    private Date getDate(Date date) {
        Date nowDate = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        try {
            nowDate = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return nowDate;
    }

    /**
     * 当 MODEL 为JOB时用于定时任务，循环条件是提醒时间必须大于等于刷新日返回值为false
     * 当 MODEL 为UPDATE时用于修改配置时刷新，循环条件是刷新日小于下次提醒日回值为false
     * @param SIGN
     * @param inspectionDate
     * @param remindDate
     * @return
     */
    private boolean judgingCycleTime(String SIGN,Date inspectionDate,Date remindDate){
        Date today = this.getDate(new Date());
        if (MODEL.equalsIgnoreCase("JOB")){
            return remindDate.before(today);
        }else if (MODEL.equalsIgnoreCase("UPDATE")){
            GregorianCalendar rentDateGc = new GregorianCalendar();
            rentDateGc.setTime(remindDate);
            if (ON_SITE_INSPECT.equalsIgnoreCase(SIGN)) {
                rentDateGc.add(GregorianCalendar.MONTH, +this.plmPliFrequencySet.getOnSiteInspect().intValue());
            } else if (OFF_SITE_INSPECT.equalsIgnoreCase(SIGN)) {
                rentDateGc.add(GregorianCalendar.MONTH, +this.plmPliFrequencySet.getOffSiteInspect().intValue());
            }

            Date newDate = rentDateGc.getTime();
            if(today.before(newDate)){
                return false;
            }else {
                return true;
            }
        }else {
            return false;
        }
    }

}
