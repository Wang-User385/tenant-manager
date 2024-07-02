package com.hand.hls.csh.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.dto.HlsCusCshConContract;
import com.hand.hls.csh.mapper.HlsCusCshConContractMapper;
import com.hand.hls.csh.service.ICshConContractService;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;


@Service
@Transactional
public class CshConContractServiceImpl extends BaseServiceImpl<HlsCusCshConContract> implements ICshConContractService {

    @Autowired
    private HlsCusCshConContractMapper mapper;

    @Override
    public List<HlsCusCshConContract> contractHomeSecondQuery(Map<String, Object> map, int page, int pagesize) {
        PageHelper.startPage(page,pagesize);
        return mapper.cshPaymentTodo(map);
    }
    @Override
    public List<HlsCusCshConContract> csh001contractHomeSecondQuery(Map<String, Object> map, int page, int pagesize,String sortName,String sortOrder) {
        String orderBy = null;
        if(sortName!=null){
            if(orderBy==null){
                orderBy=sortName+" "+sortOrder;
            }else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }
        PageHelper.startPage(page,pagesize);
        if(StringUtils.isNotEmpty(orderBy)){
            PageHelper.orderBy(orderBy);
        }
        return mapper.csh001PaymentTodo(map);
    }
    @Override
    public List<HlsCusCshConContract> contractHomeThirdQuery(Map<String, Object> map) {
		/*final List<String> paymentList = new ArrayList<String>();
		boolean paymentFlag = false;
		if(!"N".equals(map.get("payment1"))) {
			paymentFlag = true;
			paymentList.add((String) map.get("payment1"));
		}
		if(!"N".equals(map.get("payment2"))) {
			paymentFlag = true;
			paymentList.add((String) map.get("payment2"));
		}
		if(!"N".equals(map.get("payment3"))) {
			paymentFlag = true;
			paymentList.add((String) map.get("payment3"));
		}
		map.put("paymentList", paymentList);
		map.put("paymentFlag", paymentFlag);*/
        return mapper.contractHomeThirdQuery(map);
    }
}
