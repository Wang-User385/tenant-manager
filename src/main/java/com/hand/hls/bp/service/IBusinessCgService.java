//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.BusinessCategory;
import java.util.List;

public interface IBusinessCgService extends IBaseService<BusinessCategory>, ProxySelf<IBusinessCgService> {
    void businessMasterBatchDelete(List<BusinessCategory> var1);

    List<BusinessCategory> selectList(BusinessCategory var1, int var2, int var3);

    List<BusinessCategory> bpMasterOtherQuery(BusinessCategory var1, int var2, int var3);

    List<BusinessCategory> usageCategory();
}
