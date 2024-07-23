package com.hand.hls.mort.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.HlsCusConContractLeaseItem;
import com.hand.hls.cont.mapper.HlsCusConContractLeaseItemMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.mort.dto.HlsMortgage;
import com.hand.hls.mort.mapper.HlsMortgageMapper;
import com.hand.hls.mort.service.HlsMortgageService;
import com.hand.hls.prj.dto.HlsCusPrjProjectLeaseItem;
import com.hand.hls.prj.mapper.HlsCusPrjProjectLeaseItemMapper;
import com.hand.hls.utils.ResMessageException;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HlsMortgageController extends BaseController{

    @Autowired
    private HlsMortgageService hlsMortgageService;
    @Autowired
    private HlsMortgageMapper hlsMortgageMapper;

    @Autowired
    private HlsCusPrjProjectLeaseItemMapper hlsCusPrjProjectLeaseItemMapper;
    @Autowired
    private HlsCusConContractLeaseItemMapper hlsCusConContractLeaseItemMapper;
    @Autowired
    FndCodingRuleValuesService fndCodingRuleValuesService;


    @RequestMapping(value = "/mort/save")
    @ResponseBody
    public ResponseData save(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsMortgage dto = param.toJavaObject(HlsMortgage.class);

        //后台生成编码规则
        /*Map<String, String> params = new HashMap<String, String>();
        String mortgageNumber = fndCodingRuleValuesService.getCodeRuleValue(requestContext, "HLS_MORTGAGE", "HLS_MORTGAGE", "HLS_MORTGAGE", params);*/

        /*HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = new HlsCusPrjProjectLeaseItem();
        hlsCusPrjProjectLeaseItem.setProjectId(dto.getProjectId());
        List<HlsCusPrjProjectLeaseItem> select = hlsCusPrjProjectLeaseItemMapper.select(hlsCusPrjProjectLeaseItem);
        for (HlsCusPrjProjectLeaseItem cusPrjProjectLeaseItem : select) {
            HlsMortgage hlsMortgage = new HlsMortgage();
            hlsMortgage.setProjectLeaseItemId(cusPrjProjectLeaseItem.getProjectLeaseItemId());
            //hlsMortgage.setMortgageNumber(mortgageNumber);
            hlsMortgage.setProjectId(dto.getProjectId());
            hlsMortgage.setProjectNumber(dto.getProjectNumber());

            hlsMortgageMapper.insertSelective(hlsMortgage);
        }*/
        HlsCusConContractLeaseItem hlsCusConContractLeaseItem = new HlsCusConContractLeaseItem();
        hlsCusConContractLeaseItem.setContractId(dto.getContractId());
        List<HlsCusConContractLeaseItem> select2 = hlsCusConContractLeaseItemMapper.select(hlsCusConContractLeaseItem);
        for(HlsCusConContractLeaseItem cusConContractLeaseItem :select2){
            HlsMortgage hlsMortgage = new HlsMortgage();
            hlsMortgage.setContractLeaseItemId(cusConContractLeaseItem.getContractLeaseItemId());
            //hlsMortgage.setMortgageNumber(mortgageNumber);
            hlsMortgage.setContractId(dto.getContractId());
            hlsMortgage.setContractNumber(dto.getContractNumber());

            hlsMortgageMapper.insertSelective(hlsMortgage);
        }

        return new ResponseData(true);

        //return new ResponseData(hlsMortgageService.save(requestContext,dto));
    }

    @RequestMapping("/hls/lease/mortgage/submit")
    @ResponseBody
    public ResponseData submit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws ResMessageException {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsMortgage dto = param.toJavaObject(HlsMortgage.class);
        return new ResponseData(hlsMortgageService.submitWfl(dto,requestCtx));
    }


}