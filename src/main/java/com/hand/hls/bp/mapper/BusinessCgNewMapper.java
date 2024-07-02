//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.BusinessCategory;
import java.util.List;

public interface BusinessCgNewMapper extends Mapper<BusinessCategory> {
    List<BusinessCategory> selectList(BusinessCategory var1);

    List<BusinessCategory> bpMasterOtherQuery(BusinessCategory var1);

    List<BusinessCategory> usageCategory();
}
