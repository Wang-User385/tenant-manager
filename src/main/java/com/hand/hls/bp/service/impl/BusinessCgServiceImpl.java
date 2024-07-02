//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.BusinessCategory;
import com.hand.hls.bp.mapper.BusinessCgMapper;
import com.hand.hls.bp.mapper.BusinessCgNewMapper;
import com.hand.hls.bp.mapper.BusinessTyMapper;
import com.hand.hls.bp.service.IBusinessCgService;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BusinessCgServiceImpl extends BaseServiceImpl<BusinessCategory> implements IBusinessCgService {
    @Autowired
    BusinessCgMapper businessCgMapper;
    @Autowired
    BusinessTyMapper businessTyMapper;
    @Autowired
    BusinessCgNewMapper businesscgnewmapper;
    public BusinessCgServiceImpl() {
    }

    public void businessMasterBatchDelete(List<BusinessCategory> demos) {
        this.batchDelete(demos);
        Iterator var2 = demos.iterator();

        while(var2.hasNext()) {
            BusinessCategory bc = (BusinessCategory)var2.next();
            this.businessTyMapper.deleteBusinessTy(bc.getBpCategory());
        }

    }

    public List<BusinessCategory> selectList(BusinessCategory businessCategory, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return this.businessCgMapper.selectList(businessCategory);
    }

    public List<BusinessCategory> bpMasterOtherQuery(BusinessCategory businessCategory, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return this.businessCgMapper.bpMasterOtherQuery(businessCategory);
    }

    public List<BusinessCategory> usageCategory() {
        return businesscgnewmapper.usageCategory();
    }
}
