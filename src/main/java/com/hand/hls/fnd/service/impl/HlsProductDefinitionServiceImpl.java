package com.hand.hls.fnd.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.annotation.StdWho;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.fnd.dto.HlsProductDefinition;
import com.hand.hls.fnd.dto.HlsProductDefinitionPara;
import com.hand.hls.fnd.mapper.HlsProductDefDealerMapper;
import com.hand.hls.fnd.mapper.HlsProductDefinitionMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.fnd.service.IHlsProductDefinitionParaService;
import com.hand.hls.fnd.service.IHlsProductDefinitionService;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.HlsCreditPlanMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.IBpMasterReplyService;
import com.hand.hls.prj.service.IReplyProductParaService;
import com.hand.hls.prj.service.IReplyProductService;
import com.hand.hls.sys.service.IFndCompanyService;
import com.hand.hls.utils.HlsConstantUtil;
import com.hand.hls.wfl.service.IActivitiStartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsProductDefinitionServiceImpl extends BaseServiceImpl<HlsProductDefinition> implements IHlsProductDefinitionService {

    private static final String HLS_PRODUCT_DEFINITION = "hlsProductDefinition";
    private static final String DEFINITION_ID = "definitionId";
    private static final String DEFINITION_CODE = "definitionCode";
    private static final String WORKFLOW_TYPE = "workFlowType";
    private static final String DOCUMENT_CATEGORY = "documentCategory";
    private static final String DOCUMENT_TYPE = "documentType";
    private static final String BUSINESS_TYPE = "businessType";
    private static final String DOCUMENT_ID = "documentId";
    private static final String DOCUMENT_NUMBER = "documentNumber";
    private static final String DOCUMENT_NAME = "documentName";
    private static final String ALLOCATION_ID = "allocationId";

    @Autowired
    private IHlsProductDefinitionParaService iHlsProductDefinitionParaService;
    @Autowired
    private HlsProductDefinitionMapper hlsProductDefinitionMapper;
    @Autowired
    private HlsCreditPlanMapper hlsCreditPlanMapper;
    @Autowired
    private IReplyProductService iReplyProductService;
    @Autowired
    private IBpMasterReplyService iBpMasterReplyService;
    @Autowired
    private IReplyProductParaService iReplyProductParaService;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private IActivitiStartService iActivitiStartService;
    @Autowired
    private IFndCompanyService iFndCompanyService;
    @Autowired
    private HlsProductDefDealerMapper hlsProductDefDealerMapper;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    @Override
    public List<HlsProductDefinition> selectHlsProductDefinitionList(IRequest request, HlsProductDefinition hlsProductDefinition, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<HlsProductDefinition> hlsProductDefinitionList = hlsProductDefinitionMapper.selectHlsProductDefinitionList(hlsProductDefinition);
        return hlsProductDefinitionList;
    }

    @Override
    public List<HlsProductDefinition> batchUpdateProductDefinition(IRequest request, List<HlsProductDefinition> hlsProductDefinitionList) throws HlsCusException {
        for (HlsProductDefinition hlsProductDefinition : hlsProductDefinitionList) {
            if (hlsProductDefinition.getDefinitionId() == null) {
                //每个合作商仅支持关联一个产品
                Integer numByBpName = hlsProductDefDealerMapper.selectProductNumByBpName(hlsProductDefinition.getReplyProductId());
                if (numByBpName > 0){
                    throw new HlsCusException("同一个合作方仅支持一个启用的产品！");
                }
                //单据类别信息
                hlsProductDefinition.setDocumentCategory(HlsConstantUtil.HlsProductDefinition.DOCUMENT_CATEGORY);
                hlsProductDefinition.setDocumentType(HlsConstantUtil.HlsProductDefinition.DOCUMENT_TYPE);
                hlsProductDefinition.setBusinessType(HlsConstantUtil.HlsProductDefinition.DOCUMENT_CATEGORY);

                //获取编号
                if (hlsProductDefinition.getDefinitionCode() == "" || hlsProductDefinition.getDefinitionCode() == null) {
                    Map<String, String> params = new HashMap<String, String>();
                    String definitionCode = fndCodingRuleValuesService.getCodeRuleValue(request, hlsProductDefinition.getDocumentCategory(), hlsProductDefinition.getDocumentType(), hlsProductDefinition.getBusinessType(), params);
                    hlsProductDefinition.setDefinitionCode(definitionCode);
                }

                //单据所属公司及所有者
                hlsProductDefinition.setCompanyId(request.getCompanyId());
                hlsProductDefinition.setOwnerUserId(request.getUserId());
                //产品新增保存后默认为启用状态
                hlsProductDefinition.setEnabledFlag("Y");
                //是否推到租金默认为否（表里这个字段不能为空）
                hlsProductDefinition.setIntRateCalcFlag("N");

                //单据状态
                hlsProductDefinition.setProductStatus(HlsConstantUtil.WorkFlowStatus.NEW);

                //授权规则
                FndCompany fndCompany = new FndCompany();
                fndCompany.setCompanyId(request.getCompanyId());
                fndCompany = iFndCompanyService.selectByPrimaryKey(request, fndCompany);
                String companyCode = fndCompany.getCompanyCode();
                String unitCode = request.getAttribute("unitCode") == null ? "" : (String) request.getAttribute("unitCode");
                String positionCode = request.getAttribute("positionCode") == null ? "" : (String) request.getAttribute("positionCode");
                String employeeCode = request.getEmployeeCode();
                String authorityRuleString = '"' + companyCode + '"' + "." + '"' + unitCode + '"' + "." + '"' + '"' + "." + '"' + '"' + "." + '"' + '"' + "." + '"' + positionCode + '"' + "." + '"' + employeeCode + '"';

                hlsProductDefinition.setAuthorityRuleString(authorityRuleString);

                self().insertHlsProductDefinition(request, hlsProductDefinition);
                //如果选择了业务经理，就将业务经理id和部门id更新到项目表
                if(hlsProductDefinition.getEmployeeId() != null){
                    UpdatePrjProjectInfo(request,hlsProductDefinition);
                }
            } else {
                //每个合作商仅支持关联一个产品
                Integer numByBpName = hlsProductDefDealerMapper.selectProductNumByBpName(hlsProductDefinition.getReplyProductId());
                if (numByBpName > 0){
                    throw new HlsCusException("同一个合作方仅支持一个启用的产品！");
                }
                self().updateHlsProductDefinition(request, hlsProductDefinition);
                //如果选择了业务经理，就将业务经理id和部门id更新到项目表
                if(hlsProductDefinition.getEmployeeId() != null){
                    UpdatePrjProjectInfo(request,hlsProductDefinition);
                }
            }
        }
        return hlsProductDefinitionList;
    }

    @Override
    public HlsProductDefinition insertHlsProductDefinition(IRequest request, HlsProductDefinition hlsProductDefinition) throws HlsCusException {
        // 插入头行
        self().insert(request, hlsProductDefinition);
        // 判断如果行不为空，则迭代循环插入
        if (hlsProductDefinition.getHlsProductDefinitionParaList() != null) {
            saveHlsProductDefinitionPara(request, hlsProductDefinition);
        }
        return hlsProductDefinition;
    }

    @Override
    public HlsProductDefinition updateHlsProductDefinition(IRequest request, HlsProductDefinition hlsProductDefinition) throws HlsCusException {
        self().updateByPrimaryKey(request, hlsProductDefinition);
        // 判断如果行不为空，则迭代循环插入
        if (hlsProductDefinition.getHlsProductDefinitionParaList() != null) {
            saveHlsProductDefinitionPara(request, hlsProductDefinition);
        }
        return hlsProductDefinition;
    }

    /**
     * 头行数据，保存产品参数
     *
     * @param hlsProductDefinition
     */
    private void saveHlsProductDefinitionPara(IRequest request, HlsProductDefinition hlsProductDefinition) throws HlsCusException {
        for (HlsProductDefinitionPara hlsProductDefinitionPara : hlsProductDefinition.getHlsProductDefinitionParaList()) {
            if (hlsProductDefinitionPara.getProductParaId() == null) {
                // 设置头ID跟行ID一致
                hlsProductDefinitionPara.setDefinitionId(hlsProductDefinition.getDefinitionId());
                iHlsProductDefinitionParaService.insert(request, hlsProductDefinitionPara);
            } else {
                iHlsProductDefinitionParaService.updateByPrimaryKey(request, hlsProductDefinitionPara);
            }
        }

//        checkHlsProductDefinition(request, hlsProductDefinition);
    }
    /**
     * 产品定义保存，更新业务经理信息到项目表中
     *
     * @param hlsProductDefinition
     */
    private void UpdatePrjProjectInfo(IRequest iRequest, HlsProductDefinition hlsProductDefinition) throws HlsCusException {
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(hlsProductDefinition.getReplyProductId());
        prjProject.setEmployeeId(hlsProductDefinition.getEmployeeId());
        prjProject.setUnitId(hlsProductDefinition.getUnitId());
        hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest, prjProject);
    }

    /**
     * 产品校验 是否符合批复要求
     *
     * @param hlsProductDefinition
     */
    private void checkHlsProductDefinition(IRequest request, HlsProductDefinition hlsProductDefinition) throws HlsCusException {
        //获取批复产品信息
//        ReplyProduct replyProduct = new ReplyProduct();
//        replyProduct.setReplyProductId(hlsProductDefinition.getReplyProductId());
//        replyProduct = iReplyProductService.selectByPrimaryKey(request, replyProduct);
//
//        Long annualPayTimes = replyProduct.getAnnualPayTimes();
//        String exemptPenaltyInt = replyProduct.getExemptPenaltyInt();
//        Long payType = replyProduct.getPayType();
//        String priceList = replyProduct.getPriceList();
//        Long replyId = replyProduct.getReplyId();

//        BpMasterReply bpMasterReply = new BpMasterReply();
//        bpMasterReply.setReplyId(replyId);
//        bpMasterReply = iBpMasterReplyService.selectByPrimaryKey(request, bpMasterReply);
//        String latestFlag = bpMasterReply.getLatestFlag();
//        if (!"Y".equals(latestFlag)) {
//            throw new HlsCusException("关联批复已过时，请核对!");
//        }

        //获取批复产品参数信息
//        List<ReplyProductPara> replyProductParaList = iReplyProductParaService.selectReplyProductParaList(request, replyProduct.getReplyProductId(), 0, 0);

        //产品定义信息
//        hlsProductDefinition = self().selectByPrimaryKey(request, hlsProductDefinition);
//        if (annualPayTimes != null && !annualPayTimes.equals(hlsProductDefinition.getAnnualPayTimes())) {
//            throw new HlsCusException("支付频率不符合批复要求，请核对!");
//        }
//        if (exemptPenaltyInt != null && !exemptPenaltyInt.equals(hlsProductDefinition.getExemptPenaltyInt())) {
//            throw new HlsCusException("逾期宽限类型不符合批复要求，请核对!");
//        }
//        if (payType != null && !payType.equals(hlsProductDefinition.getPayType())) {
//            throw new HlsCusException("先付/后付不符合批复要求，请核对!");
//        }
//        if (priceList != null && !priceList.equals(hlsProductDefinition.getPriceList())) {
//            throw new HlsCusException("还款方式不符合批复要求，请核对!");
//        }

        List<HlsProductDefinitionPara> hlsProductDefinitionParaList;

//        for (ReplyProductPara replyProductPara : replyProductParaList) {
//            //产品参数信息
//            HlsProductDefinitionPara hlsProductDefinitionPara = new HlsProductDefinitionPara();
//            hlsProductDefinitionPara.setDefinitionId(hlsProductDefinition.getDefinitionId());
//            hlsProductDefinitionPara.setProductPara(replyProductPara.getReplyProductPara());
//            hlsProductDefinitionParaList = iHlsProductDefinitionParaService.select(request, hlsProductDefinitionPara, 0, 0);
//
//            if (hlsProductDefinitionParaList.size() == 0) {
//                throw new HlsCusException("参数" + replyProductPara.getReplyProductParaDisplay() + "未定义，不符合批复要求，请核对!");
//            }
//            hlsProductDefinitionPara = hlsProductDefinitionParaList.get(0);
//
//            BigDecimal vauleFrom = replyProductPara.getVauleFrom();
//            BigDecimal vauleTo = replyProductPara.getVauleTo();
//            if (vauleFrom != null && vauleFrom.compareTo(hlsProductDefinitionPara.getValueFrom()) > 0) {
//                throw new HlsCusException("参数" + replyProductPara.getReplyProductParaDisplay() + "范围从不符合批复要求，请核对!");
//            }
//            if (vauleTo != null && vauleTo.compareTo(hlsProductDefinitionPara.getValueTo()) < 0) {
//                throw new HlsCusException("参数" + replyProductPara.getReplyProductParaDisplay() + "范围到不符合批复要求，请核对!");
//            }
//        }
        HlsProductDefinitionPara hlsProductDefinitionPara;

        HlsCreditPlan hlsCreditPlan=new HlsCreditPlan();
        hlsCreditPlan.setSourceDocumentId(hlsProductDefinition.getReplyProductId());
        hlsCreditPlan=hlsCreditPlanMapper.selectOne(hlsCreditPlan);
        hlsProductDefinitionPara = new HlsProductDefinitionPara();
        hlsProductDefinitionPara.setDefinitionId(hlsProductDefinition.getDefinitionId());
        hlsProductDefinitionPara.setProductPara("IRR");
        hlsProductDefinitionParaList = iHlsProductDefinitionParaService.select(request, hlsProductDefinitionPara, 0, 0);
        //二期功能：优化条件判断
        if (hlsProductDefinitionParaList.isEmpty()){
            throw new HlsCusException("参数IRR未定义，请核对!");
        }
        if (hlsProductDefinitionParaList.get(0).getDefaultValue().compareTo(BigDecimal.valueOf(hlsCreditPlan.getIrr()))<0 ) {
            throw new HlsCusException("参数IRR缺省值不符合批复要求");
        }
        hlsProductDefinitionPara = new HlsProductDefinitionPara();
        hlsProductDefinitionPara.setDefinitionId(hlsProductDefinition.getDefinitionId());
        hlsProductDefinitionPara.setProductPara("DOWN_PAYMENT_RATIO");
        hlsProductDefinitionParaList = iHlsProductDefinitionParaService.select(request, hlsProductDefinitionPara, 0, 0);
        if(hlsCreditPlan.getDownPaymentRatio()!=null) {
            if (hlsProductDefinitionParaList.isEmpty()){
                throw new HlsCusException("参数首付款比例未定义，请核对!");
            }

            if (hlsProductDefinitionParaList.get(0).getDefaultValue().compareTo(BigDecimal.valueOf(hlsCreditPlan.getDownPaymentRatio()))<0 ) {
                throw new HlsCusException("参数首付款比例缺省值不符合批复要求");
            }
        }
        hlsProductDefinitionPara = new HlsProductDefinitionPara();
        hlsProductDefinitionPara.setDefinitionId(hlsProductDefinition.getDefinitionId());
        hlsProductDefinitionPara.setProductPara("DEPOSIT_RATIO");
        hlsProductDefinitionParaList = iHlsProductDefinitionParaService.select(request, hlsProductDefinitionPara, 0, 0);
        if(hlsCreditPlan.getDepositRatio()!=null) {
            if (hlsProductDefinitionParaList.isEmpty()){
                throw new HlsCusException("参数保证金比例未定义，请核对!");
            }
            if (hlsProductDefinitionParaList.get(0).getDefaultValue().compareTo(BigDecimal.valueOf(hlsCreditPlan.getDepositRatio()))<0 ) {
                throw new HlsCusException("参数保证金比例缺省值不符合批复要求");
            }
        }
        hlsProductDefinitionPara = new HlsProductDefinitionPara();
        hlsProductDefinitionPara.setDefinitionId(hlsProductDefinition.getDefinitionId());
        hlsProductDefinitionPara.setProductPara("RESIDUAL_VALUE");
        hlsProductDefinitionParaList = iHlsProductDefinitionParaService.select(request, hlsProductDefinitionPara, 0, 0);
        if(hlsCreditPlan.getResidual()!=null) {
            if (hlsProductDefinitionParaList.isEmpty()){
                throw new HlsCusException("参数名义货价未定义，请核对!");
            }
            if (hlsProductDefinitionParaList.get(0).getDefaultValue().compareTo(BigDecimal.valueOf(hlsCreditPlan.getResidual()))>0 ) {
                throw new HlsCusException("参数名义货价缺省值不符合批复要求");
            }
        }
        hlsProductDefinitionPara = new HlsProductDefinitionPara();
        hlsProductDefinitionPara.setDefinitionId(hlsProductDefinition.getDefinitionId());
        hlsProductDefinitionPara.setProductPara("LEASE_TERM");
        hlsProductDefinitionParaList = iHlsProductDefinitionParaService.select(request, hlsProductDefinitionPara, 0, 0);
        if (hlsProductDefinitionParaList.isEmpty()){
            throw new HlsCusException("参数期限(月)未定义，请核对!");
        }
        if (hlsProductDefinitionParaList.get(0).getDefaultValue().compareTo(BigDecimal.valueOf(hlsCreditPlan.getFinancingDate()*12))>0 ) {
            throw new HlsCusException("参数期限(月)缺省值不符合批复要求");
        }

        hlsProductDefinitionPara = new HlsProductDefinitionPara();
        hlsProductDefinitionPara.setDefinitionId(hlsProductDefinition.getDefinitionId());
        hlsProductDefinitionPara.setProductPara(HlsConstantUtil.HlsProductDefinition.ProductPara.PENALTY_RATE);
        hlsProductDefinitionParaList = iHlsProductDefinitionParaService.select(request, hlsProductDefinitionPara, 0, 0);
       if (hlsProductDefinitionParaList.size() == 0) {
            throw new HlsCusException("参数承租人罚息率未定义，请核对!");
        }
       //update-2023-03-16：二期：业务要求：“承租人宽限天”不需要校验必填，系统已使用另一套罚息减免逻辑，月底前均不计算罚息；
       /*hlsProductDefinitionPara = new HlsProductDefinitionPara();
       hlsProductDefinitionPara.setDefinitionId(hlsProductDefinition.getDefinitionId());
       hlsProductDefinitionPara.setProductPara(HlsConstantUtil.HlsProductDefinition.ProductPara.GRACE_PERIOD);
       hlsProductDefinitionParaList = iHlsProductDefinitionParaService.select(request, hlsProductDefinitionPara, 0, 0);
       if (hlsProductDefinitionParaList.size() == 0) {
            throw new HlsCusException("参数承租人宽限天未定义，请核对!");
       }*/

//        if (HlsConstantUtil.HlsProductDefinition.ExemptPenaltyInt.NEXT_MONTH.equals(hlsProductDefinition.getExemptPenaltyInt())) {
//            //若逾期宽限类型为宽限至次月，则承租人罚息率、厂商宽限天、厂商罚息率必填；
//            hlsProductDefinitionPara = new HlsProductDefinitionPara();
//            hlsProductDefinitionPara.setDefinitionId(hlsProductDefinition.getDefinitionId());
//            hlsProductDefinitionPara.setProductPara(HlsConstantUtil.HlsProductDefinition.ProductPara.PENALTY_RATE);
//            hlsProductDefinitionParaList = iHlsProductDefinitionParaService.select(request, hlsProductDefinitionPara, 0, 0);
//            if (hlsProductDefinitionParaList.size() == 0) {
//                throw new HlsCusException("参数承租人罚息率未定义，不符合批复要求，请核对!");
//            }
//
//            hlsProductDefinitionPara = new HlsProductDefinitionPara();
//            hlsProductDefinitionPara.setDefinitionId(hlsProductDefinition.getDefinitionId());
//            hlsProductDefinitionPara.setProductPara(HlsConstantUtil.HlsProductDefinition.ProductPara.MANUFACTURER_GRACE_DAY);
//            hlsProductDefinitionParaList = iHlsProductDefinitionParaService.select(request, hlsProductDefinitionPara, 0, 0);
//            if (hlsProductDefinitionParaList.size() == 0) {
//                throw new HlsCusException("参数厂商/合作方宽限天未定义，不符合批复要求，请核对!");
//            }
//
//            hlsProductDefinitionPara = new HlsProductDefinitionPara();
//            hlsProductDefinitionPara.setDefinitionId(hlsProductDefinition.getDefinitionId());
//            hlsProductDefinitionPara.setProductPara(HlsConstantUtil.HlsProductDefinition.ProductPara.MANUFACTURER_PENALTY_RATE);
//            hlsProductDefinitionParaList = iHlsProductDefinitionParaService.select(request, hlsProductDefinitionPara, 0, 0);
//            if (hlsProductDefinitionParaList.size() == 0) {
//                throw new HlsCusException("参数厂商/合作方罚息率未定义，不符合批复要求，请核对!");
//            }
//        } else if (HlsConstantUtil.HlsProductDefinition.ExemptPenaltyInt.BY_DAY.equals(hlsProductDefinition.getExemptPenaltyInt())) {
//            //若逾期宽限类型为按天宽限，则承租人宽限天、承租人罚息率、厂商宽限天、厂商罚息率必填
//            hlsProductDefinitionPara = new HlsProductDefinitionPara();
//            hlsProductDefinitionPara.setDefinitionId(hlsProductDefinition.getDefinitionId());
//            hlsProductDefinitionPara.setProductPara(HlsConstantUtil.HlsProductDefinition.ProductPara.GRACE_PERIOD);
//            hlsProductDefinitionParaList = iHlsProductDefinitionParaService.select(request, hlsProductDefinitionPara, 0, 0);
//            if (hlsProductDefinitionParaList.size() == 0) {
//                throw new HlsCusException("参数承租人宽限天未定义，不符合批复要求，请核对!");
//            }
//
//            hlsProductDefinitionPara = new HlsProductDefinitionPara();
//            hlsProductDefinitionPara.setDefinitionId(hlsProductDefinition.getDefinitionId());
//            hlsProductDefinitionPara.setProductPara(HlsConstantUtil.HlsProductDefinition.ProductPara.PENALTY_RATE);
//            hlsProductDefinitionParaList = iHlsProductDefinitionParaService.select(request, hlsProductDefinitionPara, 0, 0);
//            if (hlsProductDefinitionParaList.size() == 0) {
//                throw new HlsCusException("参数承租人罚息率未定义，不符合批复要求，请核对!");
//            }
//
//            hlsProductDefinitionPara = new HlsProductDefinitionPara();
//            hlsProductDefinitionPara.setDefinitionId(hlsProductDefinition.getDefinitionId());
//            hlsProductDefinitionPara.setProductPara(HlsConstantUtil.HlsProductDefinition.ProductPara.MANUFACTURER_GRACE_DAY);
//            hlsProductDefinitionParaList = iHlsProductDefinitionParaService.select(request, hlsProductDefinitionPara, 0, 0);
//            if (hlsProductDefinitionParaList.size() == 0) {
//                throw new HlsCusException("参数厂商/合作方宽限天未定义，不符合批复要求，请核对!");
//            }
//
//            hlsProductDefinitionPara = new HlsProductDefinitionPara();
//            hlsProductDefinitionPara.setDefinitionId(hlsProductDefinition.getDefinitionId());
//            hlsProductDefinitionPara.setProductPara(HlsConstantUtil.HlsProductDefinition.ProductPara.MANUFACTURER_PENALTY_RATE);
//            hlsProductDefinitionParaList = iHlsProductDefinitionParaService.select(request, hlsProductDefinitionPara, 0, 0);
//            if (hlsProductDefinitionParaList.size() == 0) {
//                throw new HlsCusException("参数厂商/合作方罚息率未定义，不符合批复要求，请核对!");
//            }
//        }
        if (HlsConstantUtil.HlsProductDefinition.REPAY_FLAG_YES.equals(hlsProductDefinition.getRepayFlag())) {
            //计算返利时，则租息率、服务费比例必填；
            hlsProductDefinitionPara = new HlsProductDefinitionPara();
            hlsProductDefinitionPara.setDefinitionId(hlsProductDefinition.getDefinitionId());
            hlsProductDefinitionPara.setProductPara(HlsConstantUtil.HlsProductDefinition.ProductPara.INT_RATE);
            hlsProductDefinitionParaList = iHlsProductDefinitionParaService.select(request, hlsProductDefinitionPara, 0, 0);
            if (hlsProductDefinitionParaList.size() == 0) {
                throw new HlsCusException("计算返利时，参数租息率需定义，请核对!");
            }

//            hlsProductDefinitionPara = new HlsProductDefinitionPara();
//            hlsProductDefinitionPara.setDefinitionId(hlsProductDefinition.getDefinitionId());
//            hlsProductDefinitionPara.setProductPara(HlsConstantUtil.HlsProductDefinition.ProductPara.LEASE_CHARGE_RATIO);
//            hlsProductDefinitionParaList = iHlsProductDefinitionParaService.select(request, hlsProductDefinitionPara, 0, 0);
//            if (hlsProductDefinitionParaList.size() == 0) {
//                throw new HlsCusException("计算返利时，参数服务费比例需定义，请核对!");
//            }
        }
        if (HlsConstantUtil.HlsProductDefinition.REPAY_FLAG_YES.equals(hlsProductDefinition.getServiceFeeRepayFlag())) {
            //计算返利时，则租息率、服务费比例必填；
//            hlsProductDefinitionPara = new HlsProductDefinitionPara();
//            hlsProductDefinitionPara.setDefinitionId(hlsProductDefinition.getDefinitionId());
//            hlsProductDefinitionPara.setProductPara(HlsConstantUtil.HlsProductDefinition.ProductPara.INT_RATE);
//            hlsProductDefinitionParaList = iHlsProductDefinitionParaService.select(request, hlsProductDefinitionPara, 0, 0);
//            if (hlsProductDefinitionParaList.size() == 0) {
//                throw new HlsCusException("计算返利时，参数租息率需定义，请核对!");
//            }

            hlsProductDefinitionPara = new HlsProductDefinitionPara();
            hlsProductDefinitionPara.setDefinitionId(hlsProductDefinition.getDefinitionId());
            hlsProductDefinitionPara.setProductPara(HlsConstantUtil.HlsProductDefinition.ProductPara.LEASE_CHARGE_RATIO);
            hlsProductDefinitionParaList = iHlsProductDefinitionParaService.select(request, hlsProductDefinitionPara, 0, 0);
            if (hlsProductDefinitionParaList.size() == 0) {
                throw new HlsCusException("计算返利时，参数服务费比例需定义，请核对!");
            }
        }
    }

    @Override
    public HlsProductDefinition submitHlsProductDefinition(IRequest request, @StdWho HlsProductDefinition hlsProductDefinitionPara) throws HlsCusException {
        HlsProductDefinition hlsProductDefinition = self().selectHlsProductDefinitionList(request, hlsProductDefinitionPara, 0, 0).get(0);
        String productStatus = hlsProductDefinition.getProductStatus();
        if (HlsConstantUtil.WorkFlowStatus.APPROVING.equals(productStatus) || HlsConstantUtil.WorkFlowStatus.APPROVED.equals(productStatus)) {
            throw new HlsCusException("产品状态有误，请核对!");
        }

        checkHlsProductDefinition(request, hlsProductDefinition);

//        更改状态
        hlsProductDefinition.setProductStatus(HlsConstantUtil.WorkFlowStatus.APPROVING);
        self().updateByPrimaryKeySelective(request, hlsProductDefinition);

        //设置工作流参数
        Map<String, Object> map = new HashMap<>();

        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(hlsProductDefinition));
        map.put(HLS_PRODUCT_DEFINITION, jsonObject.toString());
        map.put(DEFINITION_ID, hlsProductDefinition.getDefinitionId());
        map.put(DEFINITION_CODE, hlsProductDefinition.getDefinitionCode());
        map.put(WORKFLOW_TYPE, HlsConstantUtil.HlsProductDefinition.WORK_FLOW_TYPE);
        map.put(DOCUMENT_CATEGORY, hlsProductDefinition.getDocumentCategory());
        map.put(DOCUMENT_TYPE, hlsProductDefinition.getDocumentType());
        map.put(BUSINESS_TYPE, hlsProductDefinition.getBusinessType());
        map.put(DOCUMENT_ID, hlsProductDefinition.getDefinitionId());
        map.put(DOCUMENT_NUMBER, hlsProductDefinition.getDefinitionCode());
        map.put(DOCUMENT_NAME, hlsProductDefinition.getDefinitionName());

        List<HlsProductDefinition> list = new ArrayList<>();
        list.add(hlsProductDefinition);

        iActivitiStartService.start(request, list, map);

        return hlsProductDefinition;
    }

    @Override
    public HlsProductDefinition changeHlsProductDefinition(IRequest request, @StdWho HlsProductDefinition hlsProductDefinition) throws HlsCusException {
        hlsProductDefinition = self().selectHlsProductDefinitionList(request, hlsProductDefinition, 0, 0).get(0);
        String productStatus = hlsProductDefinition.getProductStatus();
        String enabledFlag = hlsProductDefinition.getEnabledFlag();
        if (!HlsConstantUtil.WorkFlowStatus.APPROVED.equals(productStatus) || !"Y".equals(enabledFlag)) {
            throw new HlsCusException("请选择启用产品!");
        }
        //更改状态
        hlsProductDefinition.setProductStatus(HlsConstantUtil.WorkFlowStatus.NEW);
        hlsProductDefinition.setEnabledFlag("N");
        self().updateByPrimaryKeySelective(request, hlsProductDefinition);

        //清空分配经销商记录
        hlsProductDefDealerMapper.deleteProductDefDealerByDefinitionId(hlsProductDefinition.getDefinitionId());

        //设置工作流参数

        return hlsProductDefinition;
    }
}