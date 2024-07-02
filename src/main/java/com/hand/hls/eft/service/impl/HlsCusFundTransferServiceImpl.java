package com.hand.hls.eft.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.eft.dto.HlsCusFundTransfer;
import com.hand.hls.eft.dto.HlsCusFundTransferList;
import com.hand.hls.eft.mapper.HlsCusFundTransferMapper;
import com.hand.hls.eft.service.HlsCusFundTransferListService;
import com.hand.hls.eft.service.HlsCusFundTransferService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.wfl.service.IActivitiStartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusFundTransferServiceImpl extends BaseServiceImpl<HlsCusFundTransfer> implements HlsCusFundTransferService {

    @Autowired
    private HlsCusFundTransferMapper fundTransferMapper;

    @Autowired
    private HlsCusFundTransferListService fundTransferListService;

    @Autowired
    private FndCodingRuleValuesService codingRuleValuesService;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private HlsEmployeeMapper employeeMapper;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Override
    public List<HlsCusFundTransfer> selectFundTransferData(IRequest iRequest, HlsCusFundTransfer fundTransfer, int page, int pageSie) {
        PageHelper.startPage(page,pageSie);
        return fundTransferMapper.selectFundTransferData(fundTransfer);
    }

    @Override
    public List<HlsCusFundTransfer> selectFundTransferChangeData(IRequest iRequest, HlsCusFundTransfer fundTransfer, int page, int pageSie) {
        PageHelper.startPage(page,pageSie);
        return fundTransferMapper.selectFundTransferChangeData(fundTransfer);
    }

    @Override
    public HlsCusFundTransfer updateFundTransferData(IRequest iRequest, HlsCusFundTransfer fundTransfer) throws HlsCusException {

        if(fundTransfer.getTransferLists()!=null){
            for(HlsCusFundTransferList transferList:fundTransfer.getTransferLists()){
                if(transferList.getTransferListId()!=null) {
                    if(HlsCusConstant.TRANSFER_BUSINESS_TYPE.FINANCE_TYPE.equals(fundTransfer.getBusinessType())){
                        transferList.setFinTransferId(fundTransfer.getTransferId());
                    }
                    fundTransferListService.updateByPrimaryKey(iRequest, transferList);
                }else{
                    transferList.setDataClass(HlsCusConstant.DATA_CLASS.NORMAL);
                    transferList.setTransferStatus(HlsCusConstant.WORKFLOW_STATUS.NEW);
                    transferList.setIsOnceWriteOff(HlsCusConstant.FLAG.Y);
                    transferList.setTransferId(fundTransfer.getTransferId());
                    transferList.setTransferNumber(fundTransfer.getApplyNumber()+fundTransferListService.selectTransferNumberMax(fundTransfer.getTransferId()));
                    fundTransferListService.insertSelective(iRequest, transferList);
                }
            }
        }

        //校验实际支付金额 资金确认岗位
        if(HlsCusConstant.FLAG.Y.equals(fundTransfer.getFinanceFlag())) {
            HlsCusFundTransferList fundTransferList=new HlsCusFundTransferList();
            fundTransferList.setFinTransferId(fundTransfer.getTransferId());
            List<HlsCusFundTransferList> transferLists = fundTransferListService.selectFundTransferList(iRequest, fundTransferList, 1, 999);
            for(HlsCusFundTransferList transferList:transferLists) {
                if (HlsCusConstant.TRANSFER_BUSINESS_TYPE.FINANCE_TYPE.equals(fundTransfer.getBusinessType()) && HlsCusConstant.TRANSFER_TYPE.PAY.equals(transferList.getTransferType())) {
                    BigDecimal paySurplusAmount = fundTransferListService.selectActualPaySurplusAmount(iRequest, transferList.getSourceDocCategory(), transferList.getSourceDocLineId());
                    if (paySurplusAmount == null) {
                        throw new HlsCusException("若由于融资发生变更,请重新选择提款/产品编号！否则请联系管理员!");
                    } else {
                        if (BigDecimal.ZERO.compareTo(paySurplusAmount) == 1) {
                            throw new HlsCusException("实际支付金额超出单据可支付金额,请检查!");
                        }
                    }
                }
            }
        }
        return fundTransfer;
    }


    @Override
    public HlsCusFundTransfer createFundTransferGap(IRequest iRequest, HlsCusFundTransfer fundTransfer) {
        fundTransfer = new HlsCusFundTransfer();
        String value = codingRuleValuesService.getCodeRuleValue(iRequest, HlsCusConstant.TRANSFER_BUSINESS_TYPE.FUND_TRANSFER, HlsCusConstant.TRANSFER_BUSINESS_TYPE.FUND_TRANSFER,HlsCusConstant.TRANSFER_BUSINESS_TYPE.FUND_TRANSFER, new HashMap<>());
        fundTransfer.setApplyNumber(value);
        fundTransfer.setApplyDate(new Date());
        fundTransfer.setApplyStatus(HlsCusConstant.WORKFLOW_STATUS.NEW);
        fundTransfer.setCompanyId(iRequest.getCompanyId());
        fundTransfer.setUnitId(Long.parseLong(iRequest.getAttribute(HlsCusFundTransfer.FIELD_UNIT_ID)));
        fundTransfer.setDataClass(HlsCusConstant.DATA_CLASS.NORMAL);
        fundTransfer.setBusinessType(HlsCusConstant.TRANSFER_BUSINESS_TYPE.GAP_TYPE);
        fundTransfer.setCreatedBy(iRequest.getUserId());
        fundTransfer.setLastUpdatedBy(iRequest.getUserId());
        fundTransferMapper.insertSelective(fundTransfer);
        return fundTransfer;
    }

    @Override
    public List<HlsCusFundTransfer> submitApprovalFundTransfer(IRequest iRequest, HlsCusFundTransfer fundTransfer)  throws HlsCusException {
       //保存数据
        fundTransfer = selectByPrimaryKey(iRequest, fundTransfer);
        List<HlsCusFundTransfer> submitTransfer = new ArrayList<>(1);
        submitTransfer.add(fundTransfer);
        databaseLockProvider.lock(submitTransfer.get(0));
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        if(ObjectUtils.isEmpty(employee)){
            throw new HlsCusException("获取提交人失败");
        }
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);
        Map<String, Object> params = new HashMap<>(1);
        if (HlsCusConstant.TRANSFER_BUSINESS_TYPE.FUND_TYPE.equalsIgnoreCase(fundTransfer.getBusinessType())) {
            if(HlsCusConstant.DATA_CLASS.NORMAL.equals(fundTransfer.getDataClass())) {
                params.put(HlsCusConstant.WORKFLOW_PARAMS.WORKFLOW_TYPE, HlsCusConstant.TRANSFER_WFL.FUND_TRANSFER_WFL);
            }else{
                params.put(HlsCusConstant.WORKFLOW_PARAMS.WORKFLOW_TYPE, HlsCusConstant.TRANSFER_WFL.FUND_TRANSFER_CHANGE_WFL);
            }
        } else if(HlsCusConstant.TRANSFER_BUSINESS_TYPE.FINANCE_TYPE.equalsIgnoreCase(fundTransfer.getBusinessType())){
            params.put(HlsCusConstant.WORKFLOW_PARAMS.WORKFLOW_TYPE, HlsCusConstant.TRANSFER_WFL.FINANCE_TRANSFER_WFL);
        } else{
            params.put(HlsCusConstant.WORKFLOW_PARAMS.WORKFLOW_TYPE, HlsCusConstant.TRANSFER_WFL.FUND_TRANSFER_WFL);
        }
        activitiStartService.start(iRequest, submitTransfer, params);
        //修改状态
        fundTransfer.setApplyStatus(HlsCusConstant.WORKFLOW_STATUS.APPROVING);
        fundTransferMapper.updateByPrimaryKeySelective(fundTransfer);
        return submitTransfer;
    }


    @Override
    public HlsCusFundTransfer selectFundTransferNewChange(Long refTransferId) {
        return fundTransferMapper.selectFundTransferNewChange(refTransferId);
    }
}