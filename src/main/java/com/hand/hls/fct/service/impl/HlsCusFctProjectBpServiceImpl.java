package com.hand.hls.fct.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fct.dto.*;
import com.hand.hls.fct.mapper.HlsCusFctProjectBpMapper;
import com.hand.hls.fct.mapper.HlsCusFctProjectMapper;
import com.hand.hls.fct.service.*;
import com.hand.hls.prj.dto.HlsCusPrjProjectBp;
import com.hand.hls.prj.service.HlsCusPrjProjectBpService;
import com.hand.hls.utils.ExportExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;





@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusFctProjectBpServiceImpl extends BaseServiceImpl<HlsCusFctProjectBp> implements HlsCusFctProjectBpService {

    @Autowired
    private HlsCusFctProjectBpMapper mapper;
    @Autowired
    private HlsCusFctProjectBpService service;
    @Autowired
    private HlsCusFctBpFinancingSituationService financingSituationService;
    @Autowired
    private HlsCusFctBpLiabilitiesService liabilitiesService;
    @Autowired
    private HlsCusFctBpZdwSituationService zdwSituationService;
    @Autowired
    private HlsCusFctProjectService hlsCusFctProjectService;
    @Autowired
    private HlsCusFctProjectMapper fctProjectMapper;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
     private HlsCusPrjProjectBpService bpService;
    @Override
    public List<HlsCusFctProjectBp> fctProjectBpQuery(HlsCusFctProjectBp hlsCusFctProjectBp, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.fctProjectBpQuery2(hlsCusFctProjectBp);
    }

    @Override
    public List<HlsCusFctProjectBp> selectSentBpName(HlsCusFctProjectBp hlsCusFctProjectBp, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.selectSentBpName(hlsCusFctProjectBp);
    }

    @Override
    public List<HlsCusFctProjectBp> fctProjectBpInfoQuery(HlsCusFctProjectBp hlsCusFctProjectBp, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.fctProjectBpInfoQuery(hlsCusFctProjectBp);
    }

    @Override
    public List<HlsCusFctProjectBp> deleteCreditor(HlsCusFctProjectBp dto, IRequest requestCtx) {
        Long chanceId = dto.getChanceId();
        /*授信方bpId*/
        Long bpId = dto.getBpId();
        HlsCusFctProjectBp hlsCusFctProjectBp = new HlsCusFctProjectBp();
        hlsCusFctProjectBp.setChanceId(chanceId);
        hlsCusFctProjectBp.setBpId(bpId);
        hlsCusFctProjectBp.setCreditGrantorParty("Y");
        List<HlsCusFctProjectBp> hlsCusFctProjectBpList = mapper.fctProjectBpQuery2(hlsCusFctProjectBp);
        for (HlsCusFctProjectBp dt : hlsCusFctProjectBpList) {
            dt.set__status("delete");
        }
        service.batchDelete(hlsCusFctProjectBpList);
        /*删除融资情况等表数据*/
        HlsCusFctBpFinancingSituation hlsCusFctBpFinancingSituation = new HlsCusFctBpFinancingSituation();
        HlsCusFctBpLiabilities hlsCusFctBpLiabilities = new HlsCusFctBpLiabilities();
        HlsCusFctBpZdwSituation hlsCusFctBpZdwSituation = new HlsCusFctBpZdwSituation();
        hlsCusFctBpFinancingSituation.setBpId(bpId);
        hlsCusFctBpLiabilities.setBpId(bpId);
        hlsCusFctBpZdwSituation.setBpId(bpId);
        hlsCusFctBpFinancingSituation.setChanceId(chanceId);
        hlsCusFctBpLiabilities.setChanceId(chanceId);
        hlsCusFctBpZdwSituation.setChanceId(chanceId);
        List<HlsCusFctBpFinancingSituation> financingSituationList = financingSituationService.select(requestCtx, hlsCusFctBpFinancingSituation, 1, 99999);
        List<HlsCusFctBpLiabilities> liabilitiesList = liabilitiesService.select(requestCtx, hlsCusFctBpLiabilities, 1, 99999);
        List<HlsCusFctBpZdwSituation> zdwSituationList = zdwSituationService.select(requestCtx, hlsCusFctBpZdwSituation, 1, 99999);
        for (HlsCusFctBpFinancingSituation dt : financingSituationList) {
            dt.set__status("delete");
        }
        financingSituationService.batchDelete(financingSituationList);
        for (HlsCusFctBpLiabilities dt : liabilitiesList) {
            dt.set__status("delete");
        }
        liabilitiesService.batchDelete(liabilitiesList);
        for (HlsCusFctBpZdwSituation dt : zdwSituationList) {
            dt.set__status("delete");
        }
        zdwSituationService.batchDelete(zdwSituationList);
        return hlsCusFctProjectBpList;
    }

    /**
     * 根据项目主键【projectId】去查询卖方【SELLER】的银行账户信息的bank_account_id
     *
     * @param request
     * @param projectId
     */
    @Override
    public List<HlsCusFctProjectBp> selectSellerInfoByProjectId(IRequest request, Long projectId) {
        return mapper.selectSellerInfoByProjectId(projectId);
    }

    /**
     * 查询合同变更的 bp信息
     *
     * @param iRequest
     * @param dto
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public List<HlsCusFctProjectBp> fctProjectBpQueryBef(IRequest iRequest, HlsCusFctProjectBp dto, int page, int pageSize) {
        // change
        HlsCusFctProject changeProject = new HlsCusFctProject();
        changeProject.setProjectId(dto.getProjectId());
        changeProject = hlsCusFctProjectService.selectByPrimaryKey(iRequest, changeProject);
        // normal
        HlsCusFctProject normalProject = new HlsCusFctProject();
        normalProject.setProjectId(changeProject.getRefProjectId());
        normalProject = hlsCusFctProjectService.selectByPrimaryKey(iRequest, normalProject);
        dto.setProjectId(normalProject.getProjectId());
        // history
        HlsCusFctProject historyProject = new HlsCusFctProject();
        historyProject.setRefProjectId(normalProject.getProjectId());
        historyProject.setDataType("HISTORY");
        historyProject.setChangeReqId(changeProject.getChangeReqId());
        List<HlsCusFctProject> historyProjectList = hlsCusFctProjectService.select(iRequest, historyProject, 1, 99999);
        if(CollectionUtils.isEmpty(historyProjectList)){
            dto.setProjectId(normalProject.getProjectId());
        }else{
            dto.setProjectId(historyProjectList.get(0).getProjectId());
        }
        PageHelper.startPage(page, pageSize);
        return mapper.fctProjectBpQuery2(dto);
    }

    @Override
    public List<HlsCusFctProjectBp> selectBpMasterNotSave(IRequest request, Long projectId) {
        return mapper.selectBpMasterNotSave(projectId);
    }

    @Override
    public List<HlsCusFctProjectBp> fctProjectBpUpdate(IRequest requestCtx, List<HlsCusFctProjectBp> dto) {
        if (dto != null) {
            dto = createSerialNumber(dto.get(0).getProjectId(), dto, dto.get(0).getBpRoleType());
            for (HlsCusFctProjectBp dt : dto) {
                if (dt.getProjectBpId() == null || dt.getProjectBpId() == 0) {
                    dt.set__status("add");
                } else {
                    if ("delete".equalsIgnoreCase(dt.get__status())) {
                        dt.set__status("delete");
                    } else {
                        dt.set__status("update");
                    }
                }
            }
        }
        return this.batchUpdate(requestCtx, dto);
    }


    //创建流水号
    private static final String WARRANTOR = "WARRANTOR";//保证信息类型
    private static final String PLEDGOR = "PLEDGOR";//质押信息类型
    private static final String MORTGAGOR = "MORTGAGOR";//抵押信息类型

    /**
     * 创建保证、抵押、质押信息流水号
     * @param projectId
     * @param dtos
     * @param ROLETYPE 当值为null时会根据传入的bpRoleType创建流水号，当不为空时会将bpRoleType赋值为ROLETYPE去创建流水号
     * @return
     */
    @Override
    public List<HlsCusFctProjectBp> createSerialNumber(Long projectId, List<HlsCusFctProjectBp> dtos,String ROLETYPE) {
        if (dtos.size() > 0) {
            HlsCusFctProject fctProject = new HlsCusFctProject();
            fctProject.setProjectId(projectId);
            fctProject.setDataClass("VIRTUAL_CON");
            fctProject = fctProjectMapper.selectByPrimaryKey(fctProject);
            databaseLockProvider.lock(fctProject);
            for (HlsCusFctProjectBp dt : dtos) {
                if(StringUtils.isNotBlank(ROLETYPE)){
                    dt.setBpRoleType(ROLETYPE);
                }
                if (StringUtils.isBlank(dt.getSerialNumber()) && fctProject.getApprovalNumber() != null) {
                    StringBuffer sbf = new StringBuffer();
                    if(WARRANTOR.equalsIgnoreCase(dt.getBpRoleType())){
                        if (fctProject.getGuarantorSum()==null||fctProject.getGuarantorSum()==0){
                            fctProject.setGuarantorSum(1L);
                        }else {
                            fctProject.setGuarantorSum(fctProject.getGuarantorSum()+1L);
                        }
                        sbf.append("BZ-").append(fctProject.getApprovalNumber()).append("-").append(StringUtils.leftPad(String.valueOf(fctProject.getGuarantorSum()),4,"0"));
                    }else if(PLEDGOR.equalsIgnoreCase(dt.getBpRoleType())){
                        if (fctProject.getPledgorSum()==null||fctProject.getPledgorSum()==0){
                            fctProject.setPledgorSum(1L);
                        }else {
                            fctProject.setPledgorSum(fctProject.getPledgorSum()+1L);
                        }
                        sbf.append("ZY-").append(fctProject.getApprovalNumber()).append("-").append(StringUtils.leftPad(String.valueOf(fctProject.getPledgorSum()),4,"0"));
                    }else if(MORTGAGOR.equalsIgnoreCase(dt.getBpRoleType())){
                        if (fctProject.getMortgagorSum()==null||fctProject.getMortgagorSum()==0){
                            fctProject.setMortgagorSum(1L);
                        }else {
                            fctProject.setMortgagorSum(fctProject.getMortgagorSum()+1L);
                        }
                        sbf.append("DY-").append(fctProject.getApprovalNumber()).append("-").append(StringUtils.leftPad(String.valueOf(fctProject.getMortgagorSum()),4,"0"));
                    }
                    dt.setSerialNumber(sbf.toString());
                }
            }
            fctProjectMapper.updateByPrimaryKeySelective(fctProject);
        }
        return dtos;
    }

    @Override
    public List<HlsCusFctProjectBp> updateExpiry(IRequest request, List<HlsCusFctProjectBp> dtos) {
        /**
         * 这里保存的是质押表的到期日，dtos中的数据是原始数据的描述，
         * 为了不将描述保存，覆盖原始数据，这里需要将到期日单独保存
         */
        if (dtos != null) {
            for (HlsCusFctProjectBp dt : dtos) {
                if (dt.getProjectBpId() != null && dt.getProjectBpId() != 0) {
                    if ("租赁".equalsIgnoreCase(dt.getBusinessType())){
                        HlsCusPrjProjectBp projectBp=new HlsCusPrjProjectBp();
                        projectBp.setPrjBpId(dt.getProjectBpId());
                        projectBp.set__status("update");
                        projectBp.setExpiryDate(dt.getExpiryDate());
                        projectBp.setRemarks(dt.getRemarks());
                        bpService.updateByPrimaryKeySelective(request,projectBp);
                    }else
                    {
                        HlsCusFctProjectBp projectBp = new HlsCusFctProjectBp();
                        projectBp.setProjectBpId(dt.getProjectBpId());
                        projectBp.set__status("update");
                        projectBp.setExpiryDate(dt.getExpiryDate());
                        projectBp.setRemarks(dt.getRemarks());
                        service.updateByPrimaryKeySelective(request,projectBp);
                    }
                }
            }
        }
        return dtos;
    }


    @Override
    public void fctProjectBpInfoDownloadExcel(HttpServletRequest request, HttpServletResponse response, HlsCusFctProjectBp hlsCusFctProjectBp) throws IOException, InvocationTargetException, IllegalAccessException {
        XSSFWorkbook xwork = new XSSFWorkbook();
        XSSFSheet sheet = xwork.createSheet("sheet1");

        String FILE_NAME=null;
        List<String> colNameList = null;
        List<String> colGetMethods = null;

        if (WARRANTOR.equalsIgnoreCase(hlsCusFctProjectBp.getBpRoleType())){
            FILE_NAME = "保证信息";
            colNameList =  Lists.newArrayList(
                    "合同查询编号", "业务合同编号", "合同名称", "客户名称","信贷业务种类","合同签订日期","业务发生日期","合同到期日期","合同有效状态",
                    "保证合同流水号", "保证人名称", "保证币种", "保证金额(元)", "保证担保形式", "备注");
            colGetMethods = Lists.newArrayList(
                    "mainContractNumber", "approvalNumber", "contractName", "creditGrantorName", "businessType","signDate","rateValidFrom","rateValidTo","contractStatus",
                    "serialNumber", "bpName","communalCurrency", "guaranteeAmount", "guaranteeType","description");
        } else if (MORTGAGOR.equalsIgnoreCase(hlsCusFctProjectBp.getBpRoleType())){
            FILE_NAME = "抵押信息";
            colNameList =  Lists.newArrayList(
                    "合同查询编号", "业务合同编号", "合同名称", "客户名称","信贷业务种类","合同签订日期","业务发生日期","合同到期日期","合同有效状态",
                    "抵押合同编号", "抵押人名称", "抵押人贷款卡编号/身份证号","评估币种", "抵押物评估价值（元）", "评估日期", "评估机构名称", "评估机构组织机构代码","抵押物种类",
                    "抵押币种","抵押金额(元)","登记机关","登记日期","抵押到期日","抵押物说明","信贷系统担保物编号","备注");
            colGetMethods =  Lists.newArrayList(
                    "mainContractNumber", "approvalNumber", "contractName", "creditGrantorName","businessType","signDate","rateValidFrom","rateValidTo","contractStatus",
                    "serialNumber", "bpName", "loanCardNum","communalOtherCurrency", "communalValue", "mortgageEvaluationDate", "mortgageEvaluationName",
                    "mortgageEvaluationCode","collateralType","communalCurrency","guaranteeAmountStr","registrationAuthority","registrationDate","expiryDate",
                    "mortgageDesc","collateralCode","description");;
        } else if (PLEDGOR.equalsIgnoreCase(hlsCusFctProjectBp.getBpRoleType())){
            FILE_NAME = "质押信息";
            colNameList = Lists.newArrayList(
                    "合同查询编号", "业务合同编号", "合同名称", "客户名称", "信贷业务种类","合同签订日期","业务发生日期","合同到期日期","合同有效状态",
                    "质押合同流水号", "出质人名称", "质押物币种", "质押物价值（元）", "质押物类型", "质押币种", "质押金额（元）","质押到期日","信贷系统质押物编号","备注");;
            colGetMethods =  Lists.newArrayList(
                    "mainContractNumber", "approvalNumber", "contractName", "creditGrantorName", "businessType","signDate","rateValidFrom","rateValidTo","contractStatus",
                    "serialNumber", "bpName", "communalOtherCurrency", "communalValue", "collateralType", "communalCurrency",
                    "guaranteeAmountStr","expiryDate","collateralCode","description");
        }

        int dataRowNum = ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
        List<HlsCusFctProjectBp> unitSelects = mapper.fctProjectBpInfoQuery(hlsCusFctProjectBp);
        int num = 1;
        for (HlsCusFctProjectBp cashflow : unitSelects) {
            //创建列
            XSSFRow row = sheet.createRow(dataRowNum++);
            this.setData(xwork, sheet, row, cashflow,colGetMethods);
        }
        ExportExcelUtil.IOWrite(xwork, null, request, response, FILE_NAME);

    }

    /**
     * 把dto中的数据设置到Excel行中
     *
     * @param row
     * @param creditContract
     */
    private void setData(XSSFWorkbook workbook, XSSFSheet sheet, XSSFRow row, HlsCusFctProjectBp creditContract,List<String> colGetMethods) throws InvocationTargetException, IllegalAccessException {
        for (int i = 0; i < colGetMethods.size(); i++) {
            XSSFCell cell = row.createCell(i);
            Object value = ExportExcelUtil.getValue(creditContract, colGetMethods.get(i));
            setValue(workbook, sheet, cell, value,i);
        }
    }

    private void setValue(XSSFWorkbook workbook, XSSFSheet sheet, XSSFCell cell, Object value,int i) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DecimalFormat df = new DecimalFormat("###,##0.00");

        if (value instanceof Double) {
            XSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.RIGHT);
            value = df.format(value);
            cell.setCellStyle(cellStyle);
        }

        if (value instanceof Long) {
            XSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.RIGHT);
            value = df.format(value);
            cell.setCellStyle(cellStyle);
        }

        if (value instanceof Date) {
            XSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            value = simpleDateFormat.format(value);
            cell.setCellStyle(cellStyle);
        }

        if (Objects.nonNull(value)) {
            ExportExcelUtil.initColWidth(sheet, i, value.toString());
            cell.setCellValue(value.toString());
        }
    }


    @Override
    public HlsCusFctProjectBp queryCreditGrantorByProjectId(Long projectId) {
        return mapper.queryCreditGrantorByProjectId(projectId);
    }
}
