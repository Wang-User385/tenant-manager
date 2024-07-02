package com.hand.hls.sys.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.FndCompany;
import java.util.List;

public interface FndCompanyMapper extends Mapper<FndCompany> {
    List<FndCompany> selectCompany(FndCompany var1);

    FndCompany selectCompanyDetail(FndCompany var1);

    List<FndCompany> queryAll(FndCompany var1);

    List<FndCompany> queryAllForLov(FndCompany var1);
}
