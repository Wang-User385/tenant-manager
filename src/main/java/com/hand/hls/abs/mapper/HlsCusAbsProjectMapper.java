package com.hand.hls.abs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.abs.dto.HlsCusAbsProject;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusAbsProjectMapper extends Mapper<HlsCusAbsProject> {

    /**
     * ABS立项信息查询
     */
    List<HlsCusAbsProject> queryProjectDetail(HlsCusAbsProject hlsCusAbsProject);

    /**
     * ABS首页pie chart信息查询
     */
    List<HlsCusAbsProject> absPieChartQuery(HlsCusAbsProject hlsCusAbsProject);


    /**
     * 资产包管理
     * @param hlsCusAbsProject
     * @return
     */
    List<HlsCusAbsProject> selectProjectPackageData(HlsCusAbsProject hlsCusAbsProject);


    /**
     * 查询做了产品的立项个数
     * @param projectId
     * @return
     */
    int selectProductCountByProjectId(@Param("projectId") Long projectId);

}

