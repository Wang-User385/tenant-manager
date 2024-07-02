package com.hand.hls.fin.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.fct.dto.HlsCusHlsCreditLine;
import com.hand.hls.fct.service.HlsCreditLineService;
import com.hand.hls.fin.dto.HlsCusCreditContract;
import com.hand.hls.fin.dto.HlsCusCreditContractLine;
import com.hand.hls.fin.dto.HlsCusLonCreditContractAttachment;
import com.hand.hls.fin.exception.AmoutOverdueException;
import com.hand.hls.fin.mapper.HlsCusCreditContractLineMapper;
import com.hand.hls.fin.mapper.HlsCusCreditContractMapper;
import com.hand.hls.fin.mapper.HlsCusLonContractWithdrawMapper;
import com.hand.hls.fin.mapper.HlsCusLonCreditContractAttachmentMapper;
import com.hand.hls.fin.service.HlsCusCreditContractLineService;
import com.hand.hls.fin.service.HlsCusICreditContractService;
import com.hand.hls.fin.service.HlsCusLonContractService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusCreditContractServiceImpl extends BaseServiceImpl<HlsCusCreditContract> implements HlsCusICreditContractService {

    @Autowired
    private HlsCusCreditContractMapper creditContractMapper;
    @Autowired
    FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    private HlsCusLonContractService hlsCusLonContractService;
    @Autowired
    private HlsCusCreditContractLineService hlsCusCreditContractLineService;

    @Autowired
    private HlsCreditLineService hlsCreditLineService;

    @Autowired
    private HlsCusLonContractWithdrawMapper lonContractWithdrawMapper;

    @Autowired
    private HlsCusCreditContractLineMapper hlsCusCreditContractLineMapper;

    @Autowired
    private HlsCusLonCreditContractAttachmentMapper hlsCusLonCreditContractAttachmentMapper;


    @Override
    public HlsCusCreditContract save(IRequest iRequest, HlsCusCreditContract creditContract) throws AmoutOverdueException {
        creditContract.setBusinessType("LON_CREDIT_CONTRACT");
        creditContract.setDocumentType("LON_CREDIT");
        creditContract.setDocumentCategory("LON_CREDIT_CONTRACT");
        creditContract.setCreditContractStatus("NEW");
        creditContract.setProposerUserId(iRequest.getUserId());
        creditContract.setCompanyId(iRequest.getCompanyId());
        creditContract.setProposerEmployeeAssignId(Long.parseLong(iRequest.getAttribute("employeeAssignId").toString()));
        creditContract.setProposerEmployeeId(Long.parseLong(iRequest.getAttribute("employeeId").toString()));
        creditContract.setDataClass("NORMAL");
        creditContract.setConfirmStatus("CONFIRMED");
        creditContract.setCancelFlag("N");
        creditContract.setIsMix("N");
        if (StringUtils.isBlank(creditContract.getCreditConNumber())) {
            Map<String, String> params = new HashMap<>();
            String value = codingRuleValuesService.getCodeRuleValue(iRequest, "LON_CREDIT_CONTRACT", creditContract.getDocumentType(), creditContract.getBusinessType(), params);
            creditContract.setCreditConNumber(value);
        }
        if (creditContract.getCreditContractId() != null) {
            //防止在修改过程中新增存在提款
            if (creditContract.getWithdrawCount() == null || creditContract.getWithdrawCount().equals(0L)) {
                int count = hlsCusCreditContractLineMapper.selectCreditWithdrawCount(creditContract.getCreditContractId(), null, "N");
                if (count > 0) {
                    HlsCusCreditContract creditContractBefore = self().selectByPrimaryKey(iRequest, creditContract);
                    creditContract.setCreditBpId(creditContractBefore.getCreditBpId());
                    creditContract.setCurrency(creditContractBefore.getCurrency());
                }
            }
            creditContract = self().updateByPrimaryKey(iRequest, creditContract);
        } else {
            creditContract.setCreditExposureAmt(0D);
            creditContract = self().insertSelective(iRequest, creditContract);
        }

        if (creditContract.getCreditContractLineList() != null) {
            for (HlsCusCreditContractLine cusCreditContractLine : creditContract.getCreditContractLineList()) {
                if (cusCreditContractLine.getCreditLineAmt().compareTo(creditContract.getCreditLineAmt()) > 0) {
                    throw new AmoutOverdueException("分项额度超出总授信额度");
                }
                Date validFrom = cusCreditContractLine.getValidFrom();
                Date validTo = cusCreditContractLine.getValidTo();
                Long l = (validTo.getTime() - validFrom.getTime()) / (3600000 * 24) * 12 / 365;
                cusCreditContractLine.setCreditTerm(Double.parseDouble(l.toString()));
                Long withdrawCount = cusCreditContractLine.getWithdrawCount();
                HlsCusHlsCreditLine hlsCreditLine = getHlsCusHlsCreditLine(creditContract);
                hlsCreditLine.setCreditExposureAmt(cusCreditContractLine.getCreditExposureAmt());
                hlsCreditLine.setCreditLineAmt(cusCreditContractLine.getCreditLineAmt());
                hlsCreditLine.setCreditLineId(cusCreditContractLine.getCreditLineId());
                hlsCreditLine.setValidFrom(cusCreditContractLine.getValidFrom());
                hlsCreditLine.setValidTo(cusCreditContractLine.getValidTo());
                hlsCreditLine.setDocumentType(cusCreditContractLine.getDocumentType());
                hlsCreditLine.setWithdrawBalance(cusCreditContractLine.getWithdrawBalance());
                hlsCreditLine.setSourceCreditLineId(cusCreditContractLine.getSourceCreditLineId());
                hlsCreditLine.setCancelFlag(cusCreditContractLine.getCancelFlag());
                hlsCreditLine.setSourceCreditFlag(cusCreditContractLine.getSourceCreditFlag());
                HlsCusHlsCreditLine hlsCreditLineBefore = null;
                if (hlsCreditLine.getCreditLineId() != null) {
                    hlsCreditLineBefore = hlsCreditLineService.selectByPrimaryKey(iRequest, hlsCreditLine);
                    if (withdrawCount == null || withdrawCount.equals(new Long(0L))) {
                        //防止界面长久不刷新 导致授信实际已经存在提款
                        int count = hlsCusCreditContractLineMapper.selectCreditWithdrawCount(null, hlsCreditLine.getCreditLineId(), "Y");
                        if (count > 0) {
                            withdrawCount = 1L;
                            hlsCreditLine.setDocumentType(hlsCreditLineBefore.getDocumentType());
                        }
                    }
                    //刚引用过来进行保存的数据
                    if ("Y".equals(hlsCreditLine.getSourceCreditFlag())) {
                        if (hlsCreditLineBefore.getValidFrom().equals(hlsCreditLine.getValidFrom())) {
                            throw new AmoutOverdueException("引用数据请务必修改授信起始日");
                        }
                        hlsCreditLineBefore.setCancelFlag("Y");
                        hlsCreditLineBefore.setCreationDate(null);
                        hlsCreditLineBefore.setLastUpdateDate(null);
                        if (hlsCreditLineBefore.getSourceCreditLineId() == null) {
                            hlsCreditLineBefore.setSourceCreditLineId(hlsCreditLine.getCreditLineId());
                        } else {
                            //两次及以上引用时
                            hlsCreditLineBefore.setSourceCreditLineId(hlsCreditLine.getSourceCreditLineId());
                        }
                        hlsCreditLineService.insertSelective(iRequest, hlsCreditLineBefore);
                        hlsCreditLine.setSourceCreditLineId(hlsCreditLineBefore.getCreditLineId());
                    }
                    hlsCreditLine = hlsCreditLineService.updateByPrimaryKey(iRequest, hlsCreditLine);
                } else {
                    hlsCreditLine.setCreditExposureAmt(0D);
                    hlsCreditLine = hlsCreditLineService.insertSelective(iRequest, hlsCreditLine);
                }

                //新增或修改 额度明细表
                cusCreditContractLine.setCreditLineId(hlsCreditLine.getCreditLineId());
                cusCreditContractLine.setCreditContractId(creditContract.getCreditContractId());
                if (cusCreditContractLine.getCreditContractLineId() != null) {
                    HlsCusCreditContractLine creditContractLineBefore = hlsCusCreditContractLineService.selectByPrimaryKey(iRequest, cusCreditContractLine);
                    if (withdrawCount != null && !withdrawCount.equals(0L)) {
                        cusCreditContractLine.setCreditCategory(creditContractLineBefore.getCreditCategory());
                    }
                    if ("Y".equals(cusCreditContractLine.getSourceCreditFlag())) {
                        creditContractLineBefore.setCreationDate(new Date());
                        creditContractLineBefore.setLastUpdateDate(new Date());
                        creditContractLineBefore.setCreditLineId(hlsCreditLineBefore.getCreditLineId());
                        hlsCusCreditContractLineService.insertSelective(iRequest, creditContractLineBefore);

                        //更新合同sourceCreditLineId
                        hlsCusLonContractService.updateLonContractSourceCreditLineId(creditContractLineBefore.getCreditContractId(), hlsCreditLine.getCreditLineId(), hlsCreditLineBefore.getCreditLineId());
                    }
                    hlsCusCreditContractLineService.updateByPrimaryKey(iRequest, cusCreditContractLine);
                } else {
                    hlsCusCreditContractLineService.insertSelective(iRequest, cusCreditContractLine);
                }

                Double creditAmt = lonContractWithdrawMapper.selectCreditDueAmount(cusCreditContractLine.getCreditLineId());
                if (creditAmt == null) {
                    creditAmt = 0D;
                }
                if (new BigDecimal(creditAmt).compareTo(new BigDecimal(0)) == -1) {
                    Double amt = hlsCusCreditContractLineMapper.selectForecastCreditAmt(null, cusCreditContractLine.getCreditLineId());
                    DecimalFormat df = new DecimalFormat("###,##0.00");
                    throw new AmoutOverdueException("分项授信金额不可小于对应的提款金额,其中预提款金额:" + df.format(amt) + "(元)");
                }
            }

        }

        Double creditLineAmtSum = hlsCusCreditContractLineMapper.selectRetainCreditAmt(creditContract.getCreditContractId(), null);
        if (creditLineAmtSum == null) {
            creditLineAmtSum = 0D;
        }
        if (new BigDecimal(creditLineAmtSum).compareTo(new BigDecimal(0)) < 0) {
            Double amt = hlsCusCreditContractLineMapper.selectForecastCreditAmt(creditContract.getCreditContractId(), null);
            DecimalFormat df = new DecimalFormat("###,##0.00");
            throw new AmoutOverdueException("授信总额度不能小于关联的提款总金额,其中预提款金额:" + df.format(amt) + "(元)");
        }


    return creditContract;
    }

    @Override
    public List<HlsCusCreditContract> unitSelect(HlsCusCreditContract creditContract, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return creditContractMapper.unitSelect(this.unitSelectDeal(creditContract));
    }


    private HlsCusCreditContract unitSelectDeal(HlsCusCreditContract creditContract) {
        if (creditContract.getLonTypes() != null) {
            String[] lonTypeStatus = creditContract.getLonTypes().split(",");
            creditContract.setLonTypeStatus(lonTypeStatus);
        }
        if (creditContract.getCreditTypes() != null) {
            String[] creditTypeStatus = creditContract.getCreditTypes().split(",");
            creditContract.setCreditTypeStatus(creditTypeStatus);
        }
        if (creditContract.getCreditCategorys() != null) {
            String[] creditCategoryStatus = creditContract.getCreditCategorys().split(",");
            creditContract.setCreditCategoryStatus(creditCategoryStatus);
        }
        if (creditContract.getCreditLineAmts() != null) {
            String[] creditLineStatus = creditContract.getCreditLineAmts().split(",");
            Double[] creditLineAmtStatus = new Double[creditLineStatus.length];
            for (int i = 0; i < creditLineStatus.length; i++) {
                Double amt = Double.parseDouble(creditLineStatus[i]);
                creditLineAmtStatus[i] = amt;
            }
            creditContract.setCreditLineAmtStatus(creditLineAmtStatus);
        }
        if (creditContract.getMinAmt() == null && creditContract.getMaxAmt() == null) {
            if (creditContract.getCreditLineAmts() != null) {
                String[] financeAmountInfo = creditContract.getCreditLineAmts().split(",");

                if (financeAmountInfo.length == 1) {
                    if ("1000".equals(financeAmountInfo[0])) {
                        creditContract.setMaxAmt(100000000d);
                    } else if ("5000".equals(financeAmountInfo[0])) {
                        creditContract.setMinAmt(100000000d);
                        creditContract.setMaxAmt(500000000d);
                    } else {
                        creditContract.setMinAmt(500000000d);
                        creditContract.setMaxAmt(1000000000d);
                    }
                } else if (financeAmountInfo.length == 2) {
                    if ("1000".equals(financeAmountInfo[0]) && "5000".equals(financeAmountInfo[1])) {
                        creditContract.setMaxAmt(500000000d);
                    } else if ("5000".equals(financeAmountInfo[0]) && "10000".equals(financeAmountInfo[1])) {
                        creditContract.setMinAmt(100000000d);
                        creditContract.setMaxAmt(1000000000d);
                    } else {
                        creditContract.setMinAmt(500000000d);
                        creditContract.setMaxAmt(1000000000d);
                        creditContract.setAmMax(100000000d);
                    }
                } else {
                    creditContract.setMaxAmt(1000000000d);

                }
            }
        }
        return creditContract;
    }

    private HlsCusHlsCreditLine  getHlsCusHlsCreditLine(HlsCusCreditContract creditContract){
        HlsCusHlsCreditLine hlsCusHlsCreditLine = new HlsCusHlsCreditLine();
        hlsCusHlsCreditLine.setCreditLineName(creditContract.getCreditContractName());
        hlsCusHlsCreditLine.setCreditLineAmt(Double.valueOf(creditContract.getCreditLineAmt()));
        hlsCusHlsCreditLine.setValidFrom(creditContract.getValidFrom());
        hlsCusHlsCreditLine.setValidTo(creditContract.getValidTo());
        hlsCusHlsCreditLine.setBpId(creditContract.getCreditBpId());
        hlsCusHlsCreditLine.setDocumentCategory("HLS_CREDIT_LINE");
        hlsCusHlsCreditLine.setBusinessType("FINANCING_LOAN");
        hlsCusHlsCreditLine.setCreditLineStatus(creditContract.getCreditContractStatus());
        hlsCusHlsCreditLine.setCompanyId(creditContract.getCompanyId());
        hlsCusHlsCreditLine.setProposerUserId(creditContract.getProposerUserId());
        hlsCusHlsCreditLine.setProposerEmployeeId(creditContract.getProposerEmployeeId());
        hlsCusHlsCreditLine.setProposerEmployeeAssignId(creditContract.getProposerEmployeeAssignId());
        hlsCusHlsCreditLine.setCurrencyCode(String.valueOf(creditContract.getCurrency()));
        hlsCusHlsCreditLine.setDelayFrom(creditContract.getDelayFrom());
        hlsCusHlsCreditLine.setDelayTo(creditContract.getDelayTo());
        hlsCusHlsCreditLine.setCompetentDept(creditContract.getCompetentDept());
        hlsCusHlsCreditLine.setFinanceType(creditContract.getFinanceType());
        hlsCusHlsCreditLine.setFirstWithdrawDate(creditContract.getFirstWithdrawDate());
        return  hlsCusHlsCreditLine;
    }

    @Override
    public int updateCreditContractExposureAmt(Long creditContractId ,Long creditLineId) {
        return creditContractMapper.updateCreditContractExposureAmt(creditContractId,creditLineId);
    }

    @Override
    public int updateCreditContractUnExposureAmt(Long creditContractId,Long creditLineId) {
        return creditContractMapper.updateCreditContractUnExposureAmt(creditContractId,creditLineId);
    }
}
