//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.FndScoreTempletType;
import com.hand.hls.bp.mapper.FndScoreTempletTypeMapper;
import com.hand.hls.bp.service.IFndScoreTempletTypeService;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FndScoreTempletTypeServiceImpl extends BaseServiceImpl<FndScoreTempletType> implements IFndScoreTempletTypeService {
    @Autowired
    private FndScoreTempletTypeMapper fndScoreTempletTypeMapper;

    public FndScoreTempletTypeServiceImpl() {
    }

    public List<FndScoreTempletType> query(IRequest request, FndScoreTempletType dto, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return this.fndScoreTempletTypeMapper.query(dto);
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    public int batchDelete(IRequest requestContext, List<FndScoreTempletType> resources) {
        int result = 0;
        if (CollectionUtils.isEmpty(resources)) {
            return result;
        } else {
            for(Iterator var4 = resources.iterator(); var4.hasNext(); ++result) {
                FndScoreTempletType resource = (FndScoreTempletType)var4.next();
                ((IFndScoreTempletTypeService)this.self()).deleteByPrimaryKey(resource);
            }

            return result;
        }
    }
}
