package com.hand.hls.plm.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.plm.dto.HlsCusShips;

import java.util.List;

public interface HlsCusShipsMapper extends Mapper<HlsCusShips> {
    /**
     * 根据项目ID查询物料是船舶的ID
     * @param projectId 项目ID
     * @return 船舶的ID
     */
    List<Float> selectShipInfoIdsByProjectId(Long projectId);

    /**
     *查询船舶租赁物信息
     * @param ship
     * @return
     */
    List<HlsCusShips> selectShip(HlsCusShips ship);
}
