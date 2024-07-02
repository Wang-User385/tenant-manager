package com.hand.hls.plm.rc.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.plm.rc.dto.HlsCusRentCollectionRule;
import com.hand.hls.plm.rc.mapper.HlsCusRentCollectionRuleMapper;
import com.hand.hls.plm.rc.service.HlsCusRentCollectionRuleService;
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
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusRentCollectionRuleServiceImpl extends BaseServiceImpl<HlsCusRentCollectionRule> implements HlsCusRentCollectionRuleService {

    @Autowired
    private HlsCusRentCollectionRuleMapper rentCollectionRuleMapper;


    @Override
    public List<HlsCusRentCollectionRule> selectRentCollectionRuleData(IRequest iRequest, HlsCusRentCollectionRule rentCollectionRule, int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        return rentCollectionRuleMapper.selectRentCollectionRuleData(rentCollectionRule);
    }

    @Override
    public int selectContractRentCount(IRequest iRequest, HlsCusRentCollectionRule rentCollectionRule) {
        return rentCollectionRuleMapper.selectContractRentCount(rentCollectionRule);
    }

    @Override
    public List<HlsCusRentCollectionRule> selectOverDateContract() {
        return rentCollectionRuleMapper.selectOverDateContract();
    }

    @Override
    public int updateLastCollectionDate(HlsCusRentCollectionRule rentCollectionRule) {
        return rentCollectionRuleMapper.updateLastCollectionDate(rentCollectionRule);
    }

    @Override
    public List<HlsCusRentCollectionRule> selectOverMonthContract() {
        return rentCollectionRuleMapper.selectOverMonthContract();
    }

    @Override
    public void exportRentCollectionRule(HttpServletRequest request, HttpServletResponse response, HlsCusRentCollectionRule rentCollectionRule) throws IOException, InvocationTargetException, IllegalAccessException {
        XSSFWorkbook xwork = new XSSFWorkbook();
        XSSFSheet sheet = xwork.createSheet("sheet1");
       final List<String> cfNameList = Lists.newArrayList(
                "合同查询编码", "业务合同编号", "客户名称", "业务类型", "逾期金额", "逾期罚息", "催收代办间隔天数", "备注", "是否启用");
       final List<String> cfGetMethods = Lists.newArrayList(
                "contractNumber", "approvalNumber", "bpName", "documentTypeDesc", "overdueAmount", "overduePenalty", "collectionDays", "description", "enabledFlag");

        int dataRowNum = ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, cfNameList);
        List<HlsCusRentCollectionRule> rentCollectionRules = rentCollectionRuleMapper.selectRentCollectionRuleData(rentCollectionRule);
        for (HlsCusRentCollectionRule collectionRule : rentCollectionRules) {
            if("Y".equals(collectionRule.getEnabledFlag())){
                collectionRule.setEnabledFlag("是");
            }else{
                collectionRule.setEnabledFlag("否");
            }
            //创建列
            XSSFRow row = sheet.createRow(dataRowNum++);
            ExportExcelUtil.setData(xwork, sheet, row, collectionRule,cfGetMethods);
        }
        ExportExcelUtil.IOWrite(xwork, null, request, response, "催收规则");
    }

    @Override
    public List<HlsCusRentCollectionRule> selectOverTimesContract() {
        return rentCollectionRuleMapper.selectOverTimesContract();
    }
}