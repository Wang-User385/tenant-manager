package com.hand.hls.plm.pli.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.plm.pli.dto.HlsCusPostloanInspection;
import com.hand.hls.plm.pli.dto.PliLeaseItem;
import com.hand.hls.plm.pli.dto.PlmPliCheckItem;
import com.hand.hls.plm.pli.mapper.HlsCusPostloanInspectionMapper;
import com.hand.hls.plm.pli.service.IPliLeaseItemService;
import com.hand.hls.plm.pli.service.PlmPliCheckItemService;
import com.hand.hls.plm.pli.service.impl.PlmPliFrequencySetUtilImpl;
import com.hand.hls.prj.dto.HlsCusPrjProjectLeaseItem;
import com.hand.hls.prj.service.HlsCusPrjProjectLeaseItemService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PliLeaseItemController extends BaseController {

    @Autowired
    private IPliLeaseItemService service;
    @Autowired
    private HlsCusPostloanInspectionMapper hlsCusPostloanInspectionMapper;
    @Autowired
    private HlsCusPrjProjectLeaseItemService hlsCusPrjProjectLeaseItemService;
    @Autowired
    private PlmPliCheckItemService plmPliCheckItemService;

    @RequestMapping(value = "/plm/pli/lease/item/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);

        JSONObject param  =(JSONObject) requestData.get("parameter");
        PliLeaseItem dto = param.toJavaObject(PliLeaseItem.class);

        return new ResponseData(service.queryPliLeaseItemInfo(dto, page, pageSize));
    }

    @RequestMapping(value = "/plm/pli/lease/item/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<PliLeaseItem> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        List<PliLeaseItem> list = service.batchUpdate(requestCtx, dto);
        //===================================================================================
        List<HlsCusPrjProjectLeaseItem> items=new ArrayList<>(dto.size());
        for (PliLeaseItem pliLeaseItem : dto) {
            HlsCusPrjProjectLeaseItem bean=new HlsCusPrjProjectLeaseItem();
            BeanUtils.copyProperties(pliLeaseItem,bean);
            HlsCusPrjProjectLeaseItem oldBean= hlsCusPrjProjectLeaseItemService.selectByPrimaryKey(requestCtx,bean);
            if (oldBean!=null)
            {
                bean.set__status("update");
                bean.setObjectVersionNumber(oldBean.getObjectVersionNumber());
                items.add(bean);
            }
        }
        //租赁物清单数据
        hlsCusPrjProjectLeaseItemService.batchUpdate(requestCtx,items);
        //===================================================================================
        //新增检查清单
        HlsCusPostloanInspection hlsCusPostloanInspection = new HlsCusPostloanInspection();
        hlsCusPostloanInspection.setPostloanInspectionId((dto.get(0).getPostLoanInspectionId()));
        List<HlsCusPostloanInspection> hlsCusPostloanInspectionList = hlsCusPostloanInspectionMapper.select(hlsCusPostloanInspection);
        String type = hlsCusPostloanInspectionList.get(0).getInspectionType();
        PlmPliCheckItem plmPliCheckItem = new PlmPliCheckItem();
        plmPliCheckItem.setPostloanInspectionId(hlsCusPostloanInspection.getPostloanInspectionId());
        plmPliCheckItem.setListCategory("PLM");
        if (PlmPliFrequencySetUtilImpl.ON_SITE_INSPECT.equalsIgnoreCase(type)) {
            for (PliLeaseItem leaseItem : dto) {
                String[] ListType = {"SURVEY", "PAWN", "LEASE", "GUARANTOR", "LESSEE"};
                plmPliCheckItem.setPlmPliItemId(leaseItem.getPlmPliItemId());
                plmPliCheckItemService.createItemForPli(plmPliCheckItem, ListType);
            }
        } else if (PlmPliFrequencySetUtilImpl.OFF_SITE_INSPECT.equalsIgnoreCase(type)) {
            String[] ListType = {"OFF_SURVEY"};
            plmPliCheckItemService.createItemForPli(plmPliCheckItem, ListType);
        }
        else if (PlmPliFrequencySetUtilImpl.ON_SITE_INSPECT_PLANE.equalsIgnoreCase(type)) {
            for (PliLeaseItem leaseItem : dto) {
                String[] ListType = {"ON_SURVEY_PLANE"};
                plmPliCheckItem.setPlmPliItemId(leaseItem.getPlmPliItemId());
                plmPliCheckItemService.createItemForPli(plmPliCheckItem, ListType);
            }
        }
        return new ResponseData(list);
    }

    @RequestMapping(value = "/plm/pli/lease/item/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<PliLeaseItem> dto) {
        PlmPliCheckItem plmPliCheckItem = new PlmPliCheckItem();
        plmPliCheckItem.set__status("delete");
        plmPliCheckItem.setPlmPliItemId(dto.get(0).getPlmPliItemId());
        plmPliCheckItemService.delete(plmPliCheckItem);
        service.batchDelete(dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/plm/pli/lease/item/delete")
    @ResponseBody
    public ResponseData deleteItem(HttpServletRequest request, @RequestBody List<PliLeaseItem> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}
