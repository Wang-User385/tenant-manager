package com.hand.hls.abs.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.hand.hap.cache.impl.SysCodeCache;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.dto.Code;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.service.ICodeService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.HlsCusAbsProFeeInfo;
import com.hand.hls.abs.dto.HlsCusAbsProductOrganization;
import com.hand.hls.abs.dto.HlsCusAbsProjectOrganization;
import com.hand.hls.abs.mapper.HlsCusAbsProFeeInfoMapper;
import com.hand.hls.abs.mapper.HlsCusAbsProductOrganizationMapper;
import com.hand.hls.abs.mapper.HlsCusAbsProjectOrganizationMapper;
import com.hand.hls.abs.service.HlsCusAbsProFeeInfoService;
import com.hand.hls.eft.dto.HlsCusFundTransferList;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.service.HlsCusImpDataService;
import com.hand.hls.fnd.utils.HlsCusImportDataUtil;
import com.hand.hls.utils.ExportExcelUtil;
import com.hand.hls.utils.HlsCusConstant;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsProFeeInfoServiceImpl extends BaseServiceImpl<HlsCusAbsProFeeInfo> implements HlsCusAbsProFeeInfoService {


    @Autowired
    private HlsCusAbsProFeeInfoMapper projectFeeInfoMapper;

    @Autowired
    private ICodeService codeService;

    @Autowired
    private HlsCusImpDataService impDataService;

    @Autowired
    private HlsCusAbsProjectOrganizationMapper absProjectOrganizationMapper;

    @Autowired
    private HlsCusAbsProductOrganizationMapper absProductOrganizationMapper;

    @Autowired
    private SysCodeCache codeCache;


    @Override
    public List<HlsCusAbsProFeeInfo> selectProjectFeeInfo(IRequest iRequest, HlsCusAbsProFeeInfo projectFeeInfo, int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        return projectFeeInfoMapper.selectProjectFeeInfo(projectFeeInfo);
    }

    @Override
    public int updateFeeOrganizationId(Long productId, Long sourceOrganizationId, Long targetOrganizationId) {
        return projectFeeInfoMapper.updateFeeOrganizationId(productId,sourceOrganizationId,targetOrganizationId);
    }


    @Override
    public int updateProductFeeAmount(Long productId) {
        return projectFeeInfoMapper.updateProductFeeAmount(productId);
    }

    @Override
    protected boolean useSelectiveUpdate() {
        return false;
    }


    @Override
    public void confirmAbsProFee(IRequest iRequest, HlsCusFundTransferList fundTransferList) throws HlsCusException {
        HlsCusAbsProFeeInfo proFeeInfo = projectFeeInfoMapper.selectByPrimaryKey(fundTransferList.getSourceDocLineId());
        if(proFeeInfo.getWriteOffAmount()==null){
            proFeeInfo.setWriteOffAmount(BigDecimal.ZERO);
        }
        if(proFeeInfo.getCnyWriteOffAmount()==null){
            proFeeInfo.setCnyWriteOffAmount(BigDecimal.ZERO);
        }
        proFeeInfo.setWriteOffAmount(proFeeInfo.getWriteOffAmount().add(fundTransferList.getActualPayAmount()));
        proFeeInfo.setCnyWriteOffAmount(proFeeInfo.getCnyWriteOffAmount().add(fundTransferList.getCnyActualPayAmount()));
        if (proFeeInfo.getWriteOffAmount().compareTo( proFeeInfo.getFeeAmount())==1) {
            throw new HlsCusException("还款金额不能大于计划还款金额");
        }
        projectFeeInfoMapper.updateByPrimaryKeySelective(proFeeInfo);
    }


    @Override
    public void exportAbsProFeeInfo(HttpServletRequest request, HttpServletResponse response, HlsCusAbsProFeeInfo absProFeeInfo) throws IOException, InvocationTargetException, IllegalAccessException {
        List<String> colNameList = null;
        List<String> colGetMethods=null;
        if("Y".equals(absProFeeInfo.getCalculateExportFlag())) {
            colNameList = Lists.newArrayList("机构名称", "费用名称","是否参与计算", "费率","期数", "预定支付日期", "币种",
                    "原币金额(元)", "锁定汇率", "本币金额(元)","附加税税率","分摊类型", "备注");
            colGetMethods = Lists.newArrayList("organizationName","feeName","calculateFlag","feeRateDesc","times","planPayDate","currencyCode",
                    "feeAmount","exchangeRate","cnyFeeAmount","addTaxRateStr","shareType","description");
        }else{
            colNameList = Lists.newArrayList("机构名称", "费用名称", "期数", "预定支付日期", "币种",
                    "原币金额(元)", "锁定汇率", "本币金额(元)", "分摊类型", "备注");
            colGetMethods = Lists.newArrayList("organizationName","feeName","times","planPayDate","currencyCode",
                    "feeAmount","exchangeRate","cnyFeeAmount","shareType","description");
        }

        XSSFWorkbook xwork = new XSSFWorkbook();
        XSSFSheet sheet = xwork.createSheet("sheet1");
        int dataRowNum = ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
        List<HlsCusAbsProFeeInfo> absProFeeInfos = projectFeeInfoMapper.selectProjectFeeInfo(absProFeeInfo);
        for (HlsCusAbsProFeeInfo feeInfo : absProFeeInfos) {
            XSSFRow row = sheet.createRow(dataRowNum++);
            if(feeInfo.getShareType()!=null) {
                feeInfo.setShareType(codeService.getCodeMeaningByValue(RequestHelper.getCurrentRequest(), "BI.COST_SHARING_TYPE", feeInfo.getShareType()));
            }
            if("Y".equals(feeInfo.getCalculateFlag())){
                feeInfo.setCalculateFlag("是");
            }else{
                feeInfo.setCalculateFlag("否");
            }
            feeInfo.setCurrencyCode(codeService.getCodeMeaningByValue(RequestHelper.getCurrentRequest(),"FCT.ACCOUNT_CURRENCY",feeInfo.getCurrencyCode()));
            feeInfo.setFeeName(codeService.getCodeMeaningByValue(RequestHelper.getCurrentRequest(),"BI.COST_NAME",feeInfo.getFeeName()));
           if(feeInfo.getFeeRate()!=null) {
               feeInfo.setFeeRateDesc((feeInfo.getFeeRate().multiply(new BigDecimal(100))) + "%");
           }
           if(feeInfo.getAddTaxRate()!=null) {
                feeInfo.setAddTaxRateStr((feeInfo.getAddTaxRate().multiply(new BigDecimal(100))) + "%");
           }
           ExportExcelUtil.setData(xwork, sheet, row, feeInfo, colGetMethods);
        }
        ExportExcelUtil.IOWrite(xwork, null, request, response, "费用信息");
    }

    /**
     * 立项费用导入
     * @param iRequest
     * @param dataMap 临时表数据
     * @param descMap 获取字段描述用Map,<key=Dto属性名,value=模板中定义的显示用名>
     * @param lang 当导入涉及到多语言时（在模板定义时勾选了多语言选项）这里会传入一个在执行导入时勾选的多语言对应的Code
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    @Override
    public int importData(IRequest iRequest, List<Map<String, String>> dataMap, Map<String, String> descMap, String lang) {
        int errorCount = 0;
        try {
            //传递时间格式
            List<CodeValue> dateFormat = codeService.selectCodeValuesByCodeName(iRequest, "FND_IMP_DATE_FORMAT");
            List<String> formats = new ArrayList<>();
            for (CodeValue c : dateFormat) {
                formats.add(c.getValue());
            }
            List<Object> objects = HlsCusImportDataUtil.dataToDto(HlsCusAbsProFeeInfo.class, dataMap, formats);

            Long proId = Long.parseLong(dataMap.get(0).get("tempKey"));
            Boolean productFlag=false;
            if("ABS_PRODUCT".equals(dataMap.get(0).get("tempParam"))){
                productFlag=true;
            }
            for (int i = 0; i < objects.size(); i++) {
                StringBuilder message = new StringBuilder();
                HlsCusAbsProFeeInfo absProFeeInfo = (HlsCusAbsProFeeInfo) objects.get(i);
                absProFeeInfo.setSourceKey(proId);
                absProFeeInfo.setCalculateFlag("N");
                if(productFlag) {
                    absProFeeInfo.setSourceType(HlsCusConstant.ABS_PRO_TYPE.PRODUCT);
                    HlsCusAbsProductOrganization productOrganization = new HlsCusAbsProductOrganization();
                    productOrganization.setProductId(proId);
                    productOrganization.setOrganizationName(absProFeeInfo.getOrganizationName());
                    List<HlsCusAbsProductOrganization> productOrganizations = absProductOrganizationMapper.select(productOrganization);
                    if(productOrganizations.size()>0){
                        absProFeeInfo.setOrganizationId(productOrganizations.get(0).getOrganizationId());
                    }else{
                        message.append("[机构名称不存在]");
                    }
                }else{
                    absProFeeInfo.setSourceType(HlsCusConstant.ABS_PRO_TYPE.PROJECT);
                    HlsCusAbsProjectOrganization projectOrganization = new HlsCusAbsProjectOrganization();
                    projectOrganization.setProjectId(proId);
                    projectOrganization.setOrganizationName(absProFeeInfo.getOrganizationName());
                    List<HlsCusAbsProjectOrganization> projectOrganizations = absProjectOrganizationMapper.queryDetail(projectOrganization);
                    if(projectOrganizations.size()>0){
                        absProFeeInfo.setOrganizationId(projectOrganizations.get(0).getOrganizationId());
                    }else{
                        message.append("[机构名称不存在]");
                    }
                }
                CodeValue feeCodeValue=getCodeValueMeaning(iRequest, "BI.COST_NAME",absProFeeInfo.getFeeName());
                if(feeCodeValue==null){
                    message.append("[费用名称不存在]");
                }else{
                    absProFeeInfo.setFeeName(feeCodeValue.getValue());
                    absProFeeInfo.setCfType(Long.parseLong(feeCodeValue.getDescription()));
                    absProFeeInfo.setCfItem(Long.parseLong(feeCodeValue.getTag()));
                }
                CodeValue currencyCodeValue=getCodeValueMeaning(iRequest, "FCT.ACCOUNT_CURRENCY",absProFeeInfo.getCurrencyCode());
                if(currencyCodeValue==null){
                    message.append("[币种不存在]");
                }else{
                    absProFeeInfo.setCurrencyCode(currencyCodeValue.getValue());
                   if("CNY".equals(absProFeeInfo.getCurrencyCode())){
                       absProFeeInfo.setExchangeRate(BigDecimal.ONE);
                   }
                }
                if(absProFeeInfo.getShareType()!=null){
                    CodeValue shareCodeValue=getCodeValueMeaning(iRequest, "BI.COST_SHARING_TYPE",absProFeeInfo.getShareType());
                    if(shareCodeValue==null){
                        message.append("[分摊类型不存在]");
                    }else{
                        absProFeeInfo.setShareType(shareCodeValue.getValue());
                    }
                }
                absProFeeInfo.setCreatedBy(iRequest.getUserId());
                absProFeeInfo.setLastUpdatedBy(iRequest.getUserId());
                absProFeeInfo.setLastUpdateLogin(iRequest.getUserId());
                if (StringUtils.isNotBlank(message.toString())) {
                    errorCount++;
                    impDataService.updateErrMessage(dataMap, i, message.toString());
                }else{
                    projectFeeInfoMapper.insertSelective(absProFeeInfo);
                }
            }

            //有一个出错 全部回滚
            if (errorCount > 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return dataMap.size() - errorCount;
            }

        }catch (Exception e){
            e.printStackTrace();
            for (Map<String, String> m : dataMap) {
                impDataService.updateErrMessage(m, e.getClass() + ":" + e.getMessage());
            }
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }

        return dataMap.size() - errorCount;
    }



    private CodeValue getCodeValueMeaning(IRequest request, String codeName, String meaing) {
        Code code = this.codeCache.getValue(codeName + "." + request.getLocale());
        if (code == null) {
            return null;
        } else if (code.getCodeValues() == null) {
            return null;
        } else {
            Iterator var5 = code.getCodeValues().iterator();
            CodeValue v;
            do {
                if (!var5.hasNext()) {
                    return null;
                }
                v = (CodeValue)var5.next();
            } while(!meaing.equals(v.getMeaning()));

            return v;
        }
    }
}
