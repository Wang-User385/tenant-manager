package com.hand.hls.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.app.dto.HlsCashflowAyncDto;
import com.hand.hls.app.entity.HlsCusItfcResponseData;
import com.hand.hls.app.mapper.HlsCashflowAyncMapper;
import com.hand.hls.app.service.HlsCashflowAyncService;
import com.hand.hls.app.utils.HLsCashFlowAyncEnum;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.prj.mapper.HlsCusPrjProjectLeaseItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCashflowAyncServiceImpl extends BaseServiceImpl<HlsCashflowAyncDto> implements HlsCashflowAyncService {

    @Autowired
    HlsCashflowAyncService hlsCashflowAyncService;

    @Autowired
    HlsCashflowAyncMapper hlsCashflowAyncMapper;

    @Autowired
    HlsCusConContractMapper hlsCusConContractMapper;

    @Autowired
    private HlsCusPrjProjectLeaseItemMapper hlsCusPrjProjectLeaseItemMapper;

    @Autowired
    HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;

    static Map<String, String> projectType = new HashMap<String, String>(200) {

    };

    static {
        projectType.put("GENERAL_OPERATING_LEASE", "26");
        projectType.put("LEASE", "27");
        projectType.put("LEASEBACK", "28");
        projectType.put("SUBLEASE", "29");
    }

    @Override
    public HlsCusItfcResponseData getResult(IRequest iRequest, String fromDate, String toDate) throws NullPointerException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        HlsCusItfcResponseData result = new HlsCusItfcResponseData();
        String compCode = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("FINANCE_INTERFACE", "compCode");
//        日期校验 有误为true
        if (fromDate.trim().length() != 0 && toDate.trim().length() != 0){
            if (dateVerification(fromDate)){
                result.setMsg(HLsCashFlowAyncEnum.DataFaild.getMessage());
                result.setData(null);
                result.setCode(HLsCashFlowAyncEnum.DataFaild.getCode());
                return result;
            }
            if (dateVerification(toDate)){
                result.setMsg(HLsCashFlowAyncEnum.DataFaild.getMessage());
                result.setData(null);
                result.setCode(HLsCashFlowAyncEnum.DataFaild.getCode());
                return result;
            }
            if (!dateComparison(fromDate,toDate)){
                result.setMsg(HLsCashFlowAyncEnum.DataFaild.getMessage());
                result.setData(null);
                result.setCode(HLsCashFlowAyncEnum.DataFaild.getCode());
                return result;
            }
        } else if (fromDate.trim().length() != 0 && toDate.trim().length() == 0){
            if (dateVerification(fromDate)){
                result.setMsg(HLsCashFlowAyncEnum.DataFaild.getMessage());
                result.setData(null);
                result.setCode(HLsCashFlowAyncEnum.DataFaild.getCode());
                return result;
            }
        } else if (fromDate.trim().length() == 0 && toDate.trim().length() != 0){
            result.setMsg("开始日期" + HLsCashFlowAyncEnum.NULL.getMessage());
            result.setData(null);
            result.setCode(HLsCashFlowAyncEnum.NULL.getCode());
            return result;
        } else if (fromDate.trim().length() == 0 && toDate.trim().length() == 0) {}
        ArrayList<Object> objects = new ArrayList<>();
        List<HlsCashflowAyncDto> repayment = null;
        repayment = hlsCashflowAyncMapper.getRepayment(fromDate, toDate);
        for (int i = 0; i < repayment.size(); i++){
            HlsCusConContract contract = new HlsCusConContract();
            contract.setContractId(repayment.get(i).getContractId());
            HlsCusConContract hlsCusConContract = hlsCusConContractMapper.selectOne(contract);
            HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
            cashflow.setContractId(hlsCusConContract.getContractId());
            ArrayList<Object> cashflowArray = new ArrayList<>();
            List<HlsCusConContractCashflow> cashflows = hlsCusConContractCashflowMapper.select(cashflow);
            HashMap<String, Object> data = new HashMap<>();
            //公司编码
            data.put("compCode", compCode);
            //项目ID唯一标识  支付表ID
            data.put("bizProjId", hlsCusConContract.getContractId().toString());
            //项目编号
            data.put("projCode", hlsCusConContract.getProjectNumber());
            //合同编号
            data.put("contractCode", hlsCusConContract.getContractNumber());
            //项目名称
            data.put("projName", hlsCusConContract.getProjectName());
            //项目类别
            data.put("projType", projectType.get(hlsCusConContract.getBusinessType()));
            //投放日
            data.put("putinDay", simpleDateFormat.format(hlsCusConContract.getLeaseStartDate()));
            //预期到期日
            data.put("expectDay", simpleDateFormat.format(hlsCusConContract.getLeaseEndDate()));
            //投放金额
            data.put("investAmt", hlsCusConContract.getFinanceAmount());
            //预期收益率
            data.put("investRate", hlsCusConContract.getIrr());
            //LRR收益率
            data.put("lrrRate", hlsCusConContract.getIrr());
            //回传类型 AL001项目投放 AL002项目回款
            data.put("ioCode", "AL001");
            //债权人
            data.put("creditorUser", hlsCusConContract.getBpName());
            //债务人
            data.put("debtorUser", hlsCusConContract.getBpName());
            //保证金
            data.put("cashAmt", hlsCusConContract.getDeposit());
            //首期租金
            data.put("firstAmt", hlsCusConContract.getDownPayment());
            //保证金比率
            data.put("cashRate", hlsCusConContract.getDepositRatio());
            //首期费率
            data.put("firstRate", hlsCusConContract.getDownPaymentRatio());
            for (int j = 0; j < cashflows.size(); j++){
                HlsCusConContractCashflow dueCash = cashflows.get(j);
                if (cashflows.get(j).getCfItem() != 1L && cashflows.get(j).getCfItem() != 0L){
                    continue;
                }
                JSONObject line = new JSONObject(new LinkedHashMap());
                String paytime;
                if (null != dueCash.getFullWriteOffDate()) {
                    paytime = simpleDateFormat.format(dueCash.getFullWriteOffDate());
                } else {
                    paytime = "";
                }
                //期数
                line.put("period", dueCash.getTimes());
                //计划本金
                line.put("periodAmount", dueCash.getPrincipal());
                //计划利息
                line.put("periodInterestAmount", dueCash.getInterest());
                //已还本金
                line.put("repayAmount", dueCash.getReceivedPrincipal());
                //已还利息
                line.put("repayInterestAmount", dueCash.getReceivedInterest());
                //计划还款日期
                line.put("periodEndDate", simpleDateFormat.format(dueCash.getDueDate()));
                //实际还款日期
                line.put("payTime", paytime);
                //逾期天数
                line.put("overdueDays", dueCash.getOverdueMaxDays());
                //应收罚息
                line.put("periodFineAmount", 0);
                //已还罚息
                line.put("repayFineAmount", 0);
                cashflowArray.add(line);
            }
            data.put("lineList",cashflowArray);
            objects.add(data);
        }
//       修改标志的修改操作
        for (int z = 0; z < repayment.size(); z++){
            HlsCashflowAyncDto cashflowAyncDto = new HlsCashflowAyncDto();
            cashflowAyncDto.setPostFlag("Y");
            cashflowAyncDto.setType(repayment.get(z).getType());
            cashflowAyncDto.setInceptionDate(repayment.get(z).getInceptionDate());
            cashflowAyncDto.setContractId(repayment.get(z).getContractId());
            cashflowAyncDto.setItfId(repayment.get(z).getItfId());
            cashflowAyncDto.setPostedDate(new Date());
            self().updateByPrimaryKeySelective(iRequest,cashflowAyncDto);
        }

        result.setData(objects);
        result.setCode(HLsCashFlowAyncEnum.SUCCESS.getCode());
        result.setMsg(HLsCashFlowAyncEnum.SUCCESS.getMessage());
        return result;
    }

    public static Boolean dateComparison(String from, String to){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date fDate = null;
        Date tDate = null;
        try {
            fDate = format.parse(from);
        } catch (ParseException e) {
            System.out.println("开始日期格式错误");
        }
        try {
            tDate = format.parse(to);
        } catch (ParseException e) {
            System.out.println("结束日期格式错误");
        }
        //是否早于给定日期
        if (tDate.before(fDate)){
            return false;
        }
        return true;
    }

    public static Boolean dateVerification(String date){
        int[] nums = {31,28,31,30,31,30,31,31,30,31,30,31};
        String pattern = "^\\d{4}\\d{2}\\d{2}";
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(date);
        if (!matcher.matches()){
            return true;
        }
        int year = Integer.parseInt(date.substring(0,4));
        int month = Integer.parseInt(date.substring(4,6));
        int day = Integer.parseInt(date.substring(6));

        if (year < 1900){
            return true;
        }
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            nums[1] = 29;
        }
        if (month < 1 || month > 12) {
            return true;
        }
        if (day < 1 || day > nums[month - 1]){
            return true;
        }
        return false;
    }

    @Override
    public void updateResult(IRequest iRequest,HlsCashflowAyncDto hlsCashflowAyncDtoNew) {
        HlsCashflowAyncDto old = hlsCashflowAyncMapper.getOneRepayment(hlsCashflowAyncDtoNew.getContractId());
        old.setPostFlag("N");
        old.setType(hlsCashflowAyncDtoNew.getType());
        old.setInceptionDate(hlsCashflowAyncDtoNew.getInceptionDate());

        self().updateByPrimaryKeySelective(iRequest,old);
    }
}
