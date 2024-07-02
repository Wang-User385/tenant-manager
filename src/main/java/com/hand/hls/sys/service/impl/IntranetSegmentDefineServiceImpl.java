package com.hand.hls.sys.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.ServiceExecutionAdvice;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.sys.mapper.IntranetSegmentDefineMapper;
import com.hand.hls.utils.IPUtils;
import com.hand.hls.utils.ResMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.sys.dto.IntranetSegmentDefine;
import com.hand.hls.sys.service.IIntranetSegmentDefineService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description：内网网段定义
 * @Author：liangxian.chen@hand-china.com
 * @Date：2023/3/1 14:44
 * @Version：1.0
 */

@Service
public class IntranetSegmentDefineServiceImpl extends BaseServiceImpl<IntranetSegmentDefine> implements IIntranetSegmentDefineService{

    private Logger logger = LoggerFactory.getLogger(ServiceExecutionAdvice.class);

    @Autowired
    private IntranetSegmentDefineMapper segmentDefineMapper;





    /**
     * 二期功能：内网网段定义批量插入或更新
     * @param iRequest
     * @param list
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public List<IntranetSegmentDefine> batchInsertUpdate(IRequest iRequest, List<IntranetSegmentDefine> list) throws Exception {
        //检验输入的IP是否合法
        for (IntranetSegmentDefine line : list) {
            if (!IPUtils.ipCheck(line.getIntranetFrom()) || !IPUtils.ipCheck(line.getIntranetTo())){
                throw new ResMessageException("填写的IP不合法！");
            }
        }
        return this.batchUpdate(iRequest, list);
    }

    /**
     * 根据条件获取内网网段定义信息
     *
     * @param define 查询条件
     * @return 所有符合条件的记录
     */
    @Override
    public List<IntranetSegmentDefine> getIntranetSegmentDefineInfo(IntranetSegmentDefine define) {
        return segmentDefineMapper.selectIntranetSegmentDefineInfo(define);
    }
}