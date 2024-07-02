package com.hand.hls.abs.controllers;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.excel.dto.ColumnInfo;
import com.hand.hap.excel.dto.ExportConfig;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.abs.dto.HlsCusAbsProFeeInfo;
import com.hand.hls.abs.service.HlsCusAbsProFeeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 费用信息(立项/产品)
 * </p>
 *
 * @author yuanyuan 2019/04/16 10:23 AM
 */
@Controller
public class HlsCusAbsProFeeInfoController extends BaseController {

    @Autowired
    private HlsCusAbsProFeeInfoService service;

    @Autowired
    private ObjectMapper objectMapper;


    /**
     * 查询
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/ct/abs/pro/fee/info/query")
    @ResponseBody
    public ResponseData query(HlsCusAbsProFeeInfo dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectProjectFeeInfo(requestContext, dto, page, pageSize));
    }


    /**
     * 保存
     * @param dto
     * @param result
     * @param request
     * @return
     */
    @RequestMapping(value = "/ct/abs/pro/fee/info/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusAbsProFeeInfo> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }


    /**
     * 删除
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping(value = "/ct/abs/pro/fee/info/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusAbsProFeeInfo> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }


    /**
     * 资金调拨单导出
     * @param request
     * @param config
     * @param httpServletResponse
     */
    @RequestMapping(value = "/ct/abs/pro/fee/info/export")
    public void exportTransferList(HttpServletRequest request, @RequestParam String config,
                                   HttpServletResponse httpServletResponse) {
        IRequest requestContext = createRequestContext(request);
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
                    ExportConfig.class, HlsCusAbsProFeeInfo.class, ColumnInfo.class);
            ExportConfig<HlsCusAbsProFeeInfo, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
           service.exportAbsProFeeInfo(request, httpServletResponse, exportConfig.getParam());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}