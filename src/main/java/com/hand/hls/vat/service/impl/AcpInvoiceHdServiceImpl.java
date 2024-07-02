package com.hand.hls.vat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.common.dto.HlsCusHapInterfaceOutbound;
import com.hand.hls.common.service.HlsCusHapInterfaceOutboundService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.prj.dto.HlsCusPrjProjectLeaseItem;
import com.hand.hls.prj.mapper.HlsCusPrjProjectLeaseItemMapper;
import com.hand.hls.utils.HttpClientUtils;
import com.hand.hls.utils.HttpExecuteResponse;
import com.hand.hls.vat.dto.HlsCusAcpInvoiceHd;
import com.hand.hls.vat.dto.HlsCusAcpInvoiceLn;
import com.hand.hls.vat.mapper.HlsCusAcpInvoiceHdMapper;
import com.hand.hls.vat.service.AcpInvoiceHdService;
import com.hand.hls.vat.service.IAcpInvoiceLnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLDecoder;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

@Service
@Transactional
public class AcpInvoiceHdServiceImpl extends BaseServiceImpl<HlsCusAcpInvoiceHd> implements AcpInvoiceHdService {

    @Value("${file.upload.dir:.}")
    private String savePath = ".";
    @Autowired
    private IFndAttachmentService fndAttachmentService;
    @Autowired
    private HlsCusHapInterfaceOutboundService hapInterfaceOutboundService;
    @Autowired
    private IAcpInvoiceLnService iAcpInvoiceLnService;
    @Autowired
    private HlsCusPrjProjectLeaseItemMapper hlsCusPrjProjectLeaseItemMapper;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
//    @Autowired
//    HlsCusAcpInvoiceHdMapper mapper;
//
//    public AcpInvoiceHdServiceImpl() {
//    }
//
//    public List<HlsCusAcpInvoiceHd> acpInvoiceQuery(Map<String, String> map) {
//        Map<String, Object> params = new HashMap();
//        params.put("count", map.get("count"));
//        params.put("diff", map.get("diff"));
//        params.put("flag", map.get("flag"));
//        String[] Items = null;
//        if (map.get("cashflowItems") != "") {
//            Items = ((String)map.get("cashflowItems")).split("、");
//        }
//
//        params.put("cashflowItems", Items);
//        params.put("amountFrom", map.get("amountFrom"));
//        params.put("amountTo", map.get("amountTo"));
//        params.put("contractInfo", map.get("contractInfo"));
//        params.put("timesFrom", map.get("timesFrom"));
//        params.put("timesTo", map.get("timesTo"));
//        return this.mapper.acpInvoiceQuery(params);
//    }
//
//    public List<HlsCusAcpInvoiceHd> acpInvoiceScaleQuery() {
//        return this.mapper.acpInvoiceScaleQuery();
//    }
//
//    public List<HlsCusAcpInvoiceHd> acpInvoicedTotleQuery() {
//        List<HlsCusAcpInvoiceHd> list = this.mapper.acpInvoicedTotleQuery();
//        List<HlsCusAcpInvoiceHd> list1 = new LinkedList();
//        HlsCusAcpInvoiceHd[] cpInvoiceHds = new HlsCusAcpInvoiceHd[6];
//
//        for(int i = 0; i < cpInvoiceHds.length; ++i) {
//            cpInvoiceHds[i] = new HlsCusAcpInvoiceHd();
//        }
//
//        cpInvoiceHds[0].setCfItem(-99L);
//        cpInvoiceHds[0].setTotalAmount(0.0D);
//        cpInvoiceHds[1].setCfItem(1L);
//        cpInvoiceHds[1].setTotalAmount(0.0D);
//        cpInvoiceHds[2].setCfItem(2L);
//        cpInvoiceHds[2].setTotalAmount(0.0D);
//        cpInvoiceHds[3].setCfItem(3L);
//        cpInvoiceHds[3].setTotalAmount(0.0D);
//        cpInvoiceHds[4].setCfItem(8L);
//        cpInvoiceHds[4].setTotalAmount(0.0D);
//        cpInvoiceHds[5].setCfItem(9L);
//        cpInvoiceHds[5].setTotalAmount(0.0D);
//        double sum = 0.0D;
//
//        int i;
//        for(i = 0; i < list.size(); ++i) {
//            HlsCusAcpInvoiceHd acp = (HlsCusAcpInvoiceHd)list.get(i);
//            if (acp.getCfItem() != 1L && acp.getCfItem() != 2L && acp.getCfItem() != 3L && acp.getCfItem() != 8L && acp.getCfItem() != 9L) {
//                sum += acp.getTotalAmount();
//            } else {
//                for(int j = 0; j < cpInvoiceHds.length; ++j) {
//                    if (acp.getCfItem() == cpInvoiceHds[j].getCfItem()) {
//                        cpInvoiceHds[j] = acp;
//                    }
//
//                    cpInvoiceHds[j].setSpecialNum(acp.getSpecialNum());
//                    cpInvoiceHds[j].setNormalNum(acp.getNormalNum());
//                }
//            }
//        }
//
//        cpInvoiceHds[0].setTotalAmount(sum);
//
//        for(i = 0; i < cpInvoiceHds.length; ++i) {
//            list1.add(cpInvoiceHds[i]);
//        }
//
//        return list1;
//    }
//
//    public List<HlsCusAcpInvoiceHd> acpInvoicedGroupQuery(IRequest requestContext) {
//        Long companyId = requestContext.getCompanyId();
//        return this.mapper.acpInvoicedGroupQuery(companyId);
//    }


}