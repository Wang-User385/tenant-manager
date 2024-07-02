package com.hand.hls.cn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cn.dto.RentInfo;

import java.util.List;

public interface RentInfoMapper extends Mapper<RentInfo>{

    List<RentInfo> conRentDetailQueryAll(RentInfo rentInfo);
    List<RentInfo> conRentCashQueryAll(RentInfo rentInfo);
    List<RentInfo> conRentCashQueryAll1(RentInfo rentInfo);
    List<RentInfo> conRentHeaderQueryAll(RentInfo rentInfo);
}