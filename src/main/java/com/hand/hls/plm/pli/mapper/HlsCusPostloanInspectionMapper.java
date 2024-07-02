package com.hand.hls.plm.pli.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.plm.pli.dto.HlsCusPostloanInspection;

import java.util.List;
import java.util.Map;

/**
 * @Description:贷后检查mapper
 * @Author: Wty
 * @Date: Created om 16:26 2018/5/24
 */
public interface HlsCusPostloanInspectionMapper extends Mapper<HlsCusPostloanInspection> {
    List<HlsCusPostloanInspection> selectBpLov(HlsCusPostloanInspection postloanInspection);

    HlsCusPostloanInspection selectInspection(HlsCusPostloanInspection postloanInspection);

    List<HlsCusPostloanInspection> homeRollTableQuery(HlsCusPostloanInspection postloanInspection);

    List<Map<String, Object>> selectUserInfo(Map<String, Object> map);

    List<Map<String, Object>> selectBpContractInfo(Map<String, Object> map);

    List<Map<String, Object>> selectCompanyInfo(Map<String, Object> map);

    List<Map<String, Object>> selectInceptContractsCreatedByBpId(Map<String, Object> map);

    List<HlsCusPostloanInspection> queryAll(HlsCusPostloanInspection postloanInspection);

}