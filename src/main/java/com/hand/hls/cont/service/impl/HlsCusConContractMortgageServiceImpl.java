package com.hand.hls.cont.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.HlsCusConContractMortgage;
import com.hand.hls.cont.service.HlsCusConContractMortgageService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.prj.dto.HlsCusPrjProjectMortgage;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMortgageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractMortgageServiceImpl extends BaseServiceImpl<HlsCusConContractMortgage> implements HlsCusConContractMortgageService {

    @Autowired
    private HlsCusPrjProjectMortgageMapper prjProjectMortgageMapper;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    /**
     * 二期功能：复制项目上的抵押物到合同相关的表上
     *
     * @param iRequest
     * @param contractId
     * @param projectId
     * @return
     */
    @Override
    public List<HlsCusConContractMortgage> saveMortgageFromPrj(IRequest iRequest, Long contractId, Long projectId) {
        List<HlsCusConContractMortgage> conContractMortgages = new ArrayList<>();

        HlsCusPrjProjectMortgage prjProjectMortgage = new HlsCusPrjProjectMortgage();
        prjProjectMortgage.setProjectId(projectId);
        List<HlsCusPrjProjectMortgage> hlsCusPrjProjectMortgageList = prjProjectMortgageMapper.select(prjProjectMortgage);
        for (HlsCusPrjProjectMortgage hlsCusPrjProjectMortgage : hlsCusPrjProjectMortgageList) {
            HlsCusConContractMortgage conContractMortgage = new HlsCusConContractMortgage();
            BeanRefUtils.beanToBean(hlsCusPrjProjectMortgage, conContractMortgage, hlsBeanRefUtilService);
            conContractMortgage.setContractId(contractId);
            self().insertSelective(iRequest,conContractMortgage);
            conContractMortgages.add(conContractMortgage);
        }

        return conContractMortgages;
    }
}