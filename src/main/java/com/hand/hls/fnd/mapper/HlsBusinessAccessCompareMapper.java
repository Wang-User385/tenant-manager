package com.hand.hls.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.HlsBusinessAccessCompare;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface HlsBusinessAccessCompareMapper extends Mapper<HlsBusinessAccessCompare>{

    List<HlsBusinessAccessCompare>  queryAll();
    

}