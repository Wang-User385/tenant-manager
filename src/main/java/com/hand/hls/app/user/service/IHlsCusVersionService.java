package com.hand.hls.app.user.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.app.user.dto.HlsCusVersion;

import java.util.List;

public interface IHlsCusVersionService extends IBaseService<HlsCusVersion>, ProxySelf<IHlsCusVersionService>{

    /**
     * 查询 版本1
     * @param hlsCusVersion  请求对象
     * @return 返回结果集
     */
    List<HlsCusVersion> selectVersion(HlsCusVersion hlsCusVersion);
    /**
     * 修改 版本1
     * @param hlsCusVersion  请求对象
     * @return 返回结果集
     */
    void updateVersion(HlsCusVersion hlsCusVersion);
}