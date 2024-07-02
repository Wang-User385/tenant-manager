package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusPrjChance;

import java.util.List;
import java.util.Map;

public interface HlsCusPrjChanceMapper extends Mapper<HlsCusPrjChance> {

    //项目立项首页环状图
    List<Map> prjHomePageGetAllStatusProjectCount(HlsCusPrjChance prjChance);
    //项目立项首页grid
    List<HlsCusPrjChance> prjHomePageProjectInfoGrid(HlsCusPrjChance prjChance);
    //立项变更历史查询
    List<Map> selectChangeReqInfo();
    //授信立项变更历史查询
    List<Map> selectCreditChangeReqInfo();
}