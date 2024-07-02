//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import java.util.Iterator;
import com.hand.hls.bp.dto.HlsCusBpMasterRelation;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.hand.hls.bp.service.HlsBpMasterRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HlsBpMasterRelationController extends BaseController {
    @Autowired
    private HlsBpMasterRelationService service;

    public HlsBpMasterRelationController() {
    }

    @RequestMapping({"/hls/bp/relation/query"})
    @ResponseBody
    public ResponseData query(HlsCusBpMasterRelation bpMasterRelation, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        return new ResponseData(this.service.queryAll(requestContext, bpMasterRelation, page, pagesize));
    }

    @RequestMapping({"/hls/bp/relation/submit"})
    @ResponseBody
    public ResponseData submit(@RequestBody List<HlsCusBpMasterRelation> bpMasterRelations, BindingResult result, HttpServletRequest request) {
        this.getValidator().validate(bpMasterRelations, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {
            IRequest requestContext = this.createRequestContext(request);
            if (bpMasterRelations != null) {
                Iterator var5 = bpMasterRelations.iterator();

                while(var5.hasNext()) {
                    HlsCusBpMasterRelation bpMasterRelation = (HlsCusBpMasterRelation)var5.next();
                    Long relationBpId = this.service.queryBpId(bpMasterRelation.getBpCode(), bpMasterRelation.getBpName());
                    bpMasterRelation.setRelationBpId(relationBpId);
                }
            }

            List<HlsCusBpMasterRelation> datas = this.service.batchRelationUpdate(requestContext, bpMasterRelations);
            return new ResponseData(datas);
        }
    }

    @RequestMapping({"/hls/bp/relation/remove"})
    @ResponseBody
    public ResponseData remove(@RequestBody List<HlsCusBpMasterRelation> lists) {
        this.service.batchRelationDelete(lists);
        return new ResponseData();
    }

    @RequestMapping({"/hls/bp/relationType/query"})
    @ResponseBody
    public ResponseData queryType(Long bpId) {
        return new ResponseData(this.service.selectRelationType(bpId));
    }

    @RequestMapping({"/hls/bp/relationCode/query"})
    @ResponseBody
    public ResponseData queryCode(Long bpId) {
        return new ResponseData(this.service.selectRelationCode(bpId));
    }

    @RequestMapping({"/hls/bp/relation/selectBpRelationType"})
    @ResponseBody
    public ResponseData selectBpRelationType(HlsCusBpMasterRelation bpMasterRelation, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        return new ResponseData(this.service.selectBpRelationType(requestContext, bpMasterRelation));
    }
}
