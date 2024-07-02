package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.YhFactoryDelayTerm;
import org.apache.ibatis.annotations.Param;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Primary
public interface YhFactoryDelayTermMapper extends Mapper<YhFactoryDelayTerm> {
    List<YhFactoryDelayTerm> yhFactoryDelayTermQuery(@Param("manufacturerId")Long manufacturerId,@Param("factoryId")Long factoryId,@Param("manufacturerIdN")String manufacturerIdN,@Param("factoryIdN")String factoryIdN);
}
