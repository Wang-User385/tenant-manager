package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.QujianDataSource;

import java.util.List;

public interface QujianDataSourceMapper extends Mapper<QujianDataSource>{
    List<QujianDataSource> selectQujianDataSource(QujianDataSource qujianDataSource);
}