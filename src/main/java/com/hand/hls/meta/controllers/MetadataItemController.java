package com.hand.hls.meta.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.BaseException;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.security.EncryptUtils;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.meta.dto.Metadata;
import com.hand.hls.meta.dto.MetadataItem;
import com.hand.hls.meta.mapper.MetadataItemMapper;
import com.hand.hls.meta.service.IMetadataItemService;
import com.hand.hls.meta.service.IMetadataService;
import com.hand.hls.widget.mapper.SysWidgetsRuleMapper;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class MetadataItemController extends BaseController {

    @Autowired
    private IMetadataItemService metadataItemService;
    @Autowired
    private MetadataItemMapper metadataItemMapper;
    @Autowired
    private IMetadataService metadataService;
    @Autowired
    private SysWidgetsRuleMapper sysWidgetsRuleMapper;

    @RequestMapping(value = "/metadata/item/query", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseData query(HttpServletRequest request, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                    @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        MetadataItem dto = param.toJavaObject(MetadataItem.class);
        return new ResponseData(metadataItemService.selectList(dto, pagenum, pagesize));
    }


    /*
     * 未完成 查询数据库所有表 {表名,主键名}
     */
    @RequestMapping("/metadata/sys/allTable/query")
    @ResponseBody
    public ResponseData allTable(HttpServletRequest request,
                    @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                    @RequestParam(defaultValue = DEFAULT_PAGE) final int pagenum,
                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize) throws Exception {
        IRequest requestCtx = createRequestContext(request);
        Map para = requestData.getParameter();
        RequestHelper.setCurrentRequest(requestCtx);
        ArrayList list = new ArrayList();
        return new ResponseData(list);
    }

    /*
     * 未完成 根据表名查询数据库所有字段 {字段名,字段类型,描述,可为空}
     */
    @RequestMapping("/metadata/sys/table/allField/query")
    @ResponseBody
    public ResponseData tableField(HttpServletRequest request, @RequestParam(required = false) String table_name,
                    @RequestParam(defaultValue = DEFAULT_PAGE) final int page,
                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize) {
        if (null == table_name || "".equals(table_name)) {
            return new ResponseData(false);
        }
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        ArrayList list = new ArrayList();
        return new ResponseData(list);
    }

    @RequestMapping(value = "/metadata/item/submit", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseData submit(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                    BindingResult result) throws BaseException {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<MetadataItem> mdis = parameter.toJavaList(MetadataItem.class);
        getValidator().validate(mdis, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        mdis.forEach(mdi -> {
            mdi.setData(EncryptUtils.aesDecrypt(mdi.getData()));
            if (mdi.getItemId() == null) {
                mdi.setItemId(UUID.randomUUID().toString().replace("-", ""));
                Metadata md = new Metadata();
                md.setMetaId(mdi.getMetadataId());
                md.setDataId(mdi.getItemId());
                metadataService.updateByPrimaryKeySelective(iRequest, md);
            } else {
                metadataService.removeSelectAction(mdi.getItemId());
            }
        });
        return new ResponseData(metadataItemService.batchUpdate(iRequest, mdis));
    }

    /**
     * @Author hzd
     * @Description TODO
     * @Date 2019/3/12 16:10
     */

    @RequestMapping(value = "/metadata/item/query/lov", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseData queryLov(HttpServletRequest request, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                    @RequestParam(required = true) Long ruleId, @RequestParam(required = false) String metadataId,
                    @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        MetadataItem mdi = param.toJavaObject(MetadataItem.class);
        mdi.setRuleId(ruleId);
        mdi.setMetadataId(StringUtils.isEmpty(metadataId) ? null : metadataId);
        List<MetadataItem> list = metadataItemMapper.selectWidgetField(mdi);
        LinkedList<MetadataItem> linkedList = new LinkedList<MetadataItem>();

        for (int i = 0; i < list.size(); i++) {
            net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(list.get(i).getData());
            if ("tree".equals(jsonObject.get("modelType"))) {
                net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(jsonObject.get("fields"));
                for (int j = 0; j < jsonArray.size(); j++) {
                    MetadataItem user = new MetadataItem();
                    user.setDataName(list.get(i).getDataName());
                    user.setMetadataId(list.get(i).getMetadataId());
                    list.get(i);
                    if (jsonArray.getJSONObject(j).has("name")) {
                        user.setDataFieldName(jsonArray.getJSONObject(j).getString("name"));
                    }
                    if (jsonArray.getJSONObject(j).has("label")) {
                        user.setDataFieldNameDescription(jsonArray.getJSONObject(j).getString("label"));
                    }
                    linkedList.add(user);
                }
            } else if ("sql".equals(jsonObject.get("modelType"))) {
                net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(jsonObject.get("sqlFields"));
                for (int j = 0; j < jsonArray.size(); j++) {
                    MetadataItem user = new MetadataItem();
                    user.setDataName(list.get(i).getDataName());
                    user.setMetadataId(list.get(i).getMetadataId());
                    if (jsonArray.getJSONObject(j).has("name")) {
                        user.setDataFieldName(jsonArray.getJSONObject(j).getString("name"));
                    }
                    if (jsonArray.getJSONObject(j).has("label")) {
                        user.setDataFieldNameDescription(jsonArray.getJSONObject(j).getString("label"));
                    }
                    linkedList.add(user);
                }
            }
        }
        ResponseData data = new ResponseData();
        Long total = (long) (linkedList.size());
        int size = linkedList.size() > pagesize ? pagesize : linkedList.size();
        data.setSuccess(true);
        data.setRows(linkedList.subList((pagenum - 1) * size,
                        (pagenum * size + 1) > total.intValue() ? total.intValue() : (pagenum * size + 1)));
        data.setTotal(total);
        return data;
    }
}
