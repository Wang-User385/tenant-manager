package com.hand.hls.hn.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.hn.dto.*;
import com.hand.hls.hn.mapper.*;
import com.hand.hls.hn.service.IPrjCheckPlanService;
import com.hand.hls.hn.service.IPrjCheckService;
import com.hand.hls.hn.service.ISelectAllConContractService;
import com.hand.hls.hn.service.impl.PrjCheckServiceImpl;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.sys.dto.SysUserAllocation;
import com.hand.hls.sys.mapper.SysUserAllocationMapper;
import com.hand.hls.utils.MathUtil;
import hls.core.sys.event.service.SysEventService;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * description
 * 零售业务租后检查
 * 每个季度月底检查 已有的合作方不需要再生成
 *
 * @author yericong 2022/12/13
 */
@Transactional(rollbackFor = Exception.class)
public class PrjCheckLsReinitDataJob extends AbstractJob {


    @Autowired
    private ISelectAllConContractService selectAllConContractService;

    @Autowired
    private PrjCheckMapper prjCheckMapper;

    @Autowired
    private IPrjCheckService prjCheckService;

    @Autowired
    private HlsCheckItemsMapper hlsCheckItemsMapper;

    @Autowired
    private PrjCheckItemLnMapper prjCheckItemLnMapper;

    @Autowired
    private PrjCheckItemHdMapper prjCheckItemHdMapper;

    @Autowired
    private SysUserAllocationMapper sysUserAllocationMapper;
    @Autowired
    private SysEventService sysEventService;

    public List<String> getQuarter(int month) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        List<String> dates = new ArrayList<String>();
        for (int a = 1; a <= 4; a++) {
            Calendar calendar = Calendar.getInstance();
            int lastMonth = (int) MathUtil.sub(MathUtil.mul(a, 3), 2 - month);
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, lastMonth);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            Date dd = calendar.getTime();
            String dc = sdf.format(dd);
            dates.add(dc);
        }
        return dates;
    }

    public Date getQuarterLastDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date dd = calendar.getTime();
        return dd;
    }

    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {
        IRequest iRequest = (IRequest) jobExecutionContext.getMergedJobDataMap().get("requestContext");

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateNowStr = sdf.format(d);

        List<String> dates = getQuarter(0);
        Date checkDate = getQuarterLastDay();
        String checkDateStr = sdf.format(checkDate);
        Date checkDate2 = sdf.parse(dateNowStr);

//        if (dates.contains(dateNowStr)) {

            PrjCheck prjCheck = new PrjCheck();
            prjCheck.setCheckDate(checkDate2);
            List<PrjCheck> prjCheckList = prjCheckMapper.queryManufacturer(prjCheck);

            for (int i = 0; i < prjCheckList.size(); i++) {
                PrjCheck prjCheck1 = prjCheckList.get(i);
                /*PrjCheck prjCheck2 = new PrjCheck();
                prjCheck2.setManufacturerId(prjCheck1.getManufacturerId());
                prjCheck2.setContractAmt(prjCheck1.getContractAmt());
                prjCheck2.setContractCnt(prjCheck1.getContractCnt());*/
                prjCheck1.setManufacturerId(prjCheckList.get(i).getManufacturerId());
                prjCheck1.setContractAmt(prjCheckList.get(i).getContractAmt());
                prjCheck1.setContractCnt(prjCheckList.get(i).getContractCnt());
                prjCheck1.setApproveSuggest("NEW");
                prjCheck1.setWriteCompany(248L);
                prjCheck1.setCheckDate(checkDate2);

                String checkNumber = "";
                List<PrjCheck> prjCheckList1 = prjCheckMapper.queryCheckAll();
                if (prjCheckList1.size() == 0) {
                    checkNumber = "LSZH" + checkDateStr + "0001";
                } else {
                    String tempNumber = prjCheckList1.get(0).getCheckNumber();
                    int tmp = Integer.valueOf(tempNumber.substring(tempNumber.length() - 4)).intValue() + 1;
                    String code = String.format("%04d", tmp);
                    checkNumber = "LSZH" + checkDateStr + code;
                }
                prjCheck1.setCheckNumber(checkNumber);
                prjCheckService.insertSelective(iRequest, prjCheck1);
            }
            //只有prj_check表里新增了合作方且检查日期是新的时才会执行
            if(prjCheckList != null && prjCheckList.size() > 0) {

                List<PrjCheck> prjCheckList2 = prjCheckMapper.queryCheckAll();

                for (int i = 0; i < prjCheckList2.size(); i++) {
                    PrjCheck prjCheck2 = prjCheckList2.get(i);

                    //事项新增 风险预警、客户基本信息、客户主体信用资质状况、客户财报、标的、抵质押品
                    List<HlsCheckItems> hlsCheckItemsList = new ArrayList<>();
                    hlsCheckItemsList = hlsCheckItemsMapper.selectAll();
                    List<String> categorys = (List) hlsCheckItemsList.stream().map(HlsCheckItems::getCheckCategory).distinct().collect(Collectors.toList());

                    for (String category : categorys) {
                        PrjCheckItemHd prjCheckItemHdInsert = new PrjCheckItemHd();
                        prjCheckItemHdInsert.setCheckId(prjCheck2.getCheckId());
                        prjCheckItemHdInsert.setCheckCategory(category);
                        prjCheckItemHdInsert.setCheckDate(prjCheck2.getCheckDate());

                        List<PrjCheckItemHd> prjCheckItemHdList = prjCheckItemHdMapper.select(prjCheckItemHdInsert);
                        if (prjCheckItemHdList.size() <= 0) {
                            prjCheckItemHdMapper.insertSelective(prjCheckItemHdInsert);
                        }
                        PrjCheckItemHd hd = prjCheckItemHdMapper.select(prjCheckItemHdInsert).get(0);

                        List<HlsCheckItems> hlsCheckItemsLnList = hlsCheckItemsList.stream().filter(item -> item.getCheckCategory().equals(category)).collect(Collectors.toList());
                        for (HlsCheckItems item : hlsCheckItemsLnList) {
                            PrjCheckItemLn prjCheckItemLnInsert = new PrjCheckItemLn();
                            prjCheckItemLnInsert.setHdId(hd.getHdId());
                            prjCheckItemLnInsert.setItems(item.getItems());
                            prjCheckItemLnInsert.setCheckType(item.getCheckType());
                            prjCheckItemLnInsert.setCheckId(prjCheck2.getCheckId());
                            prjCheckItemLnInsert.setCheckY("Y");
                            prjCheckItemLnInsert.setCheckN("N");
                            prjCheckItemLnInsert.setRequiredFlag(item.getRequiredFlag());
                            List<PrjCheckItemLn> prjCheckItemLnList = prjCheckItemLnMapper.select(prjCheckItemLnInsert);
                            if (prjCheckItemLnList.size() <= 0) {
                                prjCheckItemLnMapper.insertSelective(prjCheckItemLnInsert);
                            }
                        }
                    }
                }
            }

        }
    }
//}
