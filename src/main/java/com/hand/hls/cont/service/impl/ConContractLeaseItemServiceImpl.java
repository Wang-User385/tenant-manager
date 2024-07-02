package com.hand.hls.cont.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.account.dto.User;
import com.hand.hap.account.mapper.UserMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractLeaseItem;
import com.hand.hls.cont.mapper.HlsCusConContractLeaseItemMapper;
import com.hand.hls.cont.service.IConContractLeaseItemService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.cont.utils.ConContractUtil;
import com.hand.hls.lease.dto.LeaseItemInsurance;
import com.hand.hls.lease.service.ILeaseItemInsuranceService;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.*;
/*import com.hand.hls.prj.service.ILeaseItemInsuranceService;
import com.hand.hls.prj.service.ILeaseItemInvoiceService;
import com.hand.hls.prj.service.IPrjLeaseItemInvoiceService;
import com.hand.hls.wsdl.service.WsdlService;*/
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class ConContractLeaseItemServiceImpl extends BaseServiceImpl<HlsCusConContractLeaseItem> implements IConContractLeaseItemService {

    @Autowired
    private HlsCusPrjProjectLeaseItemMapper prjProjectLeaseItemMapper;
    @Autowired
    private PrjLeaseItemInsuranceMapper prjLeaseItemInsuranceMapper;
    @Autowired
    private ILeaseItemInsuranceService iLeaseItemInsuranceService;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsCusConContractLeaseItemMapper hlsCusConContractLeaseItemMapper;
    @Autowired
    private UserMapper userMapper;
/*    @Autowired
    private HlsBpMasterMapper hlsBpMasterMapper;*/
    /*@Autowired
    private PrjLeaseItemInvoiceMapper prjLeaseItemInvoiceMapper;
    @Autowired
    private ILeaseItemInvoiceService leaseItemInvoiceService;*/


    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<HlsCusConContractLeaseItem> saveLeaseItemFromPrj(IRequest iRequest, Long contractId, Long projectId) {
        List<HlsCusConContractLeaseItem> conContractLeaseItems = new ArrayList<>();
        HlsCusPrjProjectLeaseItem queryPrjProjectLeaseItem = new HlsCusPrjProjectLeaseItem();
        queryPrjProjectLeaseItem.setProjectId(projectId);
        List<HlsCusPrjProjectLeaseItem> hlsCusPrjProjectLeaseItemList = prjProjectLeaseItemMapper.select(queryPrjProjectLeaseItem);
        for (HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem : hlsCusPrjProjectLeaseItemList) {
            HlsCusConContractLeaseItem conContractLeaseItem = new HlsCusConContractLeaseItem();
            BeanRefUtils.beanToBean(hlsCusPrjProjectLeaseItem, conContractLeaseItem, hlsBeanRefUtilService);
            conContractLeaseItem.setContractId(contractId);
            conContractLeaseItems.add(self().insert(iRequest, conContractLeaseItem));
            //复制保险信息
            PrjLeaseItemInsurance itemInsurance = new PrjLeaseItemInsurance();
            itemInsurance.setProjectLeaseItemId(hlsCusPrjProjectLeaseItem.getProjectLeaseItemId());
            List<PrjLeaseItemInsurance> itemInsurances = prjLeaseItemInsuranceMapper.select(itemInsurance);
            for(PrjLeaseItemInsurance prjLeaseItemInsurance:itemInsurances){
                LeaseItemInsurance leaseItemInsurance = new LeaseItemInsurance();
                BeanRefUtils.beanToBean(prjLeaseItemInsurance, leaseItemInsurance, hlsBeanRefUtilService);
                leaseItemInsurance.setConLeaseItemId(conContractLeaseItem.getContractLeaseItemId());
                iLeaseItemInsuranceService.insert(iRequest,leaseItemInsurance);
            }
        }
        return conContractLeaseItems;
    }

    /*@Override
    public HlsCusConContractLeaseItem queryLeaseItem(IRequest iRequest, Long contractId, String partnersLeaseItemId) {
        return hlsCusConContractLeaseItemMapper.queryLeaseItem(contractId, partnersLeaseItemId);
    }*/


    @Override
    public List<Map<String,Object>> queryContractLeaseItem(IRequest iRequest, Map<String, Object> conContract, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        ConContractUtil.addHumpToMap(conContract);
        return hlsCusConContractLeaseItemMapper.queryContractLeaseItem(conContract);
    }

    @Override
    public void leaseItemQuery(IRequest requestContext, Map<String, Object> conContract) {
        Long userId = requestContext.getUserId();
        Long bpId = requestContext.getUserId();
        String bpType = null;
        if (userId != null) {
            User user = userMapper.selectByPrimaryKey(userId);
            bpId = user.getBpId();
            conContract.put("authorityBpId", bpId);
        }
        /*if (bpId != null) {
            HlsBpMaster hlsBpMaster = hlsBpMasterMapper.selectByPrimaryKey(bpId);
            conContract.put("authorityBpId", bpId);
            if (hlsBpMaster != null) {
                bpType = hlsBpMaster.getBpType();
            }
        }*/
        if (StringUtils.isNotEmpty(bpType)) {
            conContract.put("bpType", bpType);
        }

    }

}
