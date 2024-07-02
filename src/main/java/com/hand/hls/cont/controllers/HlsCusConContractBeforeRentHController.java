package com.hand.hls.cont.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.HlsCusConContractBeforeRentH;
import com.hand.hls.cont.service.HlsCusConContractBeforeRentHService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HlsCusConContractBeforeRentHController extends BaseController {

    @Autowired
    private HlsCusConContractBeforeRentHService service;


    @RequestMapping(value = "/con/contract/before/rent/h/query")
    @ResponseBody
    public ResponseData query(HlsCusConContractBeforeRentH dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        Long seqNumber = 0L;
        dto.setSortname("receivable_date");
        dto.setSortorder("asc");
        List<HlsCusConContractBeforeRentH> hlsCusConContractBeforeRentHList = service.select(requestContext, dto, page, pageSize);
        List<HlsCusConContractBeforeRentH> lists = new ArrayList<>();
        for (HlsCusConContractBeforeRentH contractBeforeRent : hlsCusConContractBeforeRentHList) {
            seqNumber = seqNumber + 1;
            contractBeforeRent.setSeqNumber(seqNumber);
            lists.add(contractBeforeRent);
        }
        return new ResponseData(lists);
    }


}