//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.vat.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.vat.dto.HlsCusAcpInvoiceLn;
import com.hand.hls.vat.dto.HlsInvoiceProfile;
import com.hand.hls.vat.mapper.HlsInvoiceProfileMapper;
import com.hand.hls.vat.service.HlsInvoiceProfileService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HlsInvoiceProfileController extends BaseController {
    @Autowired
    HlsInvoiceProfileService hlsInvoiceProfileService;
    @Autowired
    private HlsInvoiceProfileMapper hlsInvoiceProfileMapper;

    public HlsInvoiceProfileController() {
    }

    @RequestMapping({"/fnd/HlsInvoiceProfile/query"})
    @ResponseBody
    public ResponseData query(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        this.createRequestContext(request);

        JSONObject param = (JSONObject)requestData.get("parameter");
        String sortName=null;
        String sortOrder=null;
        if(param.get("sort_name")!=null){
            sortName = param.get("sort_name").toString();
        }
        if(param.get("sort_name")!=null){
            sortOrder=param.get("sort_order").toString();
        }

        String orderBy = null;
        if(sortName!=null){
            if(orderBy==null){
                orderBy=sortName+" "+sortOrder;
            }else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }
        PageHelper.startPage(pagenum,pagesize);
        if(StringUtils.isNotEmpty(orderBy)){
            PageHelper.orderBy(orderBy);
        }
        HlsInvoiceProfile dto = (HlsInvoiceProfile)param.toJavaObject(HlsInvoiceProfile.class);
        List<HlsInvoiceProfile> list  =hlsInvoiceProfileMapper.queryInvoiceProfile(dto);
        return new ResponseData(list);
    }

    @RequestMapping({"/fnd/HlsInvoiceProfile/submit"})
    @ResponseBody
    public ResponseData submit(@ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        ResponseData rd = null;
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsInvoiceProfile> hlsInvoiceProfiles = parameter.toJavaList(HlsInvoiceProfile.class);
        this.getValidator().validate(hlsInvoiceProfiles, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage("规则代码不可重复");
            return responseData;
        } else {
            try {
                IRequest requestContext = this.createRequestContext(request);

                for(int i = 0; i < hlsInvoiceProfiles.size(); ++i) {
                    if (((HlsInvoiceProfile)hlsInvoiceProfiles.get(i)).getCompanyId() == null) {
                        ((HlsInvoiceProfile)hlsInvoiceProfiles.get(i)).setCompanyId(new Long(requestContext.getCompanyId()));
                    }
                }

                return new ResponseData(this.hlsInvoiceProfileService.batchUpdate(requestContext, hlsInvoiceProfiles));
            } catch (Exception var9) {
                rd = new ResponseData(false);
                rd.setMessage("规则代码不可重复！");
                return rd;
            }
        }
    }

    @RequestMapping(
            value = {"/fnd/HlsInvoiceProfile/delete"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsInvoiceProfile> hlsInvoiceProfiles = parameter.toJavaList(HlsInvoiceProfile.class);
        IRequest requestContext = this.createRequestContext(request);
        this.hlsInvoiceProfileService.deleteInvoiceProfile(hlsInvoiceProfiles);
        RequestHelper.setCurrentRequest(requestContext);
        return new ResponseData(hlsInvoiceProfiles);
    }
}