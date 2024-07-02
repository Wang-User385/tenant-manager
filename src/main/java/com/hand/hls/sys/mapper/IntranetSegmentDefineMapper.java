package com.hand.hls.sys.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.sys.dto.IntranetSegmentDefine;

import java.util.List;

/**
 * @Description：内网网段定义
 * @Author：liangxian.chen@hand-china.com
 * @Date：2023/3/1 14:44
 * @Version：1.0
 */

public interface IntranetSegmentDefineMapper extends Mapper<IntranetSegmentDefine>{

    /**
     * 根据条件获取内网网段定义信息
     * @param define 查询条件
     * @return 所有符合条件的记录
     */
    List<IntranetSegmentDefine> selectIntranetSegmentDefineInfo(IntranetSegmentDefine define);

}