package com.hand.hls.pam.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.pam.mapper.HlsWarrantStockLnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.pam.dto.HlsWarrantStockLn;
import com.hand.hls.pam.service.IHlsWarrantStockLnService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsWarrantStockLnServiceImpl extends BaseServiceImpl<HlsWarrantStockLn> implements IHlsWarrantStockLnService{

    @Autowired
    private HlsWarrantStockLnMapper hlsWarrantStockLnMapper;

    @Override
    public List<HlsWarrantStockLn> warrantStockCheck(IRequest iRequest, List<HlsWarrantStockLn> list) throws HlsCusException {

        if(list != null && list.size() > 0) {
            if (list.get(0).getWarrantId() == null) {
                throw new HlsCusException("关键参数获取失败!");
            }
            List<Long> warrantIdList = new ArrayList<>();
            for(HlsWarrantStockLn warrantStockLn : list){
                if(warrantStockLn.getWarrantId() != null){
                    warrantIdList.add(warrantStockLn.getWarrantId());
                }
            }
            Long[] warrantIdS = new Long[warrantIdList.size()];
            HlsWarrantStockLn hlsWarrantStockLn = new HlsWarrantStockLn();
            hlsWarrantStockLn.setWarrantIdList(warrantIdList.toArray(warrantIdS));
            //校验是否存在 关联的 审批中的 出入库申请单据
            HlsWarrantStockLn stockLn = hlsWarrantStockLnMapper.warrantStockCheck(hlsWarrantStockLn);
            if(stockLn.getApproveCount() > 0){
                throw new HlsCusException( "权证名称为: " + stockLn.getWarrantName() + " 的权证已经关联审批中的出入库申请单据！");
            }

        }


        return list;

    }




}