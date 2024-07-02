package com.hand.hls.fnd.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.HlsCusDocumentList;
import com.hand.hls.fnd.mapper.HlsCusDocumentListMapper;
import com.hand.hls.fnd.service.HlsCusDocumentListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:附件清单ServiceImpl
 * @Author: wty
 * @Date: Created in 0:16 2018/4/16
 */
@Service
public class HlsCusDocumentListServiceImpl extends BaseServiceImpl<HlsCusDocumentList> implements HlsCusDocumentListService {

    @Autowired
    private HlsCusDocumentListMapper hlsCusDocumentListMapper;

    @Override
    public List<HlsCusDocumentList> selectList(IRequest iRequest, HlsCusDocumentList hlsCusDocumentList, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return hlsCusDocumentListMapper.selectList(hlsCusDocumentList);
    }

    /**
     * @Description:商业伙伴附件清单初始
     * @Author: Wty
     * @Date: Created om 0:19 2018/4/16
     * @param: [iRequest, hlsCusDocumentList, page, pagesize]
     * @return: java.util.List<hls.core.fnd.dto.HlsCusDocumentList>
     */
    @Override
    public List<HlsCusDocumentList> BpMasterListInit(IRequest iRequest, HlsCusDocumentList hlsCusDocumentList, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return hlsCusDocumentListMapper.BpMasterListInit(hlsCusDocumentList);
    }

    @Override
    public List<HlsCusDocumentList> selectByCategoryAndType(HlsCusDocumentList hlsCusDocumentList) {
        return hlsCusDocumentListMapper.selectByCategoryAndType(hlsCusDocumentList);
    }

}