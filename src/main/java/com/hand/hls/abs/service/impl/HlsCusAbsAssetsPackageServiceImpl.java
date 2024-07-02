package com.hand.hls.abs.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.HlsCusAbsAssetsPackage;
import com.hand.hls.abs.mapper.HlsCusAbsAssetsPackageMapper;
import com.hand.hls.abs.service.HlsCusAbsAssetsPackageService;
import com.hand.hls.utils.ExportExcelUtil;
import hls.core.utils.exception.HlsCusException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsAssetsPackageServiceImpl extends BaseServiceImpl<HlsCusAbsAssetsPackage> implements HlsCusAbsAssetsPackageService {

    @Autowired
    private HlsCusAbsAssetsPackageMapper assetsPackageMapper;

    @Override
    public List<HlsCusAbsAssetsPackage> selectAssetsPackage(IRequest iRequest, HlsCusAbsAssetsPackage absAssetsPackage, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return assetsPackageMapper.selectAssetsPackage(absAssetsPackage);
    }


    @Override
    public List<HlsCusAbsAssetsPackage> selectPackageContract(IRequest iRequest, HlsCusAbsAssetsPackage absAssetsPackage, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return assetsPackageMapper.selectPackageContract(absAssetsPackage);
    }


    @Override
    public List<HlsCusAbsAssetsPackage> selectPackageConCashFlow(IRequest iRequest, HlsCusAbsAssetsPackage absAssetsPackage, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return assetsPackageMapper.selectPackageConCashFlow(absAssetsPackage);
    }

    @Override
    public List<HlsCusAbsAssetsPackage> selectBetweenTimesCashflow(IRequest iRequest, HlsCusAbsAssetsPackage absAssetsPackage) {

        return assetsPackageMapper.selectBetweenTimesCashflow(absAssetsPackage);
    }

    @Override
    public List<HlsCusAbsAssetsPackage> selectPackageOccupyData(IRequest iRequest, Long packId) {

        return assetsPackageMapper.selectPackageOccupyData(packId);
    }

    @Override
    public List<HlsCusAbsAssetsPackage> selectPackContarctCashFlow(IRequest iRequest, HlsCusAbsAssetsPackage absAssetsPackage, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return assetsPackageMapper.selectPackContarctCashFlow(absAssetsPackage);
    }

    @Override
    public void exportCashflow(HttpServletRequest request, HttpServletResponse response, HlsCusAbsAssetsPackage absAssetsPackage) throws IOException, InvocationTargetException, IllegalAccessException {
        List<HlsCusAbsAssetsPackage> absAssetsPackages = assetsPackageMapper.selectPackageConCashFlow(absAssetsPackage);
        XSSFWorkbook xwork = new XSSFWorkbook();
        XSSFSheet sheet = xwork.createSheet("sheet1");
        final List<String> colNameList = Lists.newArrayList(
                "期数","应收日期","应收金额","其中本金","其中利息","已收金额","未收金额");
        final List<String> colGetMethods = Lists.newArrayList(
                "times","dueDate", "dueAmount", "principal", "interest","receivedAmount","surplusAmount");
        int dataRowNum = ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
        for(HlsCusAbsAssetsPackage assetsPackage:absAssetsPackages){
            //创建列
            XSSFRow row = sheet.createRow(dataRowNum++);
            ExportExcelUtil.setData(xwork,sheet, row, assetsPackage,colGetMethods);
        }
        ExportExcelUtil.IOWrite(xwork, null,request,response, "现金流明细");
    }

    @Override
    public void exportCashFlowBetweenDate(HttpServletRequest request, HttpServletResponse response, HlsCusAbsAssetsPackage absAssetsPackage) throws IOException, InvocationTargetException, IllegalAccessException {
        List<HlsCusAbsAssetsPackage> absAssetsPackages = assetsPackageMapper.selectPackContarctCashFlow(absAssetsPackage);
        XSSFWorkbook xwork = new XSSFWorkbook();
        XSSFSheet sheet = xwork.createSheet("sheet1");
        final List<String> colNameList = Lists.newArrayList(
                "业务合同编号","承租人1/原债权人","承租人2/债务人","期数","应收日期","租金","其中本金","其中利息");
        final List<String> colGetMethods = Lists.newArrayList(
                "approvalNumber","salBpName","buyBpName","times","dueDate", "dueAmount", "principal", "interest");
        int dataRowNum =ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
        for(HlsCusAbsAssetsPackage assetsPackage:absAssetsPackages){
            //创建列
            XSSFRow row = sheet.createRow(dataRowNum++);
            ExportExcelUtil.setData(xwork,sheet, row, assetsPackage,colGetMethods);
        }
        ExportExcelUtil.IOWrite(xwork, null,request,response, "归集现金流明细");
    }

    /**
     * 资产包.资产明细 导出
     *
     * @param request request
     * @param response 响应
     * @param absAssetsPackage 参数
     */
    @Override
    public void exportAssetDetails(HttpServletRequest request, HttpServletResponse response, HlsCusAbsAssetsPackage absAssetsPackage) throws InvocationTargetException, IllegalAccessException, IOException {
        Assert.notNull(absAssetsPackage,"参数信息无效!");
        if (null!=absAssetsPackage.getPackId()){
            String fileName="资产明细";
            XSSFWorkbook xwork = new XSSFWorkbook();
            XSSFSheet sheet = xwork.createSheet("sheet1");
            List <HlsCusAbsAssetsPackage> exportData=new ArrayList<>(64);
            //资产明细信息
            List<HlsCusAbsAssetsPackage> list=assetsPackageMapper.selectAssetsPackage(absAssetsPackage);
            if (!list.isEmpty())
            {
                for (HlsCusAbsAssetsPackage hlsCusAbsAssetsPackage : list) {
                    List<HlsCusAbsAssetsPackage> cashFlows=assetsPackageMapper.selectPackageConCashFlow(hlsCusAbsAssetsPackage);
                    cashFlows.forEach(cashFlow->{
                        cashFlow.setSalBpName(hlsCusAbsAssetsPackage.getSalBpName());
                        cashFlow.setBuyBpName(hlsCusAbsAssetsPackage.getBuyBpName());
                        cashFlow.setApprovalNumber(hlsCusAbsAssetsPackage.getApprovalNumber());
                        exportData.add(cashFlow);
                    });
                }
            }
            final List<String> colNameList = Lists.newArrayList(
                    "业务合同编号","承租人1/原债权人","承租人2/债务人","期数","应收日期","应收金额","其中本金","其中利息","已收金额","未收金额");
            final List<String> colGetMethods = Lists.newArrayList(
                    "approvalNumber","salBpName","buyBpName","times","dueDate", "dueAmount", "principal", "interest","receivedAmount","surplusAmount");
            int dataRowNum =ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
            for(HlsCusAbsAssetsPackage assetsPackage:exportData){
                //创建列
                XSSFRow row = sheet.createRow(dataRowNum++);
                ExportExcelUtil.setData(xwork,sheet, row, assetsPackage,colGetMethods);
            }
            if (StringUtils.isNotEmpty((String)request.getAttribute("fileName"))){
                fileName= (String) request.getAttribute("fileName");
            }
            ExportExcelUtil.IOWrite(xwork, null,request,response,fileName);
        }
        else{
            throw new IllegalArgumentException("参数信息必填");
        }
    }

    /**
     * @param requestContext
     * @param dto            检验的数据
     * @return <p>ResponseData</p>
     */
    @Override
    public ResponseData checkAssetsPackage(IRequest requestContext, List<HlsCusAbsAssetsPackage> dto) {
        ResponseData responseData=new ResponseData();
        if (CollectionUtils.isNotEmpty(dto))
        {
            String dueDateStr= dto.get(0).getAttribute1();
            StringBuilder errorMessage=new StringBuilder();
            dto.forEach(pack->{
                if ("CON_CONTRACT".equalsIgnoreCase(pack.getConDocumentCategory())){
                    checkDate(errorMessage,"CON_CONTRACT_CASHFLOW",pack,dueDateStr);
                }
                else if ("FCT_CONTRACT".equalsIgnoreCase(pack.getConDocumentCategory())){
                    checkDate(errorMessage,"FCT_QUOTATION_CASHFLOW",pack,dueDateStr);
                }
            });
            if (StringUtils.isNotEmpty(errorMessage.toString()))
            {
                responseData.setSuccess(false);
                responseData.setMessage(errorMessage.toString());
            }
        }
        return responseData;
    }
    private void checkDate(StringBuilder errorMessage,String tableName,HlsCusAbsAssetsPackage pack,String dueDateStr){
        if (assetsPackageMapper.selectTimesDateCount(pack,tableName,dueDateStr)>0)
        {
            errorMessage.append("合同编号为:[").append(pack.getContractNumber()).append("]的数据期数日期在封包日之前<br/>");
        }
    }

    /**
     * @param requestContext
     * @param dto            检验的数据
     * @return <p>ResponseData</p>
     */
    @Override
    public ResponseData checkAssetsPackagePackDate(IRequest requestContext, List<HlsCusAbsAssetsPackage> dto) {
        ResponseData responseData=new ResponseData();
        if (CollectionUtils.isNotEmpty(dto)){
            String errorMessage = "";
            for(HlsCusAbsAssetsPackage pack:dto){
                if (assetsPackageMapper.selectPackDateCount(pack)>0){
                    errorMessage = pack.getContractNumber() + "打包日期从到打包日期至之间存在已经被其他资产包/融资合同占用的现金流";
                }if(pack.getPackDateFrom().getTime() > pack.getPackDateTo().getTime()){
                    errorMessage = pack.getContractNumber() + "打包日期至不能早于打包日期从";
                }else{
                    HlsCusAbsAssetsPackage result = assetsPackageMapper.selectCashflowInfo(pack);
                    pack.setOverdueStatusN(result.getOverdueStatusN());
                }
            }

            if (StringUtils.isNotEmpty(errorMessage)){
                responseData.setSuccess(false);
                responseData.setMessage(errorMessage);
            }else{
                responseData.setSuccess(true);
                responseData.setRows(dto);
            }
        }
        return responseData;
    }

    @Override
    public void checkAssetsPackagePackDateNew(IRequest requestContext, HlsCusAbsAssetsPackage hlsCusAbsAssetsPackage) throws HlsCusException{
        if(hlsCusAbsAssetsPackage.getPackDateFrom() == null){
            throw new HlsCusException(hlsCusAbsAssetsPackage.getContractNumber()+"打包日期从不能为空");
        }else if(hlsCusAbsAssetsPackage.getPackDateTo() == null){
            throw new HlsCusException(hlsCusAbsAssetsPackage.getContractNumber()+"打包日期至不能为空");
        }else if(assetsPackageMapper.selectPackDateCount(hlsCusAbsAssetsPackage)>0){
            throw new HlsCusException(hlsCusAbsAssetsPackage.getContractNumber()+"打包日期从到打包日期至之间存在已经被其他资产包/融资合同占用的现金流");
        }else if(hlsCusAbsAssetsPackage.getPackDateFrom().getTime() > hlsCusAbsAssetsPackage.getPackDateTo().getTime()){
            throw new HlsCusException(hlsCusAbsAssetsPackage.getContractNumber()+"打包日期至不能早于打包日期从");
        }
    }
}
