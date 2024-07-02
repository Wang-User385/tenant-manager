package com.hand.hls.fct.controllers;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.excel.dto.ColumnInfo;
import com.hand.hap.excel.dto.ExportConfig;
import com.hand.hap.excel.service.IExportService;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fct.dto.HlsCusFctProjectBp;
import com.hand.hls.fct.service.HlsCusFctProjectBpService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;


@Controller
public class HlsCusFctProjectBpController extends BaseController {

    @Autowired
    private HlsCusFctProjectBpService service;
    @Autowired
    IExportService excelService;

    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping(value = "/hls/cus/fct/chance/bp/query")
    @ResponseBody
    public ResponseData creditLineDetailQuery(HlsCusFctProjectBp dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        Long chanceId = dto.getChanceId();
        if (chanceId == null) {
            chanceId = 0L;
        }
        dto.setChanceId(chanceId);
        return new ResponseData(service.fctProjectBpQuery(dto, page, pageSize));
    }

    @RequestMapping(value = "/hls/cus/fct/project/bp/query")
    @ResponseBody
    public ResponseData fctProjectBpQuery(HlsCusFctProjectBp dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        Long projectId = dto.getProjectId();
        if (projectId == null) {
            projectId = 0L;
        }
        dto.setProjectId(projectId);
        return new ResponseData(service.fctProjectBpQuery(dto, page, pageSize));
    }

    @RequestMapping(value = "/hls/cus/fct/project/bp/info/query")
    @ResponseBody
    public ResponseData fctProjectBpInfoQuery(HlsCusFctProjectBp dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        return new ResponseData(service.fctProjectBpInfoQuery(dto, page, pageSize));
    }

    /**
     * 查询合同变更前的bp信息【交易方，保证，质押，抵押】
     * @param dto
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/hls/cus/fct/project/bp/queryBef")
    @ResponseBody
    public ResponseData fctProjectBpQueryBef(HlsCusFctProjectBp dto, HttpServletRequest request,
                                             @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        Long projectId = dto.getProjectId();
        dto.setProjectId(projectId);
        return new ResponseData(service.fctProjectBpQueryBef(iRequest,dto, page, pageSize));
    }

    @RequestMapping(value = "/hls/cus/fct/project/bp/sent/query")
    @ResponseBody
    public ResponseData selectSentBpName(HlsCusFctProjectBp dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        return new ResponseData(service.selectSentBpName(dto, page, pageSize));
    }

    @RequestMapping(value = "/hls/cus/fct/chance/bp/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusFctProjectBp> dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        if (dto != null) {
            for (HlsCusFctProjectBp dt : dto) {
                if (dt.getProjectBpId() == null || dt.getProjectBpId() == 0) {
                    dt.set__status("add");
                    dt.setInterestRepaymentParty(dt.getCreditGrantorParty());
                    dt.setPrincipalRepaymentParty(dt.getCreditGrantorParty());
                } else {
                    dt.set__status("update");
                }
            }
        }
        return new ResponseData(service.batchUpdate(requestCtx,dto));
    }

    /**
     * 用于对质押表的到期日进行保存
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/hls/cus/fct/chance/bp/expiry/submit")
    @ResponseBody
    public ResponseData updateExpiry(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusFctProjectBp> dto = param.toJavaList(HlsCusFctProjectBp.class);
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.updateExpiry(requestCtx,dto));
    }

    /**
     * 项目-商业伙伴保存
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/hls/cus/fct/project/bp/submit")
    @ResponseBody
    public ResponseData updateProject(@RequestBody List<HlsCusFctProjectBp> dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        if (dto != null) {
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
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    /**
     * 合同-保证、质押、抵押、保存
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/hls/cus/fct/project/bp/contract/submit")
    @ResponseBody
    public ResponseData updateContract(@RequestBody List<HlsCusFctProjectBp> dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.fctProjectBpUpdate(requestCtx, dto));
    }

    /*@RequestMapping(value = "/fct/chance/bp/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusFctProjectBp> dto) throws Exception {
        service.batchDelete(dto);
        return new ResponseData();
    }*/

    @RequestMapping(value = "/hls/cus/fct/project/bp/remove")
    @ResponseBody
    public ResponseData deleteProject(HttpServletRequest request, @RequestBody List<HlsCusFctProjectBp> dto) throws Exception {
        service.batchDelete(dto);
        return new ResponseData();
    }

    /*立项阶段删除授信方*/
    @RequestMapping(value = "/hls/cus/fct/bp/creditor/remove")
    @ResponseBody
    public ResponseData deleteCreditor(HttpServletRequest request, @RequestBody HlsCusFctProjectBp dto) throws Exception {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.deleteCreditor(dto, requestCtx));
    }


    @RequestMapping(value = "/hls/cus/fct/project/bp/info/export")
    public void beforeQueryXLS(HttpServletRequest request, @RequestParam String config, @RequestParam(value = "bpRoleType") String bpRoleType,
                               HttpServletResponse httpServletResponse, HttpSession session) throws IOException, InvocationTargetException, IllegalAccessException {
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
                    ExportConfig.class, HlsCusFctProjectBp.class, ColumnInfo.class);
            ExportConfig<HlsCusFctProjectBp, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
            HlsCusFctProjectBp param=exportConfig.getParam();
            param.setBpRoleType(bpRoleType);
            service.fctProjectBpInfoDownloadExcel(request,httpServletResponse,param);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
