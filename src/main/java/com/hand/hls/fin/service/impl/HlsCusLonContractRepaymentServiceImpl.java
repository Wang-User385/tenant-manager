package com.hand.hls.fin.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.ICodeService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReqLn;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqLn;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqHdMapper;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqLnMapper;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusHlsCreditLine;
import com.hand.hls.fct.service.HlsCreditLineService;
import com.hand.hls.fin.dto.*;
import com.hand.hls.fin.mapper.HlsCusLonContractMapper;
import com.hand.hls.fin.mapper.HlsCusLonContractRepaymentMapper;
import com.hand.hls.fin.mapper.HlsCusLonContractWithdrawMapper;
import com.hand.hls.fin.service.*;
import com.hand.hls.fnd.service.HlsCusImpDataService;
import com.hand.hls.fnd.utils.HlsCusImportDataUtil;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.mapper.HlsCusGldLonContractFinCostMapper;
import com.hand.hls.utils.ExportExcelUtil;
import com.hand.hls.utils.HlsCusConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusLonContractRepaymentServiceImpl extends BaseServiceImpl<HlsCusLonContractRepayment> implements HlsCusLonContractRepaymentService {

    @Autowired
    private HlsCusLonContractRepaymentMapper lonContractRepaymentMapper;

    @Autowired
    private HlsCusCshPaymentReqHdMapper cshPaymentReqHdMapper;
    @Autowired
    private HlsCusCshPaymentReqLnMapper cshPaymentReqLnMapper;

    @Autowired
    private HlsCusLonContractService hlsCusLonContractService;

    @Autowired
    private HlsCreditLineService hlsCreditLineService;

    @Autowired
    private JeTrxCommonService commonService;

    @Autowired
    private HlsCusContractRepaymentLnService hlsCusContractRepaymentLnService;

    @Autowired
    private ICodeService codeService;

    @Autowired
    private HlsCusImpDataService impDataService;

    @Autowired
    private HlsCusLonContractWithdrawMapper lonContractWithdrawMapper;

    @Autowired
    private HlsCusLonContractMapper lonContractMapper;


//    @Autowired
//    private HlsCusICreditContractService creditContractService;

    @Autowired
    private HlsCusCtLonContractPledgeService lonContractPledgeService;

    @Autowired
    private HlsCusBpMasterService hlsCusBpMasterService;

    @Autowired
    private HlsCusCtLonBankAccountService hlsCusCtLonBankAccountService;

    @Autowired
    private HlsCusLonContractQuotationService hlsCusLonContractQuotationService;

    @Autowired
    private HlsCusLonContractWithdrawService lonContractWithdrawService;

    @Autowired
    private HlsCusGldLonContractFinCostMapper gldLonContractFinCostMapper;

    @Autowired
    private HlsCusLonContractRepaymentService hlsCusLonContractRepaymentService;
    @Override
    public List<Map<String, Object>> queryReport(IRequest request, Map<String, Object> map) {
        PageHelper.startPage((int) map.get("page"), (int) map.get("pageSize"));
        return lonContractRepaymentMapper.queryReport(map);
    }

    @Override
    public void batchDeleteRepayment(IRequest iRequest, List<HlsCusLonContractRepayment> dto) throws HlsCusException {
        if (dto.size() > 0) {
            for (HlsCusLonContractRepayment lonContractRepayment : dto
            ) {
                if ("Y".equalsIgnoreCase(lonContractRepayment.getConfirmFlag())) {
                    throw new HlsCusException("已经确认的还款计划不可删除，请核对！");
                }
            }

            self().batchDelete(dto);
            //计算XIRR
            HlsCusLonContractWithdraw lonContractWithdraw=new HlsCusLonContractWithdraw();
            lonContractWithdraw.setWithdrawId(dto.get(0).getWithdrawId());
            lonContractWithdraw=lonContractWithdrawService.selectByPrimaryKey(iRequest,lonContractWithdraw);
            HlsCusLonContractQuotation lonContractQuotation=new HlsCusLonContractQuotation();
            lonContractQuotation.setWithdrawId(lonContractWithdraw.getWithdrawId());
            HlsCusLonContractQuotation quotation = hlsCusLonContractQuotationService.select(iRequest,lonContractQuotation,1,1).get(0);
            lonContractWithdrawService.calXirr(iRequest,lonContractWithdraw,quotation);
        }
    }

    @Override
    public List<HlsCusLonContractRepayment> selectLonContractRep(IRequest request, HlsCusLonContractRepayment lonContractRepayment, int page, int pageSize, String model) {
        //model的设置，Rep表示本息还款计划，Fin表示融资服务费还款计划
        final String Rep = "Rep";
        final String Fin = "Fin";

        //构造参数
        if (!"".equals(lonContractRepayment.getRepaymentIds()) && lonContractRepayment.getRepaymentIds() != null) {
            String[] sArr = lonContractRepayment.getRepaymentIds().split("-");
            Long[] lArr = new Long[sArr.length];
            for(int i = 0;i<sArr.length;i++){
                lArr[i] = new Long(sArr[i]);
            }
            lonContractRepayment.setRepaymentIdList(lArr);
        }
        PageHelper.startPage(page, pageSize);
        List<HlsCusLonContractRepayment> list = null;
        if (model.equals(Rep)) {
            list = lonContractRepaymentMapper.selectLonContractRep(lonContractRepayment);
        } else {
            list = lonContractRepaymentMapper.selectLonContractFin(lonContractRepayment);
        }

        //获取已申请金额
        for (HlsCusLonContractRepayment item : list) {
            Double approvedAmount = 0D;
            HlsCusCshPaymentReqLn reqLn = new HlsCusCshPaymentReqLn();
            reqLn.setCompanyId(item.getCompanyId());
            reqLn.setSourceDocCategory("LON_CONTRACT_WITHDRAW");
            reqLn.setSourceDocId(item.getWithdrawId());
            reqLn.setSourceDocLineId(item.getRepaymentId());
            //获取当前还款id的所有付款申请行信息
            List<HlsCusCshPaymentReqLn> reqList = cshPaymentReqLnMapper.select(reqLn);
            if (reqList.size() > 0) {
                for (HlsCusCshPaymentReqLn req : reqList) {
                    //判断对应的头上的申请状态
                    HlsCusCshPaymentReqHd hd = new HlsCusCshPaymentReqHd();
                    hd.setPaymentReqId(req.getPaymentReqId());
                    String paymentStatus = "";
                    if (cshPaymentReqHdMapper.selectOne(hd) != null) {
                        paymentStatus = cshPaymentReqHdMapper.selectOne(hd).getPaymentReqStatus();
                    }

                    //如果申请状态不是被拒绝 计算申请总额
                    if (!"REJECTED".equals(paymentStatus)) {
                        approvedAmount = CalculateUtil.add(approvedAmount, req.getAmount());
                    }
                    //如果获取了参数 申请头ID 判断当前申请行是否属于当前申请头  如果属于 获取本次申请金额和申请行ID(明细页面)
                    if (lonContractRepayment.getPaymentReqId() != null && lonContractRepayment.getPaymentReqId().compareTo(0L) != 0 && req.getPaymentReqId().compareTo(lonContractRepayment.getPaymentReqId()) == 0) {
                        item.setApplyAmount(req.getAmount());
                        item.setPaymentReqId(lonContractRepayment.getPaymentReqId());
                        item.setPaymentReqLineId(req.getPaymentReqLnId());
                        item.setPaymentStatus(paymentStatus);

                    }
                }
            } else {
                item.setPaymentStatus("NEW");
            }
            //获取当前还款行的已申请金额
            item.setApprovedAmount(approvedAmount);
            //如果本次申请金额为null 初始化本次申请金额为 实际还款金额-已申请金额
            if (item.getApplyAmount() == null) {
                item.setApplyAmount(CalculateUtil.sub(item.getDueAmount(), item.getApprovedAmount()));
            }
        }
        return list;
    }

    @Override
    public void confirmRepayment(IRequest request, List<HlsCusLonContractWithdraw> lonContractWithdraws) throws hls.core.utils.exception.HlsCusException {
        HlsCusLonContract hlsCusLonContract = new HlsCusLonContract();
        hlsCusLonContract.setContractId(lonContractWithdraws.get(0).getContractId());
        hlsCusLonContract = hlsCusLonContractService.selectByPrimaryKey(request, hlsCusLonContract);

        //是否最后一期利息标识
        Boolean lastIntersetFlag=false;

        Long maxTimes = lonContractRepaymentMapper.selectInterestMaxTimes(lonContractWithdraws.get(0).getWithdrawId());

        HlsCusHlsCreditLine hlsCusHlsCreditLine = null;

        if (hlsCusLonContract.getCreditLineId() != null) {
            hlsCusHlsCreditLine = new HlsCusHlsCreditLine();
            hlsCusHlsCreditLine.setCreditLineId(hlsCusLonContract.getCreditLineId());
            hlsCusHlsCreditLine = hlsCreditLineService.selectByPrimaryKey(request, hlsCusHlsCreditLine);
        }


        String[] repaymentIdArray = null;
        if (lonContractWithdraws.get(0).getRepaymentIdList() != null) {
            repaymentIdArray = lonContractWithdraws.get(0).getRepaymentIdList().split(",");
        }

        for (String repaymentId : repaymentIdArray) {

            HlsCusLonContractRepayment hlsCusLonContractRepayment = new HlsCusLonContractRepayment();
            hlsCusLonContractRepayment.setRepaymentId(Long.parseLong(repaymentId));
            hlsCusLonContractRepayment = self().selectByPrimaryKey(request, hlsCusLonContractRepayment);
            if (hlsCusLonContractRepayment.getExchangeRate() == null) {
                hlsCusLonContractRepayment.setExchangeRate(1D);
            }

            if(maxTimes!=null&&"302".equals(hlsCusLonContractRepayment.getCfItem())&&maxTimes.equals(hlsCusLonContractRepayment.getTimes())){
                lastIntersetFlag=true;
            }


            Double rate = hlsCusLonContractRepayment.getExchangeRate() == null ? 1D : hlsCusLonContractRepayment.getExchangeRate();
            //计划还款人民币金额
            BigDecimal cnyPlannedDueAmount = ((new BigDecimal(rate.toString()).multiply(new BigDecimal(hlsCusLonContractRepayment.getPlannedDueAmount().toString()))).setScale(2, BigDecimal.ROUND_HALF_UP));
            //已还款人民币金额
            BigDecimal receiverCnyAmount = hlsCusLonContractRepayment.getCnyDueAmount()== null ? BigDecimal.ZERO : new BigDecimal(hlsCusLonContractRepayment.getCnyDueAmount().toString());
            //已还款外币金额
            BigDecimal receiverAmount = hlsCusLonContractRepayment.getDueAmount()== null ? BigDecimal.ZERO : new BigDecimal(hlsCusLonContractRepayment.getDueAmount().toString());

            //本次核销本金金额
            BigDecimal writeOffAmount = BigDecimal.ZERO;
            for (int i = 0; i < lonContractWithdraws.size(); i++) {
                HlsCusLonContractWithdraw withdraw = lonContractWithdraws.get(i);
                HlsCusContractRepaymentLn ln = new HlsCusContractRepaymentLn();
                ln.setWithdrawId(withdraw.getWithdrawId());
                ln.setRepaymentId(Long.parseLong(repaymentId));
                ln.setBankAccountId(withdraw.getBankAccountId());
                ln.setBankBranchName(withdraw.getBankBranchName());
                ln.setBankAccountName(withdraw.getBankAccountName());
                ln.setBankAccountNum(withdraw.getBankAccountNum());
                ln.setDueDate(withdraw.getDueDate());
                ln.setCnyDueAmount(withdraw.getCnyDueAmount());
                receiverCnyAmount=receiverCnyAmount.add(new BigDecimal(withdraw.getCnyDueAmount().toString()));
                if (receiverCnyAmount.compareTo(cnyPlannedDueAmount)==0) {
                    ln.setDueAmount((new BigDecimal(hlsCusLonContractRepayment.getPlannedDueAmount().toString())
                            .subtract(receiverAmount)).doubleValue());
                } else {
                    ln.setDueAmount((new BigDecimal(withdraw.getCnyDueAmount().toString()).divide(new BigDecimal(hlsCusLonContractRepayment.getExchangeRate()), 2, BigDecimal.ROUND_HALF_UP)).doubleValue());
                    receiverAmount = receiverAmount.add(new BigDecimal(ln.getDueAmount().toString()));
                }
                ln.set__status(DTOStatus.ADD);
                ln = hlsCusContractRepaymentLnService.insertSelective(request, ln);
                hlsCusLonContractRepayment.setDueDate(withdraw.getDueDate());

                if (hlsCusLonContractRepayment.getCfItem().equals(301L)) {
                    writeOffAmount = writeOffAmount.add(new BigDecimal(ln.getDueAmount().toString()));
                }

                if (hlsCusLonContract.getLonCompanyId().equals(265L) || hlsCusLonContract.getLonCompanyId().equals(266L)) {

                } else {
                    //插入凭证事物流水表
                    AbstractJeTrxService repaymentService = commonService.map.get("CSH_TRANSACTION_LOAN_RE");
                    Map params = new HashMap<>();
                    params.put("jeTrxId", ln.getLnId());
                    params.put("companyId", hlsCusLonContract.getCompanyId());
                    params.put("contractId", Long.parseLong(repaymentId));
                    params.put("sourceDoc", "LON_CONTRACT_REPAYMENT");
                    repaymentService.process(request, params);
                }
            }

            HlsCusContractRepaymentLn repaymentLnAmountSum = hlsCusContractRepaymentLnService.selectRepaymentLnAmountSum(Long.parseLong(repaymentId));
            Double cnyTotalAmount = repaymentLnAmountSum.getCnyDueAmount();
            Double totalAmount = repaymentLnAmountSum.getDueAmount();

            hlsCusLonContractRepayment.setDueAmount(totalAmount);
            hlsCusLonContractRepayment.setCnyDueAmount(cnyTotalAmount);
            if (Double.compare(hlsCusLonContractRepayment.getCnyDueAmount(), cnyPlannedDueAmount.doubleValue()) < 0) {
                hlsCusLonContractRepayment.setConfirmFlag("Y");
                hlsCusLonContractRepayment.setWriteOffFlag("PARTIAL");
            } else if (Double.compare(hlsCusLonContractRepayment.getCnyDueAmount(), cnyPlannedDueAmount.doubleValue()) == 0) {
                hlsCusLonContractRepayment.setConfirmFlag("Y");
                hlsCusLonContractRepayment.setWriteOffFlag("FULL");
            } else {
                throw new hls.core.utils.exception.HlsCusException("还款金额不能大于计划还款金额");
            }
            hlsCusLonContractRepayment.set__status("update");
            hlsCusLonContractRepayment.setWriteOffAmount(hlsCusLonContractRepayment.getDueAmount());
            hlsCusLonContractRepayment = self().updateByPrimaryKeySelective(request, hlsCusLonContractRepayment);
        }

        /**
         * 更新合同是否结清状态
         */
        hlsCusLonContractService.updateContractSettleStatus();

        //更新租赁合同质押状态
        lonContractPledgeService.updateConContractPledgeFlag();

        //更新保理合同质押状态
        lonContractPledgeService.updateFctContractPledgeFlag();

        //更新质押释放状态
        lonContractPledgeService.updatePledgeReleaseFlag();


        //如果是最后一期还款利息 更新利息分摊
        if (lastIntersetFlag && "CNY".equals(hlsCusLonContract.getCurrency())) {
            Long withdrawId = lonContractWithdraws.get(0).getWithdrawId();
            //利息总和
            Double interestplannedDueAmount = lonContractRepaymentMapper.selectInterestplannedDueAmount(withdrawId);

            //分摊最后一个的id
            Long financeCostId = gldLonContractFinCostMapper.selectWithdrawLastTimeId(withdrawId);

            Double fincomeInCludAmount = gldLonContractFinCostMapper.selectFincomeInCludSumExcept(withdrawId, financeCostId);

            Double fincomeInClud = (new BigDecimal(interestplannedDueAmount.toString()).subtract(new BigDecimal(fincomeInCludAmount.toString()))).doubleValue();

            if (fincomeInClud.compareTo(new Double(0)) == 1) {
                //更新分摊金额
                gldLonContractFinCostMapper.updateFinCostIncome(financeCostId, fincomeInClud);

            } else {
                throw new hls.core.utils.exception.HlsCusException("问题数据！分摊金额小于0");
            }
        }


    }

//
//    @Override
//    public List<HlsCusLonContractRepayment> queryChartRep(IRequest request, HlsCusLonContractRepayment hlsCusLonContractRepayment, int page, int pageSize) {
//        PageHelper.startPage(page, pageSize);
//        return lonContractRepaymentMapper.queryChartRep(hlsCusLonContractRepayment);
//    }


    @Override
    public List<HlsCusLonContractRepayment> queryList(IRequest request, HlsCusLonContractRepayment hlsCusLonContractRepayment, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return lonContractRepaymentMapper.queryList(hlsCusLonContractRepayment);
    }

//    @Override
//    public List<HlsCusLonContractRepayment> rateChangeDetailCfCompare(IRequest request, HlsCusConFloatingRateReqLn dto) {
//        return lonContractRepaymentMapper.ctRateChangeDetailCompare(dto);
//    }
//
//    @Override
//    public List<HlsCusLonContractRepayment> unitQueryRep(IRequest request, HlsCusLonContractRepayment hlsCusLonContractRepayment, int page, int pageSize) {
//        PageHelper.startPage(page, pageSize);
//        Map<String, Object> params = new HashMap<String, Object>();
        //List<String> listCashItem = new ArrayList<String>();
        //List<String> listStatus = new ArrayList<String>();
        //String[] RepCashitem = null;
        //String[] RepStatus = null;
        //Long companyId = hlsCusLonContractRepayment.getCompanyId();
        //params.put("companyId", companyId);
//        if (hlsCusLonContractRepayment.getCshItems() != null) {
//            RepCashitem = hlsCusLonContractRepayment.getCshItems().split(",");
//            for (int i = 0; i < RepCashitem.length; i++) {
//                listCashItem.add(RepCashitem[i]);
//            }
//            params.put("listCashItem", listCashItem);
//        }
//        if (hlsCusLonContractRepayment.getStatuses() != null) {
//            RepStatus = hlsCusLonContractRepayment.getStatuses().split(",");
//            for (int i = 0; i < RepStatus.length; i++) {
//                listStatus.add(RepStatus[i]);
//            }
//            params.put("listStatus", listStatus);
//        }
        //params.put("repAmtFirst", hlsCusLonContractRepayment.getRepAmtFirst());
        //params.put("repAmtSecond", hlsCusLonContractRepayment.getRepAmtSecond());
        //params.put("repAmtThird", hlsCusLonContractRepayment.getRepAmtThird());
//        List<String> orgTypeList = new ArrayList<String>();
//        String[] orgTypes = null;
//        if (hlsCusLonContractRepayment.getOrgTypes() != null) {
//            orgTypes = hlsCusLonContractRepayment.getOrgTypes().split(",");
//            for (int i = 0; i < orgTypes.length; i++) {
//                orgTypeList.add(orgTypes[i]);
//            }
//            params.put("orgTypeList", orgTypeList);
//        }
        /*List<String> currencyList = new ArrayList<String>();
        if (!StringUtils.isEmpty(hlsCusLonContractRepayment.getCurrencys())) {
            String[] currencys = hlsCusLonContractRepayment.getCurrencys().split(",");
            for (int i = 0; i < currencys.length; i++) {
                currencyList.add(currencys[i]);
            }
            params.put("currencyList", currencyList);
        }

        List<String> writeOffFlagList = new ArrayList<>();
        if (!StringUtils.isEmpty(hlsCusLonContractRepayment.getWriteOffFlags())) {
            String[] writeOffFlags = hlsCusLonContractRepayment.getWriteOffFlags().split(",");
            for (int i = 0; i < writeOffFlags.length; i++) {
                writeOffFlagList.add(writeOffFlags[i]);
            }
            params.put("writeOffFlagList", writeOffFlagList);
        }

        params.put("amtFrom", hlsCusLonContractRepayment.getAmtFrom());
        params.put("amtTo", hlsCusLonContractRepayment.getAmtTo());
        params.put("creditContractName", hlsCusLonContractRepayment.getCreditContractName());
        params.put("bpName", hlsCusLonContractRepayment.getBpName());*/
//        params.put("lonCompanyIdDesc", hlsCusLonContractRepayment.getLonCompanyIdDesc());
        /*params.put("contractNumber", hlsCusLonContractRepayment.getContractNumber());
        params.put("contractName", hlsCusLonContractRepayment.getContractName());
        params.put("majorContractNumber", hlsCusLonContractRepayment.getMajorContractNumber());
        params.put("withdrawNumber", hlsCusLonContractRepayment.getWithdrawNumber());
        params.put("queryDateFrom", hlsCusLonContractRepayment.getQueryDateFrom());
        params.put("queryDateTo", hlsCusLonContractRepayment.getQueryDateTo());
        return lonContractRepaymentMapper.unitQueryRep(params);
    }*/

    private final static String Rep_SHEET_NAME = "sheet1";
    private final static String Rep_FILE_NAME = "融资还款";
    private final static String Rep_DATE_FORMAT = "yyyy-MM-dd";
    private static final List<String> Rep_colNameList = Lists.newArrayList(
            "提款编号", "融资合同编号", "融资主合同名称", "主合同编码", "融资机构", "币种", "提款金额", "期数", "还款项目", "应还金额", "已确认还款金额", "应还日期", "核销状态");
    private static final List<String> Rep_colGetMethods = Lists.newArrayList(
            "withdrawNumber", "contractNumber", "contractName", "majorContractNumber", "bpName", "currencyName", "withdrawDueAmount", "times", "cfItemDesc", "plannedDueAmount", "writeOffAmount", "plannedDueDate", "writeOffFlagDesc");
/*
    @Override
    public void unitQueryRepDownloadExcel(IRequest iRequest, HttpServletRequest request, HttpServletResponse response, HlsCusLonContractRepayment hlsCusLonContractRepayment) throws IOException, InvocationTargetException, IllegalAccessException {
        XSSFWorkbook xwork = new XSSFWorkbook();
        XSSFSheet sheet = xwork.createSheet(Rep_SHEET_NAME);
        int dataRowNum = ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, Rep_colNameList);

        List<HlsCusLonContractRepayment> unitSelects = this.unitQueryRep(iRequest, hlsCusLonContractRepayment, 1, 9999);
        for (HlsCusLonContractRepayment cashflow : unitSelects) {
            XSSFRow row = sheet.createRow(dataRowNum++);
            setDataUnitQueryRep(xwork, sheet, row, cashflow);
        }
        ExportExcelUtil.IOWrite(xwork, null, request, response, Rep_FILE_NAME);
    }

    private void setDataUnitQueryRep(XSSFWorkbook workbook, XSSFSheet sheet, XSSFRow row, HlsCusLonContractRepayment hlsCusLonContractRepayment) throws InvocationTargetException, IllegalAccessException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Rep_DATE_FORMAT);
        DecimalFormat df = new DecimalFormat("###,##0.00");
        for (int i = 0; i < Rep_colGetMethods.size(); i++) {
            XSSFCell cell = row.createCell(i);
            Object value = ExportExcelUtil.getValue(hlsCusLonContractRepayment, Rep_colGetMethods.get(i));
            if (value instanceof Date) {
                value = simpleDateFormat.format(value);
            }

            if (value instanceof Double) {
                XSSFCellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                value = df.format(value);
                cell.setCellStyle(cellStyle);
            }

            *//*
             *在对象中withdrawDueAmount设置成了String，这里需要数字样式，
             *为了不影响其他功能，未修改实体类，在这里处理，
             *修改实体类为Double后去除这个if语句
             *//*
            if(Rep_colGetMethods.get(i).equals("withdrawDueAmount")){
                XSSFCellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                cell.setCellStyle(cellStyle);
            }

            if (Objects.nonNull(value)) {
                ExportExcelUtil.initColWidth(sheet, i, value.toString());
                //设置固定的列宽
                sheet.setColumnWidth(1,256*20);
                sheet.setColumnWidth(2,256*30);
                sheet.setColumnWidth(3,256*15);
                sheet.setColumnWidth(4,256*35);
                sheet.setColumnWidth(6,256*20);
                sheet.setColumnWidth(10,256*20);
                sheet.setColumnWidth(12,256*12);
                cell.setCellValue(value.toString());
            }

        }
    }


    @Override
    public List<HlsCusLonContractRepayment> queryDebtMaturityStructureChart(IRequest request, HlsCusLonContractRepayment hlsCusLonContractRepayment, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("companyId", hlsCusLonContractRepayment.getCompanyId());
        String[] listAmountLong = null;

        if (hlsCusLonContractRepayment.getAmountLong() != null) {
            listAmountLong = hlsCusLonContractRepayment.getAmountLong().split(",");
            if (listAmountLong != null && listAmountLong.length == 1) {
                if (13 <= Long.parseLong(listAmountLong[0])) {
                    params.put("amountLong", Long.parseLong(listAmountLong[0]));
                } else if (12 >= Long.parseLong(listAmountLong[0])) {
                    params.put("amountShort", Long.parseLong(listAmountLong[0]));
                }

            }
        }

        params.put("amtFrom", hlsCusLonContractRepayment.getAmtFrom());
        params.put("amtTo", hlsCusLonContractRepayment.getAmtTo());

        List<HlsCusLonContractRepayment> list = lonContractRepaymentMapper.queryDebtMaturityStructureChart(params);
        Double totalAmount = 0D;
        for (HlsCusLonContractRepayment dt : list) {
            totalAmount = totalAmount + dt.getReimbursementAmount();
        }

        HlsCusLonContractRepayment lonContractRepayment = new HlsCusLonContractRepayment();
        lonContractRepayment.setRepaymentId(-1L);
        lonContractRepayment.setSumReimbursementAmount(totalAmount);
        if (CollectionUtils.isNotEmpty(list)) {
            list.add(lonContractRepayment);
        }
        return list;
    }*/

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int importData(IRequest iRequest, List<Map<String, String>> dataMap, Map<String, String> descMap, String lang) {
        int errorCount = 0;
        try {
            //传递时间格式
            List<CodeValue> dateFormat = codeService.selectCodeValuesByCodeName(iRequest, "FND_IMP_DATE_FORMAT");
            List<String> formats = new ArrayList<>();
            for (CodeValue c : dateFormat) {
                formats.add(c.getValue());
            }
            List<Object> objects = HlsCusImportDataUtil.dataToDto(HlsCusLonContractRepayment.class, dataMap, formats);


            Long withdrawId = Long.parseLong(dataMap.get(0).get("tempKey"));
            HlsCusLonContractWithdraw contractWithdraw = lonContractWithdrawMapper.selectByPrimaryKey(withdrawId);
            //获取交易信息
            HlsCusLonContractQuotation hlsCusLonContractQuotationTemp = new HlsCusLonContractQuotation();
            hlsCusLonContractQuotationTemp.setWithdrawId(withdrawId);
            List<HlsCusLonContractQuotation> hlsCusLonContractQuotations = hlsCusLonContractQuotationService.select(iRequest, hlsCusLonContractQuotationTemp, 1, 9999);
            //校验NPE
            if (CollectionUtils.isNotEmpty(hlsCusLonContractQuotations)) {
                hlsCusLonContractQuotationTemp = hlsCusLonContractQuotations.get(0);
            }
            //如果是一次性还本，那么需要校验导入的还款-本金只有一条数据
            if (HlsCusConstant.LON_INTEREST_CALC_METHOD.ONCE_REPAYMENT.equalsIgnoreCase(hlsCusLonContractQuotationTemp.getInterestCalcMethod())) {
                if (objects.size() > 1) {
                    for (Map<String, String> m : dataMap) {
                        impDataService.updateErrMessage(m, "当前计息方式为：一次性还本或者利随本清，只允许有一条还款-本金");
                    }
                    return 0;
                    //throw new HlsCusException("当前计息方式为："+hlsCusLonContractQuotationTemp.getInterestCalcMethod()+"只允许有一条还款-本金");
                }
            }

            //融资合同
            HlsCusLonContract lonContract = lonContractMapper.selectByPrimaryKey(contractWithdraw.getContractId());

            //获取授信机构
            HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
            hlsCusBpMaster.setBpId(lonContract.getCreditBpId());
            hlsCusBpMaster = hlsCusBpMasterService.selectByPrimaryKey(iRequest, hlsCusBpMaster);

            HlsCusCtLonBankAccount hlsCusCtLonBankAccount = new HlsCusCtLonBankAccount();
            hlsCusCtLonBankAccount.setBpId(hlsCusBpMaster.getBpId());
            List<HlsCusCtLonBankAccount> ctLonBankAccountList = hlsCusCtLonBankAccountService.select(iRequest, hlsCusCtLonBankAccount, 1, 999999);

            //在导入之前先将当前的现金流删除掉
            HlsCusLonContractRepayment repayment=new HlsCusLonContractRepayment();
            repayment.setWithdrawId(withdrawId);
            lonContractRepaymentMapper.delete(repayment);
            for (int i = 0; i < objects.size(); i++) {
                StringBuilder message = new StringBuilder();
                HlsCusLonContractRepayment lonContractRepayment = (HlsCusLonContractRepayment) objects.get(i);
                //判断是融资-还款本金 or 融资还款-利息
                if(HlsCusConstant.LON_REPAYMENT.PRINCIPAL_CF_ITEM_DESC.equalsIgnoreCase(lonContractRepayment.getCfItemDesc())){
                    lonContractRepayment.setCfItem(301L);
                }else if(HlsCusConstant.LON_REPAYMENT.INTEREST_CF_ITEM_DESC.equalsIgnoreCase(lonContractRepayment.getCfItemDesc())){
                    //如果测算类型是系统，那么导入的现金流中不能包括 利息
                    if(HlsCusConstant.MEASUREMENT_TYPE.SYSTEM_ESTIMATION.equalsIgnoreCase(hlsCusLonContractQuotationTemp.getMeasureType())){
                        message.append("系统测算类型不支持导入还款-利息");
                    }
                    lonContractRepayment.setCfItem(302L);
                }else{
                    message.append("不支持的现金流项目");
                }

                lonContractRepayment.setPlannedDueAmount((new BigDecimal(lonContractRepayment.getPlannedDueAmount().toString()).setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue());
                lonContractRepayment.setCnyDueAmount(0D);
                lonContractRepayment.setDueAmount(0D);
                lonContractRepayment.setWriteOffAmount(0D);
                lonContractRepayment.setInterestFlag("Y");
                lonContractRepayment.setPlannedCalcDate(lonContractRepayment.getPlannedCalcDate());
                lonContractRepayment.setPlannedDueDate(lonContractRepayment.getPlannedCalcDate());
                lonContractRepayment.setCfType(70L);
                lonContractRepayment.setCfDirection("OUTFLOW");
                lonContractRepayment.setCfStatus("RELEASE");
                lonContractRepayment.setWriteOffFlag("NOT");
                lonContractRepayment.setConfirmFlag("N");
                lonContractRepayment.setWithdrawId(contractWithdraw.getWithdrawId());
                lonContractRepayment.setContractId(contractWithdraw.getContractId());
                //      lonContractRepayment.setPlannedDueDate(lonContractRepayment.getPlannedCalcDate());
                lonContractRepayment.setReceivedBpName(hlsCusBpMaster.getBpName());
                if (ctLonBankAccountList.size() > 0) {
                    lonContractRepayment.setReceivedBankAccountName(ctLonBankAccountList.get(0).getBankAccountName());
                    lonContractRepayment.setReceivedBankAccountNum(ctLonBankAccountList.get(0).getBankAccountNum());
                }
                if (lonContractRepayment.getExchangeRate() == null) {
                    lonContractRepayment.setExchangeRate(1D);
                }
                lonContractRepayment.setCreatedBy(iRequest.getUserId());
                lonContractRepayment.setLastUpdatedBy(iRequest.getUserId());
                lonContractRepayment.setLastUpdateLogin(iRequest.getUserId());
                //后续操作
                lonContractRepaymentMapper.insertSelective(lonContractRepayment);
                Double planAmountSum = lonContractRepaymentMapper.selectPlanAmountSum(contractWithdraw.getWithdrawId(), "Y");


                if (planAmountSum > contractWithdraw.getDueAmount()) {
                    message.append("[还款计划总金额超出限制]");
                }
                //融资币种
                if ("CNY".equals(lonContract.getCurrency())) {
                    if (new BigDecimal(1).compareTo(new BigDecimal(lonContractRepayment.getExchangeRate())) != 0) {
                        message.append("[融资币种为人民币,汇率只能是1]");
                    }
                }

                if (org.apache.commons.lang3.StringUtils.isNotBlank(message.toString())) {
                    errorCount++;
                    impDataService.updateErrMessage(dataMap, i, message.toString());
                }

            }

            if (errorCount > 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return dataMap.size() - errorCount;
            }

            lonContractWithdrawService.calXirr(iRequest,contractWithdraw,hlsCusLonContractQuotationTemp);
            //外币汇算
            HlsCusLonContractRepayment hlsCusLonContractRepayment1 = new HlsCusLonContractRepayment();
            hlsCusLonContractRepayment1.setCfItem(301L);
            hlsCusLonContractRepayment1.setWithdrawId(contractWithdraw.getWithdrawId());
            Double pricipalAmountSum = hlsCusLonContractRepaymentService.cfItemAmountSum(iRequest, hlsCusLonContractRepayment1);
            hlsCusLonContractRepayment1.setCfItem(302L);
            Double interestAmountSum = hlsCusLonContractRepaymentService.cfItemAmountSum(iRequest, hlsCusLonContractRepayment1);
            if (!"CNY".equalsIgnoreCase(lonContract.getCurrency())) {
                hlsCusLonContractQuotationTemp.setConvertPayPrincipalAmount(pricipalAmountSum);
                hlsCusLonContractQuotationTemp.setConvertPayInterestAmount(interestAmountSum);
            } else {
                hlsCusLonContractQuotationTemp.setConvertPayPrincipalAmount(null);
                hlsCusLonContractQuotationTemp.setConvertPayInterestAmount(null);
                hlsCusLonContractQuotationTemp.setConvertCurrency(null);
                hlsCusLonContractQuotationTemp.setConvertBeginPrincipalAmount(null);
                hlsCusLonContractQuotationTemp.setExchangeRate(null);
            }
            hlsCusLonContractQuotationService.updateByPrimaryKey(iRequest, hlsCusLonContractQuotationTemp);
        }  catch (Exception e) {
            e.printStackTrace();
            for (Map<String, String> m : dataMap) {
                impDataService.updateErrMessage(m, e.getClass() + ":" + e.getMessage());
            }
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }

        return dataMap.size() - errorCount;
    }

    @Override
    public List<Map> queryRep(IRequest request, HlsCusLonContractRepayment hlsCusLonContractRepayment, int page, int pageSize) {
        return lonContractRepaymentMapper.queryRep(hlsCusLonContractRepayment);
    }

    //sheet名称
    private final static String SHEET_NAME = "sheet1";
    //文件名称
    private final static String FILE_NAME_REPAY = "本息还款计划";
    private final static String FILE_NAME_PAY = "融资费用付款计划";
    private static final List<String> repayColNameList = Lists.newArrayList(
            "期数", "现金流项目", "还款日期", "本币还款金额");
    private static final List<String> repayColGetMethods = Lists.newArrayList("times", "cfItemDesc", "plannedDueDate",
            "cnyPlannedDueAmount");
    private static final List<String> payColNameList = Lists.newArrayList(
            "期数", "现金流项目", "计划付款日期", "本币付款金额");
    private static final List<String> payColGetMethods = Lists.newArrayList("times", "cfItemDesc", "plannedDueDate",
            "cnyPlannedDueAmount");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

/*    @Override
    public void exportRepaymentReport(HttpServletRequest request, HttpServletResponse response, HlsCusLonContractRepayment hlsCusLonContractRepayment) throws IOException, InvocationTargetException, IllegalAccessException {

        XSSFWorkbook xwork = new XSSFWorkbook();
        XSSFSheet sheet = xwork.createSheet(SHEET_NAME);
        List<HlsCusLonContractRepayment> lonContractReps = lonContractRepaymentMapper.selectLonContractRep(hlsCusLonContractRepayment);


        if ("Y".equals(hlsCusLonContractRepayment.getInterestFlag())) {
            int dataRowNum = ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, repayColNameList);
            for (HlsCusLonContractRepayment repayment : lonContractReps) {
                XSSFRow row = sheet.createRow(dataRowNum++);
                setData(xwork, sheet, row, repayment, repayColGetMethods);
            }
            ExportExcelUtil.IOWrite(xwork, null, request, response, FILE_NAME_REPAY);
        } else {
            int dataRowNum = ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, payColNameList);
            for (HlsCusLonContractRepayment repayment : lonContractReps) {
                XSSFRow row = sheet.createRow(dataRowNum++);
                setData(xwork, sheet, row, repayment, payColGetMethods);
            }
            ExportExcelUtil.IOWrite(xwork, null, request, response, FILE_NAME_PAY);
        }

    }*/



    @Override
    public List<HlsCusLonContractRepayment> selectList(IRequest iRequest, HlsCusLonContractRepayment lonContractRepayment,
                                                       Integer page, Integer pageSize) {
        if(page!=null && pageSize!=null){
            PageHelper.startPage(page,pageSize);
        }
        return lonContractRepaymentMapper.select(lonContractRepayment);
    }

    /**
     * 查询 融资提款的还款本金，或者还款还款利息之和
     *
     * @param request
     * @param hlsCusLonContractRepayment
     */
    @Override
    public Double cfItemAmountSum(IRequest request, HlsCusLonContractRepayment hlsCusLonContractRepayment) {
        return lonContractRepaymentMapper.cfItemAmountSum(hlsCusLonContractRepayment);
    }


    /**
     * 把数据设置到Excel行中
     *
     * @param
     * @param
     */
  /*  private void setData(XSSFWorkbook workbook, XSSFSheet sheet, XSSFRow row, HlsCusLonContractRepayment repayment, List<String> colGetMethods) throws IOException, InvocationTargetException, IllegalAccessException {
        DecimalFormat df = new DecimalFormat("###,##0.00");
        for (int i = 0; i < colGetMethods.size(); i++) {
            XSSFCell cell = row.createCell(i);
            Object value = ExportExcelUtil.getValue(repayment, colGetMethods.get(i));
            if (value instanceof Date) {
                value = simpleDateFormat.format(value);
            }
            if (value instanceof Double && !"exchangeRate".equals(colGetMethods.get(i))) {
                XSSFCellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                value = df.format(value);
                cell.setCellStyle(cellStyle);
            }
            if (Objects.nonNull(value)) {
                ExportExcelUtil.initColWidth(sheet, i, value.toString());
                cell.setCellValue(value.toString());
            }

        }
    }



    @Override
    public List<HlsCusLonContractRepayment> selectLonContractChangeAfterRep(IRequest request, HlsCusLonContractRepayment lonContractRepayment, int page, int pageSize, String model) {
        //model的设置，Rep表示本息还款计划，Fin表示融资服务费还款计划
        final String Rep = "Rep";
        final String Fin = "Fin";

        //构造参数
        if (!"".equals(lonContractRepayment.getRepaymentIds()) && lonContractRepayment.getRepaymentIds() != null) {
            lonContractRepayment.setRepaymentIdList(lonContractRepayment.getRepaymentIds().split("-"));
        }
        PageHelper.startPage(page, pageSize);
        List<HlsCusLonContractRepayment> list = null;
        if (model.equals(Rep)) {
            list = lonContractRepaymentMapper.selectLonContractChangeAfterRep(lonContractRepayment);
        } else {
            list = lonContractRepaymentMapper.selectLonContractFin(lonContractRepayment);
        }

        //获取已申请金额
        for (HlsCusLonContractRepayment item : list) {
            Double approvedAmount = 0D;
            HlsCusCshPaymentReqLn reqLn = new HlsCusCshPaymentReqLn();
            reqLn.setCompanyId(item.getCompanyId());
            reqLn.setSource_doc_category("LON_CONTRACT_WITHDRAW");
            reqLn.setSource_doc_id(item.getWithdrawId());
            reqLn.setSource_doc_line_id(item.getRepaymentId());
            //获取当前还款id的所有付款申请行信息
            List<HlsCusCshPaymentReqLn> reqList = cshPaymentReqLnMapper.select(reqLn);
            if (reqList.size() > 0) {
                for (HlsCusCshPaymentReqLn req : reqList) {
                    //判断对应的头上的申请状态
                    HlsCusCshPaymentReqHd hd = new HlsCusCshPaymentReqHd();
                    hd.setPayment_req_id(req.getPayment_req_id());
                    String paymentStatus = "";
                    if (cshPaymentReqHdMapper.selectOne(hd) != null) {
                        paymentStatus = cshPaymentReqHdMapper.selectOne(hd).getPayment_req_status();
                    }

                    //如果申请状态不是被拒绝 计算申请总额
                    if (!"REJECTED".equals(paymentStatus)) {
                        approvedAmount = CalculateUtil.add(approvedAmount, req.getAmount());
                    }
                    //如果获取了参数 申请头ID 判断当前申请行是否属于当前申请头  如果属于 获取本次申请金额和申请行ID(明细页面)
                    if (lonContractRepayment.getPaymentReqId() != null && lonContractRepayment.getPaymentReqId().compareTo(0L) != 0 && req.getPayment_req_id().compareTo(lonContractRepayment.getPaymentReqId()) == 0) {
                        item.setApplyAmount(req.getAmount());
                        item.setPaymentReqId(lonContractRepayment.getPaymentReqId());
                        item.setPaymentReqLineId(req.getPayment_req_ln_id());
                        item.setPaymentStatus(paymentStatus);

                    }
                }
            } else {
                item.setPaymentStatus("NEW");
            }
            //获取当前还款行的已申请金额
            item.setApprovedAmount(approvedAmount);
            //如果本次申请金额为null 初始化本次申请金额为 实际还款金额-已申请金额
            if (item.getApplyAmount() == null) {
                item.setApplyAmount(CalculateUtil.sub(item.getDueAmount(), item.getApprovedAmount()));
            }
        }
        return list;
    }*/


    @Override
    public void confirmFundRepayment(IRequest request,  HlsCusContractRepaymentLn repaymentLn) throws HlsCusException {

        //提款
        HlsCusLonContractWithdraw lonContractWithdraw = lonContractWithdrawMapper.selectByPrimaryKey(repaymentLn.getWithdrawId());

        //合同
        HlsCusLonContract hlsCusLonContract = lonContractMapper.selectByPrimaryKey(lonContractWithdraw.getContractId());

        //是否最后一期利息标识
        Boolean lastIntersetFlag=false;

        Long maxTimes = lonContractRepaymentMapper.selectInterestMaxTimes(lonContractWithdraw.getWithdrawId());

        //提款计划
        HlsCusLonContractRepayment lonContractRepayment=(HlsCusLonContractRepayment)lonContractRepaymentMapper.selectByPrimaryKey(repaymentLn.getRepaymentId());

        hlsCusContractRepaymentLnService.insertSelective(request, repaymentLn);

        //插入凭证事物流水表
        AbstractJeTrxService repaymentService = JeTrxCommonService.map.get("CSH_TRANSACTION_LOAN_RE");
        Map params = new HashMap<>();
        params.put("jeTrxId", repaymentLn.getLnId());
        params.put("companyId", hlsCusLonContract.getCompanyId());
        params.put("contractId", repaymentLn.getRepaymentId());
        params.put("sourceDoc", "LON_CONTRACT_REPAYMENT");
        repaymentService.process(request, params);

        HlsCusContractRepaymentLn repaymentLnAmountSum = hlsCusContractRepaymentLnService.selectRepaymentLnAmountSum(repaymentLn.getRepaymentId());

        lonContractRepayment.setDueAmount(repaymentLnAmountSum.getDueAmount());
        lonContractRepayment.setCnyDueAmount(repaymentLnAmountSum.getCnyDueAmount());
        if (Double.compare(lonContractRepayment.getDueAmount(), lonContractRepayment.getPlannedDueAmount()) < 0) {
            lonContractRepayment.setConfirmFlag("Y");
            lonContractRepayment.setWriteOffFlag("PARTIAL");
        } else if (Double.compare(lonContractRepayment.getDueAmount(),lonContractRepayment.getPlannedDueAmount()) == 0) {
            lonContractRepayment.setConfirmFlag("Y");
            lonContractRepayment.setWriteOffFlag("FULL");
        } else {
            throw new HlsCusException("还款金额不能大于计划还款金额");
        }
        lonContractRepayment.set__status("update");
        lonContractRepayment.setWriteOffAmount(lonContractRepayment.getDueAmount());
        lonContractRepayment.setDueDate(repaymentLn.getDueDate());
        self().updateByPrimaryKeySelective(request, lonContractRepayment);


        //判断是否是最后一期利息
        if(maxTimes!=null&&new Long(302L).equals(lonContractRepayment.getCfItem())&&maxTimes.equals(lonContractRepayment.getTimes())&&"FULL".equals(lonContractRepayment.getWriteOffFlag())){
            lastIntersetFlag=true;
        }


        /**
         * 更新合同是否结清状态
         */
        hlsCusLonContractService.updateContractSettleStatus();

        //更新租赁合同质押状态
        lonContractPledgeService.updateConContractPledgeFlag();

        //更新保理合同质押状态
        lonContractPledgeService.updateFctContractPledgeFlag();


        //如果是最后一期还款利息 更新利息分摊
        if (lastIntersetFlag && "CNY".equals(hlsCusLonContract.getCurrency())) {
            Long withdrawId = lonContractWithdraw.getWithdrawId();
            //利息总和
            Double interestplannedDueAmount = lonContractRepaymentMapper.selectInterestplannedDueAmount(withdrawId);

            //分摊最后一个的id
            Long financeCostId = gldLonContractFinCostMapper.selectWithdrawLastTimeId(withdrawId);

            Double fincomeInCludAmount = gldLonContractFinCostMapper.selectFincomeInCludSumExcept(withdrawId, financeCostId);

            Double fincomeInClud = (new BigDecimal(interestplannedDueAmount.toString()).subtract(new BigDecimal(fincomeInCludAmount.toString()))).doubleValue();

            if (fincomeInClud.compareTo(new Double(0)) == 1) {
                //更新分摊金额
                gldLonContractFinCostMapper.updateFinCostIncome(financeCostId, fincomeInClud);

            } else {
                throw new HlsCusException("问题数据！分摊金额小于0");
            }
        }
    }

    @Override
    public List<HlsCusLonContractRepayment> selectLonContractRepAndFin(HlsCusLonContractRepayment hlsCusLonContractRepayment) {
        return lonContractRepaymentMapper.selectLonContractRepAndFin(hlsCusLonContractRepayment);
    }
}