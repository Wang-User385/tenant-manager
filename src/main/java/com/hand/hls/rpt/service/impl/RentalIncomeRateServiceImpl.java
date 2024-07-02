package com.hand.hls.rpt.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.dto.HlsCashflowItem;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.fnd.mapper.HlsCfItemMapper;
import com.hand.hls.rpt.dto.RentalIncomeRate;
import com.hand.hls.rpt.mapper.RentalIncomeRateMapper;
import com.hand.hls.rpt.service.IRentalIncomeRateService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor={Exception.class})
public class RentalIncomeRateServiceImpl
        extends BaseServiceImpl<RentalIncomeRate>
        implements IRentalIncomeRateService
{
    @Autowired
    private RentalIncomeRateMapper mapper;
    @Autowired
    FndInterfaceLinesMapper fndInterfaceLinesMapper;
    @Autowired
    private HlsCusBpMasterMapper bpMasterMapper;
    @Autowired
    private HlsCfItemMapper hlsCfItemMapper;
    @Autowired
    private IRentalIncomeRateService IRentalIncomeRateService;

    public List<RentalIncomeRate> queryRentalRateInfo(IRequest request, RentalIncomeRate RentalIncomeRate, int page, int pageSize)
    {
        PageHelper.startPage(page, pageSize);
        return this.mapper.queryRentalRateInfo(RentalIncomeRate);
    }

    public List<RentalIncomeRate> queryRentalIncomeRate(IRequest request, RentalIncomeRate RentalIncomeRate, int page, int pageSize)
    {
        PageHelper.startPage(page, pageSize);
        return this.mapper.queryRentalIncomeRate(RentalIncomeRate);
    }

    public void IRentalIncomeRateExcelImport(IRequest iRequest, Long hdId)
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        FndInterfaceLines FndInterfaceLines = new FndInterfaceLines();
        FndInterfaceLines.setHeaderId(hdId);
        List<FndInterfaceLines> fndInterfaceLines = this.fndInterfaceLinesMapper.fndInterfaceLinesQuery(FndInterfaceLines);

        RentalIncomeRate RentalIncomeRateParam = new RentalIncomeRate();
        for (FndInterfaceLines fndInterfaceLine : fndInterfaceLines)
        {
            RentalIncomeRate RentalIncomeRate = new RentalIncomeRate();
            String organizationName = fndInterfaceLine.getAttributes_2();
            String bpName = fndInterfaceLine.getAttributes_3();
            String contractNumber = fndInterfaceLine.getAttributes_4();
            String dueDate = fndInterfaceLine.getAttributes_5();
            String times = fndInterfaceLine.getAttributes_6();
            String cashflowItemDesc = fndInterfaceLine.getAttributes_7();
            String lastReceivedDate = fndInterfaceLine.getAttributes_8();
            String currencyName = fndInterfaceLine.getAttributes_9();
            String intRate = fndInterfaceLine.getAttributes_10();
            String dueAmount = fndInterfaceLine.getAttributes_11();
            String receivedAmount = fndInterfaceLine.getAttributes_12();
            String overdueAmount = fndInterfaceLine.getAttributes_13();
            String businessUnit = fndInterfaceLine.getAttributes_14();
            String planJudge = fndInterfaceLine.getAttributes_15();

            HlsCashflowItem hlsCashflowItem = new HlsCashflowItem();
            hlsCashflowItem.setDescription(cashflowItemDesc.toString());
            List<HlsCashflowItem> hlsCashflowItemList = this.hlsCfItemMapper.select(hlsCashflowItem);

            String CfItem = null;
            if (hlsCashflowItemList.size() > 0) {
                CfItem = ((HlsCashflowItem)hlsCashflowItemList.get(0)).getCfItem();
            }
            try
            {
                RentalIncomeRateParam.setDueDateFrom(df.parse(dueDate));
                RentalIncomeRateParam.setDueDateTo(df.parse(dueDate));
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            RentalIncomeRateParam.setContractNumber(contractNumber);
            RentalIncomeRateParam.setBpName(bpName);
            RentalIncomeRateParam.setTimes(Long.valueOf(Long.parseLong(times)));
            RentalIncomeRateParam.setCfItem(Long.valueOf(Long.parseLong(CfItem)));

            List<RentalIncomeRate> RentalIncomeRateDetail = this.mapper.queryRentalRateInfo(RentalIncomeRateParam);
            if (RentalIncomeRateDetail.size() > 0)
            {
                RentalIncomeRate.setSourcesFlag("BOTH");
                RentalIncomeRate.setCashflowId(((RentalIncomeRate)RentalIncomeRateDetail.get(0)).getCashflowId());
                RentalIncomeRate.setCashflowId(((RentalIncomeRate)RentalIncomeRateDetail.get(0)).getContractId());
                RentalIncomeRate.setCfItem(((RentalIncomeRate)RentalIncomeRateDetail.get(0)).getCfItem());
            }
            else
            {
                RentalIncomeRate.setSourcesFlag("IMPORT");
            }
            HlsCusBpMaster bpMasterParam = new HlsCusBpMaster();
            bpMasterParam.setBpName(bpName);
            List<HlsCusBpMaster> bpMasters = this.bpMasterMapper.select(bpMasterParam);
            if (bpMasters.size() > 0) {
                RentalIncomeRate.setBpId(((HlsCusBpMaster)bpMasters.get(0)).getBpId());
            }
            RentalIncomeRate.setOrganizationName(organizationName);
            RentalIncomeRate.setBpName(bpName);
            try
            {
                RentalIncomeRate.setDueDate(df.parse(dueDate));
                RentalIncomeRate.setLastReceivedDate(df.parse(lastReceivedDate));
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            RentalIncomeRate.setCfItem(Long.valueOf(Long.parseLong(CfItem)));
            RentalIncomeRate.setTimes(Long.valueOf(Long.parseLong(times)));
            RentalIncomeRate.setCurrencyName(currencyName);
            RentalIncomeRate.setIntRate(Double.valueOf(Double.parseDouble(intRate)));
            RentalIncomeRate.setDueAmount(Double.valueOf(Double.parseDouble(dueAmount)));
            RentalIncomeRate.setReceivedAmount(Double.valueOf(Double.parseDouble(receivedAmount)));
            RentalIncomeRate.setOverdueAmount(Double.valueOf(Double.parseDouble(overdueAmount)));
            RentalIncomeRate.setBusinessUnit(businessUnit);
            RentalIncomeRate.setPlanJudge(planJudge);

            List<RentalIncomeRate> queryRentalIncomeRate = this.mapper.queryRentalIncomeRate(RentalIncomeRateParam);
            if (queryRentalIncomeRate.size() > 0) {
                this.IRentalIncomeRateService.batchDelete(queryRentalIncomeRate);
            }
            this.IRentalIncomeRateService.insert(iRequest, RentalIncomeRate);
        }
    }
    //租金回收率汇总查询
    public List<RentalIncomeRate> rentalIncomeRateTotal(IRequest request, RentalIncomeRate RentalIncomeRate, int page, int pageSize)
    {
        PageHelper.startPage(page, pageSize);
        return this.mapper.rentalIncomeRateTotal(RentalIncomeRate);
    }

    public List<RentalIncomeRate> rateExcelImportSelect(Long headerId)
    {
        List<RentalIncomeRate> tempList = this.mapper.rateExcelImportSelect(headerId);
        return tempList;
    }
}