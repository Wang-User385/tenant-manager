package com.hand.hls.plm.pli.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.plm.pli.dto.HlsCusPostloanInspectionConclusionH;
import com.hand.hls.plm.pli.mapper.HlsCusPostloanInspectionConclusionHMapper;
import com.hand.hls.plm.pli.service.HlsCusIPostloanInspectionConclusionHService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description:贷后检查历史结论serviceImpl
 * @Author: Wty
 * @Date: Created om 16:28 2018/5/24
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPostloanInspectionConclusionHServiceImpl extends BaseServiceImpl<HlsCusPostloanInspectionConclusionH> implements HlsCusIPostloanInspectionConclusionHService {

    @Autowired
    private HlsCusPostloanInspectionConclusionHMapper mapper;

    /**
     * @Description:查询对应的贷后检查历史结论
     * @Author: Wty
     * @Date: Created om 16:20 2018/5/25
     */
    @Override
    public List<HlsCusPostloanInspectionConclusionH> selectConclusionH(IRequest iRequest, HlsCusPostloanInspectionConclusionH postloanInspectionConclusionH, int page, int pageSize) {
        Long postloadInspectionId = postloanInspectionConclusionH.getPostloanInspectionId();
        //如果有贷后检查id则根据贷后检查Id来查
        if (postloadInspectionId != null && postloadInspectionId != 0) {
            PageHelper.startPage(page, pageSize);
            return mapper.selectPostLoanConclusionH(postloanInspectionConclusionH);
        }else {
            //没有贷后检查id，则说明是新建的，通过bpId查找贷后检查表
            return mapper.selectPostLoanConclusionHByBpId(postloanInspectionConclusionH);
        }
    }
}