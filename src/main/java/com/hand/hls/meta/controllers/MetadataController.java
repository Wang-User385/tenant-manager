package com.hand.hls.meta.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.BaseException;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.core.web.view.IDGenerator;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.meta.dto.Metadata;
import com.hand.hls.meta.dto.MetadataItem;
import com.hand.hls.meta.mapper.MetadataMapper;
import com.hand.hls.meta.service.IMetadataItemService;
import com.hand.hls.meta.service.IMetadataService;
import com.hand.hls.sys.dto.SysRoleMetadata;
import com.hand.hls.sys.service.ISysRoleMetadataService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MetadataController extends BaseController {

    @Autowired
    private IMetadataService service;
    @Autowired
    private IMetadataItemService iMetadataItemService;
    @Autowired
    private ISysRoleMetadataService sysRoleMetadataService;

    @Autowired
    private MetadataMapper metadataMapper;

    @RequestMapping(value = "/metadata/query", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseData query(HttpServletRequest request,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                              @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Metadata dto = param.toJavaObject(Metadata.class);
        return new ResponseData(service.select(iRequest, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/metadata/submit", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseData submit(HttpServletRequest request,
                               @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                               BindingResult result) throws BaseException {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<Metadata> mds = parameter.toJavaList(Metadata.class);
        getValidator().validate(mds, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        mds.forEach(md -> {
            if (md.getMetaId() == null) {
                md.setMetaId(IDGenerator.getInstance().generate());
            }
            if(md.get__status() == null){
                md.set__status(md.getStatus());
            }
            if (md.get__status().equals("insert")) {
                MetadataItem mdi = new MetadataItem();
                mdi.setItemId(IDGenerator.getInstance().generate());
                mdi.setMetadataId(md.getMetaId());
                md.setDataId(mdi.getItemId());
                iMetadataItemService.insertSelective(iRequest, mdi);

                //初始化默认角色权限
                iMetadataItemService.initRoleMetadata(mdi);
            }
        });
        mds = service.batchUpdate(iRequest, mds);
        return new ResponseData(mds);
    }

    @RequestMapping(value = "/metadata/delete", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,
                               @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<Metadata> mds = parameter.toJavaList(Metadata.class);
        mds.forEach(md -> {
            //关联删除metadata_item
            MetadataItem mdi = new MetadataItem();
            mdi.setItemId(md.getDataId());
            mdi.setMetadataId(md.getMetaId());
            iMetadataItemService.deleteByPrimaryKey(mdi);

            //关联删除sys role metadata(角色数据建模权限)
            SysRoleMetadata sysRoleMetadata = new SysRoleMetadata();
            sysRoleMetadata.setMetadataId(md.getMetaId());
            List<SysRoleMetadata> lists = sysRoleMetadataService.selectSelective(iRequest,sysRoleMetadata);
            sysRoleMetadataService.batchDelete(lists);
        });
        service.batchDelete(mds);
        return new ResponseData(mds);
    }

    @RequestMapping(value = "/metadata/query/all", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseData queryAll(HttpServletRequest request,
                                 @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                 @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Metadata dto = param.toJavaObject(Metadata.class);
        dto.setDataType("DATA");
        return new ResponseData(service.selectFilterRole(iRequest, dto));
    }

    @RequestMapping(value = "/metadata/query/lov", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseData queryLov(HttpServletRequest request,
                                 @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                 @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Metadata dto = param.toJavaObject(Metadata.class);
        dto.setDataType("DATA");
        return new ResponseData(service.select(iRequest, dto, pagenum, pagesize));
    }

    @RequestMapping("/metadata/sql/generate/field")
    @ResponseBody
    public ResponseData resolveFields(HttpServletRequest request,
                                      @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        return service.resolveFields(iRequest, requestData);
    }

    @RequestMapping("/metadata/cache/remove")
    @ResponseBody
    public ResponseData removeSelectAction(String metadataId) {
        Object o = service.removeSelectAction(metadataId);
        return new ResponseData(o != null);
    }


    @RequestMapping("/metadata/query/data")
    @ResponseBody
    public ResponseData queryData(HttpServletRequest request, String metadataId,
                                  @RequestParam Map params,
                                  @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                  @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        try {
            params.putAll(requestData.getParameter());
            if ("true".equals(params.get("_fetchall"))) {
                pagenum = 0;
            }
            List list = service.queryDataByMetadataId(metadataId, new HashMap<>(), params, pagenum, pagesize);
            return new ResponseData(list);
        } catch (IOException e) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(e.getMessage());
            return responseData;
        }
    }
}