package com.hand.hls.plm.nm.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.annotation.StdWho;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.mapper.HlsCusWriteOffMatchMapper;
import com.hand.hls.fct.dto.HlsCusFctQuotationCashflow;
import com.hand.hls.fct.mapper.HlsCusFctQuotationCashflowMapper;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.plm.nm.dto.PlmNoticeSent;
import com.hand.hls.plm.nm.mapper.PlmNoticeSentMapper;
import com.hand.hls.plm.nm.service.PlmNoticeSentService;
import com.hand.hls.sys.dto.SysUser;
import hls.core.sys.event.service.SysEventService;
import hls.core.sys.event.utils.SysEventCodeUtil;
import com.hand.hls.sys.mapper.SysUserMapper;
import com.hand.hls.utils.HlsCusConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class PlmNoticeSentServiceImpl extends BaseServiceImpl<PlmNoticeSent> implements PlmNoticeSentService {






    @Autowired
    private PlmNoticeSentMapper mapper;

    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;

    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private HlsCusFctQuotationCashflowMapper quotationCashflowMapper;

    @Autowired
    private HlsCusWriteOffMatchMapper writeOffMatchMapper;
    @Override
    public List<PlmNoticeSent> selectNoticeSentAll(IRequest iRequest, PlmNoticeSent dto, int page, int pageSize) {
        String positionCode=iRequest.getAttribute("positionCode");
        dto.setPositionCode(positionCode);
        PageHelper.startPage(page, pageSize);
        return mapper.selectNoticeSentAll(dto);
    }

    @Override
    public List<PlmNoticeSent> creditSave(IRequest iRequest, PlmNoticeSent dto) {
        List<PlmNoticeSent> list = new ArrayList<>();
        if (dto.getNoticeId() == null || dto.getNoticeId() == 0) {
            dto.setDocumentCategory("PLM_NM_NOTICE");
            dto.setDocumentType("NM");
            dto.set__status(HlsCusConstant.DATA_STATUS.STATUS_ADD);
            dto.setCreatedBy(iRequest.getUserId());
            dto.setLastUpdatedBy(iRequest.getUserId());
            mapper.insertSelective(dto);
        } else {
            dto.set__status(HlsCusConstant.DATA_STATUS.STATUS_UPDATE);
            dto.setLastUpdatedBy(iRequest.getUserId());
            mapper.updateByPrimaryKeySelective(dto);
        }
        list.add(dto);
        return list;
    }

    /*@Override
    public List<PlmNoticeSent> creditNotice(IRequest iRequest, PlmNoticeSent dto) {
        List<PlmNoticeSent> list = new ArrayList<>();
        list = creditSave(iRequest, dto);

        //查询该合同的客户名称
        Map<String, Object> paramsEvent2 = new HashMap<String, Object>();
        HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
        hlsCusBpMaster.setBpId(dto.getBpId());
        hlsCusBpMaster = hlsCusBpMasterMapper.selectByPrimaryKey(hlsCusBpMaster);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String DUE_DATE =sdf.format(dto.getDueDate());
        if (StringUtils.isEmpty(DUE_DATE))
        {
            //查询应收账款日
            HlsCusFctQuotationCashflow cashflow = new HlsCusFctQuotationCashflow();
            cashflow.setTimes(dto.getTimes());
            cashflow.setContractId(dto.getContractId());
            cashflow.setDocumentCategory(dto.getDocumentStyle());
            PageHelper.orderBy("due_date asc");
            List<HlsCusFctQuotationCashflow> cashflowList = quotationCashflowMapper.selectNoticePrint(cashflow);
            if (cashflowList.size() > 0) {
                cashflow = cashflowList.get(0);
                DUE_DATE = sdf.format(cashflow.getDueDate());
            }
        }
        //创建消息
        String builder = hlsCusBpMaster.getBpName() + "的" + dto.getApprovalNumber() + "合同的第" + dto.getTimes() + "期应收款的通知书已寄送，应收日期为" +
                DUE_DATE + "," + dto.getCourierCompany() + ":" + dto.getTrackingNumber() + "，请及时关注。";
        paramsEvent2.put("message", builder);
        paramsEvent2.put("noticeTitle", "寄送通知书");
        paramsEvent2.put("noticeType", "NOTICE");
        paramsEvent2.put("url", "");
        paramsEvent2.put("eventCode", SysEventCodeUtil.PLM_NM_NOTICE_SENT);
        paramsEvent2.put("level", 1L);

        //查询合同的主协办经理
        List<SysUser> sysUsers = sysUserMapper.selectContractManager(dto.getContractNumber());
        IRequest request = RequestHelper.newEmptyRequest();
        for (SysUser user : sysUsers) {
            request.setUserId(user.getUserId());
            sysEventService.eventSave(request, dto.getNoticeId(), dto.getDocumentCategory(), dto.getDocumentType(), "BAC", SysEventCodeUtil.PLM_LOANMANAGEMENT1, "P2D", paramsEvent2);
        }

        return list;
    }*/

 /*   @Override
    public void noticePrintReminder(CountDownLatch latch) {
        log.info("=============通知书打印提醒定时消息推送开始==========["
                + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date())
                + "]=============");
        HlsCusFctQuotationCashflow cashflow = new HlsCusFctQuotationCashflow();
        List<HlsCusFctQuotationCashflow> cashflowList = quotationCashflowMapper.selectNoticePrint(cashflow);

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

        //获取满足推送条件的现金流信息
        List<Integer> NonCircularDay = new ArrayList<>();
        NonCircularDay.add(-45);
        Set<HlsCusFctQuotationCashflow> cashflowsNew = new HashSet<>(pushTime(cashflowList, NonCircularDay, null, null, null));
        //风险管理部运营岗
        List<String> positionCodeList = new ArrayList<>();
        positionCodeList.add("00320");
        positionCodeList.add("05530");
        Set<SysUser> sysUsers = new HashSet<>(sysUserMapper.selectUserByPositionCode(null, positionCodeList));
        IRequest request = RequestHelper.newEmptyRequest();

        //创建消息
        Map<String, Object> paramsEvent = new HashMap<>();
        paramsEvent.put("noticeTitle", "通知书打印提醒");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("eventCode", SysEventCodeUtil.NOTICE_PRINT_REMINDER);
        paramsEvent.put("level", 1L);

        for (HlsCusFctQuotationCashflow dto : cashflowsNew) {
            String builder = dto.getContractNumber() + "/" + dto.getContractName() + "第" + dto.getTimes() + "期租金" + dto.getCfProject() +
                    "的应收日为" + f.format(dto.getDueDate()) + "，请及时打印租金通知书。";
            paramsEvent.put("message", builder);
            for (SysUser user : sysUsers) {
                request.setUserId(user.getUserId());
                sysEventService.eventSave(request, dto.getQuotationCashflowId(), SysEventCodeUtil.NOTICE_PRINT_REMINDER, SysEventCodeUtil.NOTICE_PRINT_REMINDER, "BAC", SysEventCodeUtil.PLM_LOANMANAGEMENT1, "P2D", paramsEvent);
            }
        }
        log.info("=============通知书打印提醒定时消息推送结束==========["
                + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date())
                + "]=============");
        latch.countDown();
    }
*/
/*
    @Override
    public void noticePrintCollection(CountDownLatch latch) {
        log.info("=============催收通知书打印提醒定时消息推送开始==========["
                + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date())
                + "]=============");

        try {
            HlsCusFctQuotationCashflow cashflow = new HlsCusFctQuotationCashflow();
            cashflow.setAttribute1("Y");
            List<HlsCusFctQuotationCashflow> cashflowList = quotationCashflowMapper.selectNoticePrint(cashflow);

            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

//            List<HlsCusFctQuotationCashflow>   cashflowListNew = compareAmount(cashflowList);

            List<SysUser> sysUsers = new ArrayList<>();
            //风险管理部资产管理岗、部门负责人
            List<String> positionCodeList = new ArrayList<>();
            positionCodeList.add("05540");
            positionCodeList.add("00340");
            positionCodeList.add("00300");
            positionCodeList.add("05500");
            List<SysUser> sysUsers1 = sysUserMapper.selectUserByPositionCode(null, positionCodeList);


            if (sysUsers1 != null && sysUsers1.size() > 0) {
                sysUsers.addAll(sysUsers1);
            }
            IRequest request = RequestHelper.newEmptyRequest();

            //创建消息
            Map<String, Object> paramsEvent = new HashMap<>();
            paramsEvent.put("noticeTitle", "催收通知书打印提醒");
            paramsEvent.put("noticeType", "NOTICE");
            paramsEvent.put("url", "");
            paramsEvent.put("eventCode", SysEventCodeUtil.NOTICE_PRINT_COLLECTION);
            paramsEvent.put("level", 1L);
            Set<HlsCusFctQuotationCashflow> cashFlowSetNew=new HashSet<>(cashflowList);
            for (HlsCusFctQuotationCashflow dto : cashFlowSetNew) {
                String builder = dto.getContractNumber() + "/" + dto.getContractName() + "第" + dto.getTimes() + "期租金" + dto.getCfProject() +
                        "的应收日为" + f.format(dto.getDueDate()) + "，请及时打印催收通知书。";
                paramsEvent.put("message", builder);

                //项目主协办经理
                List<SysUser> sysUsers2 = sysUserMapper.selectContractManager(dto.getContractNumber());
                //项目经理部门负责人
                List<SysUser> sysUsers3 = sysUserMapper.selectContractManagerPrincipal(dto.getContractNumber());
                if (sysUsers2 != null && sysUsers2.size() > 0) {
                    sysUsers.addAll(sysUsers2);
                }
                if (sysUsers3 != null && sysUsers3.size() > 0) {
                    sysUsers.addAll(sysUsers3);
                }
                for (SysUser user : sysUsers) {
                    request.setUserId(user.getUserId());
                    sysEventService.eventSave(request, dto.getQuotationCashflowId(), SysEventCodeUtil.NOTICE_PRINT_COLLECTION, SysEventCodeUtil.NOTICE_PRINT_COLLECTION, "BAC", SysEventCodeUtil.PLM_LOANMANAGEMENT1, "P2D", paramsEvent);
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            latch.countDown();
            log.info("=============催收通知书打印提醒定时消息推送结束==========["
                    + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date())
                    + "]=============");
        }
    }*/

   /* @Override
    public void paymentSituation(CountDownLatch latch) {
        log.info("=============关注回款情况提醒定时消息推送开始==========["
                + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date())
                + "]=============");
        HlsCusFctQuotationCashflow cashflow = new HlsCusFctQuotationCashflow();
        cashflow.setAttribute2("XX");
        List<HlsCusFctQuotationCashflow> cashflowList = quotationCashflowMapper.selectNoticePrint(cashflow);
        List<HlsCusFctQuotationCashflow> cashflowListNew = new ArrayList<>();

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

        //获取满足推送条件的现金流信息
        List<Integer> NonCircularDay = new ArrayList<>();
        NonCircularDay.add(-14);
        NonCircularDay.add(-1);
        cashflowListNew = pushTime(cashflowList, NonCircularDay, null, null, null);
        cashflowListNew = compareAmount(cashflowListNew);

        //创建消息
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        paramsEvent.put("noticeTitle", "关注回款情况提醒");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("eventCode", SysEventCodeUtil.PAYMENT_SITUATION);
        paramsEvent.put("level", 1L);

        IRequest request = RequestHelper.newEmptyRequest();
        Set<HlsCusFctQuotationCashflow> cashFlowSetNew=new HashSet<>(cashflowListNew);
        for (HlsCusFctQuotationCashflow dto : cashFlowSetNew) {
            List<SysUser> sysUsers = sysUserMapper.selectContractManager(dto.getContractNumber());
            String builder = dto.getContractNumber() + "/" + dto.getContractName() + "第" + dto.getTimes() + "期租金" + dto.getCfProject() +
                    "的应收日为" + f.format(dto.getDueDate()) + "，请及时关注回款情况。";
            paramsEvent.put("message", builder);
            for (SysUser user : sysUsers) {
                request.setUserId(user.getUserId());
                sysEventService.eventSave(request, dto.getQuotationCashflowId(), SysEventCodeUtil.PAYMENT_SITUATION, SysEventCodeUtil.PAYMENT_SITUATION, "BAC", SysEventCodeUtil.PLM_LOANMANAGEMENT1, "P2D", paramsEvent);
            }
        }
        latch.countDown();
        log.info("=============关注回款情况提醒定时消息推送结束==========["
                + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date())
                + "]=============");
    }*/
/*
    //对照CSH_WRITE_OFF_MATCH表，如果现金流的金额不等于应收金额就要发起催款通知
    private List<HlsCusFctQuotationCashflow> compareAmount(List<HlsCusFctQuotationCashflow> cashflowList) {
        List<HlsCusFctQuotationCashflow> newCashflowList = new ArrayList<>();
        for (HlsCusFctQuotationCashflow dto : cashflowList) {
//            HlsCusWriteOffMatch match = new HlsCusWriteOffMatch();
//            match.setCashflowId(dto.getQuotationCashflowId());
//            List<HlsCusWriteOffMatch> matchList = writeOffMatchMapper.selectSumAmount(match);
//            if (matchList != null && matchList.size() > 0) {
//                if (dto.getDueAmount() > matchList.get(0).getWithdrawAmount()) {
//                    newCashflowList.add(dto);
//                }
//            }
            if (dto.getQuotationId() == null) {
                newCashflowList.add(dto);
            }
        }
        return newCashflowList;
    }*/

    /*private List<HlsCusFctQuotationCashflow> pushTime(List<HlsCusFctQuotationCashflow> cashflowList, List<Integer> NonCircularDay
            , List<Integer> CircularDay, List<Integer> NonCircularMonth, List<Integer> CircularMonth) {

        List<HlsCusFctQuotationCashflow> newCashflowList = new ArrayList<>();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        if (NonCircularDay != null && NonCircularDay.size() > 0) {
            for (Integer nday : NonCircularDay) {
                try {
                    for (HlsCusFctQuotationCashflow dto : cashflowList) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(dto.getDueDate());
                        c.add(Calendar.DATE, nday);
                        Date newday = c.getTime();

                        if (DateUtils.isSameDay(f.parse(f.format(newday)), f.parse(f.format(today)))) {
                            newCashflowList.add(dto);
                        }
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        }
        if (CircularDay != null && CircularDay.size() > 0) {
            for (Integer nday : CircularDay) {
                for (HlsCusFctQuotationCashflow dto : cashflowList) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(dto.getDueDate());
                    c.add(Calendar.DATE, nday);
                    Date newDate = c.getTime();

                    while (compareDate(today, newDate) > 0) {
                        c.setTime(dto.getDueDate());
                        c.add(Calendar.DATE, nday);
                        newDate = c.getTime();
                    }

                    if (DateUtils.isSameDay(newDate, today)) {
                        newCashflowList.add(dto);
                    }
                }
            }
        }
        if (NonCircularMonth != null && NonCircularMonth.size() > 0) {
            for (Integer nMonth : NonCircularMonth) {
                for (HlsCusFctQuotationCashflow dto : cashflowList) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(dto.getDueDate());
                    c.add(Calendar.MONTH, nMonth);
                    Date newDate = c.getTime();
                    if (DateUtils.isSameDay(newDate, today)) {
                        newCashflowList.add(dto);
                    }
                }
            }

        }
        return newCashflowList;
    }
*/
    private int compareDate(Date date1, Date date2) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate1 = date1;
        Date newDate2 = date2;
        try {
            newDate1 = f.parse(f.format(newDate1));
            newDate2 = f.parse(f.format(newDate2));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (newDate1.before(newDate2)) {
            return -1;
        }

        if (newDate1.after(newDate2)) {
            return 1;
        }

        return 0;
    }
}
