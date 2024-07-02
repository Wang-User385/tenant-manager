package com.hand.hls.cont.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractBp;
import com.hand.hls.cont.mapper.HlsCusConContractBpMapper;
import com.hand.hls.cont.service.HlsCusConContractBpService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.fnd.service.HlsCusImpDataService;
import com.hand.hls.prj.dto.HlsCusPrjProjectBp;
import com.hand.hls.prj.mapper.HlsCusPrjProjectBpMapper;
import com.hand.hls.utils.HlsCusImportDataUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractBpServiceImpl extends BaseServiceImpl<HlsCusConContractBp> implements HlsCusConContractBpService {



    @Autowired
    private HlsCusImpDataService impDataService;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    @Autowired
    private HlsCusConContractBpMapper hlsCusConContractBpMapper;

    @Autowired
    private HlsCusPrjProjectBpMapper prjProjectBpMapper;

    /**
     * 通用导入示例
     */
    @Override
    public int importData(IRequest iRequest, List<Map<String, String>> dataMap, Map<String, String> descMap, String lang) {
        /**
         * 方式一 在此校验
         */
        for(Map<String,String> m:dataMap){
            for (Map.Entry<String, String> entry : m.entrySet()) {

            }
            //错误时更新
            impDataService.updateErrMessage(m,"");
        }



        try {
            List<Object> objects = HlsCusImportDataUtil.dataToDto(HlsCusConContractBp.class, dataMap, null);
            for(int i=0;i<objects.size();i++){
                HlsCusConContractBp hlsCusConContractBp=(HlsCusConContractBp)objects.get(i);
                //后续操作

                /**
                 * 方式二 在此校验
                 */

                //错误时更新
                impDataService.updateErrMessage(dataMap.get(i), "");
            }
        }  catch (Exception e) {
            e.printStackTrace();
        }
        //返回正确的条数
        return dataMap.size();
    }

    @Override
    public List<Map> queryByContractId(HlsCusConContractBp bp, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return hlsCusConContractBpMapper.queryByContractId(bp);
    }

    /**
     * 二期功能：进件投放审查通过后，复制项目bp
     *
     * @param iRequest
     * @param contractId
     * @param projectId
     * @return
     */
    @Override
    public List<HlsCusConContractBp> saveConContractBpFromPrj(IRequest iRequest, Long contractId, Long projectId) {
        //除了承租人的商业伙伴
        HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
        hlsCusPrjProjectBp.setProjectId(projectId);
        //过滤掉承租人
        List<HlsCusPrjProjectBp> prjProjectBpList = prjProjectBpMapper.select(hlsCusPrjProjectBp).stream().filter(o -> !"TENANT".equals(o.getBpCategroy())).collect(Collectors.toList());

        //承租人逻辑变更，共用一个承租人
        HlsCusPrjProjectBp tenantBp = new HlsCusPrjProjectBp();
        tenantBp.setProjectId(projectId);
        List<HlsCusPrjProjectBp> tenantList = prjProjectBpMapper.select(tenantBp).stream().filter(item -> "TENANT".equals(item.getBpCategroy())).collect(Collectors.toList());
        if (tenantList == null || tenantList.size() != 1) {
            throw new IllegalArgumentException("项目承租人信息有误!");
        }
        prjProjectBpList.addAll(tenantList);

        if (CollectionUtils.isEmpty(prjProjectBpList)) {
            throw new IllegalArgumentException("project bp不存在");
        }

        List<HlsCusConContractBp> hlsCusConContractBps = new ArrayList<>();
        for (HlsCusPrjProjectBp prjProjectBp : prjProjectBpList) {
            HlsCusConContractBp hlsCusConContractBp = new HlsCusConContractBp();
            BeanRefUtils.beanToBean(prjProjectBp, hlsCusConContractBp, hlsBeanRefUtilService);
            hlsCusConContractBp.setContractId(contractId);
            hlsCusConContractBp.setBpCategory(prjProjectBp.getBpCategroy());
            hlsCusConContractBp.setBpType(prjProjectBp.getBpType());
            hlsCusConContractBps.add(self().insert(iRequest, hlsCusConContractBp));
        }

        return hlsCusConContractBps;
    }

    /**
     * 二期功能：付款申请创建-付款对象银行信息Lov
     *
     * @param iRequest
     * @param bp
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<HlsCusConContractBp> queryPaymentBpBankInfoLov(IRequest iRequest, HlsCusConContractBp bp, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return this.hlsCusConContractBpMapper.queryPaymentBpBankInfoLov(bp);
    }
}