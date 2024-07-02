package com.hand.hls.app.user.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.app.user.dto.HlsCusVersion;
import org.apache.ibatis.annotations.Param;

/**
 * @author liao
 */
public interface HlsCusVersionMapper extends Mapper<HlsCusVersion>{

    /**
     * 统一 修改版本号
     * @param appVersion 版本号
     */
    void updateAppVersion(@Param("appVersion") String appVersion);
}