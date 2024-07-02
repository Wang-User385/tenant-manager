package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsScoreTarget;
import com.hand.hls.bp.dto.QujianDataSource;

import java.util.List;

public interface IQujianDataSourceService extends IBaseService<QujianDataSource>, ProxySelf<IQujianDataSourceService>{
    List<QujianDataSource> selectQujianDataSource(IRequest var1, QujianDataSource var2, int var3, int var4);
}