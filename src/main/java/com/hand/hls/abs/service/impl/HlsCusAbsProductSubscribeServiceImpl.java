package com.hand.hls.abs.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.HlsCusAbsProduct;
import com.hand.hls.abs.dto.HlsCusAbsProductStructure;
import com.hand.hls.abs.dto.HlsCusAbsProductSubscribe;
import com.hand.hls.abs.mapper.HlsCusAbsProductStructureMapper;
import com.hand.hls.abs.mapper.HlsCusAbsProductSubscribeMapper;
import com.hand.hls.abs.service.HlsCusAbsProductService;
import com.hand.hls.abs.service.HlsCusAbsProductSubscribeService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusHlsCreditLine;
import com.hand.hls.fct.mapper.HlsCreditLineMapper;
import com.hand.hls.fin.mapper.HlsCusLonContractWithdrawMapper;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.utils.HlsCusConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsProductSubscribeServiceImpl extends BaseServiceImpl<HlsCusAbsProductSubscribe> implements HlsCusAbsProductSubscribeService {
    @Autowired
    private HlsCusAbsProductSubscribeMapper hlsCusAbsProductSubscribeMapper;
    @Autowired
    private HlsCusAbsProductService hlsCusAbsProductService;
    @Autowired
    private JeTrxCommonService jeTrxCommonService;
    @Autowired
    private HlsCusLonContractWithdrawMapper lonContractWithdrawMapper;
    @Autowired
    private HlsCreditLineMapper hlsCreditLineMapper;
    @Autowired
    private HlsCusAbsProductStructureMapper productStructureMapper;

    @Override
    public List<HlsCusAbsProductSubscribe> queryProductSubscribe(IRequest request, HlsCusAbsProductSubscribe hlsCusAbsProductSubscribe, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusAbsProductSubscribeMapper.queryProductSubscribe(hlsCusAbsProductSubscribe);
    }

    @Override
    public List<HlsCusAbsProductSubscribe> saveProductSubscribe(IRequest request, List<HlsCusAbsProductSubscribe> list) throws HlsCusException {
        for (HlsCusAbsProductSubscribe subscribe : list) {
            if(null == subscribe.getSubscribeId()){
                subscribe.set__status(DTOStatus.ADD);
            }else{
                subscribe.set__status(DTOStatus.UPDATE);
            }
            if("Y".equals(subscribe.getReleCreditFlag())){
                HlsCusHlsCreditLine hlsCreditLine = (HlsCusHlsCreditLine)hlsCreditLineMapper.selectByPrimaryKey(subscribe.getCreditLineId());
                if(hlsCreditLine.getValidFrom().compareTo(subscribe.getSubscribeDate())==1 || subscribe.getSubscribeDate().compareTo(hlsCreditLine.getValidTo())==1){
                    throw new HlsCusException("提款起始日不在授信期限内,请检查!");
                }
            }
        }
        HlsCusAbsProductStructure structure =new HlsCusAbsProductStructure();
        structure.setProductId(list.get(0).getProductId());
        Map<Long,Double> res= productStructureMapper.selectProductStructureData(structure)
                .stream().collect(Collectors.groupingBy(HlsCusAbsProductStructure::getStructureId,
                        Collectors.summingDouble(no->no.getPublishAmount().doubleValue())));
        List<HlsCusAbsProductSubscribe> productSubscribes = self().batchUpdate(request, list);
        for (HlsCusAbsProductSubscribe hlsCusAbsProductSubscribe : list) {
            BigDecimal subscribeAmountSum = hlsCusAbsProductSubscribeMapper
                    .selectSubscribeAmountSum(hlsCusAbsProductSubscribe.getProductId(),hlsCusAbsProductSubscribe.getStructureId());
            if(subscribeAmountSum.compareTo(new BigDecimal(res.get(hlsCusAbsProductSubscribe.getStructureId())))>0){
                throw new HlsCusException("认购级别为:["+hlsCusAbsProductSubscribe.getProjectGrade()+"]的认购金额总额不能超过总发行金额!");
            }
        }

        for (HlsCusAbsProductSubscribe subscribe : productSubscribes) {
            if ("Y".equals(subscribe.getReleCreditFlag())) {
                if (subscribe.getCreditLineId() != null) {
                    //分项额度可用额度
                    Double creditAmt = lonContractWithdrawMapper.selectCreditDueAmount(subscribe.getCreditLineId());
                    if (creditAmt == null) {
                        creditAmt = 0D;
                    }
                    if (new BigDecimal(creditAmt.toString()).compareTo(new BigDecimal(0)) == -1) {
                        throw new HlsCusException("认购金额超出授信实际可用金额！(包括预占用)");
                    }
                }
            }
        }
        return productSubscribes;
    }

    @Override
    public List<HlsCusAbsProductSubscribe> confirmProductSubscribe(IRequest request, List<HlsCusAbsProductSubscribe> list) throws HlsCusException {
        for (HlsCusAbsProductSubscribe subscribe : list) {
            subscribe.setSubscribeStatus(HlsCusConstant.WORKFLOW_STATUS.CONFIRM);
        }
        saveProductSubscribe(request,list);
        HlsCusAbsProduct hlsCusAbsProduct = new HlsCusAbsProduct();
        hlsCusAbsProduct.setProductId(list.get(0).getProductId());
        hlsCusAbsProduct.setOwnerStatus(HlsCusConstant.WORKFLOW_STATUS.CONFIRM);
        hlsCusAbsProduct.setSubscribeFlag(HlsCusConstant.FLAG.Y);
        hlsCusAbsProductService.updateByPrimaryKeySelective(request, hlsCusAbsProduct);
        hlsCusAbsProduct = hlsCusAbsProductService.selectByPrimaryKey(request, hlsCusAbsProduct);

        for (HlsCusAbsProductSubscribe hlsCusAbsProductSubscribe : list) {
            //插入凭证事物流水表
            AbstractJeTrxService abstractJeTrxService = jeTrxCommonService.map.get("ABS_PRODUCT_SUBSCRIBE");
            Map params = new HashMap<>();
            params.put("jeTrxId", hlsCusAbsProductSubscribe.getSubscribeId());
            params.put("companyId", hlsCusAbsProduct.getCompanyId());
            params.put("contractId", hlsCusAbsProductSubscribe.getSubscribeId());
            params.put("sourceDoc", "CT_ABS_PRODUCT_SUBSCRIBE");
            abstractJeTrxService.process(request, params);
        }
        return list;
    }


    @Override
    public void batchDeleteProductSubscribe(IRequest request, List<HlsCusAbsProductSubscribe> list) throws HlsCusException {
        for (HlsCusAbsProductSubscribe subscribe : list) {
            if(HlsCusConstant.WORKFLOW_STATUS.CONFIRM.equals(subscribe.getSubscribeStatus())){
                throw new HlsCusException("请勿删除已经确认的数据");
            }
        }
        self().batchDelete(list);
    }

    @Override
    public int selectSubscribeNotReleaseCount(Long productId) {
        return hlsCusAbsProductSubscribeMapper.selectSubscribeNotReleaseCount(productId);
    }

    @Override
    protected boolean useSelectiveUpdate() {
        return false;
    }
}
