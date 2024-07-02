package com.hand.hls.gld.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.common.Mapper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.gld.dto.Period;
import com.hand.hls.gld.mapper.PeriodMapper;
import com.hand.hls.gld.service.IPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class PeriodServiceImpl extends BaseServiceImpl<Period> implements IPeriodService {

    @Autowired
    private PeriodMapper periodMapper;

    @Override
    public List<Period> periodCreate(IRequest request, HashMap param) {

        List<Period> list = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        Long yearFrom = Long.valueOf(param.get("year_from").toString());
        Long yearTo = Long.valueOf(param.get("year_to").toString());
        Long periodSetId = Long.valueOf(param.get("period_set_id").toString());
        String flag = param.get("adjust_flag")!=null? (String) param.get("adjust_flag") :null;

        for (int y = Math.toIntExact(yearFrom); y <= Math.toIntExact(yearTo) ; y++) {
            Date adjust = null;

            if (!exists(mappers.get(0),periodSetId,y)){//先判断是否有这个年份的数据
                for (int m = 0; m <= 12; m++) {
                    Period period = new Period();

                    cal.set(Calendar.YEAR,y);
                    cal.set(Calendar.MONTH,m);
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                    Date lastDate = cal.getTime();//当前月最后一天
                    lastDate = format(lastDate);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    Date firstDate = cal.getTime();//当前月第一天
                    firstDate = format(firstDate);

                    //避免日历取出的日期有误,必须要循环一次月份为0的情况,但是月份为0时,不做额外处理
                    if (m == 0)
                        continue;

                    //把当年12月的最后一天记录,赋值给adjust,以便于直接取值
                    if (m==12)
                        adjust = lastDate;

                    Long year = Long.valueOf(y);
                    Long month = Long.valueOf(m);

                    period.setPeriodSetId(periodSetId);
                    //拼接期间名称为yyyy_mm
                    if (m<10)
                        period.setPeriodName(year.toString()+"-0"+month.toString());
                    else
                        period.setPeriodName(year.toString()+"-"+month.toString());

                    period.setPeriodYear(year);
                    period.setPeriodMonth(month);
                    period.setStartDate(firstDate);
                    period.setEndDate(lastDate);

                    period = self().insertSelective(request,period);
                    list.add(period);
                }
                if ("Y".equalsIgnoreCase(flag)){
                    Period period = new Period();
                    period.setPeriodSetId(periodSetId);
                    period.setPeriodName(y+"-"+13);
                    period.setPeriodYear(Long.valueOf(y));
                    period.setPeriodMonth(13L);
                    period.setStartDate(adjust);
                    period.setEndDate(adjust);
                    period.setAdjustmentFlag("Y");
                    period = self().insertSelective(request,period);
                    list.add(period);
                }
            }
        }
        return list;
    }

    @Override
    public List<Period> periodQuery4Lov(IRequest request, Period condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return periodMapper.periodQuery4Lov(condition);
    }

    @Override
    public List<Period> periodNameLov(IRequest request, Period condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return periodMapper.periodNameLov(condition);
    }

    /**
     * 格式化时间,去除时分秒
     * @param date
     * @return
     */
    private static Date format(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String s = sdf.format(date);
        Date result;
        try {
            result =  sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
            result = date;
        }
        return result;
    }

    private static boolean exists(Mapper<Period> mapper, Long periodSetId, int y){

        Period p = new Period();
        Long year = Long.valueOf(y);
        p.setPeriodSetId(periodSetId);
        p.setPeriodYear(year);
        List list = mapper.select(p);
        if (list.size()>0)
            return true;
        return false;
    }
}