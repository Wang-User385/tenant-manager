package com.hand.hls.plm.rc.controllers;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.excel.dto.ColumnInfo;
import com.hand.hap.excel.dto.ExportConfig;
import com.hand.hap.excel.service.IExportService;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.plm.rc.dto.HlsCusRentCollection;
import com.hand.hls.plm.rc.service.HlsCusIRentCollectionService;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.List;

/**
 * @Description:租金催收controller
 * @Author: Wty
 * @Date: Created om 21:39 2018/6/6
 */
@Controller
public class HlsCusRentCollectionController extends BaseController {

    @Autowired
    private HlsCusIRentCollectionService service;

    @Autowired
    IExportService excelService;

    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping(value = "/plm/rent/collection/query")
    @ResponseBody
    public ResponseData query(HlsCusRentCollection dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryAll(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/plm/rent/collection/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusRentCollection> dto, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        Long companyId = requestCtx.getCompanyId();
        if (CollectionUtils.isNotEmpty(dto)) {
            for (int i = 0; i < dto.size(); i++) {
                dto.get(i).setCompanyId(companyId);
            }
        }
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/plm/rent/collection/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusRentCollection> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/plm/rent/collection/export")
    public void createLonChangeReqXLS(HttpServletRequest request, @RequestParam String config,
                                      HttpServletResponse httpServletResponse, HttpSession session) {
        IRequest requestContext = createRequestContext(request);
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
                    ExportConfig.class, HlsCusRentCollection.class, ColumnInfo.class);
            ExportConfig<HlsCusRentCollection, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
            service.exportRentCollectionReport(request, httpServletResponse, exportConfig.getParam());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}