package com.hand.hls.prj.mapper;


import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.ZxBpOrgbase;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ZxBpOrgbaseMapper extends Mapper<ZxBpOrgbase> {
    List<ZxBpOrgbase> queryZxBpOrgbase(Map params);
    List<ZxBpOrgbase> queryZxBpOrgbase2(Map params);

    /**
     * 更新授信信息
     */
    int updateZxBpOrgbase(@Param("bpId") Long bpId, @Param("regnotype")String regnotype, @Param("regno")String regno);
}
