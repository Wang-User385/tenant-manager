package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusPrjProjectInsure;

import java.util.List;

public interface HlsCusPrjProjectInsureMapper extends Mapper<HlsCusPrjProjectInsure> {

    List<HlsCusPrjProjectInsure> queryInsureInfoByProjectId(HlsCusPrjProjectInsure hlsCusPrjProjectInsure);

    List<HlsCusPrjProjectInsure> queryContractInsureInfo(HlsCusPrjProjectInsure hlsCusPrjProjectInsure);

    List<HlsCusPrjProjectInsure> queryAll(HlsCusPrjProjectInsure hlsCusPrjProjectInsure);
    List<HlsCusPrjProjectInsure> queryAllConfirm(HlsCusPrjProjectInsure hlsCusPrjProjectInsure);

    List<HlsCusPrjProjectInsure> queryMes();
}