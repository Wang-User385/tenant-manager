package com.hand.hls.abs.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.HlsCusAbsProductCashDetail;
import com.hand.hls.abs.dto.HlsCusAbsProductCollection;
import com.hand.hls.abs.mapper.HlsCusAbsProductCashDetailMapper;
import com.hand.hls.abs.service.HlsCusAbsProductCashDetailService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.utils.ExportExcelUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsProductCashDetailServiceImpl extends BaseServiceImpl<HlsCusAbsProductCashDetail> implements HlsCusAbsProductCashDetailService {

    @Autowired
    private HlsCusAbsProductCashDetailMapper hlsCusAbsProductCashDetailMapper;
    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;

    @Override
    public List<HlsCusAbsProductCashDetail> selectProductCashDetailData(IRequest iRequest, HlsCusAbsProductCashDetail productCashDetail, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return hlsCusAbsProductCashDetailMapper.selectProductCashDetailData(productCashDetail);
    }

    @Override
    public int deleteCashDetailByProduct(Long productId,String dataClass) {
        return hlsCusAbsProductCashDetailMapper.deleteCashDetailByProduct(productId,dataClass);
    }


    @Override
    public BigDecimal selectCashPrincipalSum(Long productId,Long times) {
        return hlsCusAbsProductCashDetailMapper.selectCashPrincipalSum(productId,times);
    }

    @Override
    public void exportCashDeatil(HttpServletRequest request, HttpServletResponse response, HlsCusAbsProductCashDetail hlsCusFctQuotationCashflow) throws IOException, InvocationTargetException, IllegalAccessException {
        List<HlsCusAbsProductCashDetail> finCostList = hlsCusAbsProductCashDetailMapper.selectProductCashDetailData(hlsCusFctQuotationCashflow);
        XSSFWorkbook xwork = new XSSFWorkbook();
        XSSFSheet sheet = xwork.createSheet("sheet1");
        final List<String> colNameList = Lists.newArrayList(
                "优先级别","项目评级","兑付本金(元)","兑付利息(元)","兑付总金额(元)");
        final List<String> colGetMethods = Lists.newArrayList(
                "projectStructureDesc","projectGrade", "cashPrincipal", "cashInterest", "cashAmountSum");
        int dataRowNum = ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
        for(HlsCusAbsProductCashDetail cashDetail:finCostList){
            //创建列
            XSSFRow row = sheet.createRow(dataRowNum++);
            ExportExcelUtil.setData(xwork,sheet, row, cashDetail,colGetMethods);
        }
        ExportExcelUtil.IOWrite(xwork, null,request,response, "兑付反馈");

    }

    @Override
    public void deleteCashDetailByCollection(HlsCusAbsProductCollection hlsCusAbsProductCollection) {
        hlsCusAbsProductCashDetailMapper.deleteCashDetailByCollection(hlsCusAbsProductCollection);
    }

    @Override
    public void ctAbsProductCashImport(IRequest iRequest, Long hdId , Long productId) throws HlsCusException, ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        List<FndInterfaceLines> fndInterfaceLinesList = getInterfaceData(hdId, 0L);

        //先删除现金流
        HlsCusAbsProductCashDetail absProductCashDetail = new HlsCusAbsProductCashDetail();
        absProductCashDetail.setProductId(productId);
        List<HlsCusAbsProductCashDetail> deleteCashList = hlsCusAbsProductCashDetailMapper.select(absProductCashDetail);
        self().batchDelete(deleteCashList);

        for (FndInterfaceLines fndInterfaceLine : fndInterfaceLinesList) {
            HlsCusAbsProductCashDetail record = new HlsCusAbsProductCashDetail();

            Long times = 0L;
            Date cashDate = new Date();
            Double planCashInterest = 0.0;
            Double planCashPrincipal = 0.0;

            //期数
            if (fndInterfaceLine.getAttributes_1() != null) {
                times = Long.parseLong(fndInterfaceLine.getAttributes_1());
            } else {
//                throw new HlsCusException("期数不能为空！");
            }

            //兑付日
            if (fndInterfaceLine.getAttributes_2() != null) {
                cashDate = df.parse(fndInterfaceLine.getAttributes_2());
            } else {
                throw new HlsCusException("还款日期不能为空！");
            }

            //兑付利息(元)
            if (fndInterfaceLine.getAttributes_3() != null) {
                planCashInterest = Double.parseDouble(fndInterfaceLine.getAttributes_3());
            } else {
//                throw new HlsCusException("兑付利息(元)不能为空！");
            }

            //兑付本金(元)
            if (fndInterfaceLine.getAttributes_4() != null) {
                planCashPrincipal = Double.parseDouble(fndInterfaceLine.getAttributes_4());
            } else {
//                throw new HlsCusException("兑付本金(元)不能为空！");
            }


            record.setTimes(times);
            record.setCashDate(cashDate);
            record.setPlanCashInterest(new BigDecimal(planCashInterest));
            record.setPlanCashPrincipal(new BigDecimal(planCashPrincipal));
            record.setProductId(productId);

            if (productId == null) {
                throw new HlsCusException("未正确获取productId！");
            }
            self().insertSelective(iRequest, record);
        }
    }
    //获取接口表数据
    public List<FndInterfaceLines> getInterfaceData(Long hdId, Long readLine) {

        FndInterfaceLines fndInterfaceLines = new FndInterfaceLines();
        fndInterfaceLines.setHeaderId(hdId);
        fndInterfaceLines.setReadLine(readLine);
        List<FndInterfaceLines> fndInterfaceLinesList = fndInterfaceLinesMapper.fndInterfaceLinesDetailQuery(fndInterfaceLines);
        return fndInterfaceLinesList;

    }

    @Override
    public void ctAbsProductCashConfirm(IRequest iRequest, Long cash_detail_id){
        HlsCusAbsProductCashDetail hlsCusAbsProductCashDetail = new HlsCusAbsProductCashDetail();
        hlsCusAbsProductCashDetail.setCashDetailId(cash_detail_id);
        hlsCusAbsProductCashDetail.setRemittanceStatus("CONFIRM");
        self().updateByPrimaryKeySelective(iRequest, hlsCusAbsProductCashDetail);
    }
}