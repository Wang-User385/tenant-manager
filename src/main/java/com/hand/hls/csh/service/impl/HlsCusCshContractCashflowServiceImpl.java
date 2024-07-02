package com.hand.hls.csh.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.mapper.HlsCusCshContractCashflowMapper;
import com.hand.hls.csh.dto.HlsCusCshContractCashflow;
import com.hand.hls.csh.service.HlsCusCshContractCashflowService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusCshContractCashflowServiceImpl  extends BaseServiceImpl<HlsCusCshContractCashflow> implements HlsCusCshContractCashflowService {
    @Autowired
    private HlsCusCshContractCashflowMapper hlsCusCshContractCashflowMapper;
    @Override
    public List<Map> selectNotFullContractCashflowHome(IRequest request, HlsCusCshContractCashflow hlsCusCshContractCashflow, int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        return hlsCusCshContractCashflowMapper.selectNotFullContractCashflowHome(hlsCusCshContractCashflow);
    }

    public List<Map> selectAllContractCashflowHome(IRequest request, HlsCusCshContractCashflow hlsCusCshContractCashflow, int page, int pageSize,String sortName,String sortOrder) {
        String orderBy = null;
        if(sortName!=null){
            if(orderBy==null){
                orderBy=sortName+" "+sortOrder;
            }else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }
        PageHelper.startPage(page,pageSize);
        if(StringUtils.isNotEmpty(orderBy)){
            PageHelper.orderBy(orderBy);
        }

        return hlsCusCshContractCashflowMapper.selectAllContractCashflowHome(hlsCusCshContractCashflow);
    }

    public List<Map> selectRecoveryRate(IRequest request, HlsCusCshContractCashflow hlsCusCshContractCashflow, int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        return hlsCusCshContractCashflowMapper.selectRecoveryRate(hlsCusCshContractCashflow);
    }

    public List<Map> selectUnitRecoveryRate(IRequest request, HlsCusCshContractCashflow hlsCusCshContractCashflow, int page, int pageSize,String sortName,String sortOrder) {
        String orderBy = null;
        if(sortName!=null){
            if(orderBy==null){
                orderBy=sortName+" "+sortOrder;
            }else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }
        PageHelper.startPage(page,pageSize);
        if(StringUtils.isNotEmpty(orderBy)){
            PageHelper.orderBy(orderBy);
        }
        return hlsCusCshContractCashflowMapper.selectUnitRecoveryRate(hlsCusCshContractCashflow);
    }

    public List<HlsCusCshContractCashflow> selectUnitLov(IRequest request, HlsCusCshContractCashflow hlsCusCshContractCashflow, int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        return hlsCusCshContractCashflowMapper.selectUnitLov(hlsCusCshContractCashflow);
    }

        private final static String SHEET_NAME = "sheet1";//sheet名称
    private final static String FILE_NAME = "应收实收情况统计";//文件名称
    private static final List<String> colNameList = Lists.newArrayList(
            "业务合同编码", "合同编号", "合同名称", "客户编号", "客户名称", "项目经理", "业务部门", "期数/总期数", "应收日期", "应收金额", "本金", "利息", "咨询服务费", "保证金",
            "手续费", "首付款", "租前息", "留购金", "实收金额", "未收金额", "币种", "回款银行", "回款账户", "核销状态");
    private static final List<String> colGetMethods = Lists.newArrayList("approvalNumber", "contractNumber", "contractName", "bpCode", "bpName", "managerName", "unitName",
            "timesTotal", "dueDate", "dueAmount", "thePrincipal", "theInterest", "theConsultingServiceFee", "theMargin", "theLeaseCharge", "theDownPayment", "thePreRent", "theResidualValue", "receivedAmount", "surplusAmount", "currencyName", "backBankName", "backBankAccountNum", "writeOffFlagDesc");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
}
