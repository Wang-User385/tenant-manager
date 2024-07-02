package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.BpCreditMotherline;

import java.util.List;

public interface BpCreditMotherlineMapper extends Mapper<BpCreditMotherline>{

    List<BpCreditMotherline> queryAll(BpCreditMotherline bpCreditMotherline);

}