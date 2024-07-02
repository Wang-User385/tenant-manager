//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.gld.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.mapper.FirmDefineMapper;
import com.hand.hls.fnd.mapper.HLSCurrencyMapper;
import com.hand.hls.gld.dto.HlsCusJeHead;
import com.hand.hls.gld.dto.JeLine;
import com.hand.hls.gld.dto.PeriodStatus;
import com.hand.hls.gld.mapper.AccountMapper;
import com.hand.hls.gld.mapper.CostCenterMapper;
import com.hand.hls.gld.mapper.HlsCusJeHeadMapper;
import com.hand.hls.gld.mapper.JeLineMapper;
import com.hand.hls.gld.mapper.PeriodStatusMapper;
import com.hand.hls.gld.service.IJeHeadService;
import com.hand.hls.gld.service.IJeLineService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.hand.hls.utils.ExportExcelUtil;
import com.hand.hls.vat.dto.HlsCusAcrInvoiceHd;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@Transactional(
        rollbackFor = {Exception.class}
)
public class JeHeadServiceImpl extends BaseServiceImpl<HlsCusJeHead> implements IJeHeadService {
    @Autowired
    private IJeLineService jeLineService;
    @Autowired
    private PeriodStatusMapper periodStatusMapper;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsCusJeHeadMapper mapper;
    @Autowired
    private FirmDefineMapper firmDefineMapper;
    @Autowired
    private CostCenterMapper centerMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private HLSCurrencyMapper hlsCurrencyMapper;
    @Autowired
    private JeLineMapper lineMapper;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    public JeHeadServiceImpl() {
    }

    public List<HlsCusJeHead> jeListQuery(Map<String, Object> map, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return this.mapper.jeListQuery(map);
    }

    public List<JeLine> jeConfirm(List<HlsCusJeHead> list, IRequest requestCtx, Long companyId, Long userId) {
        Map validate = this.validate(list, companyId);
        if ("false".equals(validate.get("success"))) {
            throw new IllegalArgumentException((String)validate.get("message"));
        } else {
            List<JeLine> listLine = new ArrayList();

            for(int i = 0; i < list.size(); ++i) {
                new ArrayList();
                Map<String, Object> map = new HashMap();
                map.put("jeHeadId", ((HlsCusJeHead)list.get(i)).getJeHeadId());
                List<JeLine> jeLineList = this.jeLineService.queryNoPage(map);

                for(int j = 0; j < jeLineList.size(); ++j) {
                    this.databaseLockProvider.lock(jeLineList.get(j));
                    ((JeLine)jeLineList.get(j)).setJeConfirmDate(new Date());
                    ((JeLine)jeLineList.get(j)).setJeConfirmedBy(userId);
                    ((JeLine)jeLineList.get(j)).setJeHeadId(((HlsCusJeHead)list.get(i)).getJeHeadId());
                    ((JeLine)jeLineList.get(j)).set__status("update");
                    ((JeLine)jeLineList.get(j)).setJeStatus("CONFIRM");
                }

                listLine = ((IJeLineService)this.jeLineService.self()).batchUpdate(requestCtx, jeLineList);
                ((HlsCusJeHead)list.get(i)).setCompanyId(companyId);
                ((HlsCusJeHead)list.get(i)).setJeConfirmedBy(userId);
                ((HlsCusJeHead)list.get(i)).setJeConfirmDate(new Date());
                ((IJeHeadService)this.self()).updateByPrimaryKeySelective(requestCtx, list.get(i));
            }

            return (List)listLine;
        }
    }

    @Override
    public List<Map> selectHeadInfo(IRequest iRequest, HlsCusJeHead hlsCusJeHead, int page, int pagesize) {
        if(hlsCusJeHead.getSortname() != null && hlsCusJeHead.getSortorder() != null) {
            PageHelper.startPage(page, pagesize, hlsCusJeHead.getSortname() + " " + hlsCusJeHead.getSortorder());
        }else{
            PageHelper.startPage(page, pagesize);
        }
        return mapper.selectHeadInfo(hlsCusJeHead);
    }

    @Override
    public List<HlsCusJeHead>  gldJeDataQuery(IRequest iRequest, HlsCusJeHead hlsCusJeHead, int page, int pagesize) {
        PageHelper.startPage(page,pagesize);
        return mapper.gldJeDataQuery(hlsCusJeHead);
    }



    public Map validate(List<HlsCusJeHead> list, Long companyId) {
        Map result = new HashMap();
        Iterator var4 = list.iterator();

        while(var4.hasNext()) {
            HlsCusJeHead item = (HlsCusJeHead)var4.next();
            item = (HlsCusJeHead)this.mapper.selectByPrimaryKey(item.getJeHeadId());
            if (!"NEW".equalsIgnoreCase(item.getJeStatus())) {
                result.put("success", "false");
                result.put("message", "凭证号为:" + item.getJeNumber() + "的凭证已被确认,不可重复确认");
                break;
            }

            Map<String, Object> map = new HashMap();
            map.put("jeHeadId", item.getJeHeadId());
            List<JeLine> jeLines = this.jeLineService.queryNoPage(map);
            Double cr = 0.0D;
            Double dr = 0.0D;
            Double crf = 0.0D;
            Double drf = 0.0D;
            Iterator var12 = jeLines.iterator();

            while(var12.hasNext()) {
                JeLine jeline = (JeLine)var12.next();
                cr = CalculateUtil.add(cr, jeline.getCrAmount());
                dr = CalculateUtil.add(dr, jeline.getDrAmount());
                crf = CalculateUtil.add(crf, jeline.getCrFunctionalAmount());
                drf = CalculateUtil.add(drf, jeline.getDrFunctionalAmount());
                if (jeline.getCompanyId() == null) {
                    result.put("success", "false");
                    result.put("message", "凭证号为:" + item.getJeNumber() + "的公司id为空");
                    return result;
                }

                if (this.firmDefineMapper.selectByPrimaryKey(jeline.getCompanyId()) == null) {
                    result.put("success", "false");
                    result.put("message", "凭证号为:" + item.getJeNumber() + "的凭证公司id取值不合法");
                    return result;
                }

                if (jeline.getCostCenterId() == null) {
                    result.put("success", "false");
                    result.put("message", "凭证号为:" + item.getJeNumber() + "的凭证成本中心id为空");
                    return result;
                }

                if (this.centerMapper.selectByPrimaryKey(jeline.getCostCenterId()) == null) {
                    result.put("success", "false");
                    result.put("message", "凭证号为:" + item.getJeNumber() + "的凭证成本中心id取值不合法");
                    return result;
                }

                if (jeline.getAccountId() == null) {
                    result.put("success", "false");
                    result.put("message", "凭证号为:" + item.getJeNumber() + "的凭证科目id为空");
                    return result;
                }

                if (this.accountMapper.selectByPrimaryKey(jeline.getAccountId()) == null) {
                    result.put("success", "false");
                    result.put("message", "凭证号为:" + item.getJeNumber() + "的凭证科目id取值不合法");
                    return result;
                }

                if (jeline.getCurrency() == null) {
                    result.put("success", "false");
                    result.put("message", "凭证号为:" + item.getJeNumber() + "的凭证币种为空");
                }

                if (this.hlsCurrencyMapper.selectByPrimaryKey(jeline.getCurrency()) == null) {
                    result.put("success", "false");
                    result.put("message", "凭证号为:" + item.getJeNumber() + "的凭证币种取值不合法");
                }

                if ((jeline.getCrAmount() == null || jeline.getCrFunctionalAmount() == null || jeline.getDrAmount() != null || jeline.getDrFunctionalAmount() != null) && (jeline.getCrAmount() != null || jeline.getCrFunctionalAmount() != null || jeline.getDrAmount() == null || jeline.getDrFunctionalAmount() == null)) {
                    result.put("success", "false");
                    result.put("message", "凭证号为:" + item.getJeNumber() + "的凭证借贷取值不合法");
                }

                if ((jeline.getCrAmount() != null || jeline.getCrFunctionalAmount() != null || jeline.getDrAmount() == null || jeline.getDrFunctionalAmount() == null) && (jeline.getCrAmount() == null || jeline.getCrFunctionalAmount() == null || jeline.getDrAmount() != null || jeline.getDrFunctionalAmount() != null)) {
                    result.put("success", "false");
                    result.put("message", "凭证号为:" + item.getJeNumber() + "的凭证借贷取值不合法");
                }
            }

            if (cr.compareTo(dr) != 0) {
                result.put("success", "false");
                result.put("message", "凭证号为:" + item.getJeNumber() + "的凭证借贷金额不等");
                break;
            }

            if (crf.compareTo(drf) != 0) {
                result.put("success", "false");
                result.put("message", "凭证号为:" + item.getJeNumber() + "的凭证借贷金额不等");
                break;
            }

            PeriodStatus periodStatus = new PeriodStatus();
            periodStatus.setPeriodName(item.getPeriodName());
            periodStatus.setCompanyId(companyId);
            periodStatus = (PeriodStatus)this.periodStatusMapper.selectOne(periodStatus);
            if (periodStatus == null) {
                result.put("success", "false");
                result.put("message", "凭证号为:" + item.getJeNumber() + "未找到对应的期间状态");
                break;
            }

            if ("CLOSE".equalsIgnoreCase(periodStatus.getStatus())) {
                result.put("success", "false");
                result.put("message", "凭证号为:" + item.getJeNumber() + "对应的期间状态为关闭");
                break;
            }
        }

        return result;
    }

    @Override
    public void jeLineExport(HttpServletRequest iRequest, HttpServletResponse response, HlsCusJeHead dto) throws IOException,InvocationTargetException, IllegalAccessException{
        IRequest requestCtx = RequestHelper.getCurrentRequest();
        XSSFWorkbook xwork = new XSSFWorkbook();
        XSSFSheet sheet = xwork.createSheet("凭证行信息");
        List<String> colNameList = Arrays.asList("科目代码","科目描述","行摘要", "参考段一", "参考段二", "参考段三", "参考段四", "原币借方", "原币贷方", "本币借方", "本币贷方");
        int dataRowNum = ExportExcelUtil.createCommonExcelHead(xwork, sheet,null, colNameList);

        JeLine jeLine = new JeLine();

        jeLine.setJeHeadId(dto.getJeHeadId());
        List<JeLine> jeLineList = lineMapper.selectJeLineExportInfo(jeLine);

        for (JeLine item : jeLineList) {
            XSSFRow row = sheet.createRow(dataRowNum++);
            setData(xwork, sheet, row, item);
        }
        ExportExcelUtil.IOWrite(xwork, null, iRequest, response, "凭证行信息");
    }

    private void setData(XSSFWorkbook workbook, XSSFSheet sheet, XSSFRow row, JeLine jeLine) throws InvocationTargetException, IllegalAccessException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DecimalFormat df = new DecimalFormat("###,###,###,###,###,###,##0.00");
        List<String> colGetMethods = Arrays.asList("accountCode","accountIdN","jeDescription", "segment1", "segment2", "segment3", "segment4", "drAmount", "crAmount", "drFunctionalAmount", "crFunctionalAmount");
        for (int i = 0; i < colGetMethods.size(); i++) {
            XSSFCell cell = row.createCell(i);
            Object value;

            System.out.println(colGetMethods.get(i));
            value = ExportExcelUtil.getValue(jeLine, colGetMethods.get(i));

            if (value instanceof Date) {
                value = simpleDateFormat.format(value);
            }
            if (value instanceof Double) {
                XSSFCellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                value = df.format(value);
                cell.setCellStyle(cellStyle);
            }
            if (Objects.nonNull(value)) {
                ExportExcelUtil.initColWidth(sheet, i, value.toString());
                cell.setCellValue(value.toString());
            }

        }
    }

}
