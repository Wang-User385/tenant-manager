package com.hand.hls.gld.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.gld.dto.HlsCusContractFinanceIncome;
import com.hand.hls.gld.dto.HlsCusCtDocumentFinIncome;
import com.hand.hls.gld.dto.Period;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.gld.dto.*;
import com.hand.hls.gld.mapper.ContractFinanceIncomeMapper;
import com.hand.hls.gld.service.*;


import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.vat.dto.HlsCusAcpInvoiceLn;
import com.hand.hls.gld.service.GldLonContractFinCostService;
import com.hand.hls.gld.service.IContractFinanceIncomeService;
import com.hand.hls.gld.service.IGldFinanceIncomeDayInterfaceService;
import com.hand.hls.vat.exception.AcpInvoiceException;
import hls.core.utils.exception.HlsCusException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uncertain.composite.CompositeMap;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: WuJun
 * @Date: 2020/02/07
 * @Time: 10:00
 */

@Service

@Transactional(rollbackFor = Exception.class)
public class ContractFinanceIncomeServiceImpl extends BaseServiceImpl<HlsCusContractFinanceIncome> implements IContractFinanceIncomeService {

    @Autowired
    private GldLonContractFinCostService finCostService;

    @Autowired
    private ContractFinanceIncomeMapper contractFinanceIncomeMapper;
    @Autowired
    private IContractFinanceIncomeService contractFinanceIncomeService;

    @Autowired
    private IGldFinanceIncomeDayInterfaceService iGldFinanceIncomeDayInterfaceService;

    @Autowired
    private HlsCusConContractCashflowMapper cashflowMapper;
    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;

    @Autowired
    private HlsCusConContractMapper conContractMapper;

    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private IGldFinanceIncomeDayService gldFinanceIncomeDayService;

    @Override
    public List<HlsCusContractFinanceIncome> queryContractFinanceIncome(IRequest iRequest, HlsCusContractFinanceIncome hlsCusContractFinanceIncome, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return contractFinanceIncomeMapper.queryContractFinanceIncome(hlsCusContractFinanceIncome);
    }

    @Override
    public List<Period> periodNameQueryForComb(IRequest request, Period condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return this.contractFinanceIncomeMapper.periodNameQueryForComb(condition);
    }

    @Override
    public List<CompositeMap> selectDefaultPeriodName(CompositeMap compositeMap, String whereStr) {
        return this.contractFinanceIncomeMapper.selectDefaultPeriodName(compositeMap);
    }

    @Override
    public HlsCusContractFinanceIncome queryConFinanceIncomeByKey(IRequest iRequest, HlsCusContractFinanceIncome hlsCusContractFinanceIncome) {
        return contractFinanceIncomeMapper.queryConFinanceIncomeByKey(hlsCusContractFinanceIncome);
    }

    @Override
    public List<HlsCusCtDocumentFinIncome> queryCtDocumentFinIncomeByCondition(IRequest iRequest, HlsCusCtDocumentFinIncome hlsCusCtDocumentFinIncome, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return contractFinanceIncomeMapper.queryCtDocumentFinIncomeByCondition(hlsCusCtDocumentFinIncome);
    }

    @Override
    public void financeIncomeSharing(IRequest iRequest, List<HlsCusConContract> list) throws Exception {
        HashMap map = new HashMap();
        if (list.size() > 0) {
            //查询出所有摊销相关的现金流
            Set<Long> allContractIdSet = list.stream().collect(Collectors.groupingBy(HlsCusConContract::getContractId)).keySet();
            List<Long> allContractIdList = new ArrayList<>(allContractIdSet);

        /*  1(包含10)    租金 租前息
            3	        手续费
            41	        资产管理费
            501		    保证金利息
            66		    咨询费
            69		    其他费用
            */

            String[][] conditions = {
                    {"1", "INTEREST_BALANCE"},
                    {"1", "INTEREST_STRAIGHT"},
                    {"1", "IMPLICIT_INTEREST"},
                    {"3", "COST_PROPORTION"},
                    {"3", "COST_STRAIGHT"},
                    {"41", "COST_PROPORTION"},
                    {"41", "COST_STRAIGHT"},
                    {"66", "COST_PROPORTION"},
                    {"66", "COST_STRAIGHT"},
                    {"69", "COST_PROPORTION"},
                    {"69", "COST_STRAIGHT"},
                    {"501", "COST_PROPORTION"},
                    {"501", "COST_STRAIGHT"}

            };
            for (String[] condition : conditions) {

                Example allAmortizationCashflow = new Example(HlsCusConContractCashflow.class);
                Example.Criteria cashflowCriteria = allAmortizationCashflow.createCriteria();

                cashflowCriteria.andIn("contractId", allContractIdList).
                        andEqualTo("cfItem", condition[0]).
                        andEqualTo("cfStatus", "RELEASE").
                        andEqualTo("amortizationMethod", condition[1]);


                List<HlsCusConContractCashflow> cashflowList = cashflowMapper.selectByExample(allAmortizationCashflow);
                List<HlsCusConContract> contractList = new ArrayList<>();
                for (Long contractId : cashflowList.stream().collect(Collectors.groupingBy(HlsCusConContractCashflow::getContractId)).keySet()) {
                    HlsCusConContract contract = new HlsCusConContract();
                    contract.setContractId(contractId);
                    contractList.add(contract);
                }
                if (contractList.size() > 0) {
                    map.put("bizType", "LEASE");
                    map.put("shareType", condition[1]);
                    map.put("cfItem", condition[0]);
                    iGldFinanceIncomeDayInterfaceService.start(iRequest, contractList, map);
                }
            }
        }
    }

    @Override
    public List<HlsCusContractFinanceIncome> reportQuery(IRequest iRequest, HlsCusContractFinanceIncome contractFinanceIncome, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return contractFinanceIncomeMapper.reportQuery(contractFinanceIncome);
    }

    @Override
    public List<HlsCusContractFinanceIncome> selectFeeByCfItem(IRequest iRequest, HlsCusContractFinanceIncome contractFinanceIncome, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return contractFinanceIncomeMapper.selectFeeByCfItem(contractFinanceIncome);
    }

    @Override
    public List<HlsCusContractFinanceIncome> selectFeeByCfItemForPreLeaseInterest(IRequest iRequest, HlsCusContractFinanceIncome contractFinanceIncome, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return contractFinanceIncomeMapper.selectFeeByCfItemForPreLeaseInterest(contractFinanceIncome);
    }

    @Override
    public List<HlsCusContractFinanceIncome> selectFeeByCfItemForInterest(IRequest iRequest, HlsCusContractFinanceIncome contractFinanceIncome, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return contractFinanceIncomeMapper.selectFeeByCfItemForInterest(contractFinanceIncome);
    }

    @Override
    public List<HlsCusConContract> selectStampDuty(HlsCusConContract hlsCusConContract) {
        return contractFinanceIncomeMapper.selectStampDuty(hlsCusConContract);
    }

    @Override
    public List<HlsCusContractFinanceIncome> selectImportTempList(Long headerId, Long contractId) throws ParseException {
        List<HlsCusContractFinanceIncome> tempList = contractFinanceIncomeMapper.selectImportTempList(headerId);
        tempList.forEach((item) -> {
            try {
                item.setErrorMsg(this.validImportRecord(item, contractId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return tempList;
    }

    private String validImportRecord(HlsCusContractFinanceIncome item, Long contractId) throws ParseException {
        if (!StringUtils.isAnyBlank(new CharSequence[]{item.getContractNumber(), item.getPeriodName(), item.getCfItemN(), item.getTimes(), String.valueOf(item.getDays())}) && item.getFinanceIncome() != null) {
            StringBuffer errMsg = new StringBuffer();
            HlsCusPrjProject prj_project =new HlsCusPrjProject();
            prj_project.setProjectNumber(item.getContractNumber());
            HlsCusPrjProject result = this.hlsCusPrjProjectMapper.selectOne(prj_project);
            if (result == null) {
                errMsg.append("合同信息有误;");
                return errMsg.toString();
            } else if (!result.getContractId().equals(contractId)) {
                errMsg.append("导入的合同信息与所选择的合同信息不匹配;");
                return errMsg.toString();
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                Date startDate = sdf.parse(sdf.format(item.getStartDate()));
                Date endDate = sdf.parse(sdf.format(item.getEndDate()));
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);
                long time1 = cal.getTimeInMillis();
                cal.setTime(endDate);
                long time2 = cal.getTimeInMillis();
                long betweenDays = (time2 - time1) / (1000 * 3600 * 24) + 1;

                if (betweenDays != item.getDays()) {
                    errMsg.append("天数信息有误;");
                    return errMsg.toString();
                }

                List<Map> kindMap = cashflowMapper.selectCashflowItem();
                boolean itemFlag = false;
                Iterator var10 = kindMap.iterator();

                while (var10.hasNext()) {
                    Map map = (Map) var10.next();
                    if (item.getCfItemN().equals(map.get("description"))) {
                        itemFlag = true;
                    }
                }

                if (!itemFlag) {
                    errMsg.append("现金流类型不存在;");
                }

                return errMsg.toString();
            }
        } else {
            return "导入信息不完整!请核实后再上传";
        }
    }

    @Override
    public void importConfirm(IRequest request, List<HlsCusContractFinanceIncome> list, Long contractId) {
        long errorCount = list.stream().filter((o) -> {
            return StringUtils.isNoneBlank(new CharSequence[]{o.getErrorMsg()});
        }).count();
        if (errorCount > 0L) {
//            throw new HlsCusException("数据不合规范，请查看错误信息后再导入!");
        } else {
            List<HlsCusContractFinanceIncome> incomeList = this.fillInfo(request, list);
            List<HlsCusContractFinanceIncome> result = this.batchUpdate(request, incomeList);

            for (HlsCusContractFinanceIncome item : result) {
                Date startDate = item.getStartDate();
                Date start = startDate;
                Date endDate = item.getEndDate();
                Calendar cd = Calendar.getInstance();

                List<GldFinanceIncomeDay> dayList = new ArrayList<>();
                Double lastAmount = 0D;
                while (startDate.getTime() <= endDate.getTime()) {
                    DecimalFormat dF = new DecimalFormat("#######################0.00");
                    Double amortizationAmount = Double.parseDouble(dF.format(item.getFinanceIncome() / item.getDays()));

                    HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
                    contractCashflow.setCashflowId(item.getGldCashflowId());
                    contractCashflow = hlsCusConContractCashflowService.selectByPrimaryKey(request, contractCashflow);

                    GldFinanceIncomeDay gldFinanceIncomeDay = new GldFinanceIncomeDay();
                    gldFinanceIncomeDay.setContractId(item.getContractId());
                    gldFinanceIncomeDay.setCashflowId(item.getGldCashflowId());
                    gldFinanceIncomeDay.setCompanyId(item.getCompanyId());
                    gldFinanceIncomeDay.setPeriodName(item.getPeriodName());
                    gldFinanceIncomeDay.setAmortizationDate(start);
                    gldFinanceIncomeDay.setCfItem(contractCashflow.getCfItem());

                    if (startDate.getTime() != endDate.getTime()) {
                        gldFinanceIncomeDay.setAmortizationAmount(amortizationAmount);
                        gldFinanceIncomeDay.setPreAmortizationAmount(amortizationAmount);
                        lastAmount += amortizationAmount;
                    } else {
                        gldFinanceIncomeDay.setAmortizationAmount(Double.parseDouble(dF.format(item.getFinanceIncome() - lastAmount)));
                        gldFinanceIncomeDay.setPreAmortizationAmount(Double.parseDouble(dF.format(item.getFinanceIncome() - lastAmount)));
                    }
                    gldFinanceIncomeDay.setAdjustmentAmount(0D);

                    gldFinanceIncomeDay.set__status("insert");
                    dayList.add(gldFinanceIncomeDay);

                    cd.setTime(start);
                    cd.add(Calendar.DATE, 1);
                    start = cd.getTime();

                    cd.setTime(startDate);
                    cd.add(Calendar.DATE, 1);
                    startDate = cd.getTime();
                }
                try{
                    throw new HlsCusException("");
                }catch(Exception e){
                    e.printStackTrace();
                }
                gldFinanceIncomeDayService.batchUpdate(request, dayList);
            }
        }
    }

    private List<HlsCusContractFinanceIncome> fillInfo(IRequest request, List<HlsCusContractFinanceIncome> dto) {
        Iterator var2 = dto.iterator();

        while (var2.hasNext()) {

            HlsCusContractFinanceIncome item = (HlsCusContractFinanceIncome) var2.next();

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                item.setStartDate(df.parse(df.format(item.getStartDate())));
                item.setEndDate(df.parse(df.format(item.getEndDate())));
            } catch (Exception e) {
                e.printStackTrace();
            }

            DecimalFormat dF = new DecimalFormat("#######################0.00");
            item.setFinanceIncome(Double.parseDouble(dF.format(item.getFinanceIncome())));

            //设置合同id
            HlsCusConContract conContract = new HlsCusConContract();
            conContract.setContractNumber(item.getContractNumber());
            conContract = hlsCusConContractService.selectSelective(request, conContract).get(0);
            item.setContractId(conContract.getContractId());

            //设置现金流
            List<Map> kindMap = cashflowMapper.selectCashflowItem();
            String cfType = "";
            String cfItem = "";
            Iterator var10 = kindMap.iterator();

            while (var10.hasNext()) {
                Map map = (Map) var10.next();
                if (item.getCfItemN().equals(map.get("description"))) {
                    cfType = map.get("cfType").toString();
                    cfItem = map.get("cfItem").toString();
                }
            }
            HlsCusConContractCashflow conCashflow = new HlsCusConContractCashflow();
            conCashflow.setContractId(item.getContractId());
            conCashflow.setCfItem(Long.parseLong(cfItem));
            conCashflow.setCfType(Long.parseLong(cfType));
            conCashflow.setTimes(Long.parseLong(item.getTimes()));
            conCashflow = hlsCusConContractCashflowService.selectSelective(request, conCashflow).get(0);
            item.setGldCashflowId(conCashflow.getCashflowId());

            //设置数据来源
            item.setSourceType("CON_CONTRACT");
            item.setSourceId(conContract.getContractId());
            //设置报价
            item.setQuotationId(conContract.getQuotationId());

            item.setPostFlag("N");
            item.setCompanyId(request.getCompanyId());
            item.set__status("insert");
        }
        return dto;
    }

    @Override
    public List<HlsCusContractFinanceIncome> selectFinanceIncome(IRequest iRequest, HlsCusContractFinanceIncome contractFinanceIncome, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return contractFinanceIncomeMapper.selectFinanceIncome(contractFinanceIncome);
    }
}