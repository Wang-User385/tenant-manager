package com.hand.hls.abs.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.abs.dto.HlsCusAbsAssetsPack;
import com.hand.hls.abs.service.HlsCusAbsAssetsPackService;
import hls.core.utils.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author yuanyuan 2019/04/01 9:03 PM
 */
@Controller
public class HlsCusAbsAssetsPackController extends BaseController {

    @Autowired
    private HlsCusAbsAssetsPackService service;


    /**
     * 查询
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/ct/abs/assets/pack/query")
    @ResponseBody
    public ResponseData query(HlsCusAbsAssetsPack dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectAssetsPack(requestContext, dto, page, pageSize));
    }

    //@RequestBody HlsCusAbsAssetsPack assetsPack,
    /**
     * 更新
     * @param assetsPack
     * @param result
     * @param request
     * @return
     */
    @RequestMapping(value = "/ct/abs/assets/pack/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws HlsCusException {

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusAbsAssetsPack assetsPack = param.toJavaObject(HlsCusAbsAssetsPack.class);

        getValidator().validate(assetsPack, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(Arrays.asList(service.submitAssetsPack(requestCtx,assetsPack)));
    }

    /**
     * 删除
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping(value = "/ct/abs/assets/pack/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusAbsAssetsPack> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    /**
     * 作废
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/ct/abs/assets/pack/cancel")
    @ResponseBody
    public ResponseData cancelPack(HlsCusAbsAssetsPack dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        service.cancelAssetsPack(requestContext,dto);
        return new ResponseData();
    }

    @RequestMapping("/ct/abs/assets/pack/lov")
    @ResponseBody
    public ResponseData getLovData(HlsCusAbsAssetsPack dto){
        return new ResponseData(service.getLovData(dto));
    }

    @InitBinder
    public void InitBinder(WebDataBinder binder, WebRequest request)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        //参数为true表示允许为空值
        CustomDateEditor editor = new CustomDateEditor(df,true);
        binder.registerCustomEditor(Date.class, editor);
    }
}
