package com.hand.hls.pam.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.hls.service.HlsCusHlsMarketingReportService;
import com.hand.hls.pam.dto.*;
import com.hand.hls.pam.mapper.HlsCusLeaseItemMapper;
import com.hand.hls.pam.service.*;
import com.hand.hls.prj.dto.HlsCusPrjProjectLeaseItem;
import com.hand.hls.prj.dto.HlsCusPrjProjectPledge;
import com.hand.hls.prj.mapper.HlsCusPrjProjectLeaseItemMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectPledgeMapper;
import com.hand.hls.sys.mapper.FndCompanyMapper;
import com.hand.hls.utils.ResMessageException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Qian Yuanfeng
 * @date 2020/4/14 - 14:26
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusLeaseItemServiceImpl extends BaseServiceImpl<HlsCusLeaseItem> implements HlsCusLeaseItemService {

    public static final String Y = "Y";
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Autowired
    private HlsCusLeaseItemMapper hlsCusLeaseItemMapper;

    @Autowired
    private HlsCusItemTaxesService hlsCusItemTaxesService;

    @Autowired
    private IHlsLeaseItemDetailService hlsLeaseItemDetailService;

    @Autowired
    private IHlsLeaseItemTransferService hlsLeaseItemTransferService;

    @Autowired
    private HlsCusHlsLeaseItemAttachmentService hlsLeaseItemAttachmentService;
    @Autowired
    private HlsCusPrjProjectPledgeMapper hlsCusPrjProjectPledgeMapper;

    @Autowired
    private HlsCusPrjProjectLeaseItemMapper hlsCusPrjProjectLeaseItemMapper;

    @Autowired
    private HlsCusHlsMarketingReportService hlsCusHlsMarketingReportService;

    @Autowired
    private IHlsCusLeaseItemListService hlsCusLeaseItemListService;

    @Autowired
    FndCompanyMapper fndCompanyMapper;

    private static String DOCUMENT_CATEGORY = "HLS_LEASE_ITEM";
    private static String DOCUMENT_TYPE = "HLS_LEASE_ITEM";
    private static String BUSINESS_TYPE = "HLS_LEASE_ITEM";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HlsCusLeaseItemManage leaseSaveSubmit(IRequest iRequest, HlsCusLeaseItemManage hlsCusLeaseItemManage) {
        HlsCusLeaseItem hlsCusLeaseItem = new HlsCusLeaseItem();
        hlsCusLeaseItem = hlsCusLeaseItemManage.getHlsCusLeaseItem();
        if (!ObjectUtils.isEmpty(hlsCusLeaseItem)) {

            //如果主键为空或者为0，那么就是新建，新建的时候使用编码规则获取租赁物编号
            if (hlsCusLeaseItem.getLeaseItemId() == null || hlsCusLeaseItem.getLeaseItemId() == 0) {

                //编码规则 ZLW+YYYYMMDD+3位流水号
                Map<String, String> params = new HashMap<>();
                String ruleCode = fndCodingRuleValuesService.getCodeRuleValue(iRequest, DOCUMENT_CATEGORY, DOCUMENT_TYPE, BUSINESS_TYPE, params);
                hlsCusLeaseItem.setLeaseItemCode(ruleCode);
                if (iRequest.getUserId() != null) {
                    hlsCusLeaseItem.setCreatedBy(iRequest.getUserId());
                }
                String authorityRuleString = hlsCusHlsMarketingReportService.generateAuthorityString(iRequest);
                hlsCusLeaseItem.setAuthorityRuleString(authorityRuleString);

                hlsCusLeaseItem = self().insertSelective(iRequest, hlsCusLeaseItem);

            } else {
                hlsCusLeaseItem = self().updateByPrimaryKeySelective(iRequest, hlsCusLeaseItem);
            }
        }


        //租赁物相关税费信息
        List<HlsCusLeaseItemTaxes> hlsCusLeaseItemTaxesList = hlsCusLeaseItemManage.getHlsCusLeaseItemTaxesList();
        if (CollectionUtils.isNotEmpty(hlsCusLeaseItemTaxesList)) {
            for (HlsCusLeaseItemTaxes hlsCusLeaseItemTaxes : hlsCusLeaseItemTaxesList) {
                //判断是新增还是更新
                if (hlsCusLeaseItemTaxes.getLeaseTaxesId() == null || hlsCusLeaseItemTaxes.getLeaseTaxesId() == 0) {
                    hlsCusLeaseItemTaxes.setLeaseItemId(hlsCusLeaseItem.getLeaseItemId());
                    hlsCusItemTaxesService.insertSelective(iRequest, hlsCusLeaseItemTaxes);
                } else {
                    hlsCusItemTaxesService.updateByPrimaryKeySelective(iRequest, hlsCusLeaseItemTaxes);
                }
            }
        }

        //租赁物明细
        List<HlsLeaseItemDetail> hlsLeaseItemDetailList = hlsCusLeaseItemManage.getHlsLeaseItemDetailList();
        if (CollectionUtils.isNotEmpty(hlsLeaseItemDetailList)) {
            for (HlsLeaseItemDetail hlsLeaseItemDetail : hlsLeaseItemDetailList) {
                //判断是新增还是更新
                if (hlsLeaseItemDetail.getLeaseItemDetailId() == null || hlsLeaseItemDetail.getLeaseItemDetailId() == 0) {
                    hlsLeaseItemDetail.setLeaseItemId(hlsCusLeaseItem.getLeaseItemId());
                    hlsLeaseItemDetailService.insertSelective(iRequest, hlsLeaseItemDetail);
                } else {
                    hlsLeaseItemDetailService.updateByPrimaryKeySelective(iRequest, hlsLeaseItemDetail);
                }
            }
        }

        //资产转移相关信息
        List<HlsLeaseItemTransfer> hlsLeaseItemTransferList = hlsCusLeaseItemManage.getHlsLeaseItemTransferList();
        if (CollectionUtils.isNotEmpty(hlsLeaseItemTransferList)) {
            for (HlsLeaseItemTransfer hlsLeaseItemTransfer : hlsLeaseItemTransferList) {
                //判断是新增还是更新
                if (hlsLeaseItemTransfer.getLeaseAssetId() == null || hlsLeaseItemTransfer.getLeaseAssetId() == 0) {
                    hlsLeaseItemTransfer.setLeaseItemId(hlsCusLeaseItem.getLeaseItemId());
                    hlsLeaseItemTransferService.insertSelective(iRequest, hlsLeaseItemTransfer);
                } else {
                    hlsLeaseItemTransferService.updateByPrimaryKeySelective(iRequest, hlsLeaseItemTransfer);
                }
            }
        }

        //附件信息
        List<HlsCusHlsLeaseItemAttachment> hlsLeaseItemAttachmentList = hlsCusLeaseItemManage.getHlsLeaseItemAttachmentList();
        if (CollectionUtils.isNotEmpty(hlsLeaseItemAttachmentList)) {
            for (HlsCusHlsLeaseItemAttachment hlsLeaseItemAttachment : hlsLeaseItemAttachmentList) {
                //判断是新增还是更新
                if (hlsLeaseItemAttachment.getLeaseItemAttachmentId() == null || hlsLeaseItemAttachment.getLeaseItemAttachmentId() == 0) {
                    hlsLeaseItemAttachment.setLeaseItemId(hlsCusLeaseItem.getLeaseItemId());
                    hlsLeaseItemAttachmentService.insertSelective(iRequest, hlsLeaseItemAttachment);
                } else {
                    hlsLeaseItemAttachmentService.updateByPrimaryKeySelective(iRequest, hlsLeaseItemAttachment);
                }
            }
        }



        //租赁物清单信息
        List<HlsCusLeaseItemList> hlsCusLeaseItemLists = hlsCusLeaseItemManage.getHlsCusLeaseItemList();
        if (CollectionUtils.isNotEmpty(hlsCusLeaseItemLists)) {
            for (HlsCusLeaseItemList hlsCusLeaseItemList : hlsCusLeaseItemLists) {
                //判断是新增还是更新
                if (hlsCusLeaseItemList.getHlsLeaseItemListId() == null || hlsCusLeaseItemList.getHlsLeaseItemListId() == 0) {
                    hlsCusLeaseItemList.setLeaseItemId(hlsCusLeaseItem.getLeaseItemId());
                    hlsCusLeaseItemListService.insertSelective(iRequest, hlsCusLeaseItemList);
                } else {
                    hlsCusLeaseItemListService.updateByPrimaryKeySelective(iRequest, hlsCusLeaseItemList);
                }
            }
        }

        return hlsCusLeaseItemManage;
    }

    /**
     * @param iRequest
     * @param hlsCusLeaseItem
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public List<HlsCusLeaseItem> selectPamLeaseItem(IRequest iRequest, HlsCusLeaseItem hlsCusLeaseItem, Integer page, Integer pageSize) {

        if (page != null && pageSize != null) {
            PageHelper.startPage(page, pageSize);
        }
        return hlsCusLeaseItemMapper.selectPamLeaseItem(hlsCusLeaseItem);

    }

    /**
     * 租赁物跳转详细页面查询
     *
     * @param iRequest
     * @param hlsCusLeaseItem
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public List<HlsCusLeaseItem> selectModelByCondition(IRequest iRequest, HlsCusLeaseItem hlsCusLeaseItem, Integer page, Integer pageSize) {
        if (page != null && pageSize != 0) {
            PageHelper.startPage(page, pageSize);
        }
        return hlsCusLeaseItemMapper.selectModelByCondition(hlsCusLeaseItem);
    }

    /**
     * 抵质押物作废
     *
     * @param iRequest
     * @return
     */

    @Override
    public List<HlsCusLeaseItem> invalidLeaseItem(IRequest iRequest, List<HlsCusLeaseItem> hlsCusLeaseItems) throws ResMessageException {
        List<HlsCusLeaseItem> list = new ArrayList<>();
        for (int i = 0; i < hlsCusLeaseItems.size(); i++) {
            HlsCusPrjProjectPledge hlsCusPrjProjectPledge = new HlsCusPrjProjectPledge();
            hlsCusPrjProjectPledge.setLeaseItemId(hlsCusLeaseItems.get(i).getLeaseItemId());
            List<HlsCusPrjProjectPledge> prjProjectPledges = hlsCusPrjProjectPledgeMapper.select(hlsCusPrjProjectPledge);
            if (prjProjectPledges.size() > 0) {
                throw new ResMessageException("抵押物已被抵押，无法作废！");
            } else {
                HlsCusLeaseItem leaseItem = new HlsCusLeaseItem();
                leaseItem.setLeaseItemId(hlsCusLeaseItems.get(i).getLeaseItemId());
                leaseItem.setEnabledFlag("N");
                leaseItem = self().updateByPrimaryKeySelective(iRequest, leaseItem);
                list.add(leaseItem);
            }
        }
        return list;
    }

    /**
     * 租赁物作废
     *
     * @param iRequest
     * @return
     */

    @Override
    public List<HlsCusLeaseItem> invalidLeaseItemRental(IRequest iRequest, List<HlsCusLeaseItem> hlsCusLeaseItems) throws ResMessageException {
        List<HlsCusLeaseItem> list = new ArrayList<>();
        for (int i = 0; i < hlsCusLeaseItems.size(); i++) {
            HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = new HlsCusPrjProjectLeaseItem();
            hlsCusPrjProjectLeaseItem.setLeaseItemId(hlsCusLeaseItems.get(i).getLeaseItemId());
            List<HlsCusPrjProjectLeaseItem> prjProjectPledges = hlsCusPrjProjectLeaseItemMapper.select(hlsCusPrjProjectLeaseItem);
            int count = i + 1;
            if (prjProjectPledges.size() > 0) {
                throw new ResMessageException("您选中的租赁物已关联项目 ，无法作废！");
            } else {
                HlsCusLeaseItem leaseItem = new HlsCusLeaseItem();
                leaseItem.setLeaseItemId(hlsCusLeaseItems.get(i).getLeaseItemId());
                leaseItem.setEnabledFlag("N");
                leaseItem = self().updateByPrimaryKeySelective(iRequest, leaseItem);
                list.add(leaseItem);
            }
        }
        return list;
    }


    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;

    //获取接口表数据
    public List<FndInterfaceLines> getInterfaceData(Long hdId, Long readLine) {

        FndInterfaceLines fndInterfaceLines = new FndInterfaceLines();
        fndInterfaceLines.setHeaderId(hdId);
        fndInterfaceLines.setReadLine(readLine);
        List<FndInterfaceLines> fndInterfaceLinesList = fndInterfaceLinesMapper.fndInterfaceLinesDetailQuery(fndInterfaceLines);
        return fndInterfaceLinesList;
    }

    // 校验字段是否有值
    public static void validate(String message, Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null || "".equals(objects[i]) || "null".equals(objects[i])) {
                throw new RuntimeException(message);
            }
        }
    }

    @Override
    public void receiptImportPledgeDc(IRequest iRequest, Long hdId, Long leaseItemId, String patternDet) throws ExcelException, SQLException, ParseException {
        List<FndInterfaceLines> fndInterfaceLinesList = getInterfaceData(hdId, 2L);

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        for (FndInterfaceLines fndInterfaceLine : fndInterfaceLinesList) {
            HlsCusLeaseItem record = new HlsCusLeaseItem();

            if(patternDet.equals("MOVABLE_PROPERTY_PLEDGE")){
                //序号
                String seq = fndInterfaceLine.getAttributes_1();
                //抵押物名称
                String fullName = fndInterfaceLine.getAttributes_2();
                // 抵押人
                String mortgagor = fndInterfaceLine.getAttributes_3();
                // 数量
                String quantity = fndInterfaceLine.getAttributes_4();
                // 计量单位
                String uom = fndInterfaceLine.getAttributes_5();
                // 单价
                String price = fndInterfaceLine.getAttributes_6();
                // 原购置价
                String originalAssetValue = fndInterfaceLine.getAttributes_7();
                // 市场价值/评估价值
                String assetValue = fndInterfaceLine.getAttributes_8();
                // 规格型号
                String specification = fndInterfaceLine.getAttributes_9();
                // 供应商
                String venderName = fndInterfaceLine.getAttributes_10();
                // 存放位置
                String installationSite = fndInterfaceLine.getAttributes_11();

                // 登记机关
                String registrationAgency = fndInterfaceLine.getAttributes_12();
                // 登记编号
                String internationalRegistrationMsn = fndInterfaceLine.getAttributes_13();


                // 登记时间
                Date mortgageDateFrom = null;
                if(fndInterfaceLine.getAttributes_14()!=null){
                    mortgageDateFrom = df.parse(fndInterfaceLine.getAttributes_14());
                }
                // 登记到期日
                Date mortgageDateTo = null;
                if(fndInterfaceLine.getAttributes_15()!=null){
                    // 登记到期日
                    mortgageDateTo = df.parse(fndInterfaceLine.getAttributes_15());
                }

                // 备注
                String description = fndInterfaceLine.getAttributes_16();

                //必填校验
//            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:租赁物名称不能为空", assetName);
//            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:单价不能为空", price);
//            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:数量不能为空", quantity);
//
//            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:规格不能为空", specification);
//            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:生产厂商不能为空", manufacturer);
//            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:序列/识别号不能为空", assetNum);
//            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:坐落位置不能为空", installationSite);


                record.setLeaseItemDetId(leaseItemId);
                record.setPatternDet(patternDet);

                record.setFullName(fullName);
                record.setPrice(Double.valueOf(price));
                record.setQuantity(Long.valueOf(quantity));
                record.setSpecification(specification);
                record.setMortgagor(mortgagor);
                record.setUom(uom);
                record.setOriginalAssetValue(Double.valueOf(originalAssetValue));
                record.setAssetValue(Double.valueOf(assetValue));
                record.setInstallationSite(installationSite);
                record.setDescription(description);
                record.setVenderName(venderName);
                record.setRegistrationAgency(registrationAgency);
                record.setInternationalRegistrationMsn(internationalRegistrationMsn);
                record.setMortgageDateFrom(mortgageDateFrom);
                record.setMortgageDateTo(mortgageDateTo);

            }else if(patternDet.equals("NO_MOVABLE_PROPERTY_PLEDGE")){

                //序号
                String seq = fndInterfaceLine.getAttributes_1();
                //抵押物名称
                String fullName = fndInterfaceLine.getAttributes_2();
                // 抵押人
                String mortgagor = fndInterfaceLine.getAttributes_3();
                // 坐落位置
                String installationSite = fndInterfaceLine.getAttributes_4();
                // 面积（㎡）
                String floorArea = fndInterfaceLine.getAttributes_5();
                // 原购置价
                String originalAssetValue = fndInterfaceLine.getAttributes_6();
                // 市场价值/评估价值
                String assetValue = fndInterfaceLine.getAttributes_7();
                // 不动产权证号
                String equityNumber = fndInterfaceLine.getAttributes_8();


                // 登记机关
                String registrationAgency = fndInterfaceLine.getAttributes_9();
                // 登记编号
                String internationalRegistrationMsn = fndInterfaceLine.getAttributes_10();


                // 登记时间
                Date mortgageDateFrom = null;
                if(fndInterfaceLine.getAttributes_11()!=null){
                    mortgageDateFrom = df.parse(fndInterfaceLine.getAttributes_11());
                }
                // 登记到期日
                Date mortgageDateTo = null;
                if(fndInterfaceLine.getAttributes_12()!=null){
                    // 登记到期日
                    mortgageDateTo = df.parse(fndInterfaceLine.getAttributes_12());
                }

                // 备注
                String description = fndInterfaceLine.getAttributes_13();

                record.setLeaseItemDetId(leaseItemId);
                record.setPatternDet(patternDet);

                record.setFullName(fullName);
                record.setMortgagor(mortgagor);
                record.setInstallationSite(installationSite);
                if(floorArea!=null){
                    record.setFloorArea(Double.valueOf(floorArea));
                }

                if(originalAssetValue!=null){
                    record.setOriginalAssetValue(Double.valueOf(originalAssetValue));
                }

                if(assetValue!=null){
                    record.setAssetValue(Double.valueOf(assetValue));
                }



                record.setEquityNumber(equityNumber);
                record.setRegistrationAgency(registrationAgency);
                record.setInternationalRegistrationMsn(internationalRegistrationMsn);
                record.setMortgageDateFrom(mortgageDateFrom);
                record.setMortgageDateTo(mortgageDateTo);
                record.setDescription(description);

            }else if(patternDet.equals("DZ_PROPERTY_PLEDGE")){
//序号
                String seq = fndInterfaceLine.getAttributes_1();
                // 出质人
                String mortgagor = fndInterfaceLine.getAttributes_2();
                // 标的方
                String pledgeContractNum = fndInterfaceLine.getAttributes_3();
                // 质押财产价值
                String pledgeAssetValue = fndInterfaceLine.getAttributes_4();
                // 市场价值/评估价值
                String assetValue = fndInterfaceLine.getAttributes_5();
                // 质押财产描述
                String pledgeAssetDec = fndInterfaceLine.getAttributes_6();

                // 登记机关
                String registrationAgency = fndInterfaceLine.getAttributes_7();
                // 登记编号
                String internationalRegistrationMsn = fndInterfaceLine.getAttributes_8();


                // 登记时间
                Date mortgageDateFrom = null;
                if(fndInterfaceLine.getAttributes_9()!=null){
                    mortgageDateFrom = df.parse(fndInterfaceLine.getAttributes_9());
                }
                // 登记到期日
                Date mortgageDateTo = null;
                if(fndInterfaceLine.getAttributes_10()!=null){
                    // 登记到期日
                    mortgageDateTo = df.parse(fndInterfaceLine.getAttributes_10());
                }

                // 备注
                String description = fndInterfaceLine.getAttributes_11();

                record.setLeaseItemDetId(leaseItemId);
                record.setPatternDet(patternDet);

                record.setPledgeContractNum(pledgeContractNum);
                if(pledgeAssetValue!=null){
                    record.setPledgeAssetValue(Long.valueOf(pledgeAssetValue));
                }

                if(pledgeAssetDec!=null){
                    record.setPledgeAssetDec(Long.valueOf(pledgeAssetDec));
                }

                if(assetValue!=null){
                    record.setAssetValue(Double.valueOf(assetValue));
                }

                record.setMortgagor(mortgagor);
                record.setRegistrationAgency(registrationAgency);
                record.setInternationalRegistrationMsn(internationalRegistrationMsn);
                record.setMortgageDateFrom(mortgageDateFrom);
                record.setMortgageDateTo(mortgageDateTo);
                record.setDescription(description);
            }




            //赋值 插入
            //record.setSeq(Long.valueOf(seq));

            //插入
            self().insertSelective(iRequest, record);
        }

    }

    @Override
    public String generateAuthorityString(IRequest iRequest) {
        Long companyId = iRequest.getCompanyId();
        HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest().getSession();
        FndCompany company = fndCompanyMapper.selectByPrimaryKey(companyId);
        String unitCode = (String) session.getAttribute("unitCode");
        String positionCode = (String) session.getAttribute("positionCode");
        String empCode = iRequest.getEmployeeCode();
       String authorityString = '"' + company.getCompanyCode() + '"' + "." + '"' + unitCode + '"' + "." + '"' + positionCode + '"' + "." + '"' + empCode + '"';
        //String authorityString = '"' + company.getCompanyCode() + '"' + "." + '"' + unitCode + '"' + "." + '"' +  positionCode + '"' + "." + '"' + '"' + "." + '"' + '"' + "." + '"'  + positionCode + '"' + "." + '"'+  empCode + '"';
        return authorityString;
    }


}
