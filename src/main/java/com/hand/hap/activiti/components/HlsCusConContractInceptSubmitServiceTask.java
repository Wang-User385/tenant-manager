package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.eas.dto.HlsCusEasBasicData;
import com.hand.hls.eas.dto.HlsCusEasLogin;
import com.hand.hls.eas.dto.HlsCusEasSourceRecord;
import com.hand.hls.eas.mapper.HlsCusEasSourceRecordMapper;
import com.hand.hls.eas.service.IHlsCusEasLoginService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.gld.service.IContractFinanceIncomeService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import com.hand.hls.sys.service.SysUserService;
import hls.core.sys.event.service.SysEventService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by xuju on 2018/04/27.
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractInceptSubmitServiceTask implements JavaDelegate, IActivitiBean {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private SysUserService userService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusConContractService service;
    @Autowired
    private JeTrxCommonService jeTrxCommonService;



    @Autowired
    private IContractFinanceIncomeService iContractFinanceIncomeService;

    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;

    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;

    public HlsCusConContractInceptSubmitServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag = null;
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Date leaseDateAdjust = (Date) delegateExecution.getVariable("leaseDateAdjust");
        String contractId = delegateExecution.getProcessInstanceBusinessKey();
        HlsCusConContract resultHlsCusConContract = new HlsCusConContract();
        //根据状态修改合同信息
        resultHlsCusConContract.setContractId(Long.parseLong(contractId));
        resultHlsCusConContract = service.selectByPrimaryKey(requestCtx, resultHlsCusConContract);
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(resultHlsCusConContract.getProjectId());
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, hlsCusPrjProject);

        databaseLockProvider.lock(resultHlsCusConContract);
        if ("APPROVED".equalsIgnoreCase(result)) {
//            flag = "INCEPT";
//            resultHlsCusConContract.setContractStatus(flag);
            resultHlsCusConContract.setInceptWflStatus("APPROVED");
            service.updateByPrimaryKeySelective(requestCtx, resultHlsCusConContract);
//            hlsCusPrjProject.setContractStatus(flag);
//            hlsCusPrjProjectService.updateByPrimaryKey(requestCtx, hlsCusPrjProject);
//            hlsCusConContractService.calcConFinIncome(requestCtx, resultHlsCusConContract);

            List<HlsCusConContract> list = new ArrayList<>();
            list.add(resultHlsCusConContract);
            if(resultHlsCusConContract.getQuotationId() != null){
                HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
                hlsCusPrjQuotation.setQuotationId(resultHlsCusConContract.getQuotationId());
                HlsCusPrjQuotation cusPrjQuotation  = hlsCusPrjQuotationMapper.selectByPrimaryKey(hlsCusPrjQuotation);
                cusPrjQuotation.setLeaseStartDate(leaseDateAdjust);
                Date firstReleaseDate =  cusPrjQuotation.getFirstReleaseDate();

                int months =  getMonthDiff(leaseDateAdjust , firstReleaseDate);
                cusPrjQuotation.setPreLeaseTerm(Double.valueOf(months));

                hlsCusPrjQuotationMapper.updateByPrimaryKey(cusPrjQuotation);

                //计算
                try {
                    hlsCusPrjQuotationService.quotationReCalc(RequestHelper.getCurrentRequest(true)  ,cusPrjQuotation.getQuotationId(),true);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    throw new IllegalArgumentException("计算失败！" + e.getMessage());
                }
            }

            //起租时收益分摊
            try {
                iContractFinanceIncomeService.financeIncomeSharing(requestCtx, list);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

        } else if ("APPROVED_RETURN".equalsIgnoreCase(result)) {
//            flag = "SIGN";
            resultHlsCusConContract.setInceptWflStatus(result);
//            resultHlsCusConContract.setContractStatus(flag);
            service.updateByPrimaryKeySelective(requestCtx, resultHlsCusConContract);
        }
    }

    /**
     * 获取两个日期相差的月数
     */
    public static int getMonthDiff(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int month2 = c2.get(Calendar.MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        // 获取年的差值
        int yearInterval = year1 - year2;
        // 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
        if (month1 < month2 || month1 == month2 && day1 < day2) {
            yearInterval--;
        }
        // 获取月数差值
        int monthInterval = (month1 + 12) - month2;
        if (day1 < day2) {
            monthInterval--;
        }
        monthInterval %= 12;
        int monthsDiff = Math.abs(yearInterval * 12 + monthInterval);
        return monthsDiff;
    }

}
