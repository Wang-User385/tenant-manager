//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.ast.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.ast.dto.AstFiveClassTarget;
import com.hand.hls.ast.mapper.AstFiveClassTargetMapper;
import com.hand.hls.ast.service.IAstFiveClassTargetService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AstFiveClassTargetServiceImpl extends BaseServiceImpl<AstFiveClassTarget> implements IAstFiveClassTargetService {
    @Autowired
    private AstFiveClassTargetMapper astFiveClassTargetMapper;

    public AstFiveClassTargetServiceImpl() {
    }

    public List<AstFiveClassTarget> queryAll(AstFiveClassTarget astFiveClassTarget, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return this.astFiveClassTargetMapper.queryAll1(astFiveClassTarget);
    }
}
