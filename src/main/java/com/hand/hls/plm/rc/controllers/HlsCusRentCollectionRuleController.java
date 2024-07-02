package com.hand.hls.plm.rc.controllers;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.excel.dto.ColumnInfo;
import com.hand.hap.excel.dto.ExportConfig;
import com.hand.hap.excel.service.IExportService;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.plm.rc.dto.HlsCusRentCollectionRule;
import com.hand.hls.plm.rc.service.HlsCusRentCollectionRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * <p>
 *  租金催收规则
 * </p>
 * 
 * @author yuanyuan 2019/03/25 5:53 PM
 */
@Controller
public class HlsCusRentCollectionRuleController extends BaseController {

    @Autowired
    private HlsCusRentCollectionRuleService rentCollectionRuleService;

    @Autowired
    IExportService excelService;
    @Autowired
    ObjectMapper objectMapper;


    /**
     * 催收规则查询
     * @param rentCollectionRule
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/plm/rent/collection/rule/query")
    @ResponseBody
    public ResponseData query(HlsCusRentCollectionRule rentCollectionRule, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(rentCollectionRuleService.selectRentCollectionRuleData(requestContext, rentCollectionRule, page, pageSize));
    }

    /**
     * 催收规则保存
     * @param rentCollectionRuleList
     * @param result
     * @param request
     * @return
     */
    @RequestMapping(value = "/plm/rent/collection/rule/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusRentCollectionRule> rentCollectionRuleList, BindingResult result, HttpServletRequest request) {
        getValidator().validate(rentCollectionRuleList, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        for(HlsCusRentCollectionRule rentCollectionRule:rentCollectionRuleList){
            if(rentCollectionRuleService.selectContractRentCount(requestCtx,rentCollectionRule)>0){
                ResponseData responseData=new ResponseData(false);
                responseData.setMessage("当前合同"+rentCollectionRule.getContractNumber()+"催收记录已经存在,请勿重复维护!");
                return responseData;
            }
        }
        return new ResponseData(rentCollectionRuleService.batchUpdate(requestCtx, rentCollectionRuleList));
    }

    /**
     * 催收规则删除
     * @param request
     * @param rentCollectionRuleList
     * @return
     */
    @RequestMapping(value = "/plm/rent/collection/rule/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusRentCollectionRule> rentCollectionRuleList) {
        rentCollectionRuleService.batchDelete(rentCollectionRuleList);
        return new ResponseData();
    }



    @RequestMapping(value = "/plm/rent/collection/rule/export")
    public void createFctCashXLS(HttpServletRequest request, @RequestParam String config,
                                 HttpServletResponse httpServletResponse, HttpSession session) throws IOException, InvocationTargetException, IllegalAccessException {
        IRequest requestContext = createRequestContext(request);
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
                    ExportConfig.class, HlsCusRentCollectionRule.class, ColumnInfo.class);
            ExportConfig<HlsCusRentCollectionRule, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
            rentCollectionRuleService.exportRentCollectionRule(request, httpServletResponse, exportConfig.getParam());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}