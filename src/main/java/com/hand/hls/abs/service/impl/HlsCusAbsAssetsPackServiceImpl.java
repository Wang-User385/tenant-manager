package com.hand.hls.abs.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.HlsCusAbsAssetsPack;
import com.hand.hls.abs.dto.HlsCusAbsAssetsPackage;
import com.hand.hls.abs.mapper.HlsCusAbsAssetsPackMapper;
import com.hand.hls.abs.service.HlsCusAbsAssetsPackService;
import com.hand.hls.abs.service.HlsCusAbsAssetsPackageService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.utils.HlsCusConstant;
import hls.core.utils.exception.HlsCusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsAssetsPackServiceImpl extends BaseServiceImpl<HlsCusAbsAssetsPack> implements HlsCusAbsAssetsPackService {

    @Autowired
    private HlsCusAbsAssetsPackMapper assetsPackMapper;

    @Autowired
    private HlsCusAbsAssetsPackageService assetsPackageService;

    @Autowired
    private FndCodingRuleValuesService codingRuleValuesService;

    @Override
    public List<HlsCusAbsAssetsPack> selectAssetsPack(IRequest iRequest, HlsCusAbsAssetsPack assetsPack, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return assetsPackMapper.selectAssetsPack(assetsPack);
    }


    @Override
    public HlsCusAbsAssetsPack submitAssetsPack(IRequest iRequest, HlsCusAbsAssetsPack assetsPack) throws HlsCusException {
        //修改
        if(assetsPack.getPackId()!=null&&!new Long(0L).equals(assetsPack.getPackId())){
            self().updateByPrimaryKeySelective(iRequest,assetsPack);
        }else{
            //新增
            Map<String, String> params = new HashMap<>();
            //获取编码
            String number = codingRuleValuesService.getCodeRuleValue(iRequest, assetsPack.getDocumentCategory(), assetsPack.getDocumentType(), assetsPack.getBusinessType(), params);
            assetsPack.setPackNumber(number);
            self().insertSelective(iRequest,assetsPack);
        }

        //行表
        if(assetsPack.getAbsAssetsPackageList()!=null){
            for(HlsCusAbsAssetsPackage assetsPackage:assetsPack.getAbsAssetsPackageList()){
                assetsPackage.setPackId(assetsPack.getPackId());
                //校验
                assetsPackageService.checkAssetsPackagePackDateNew(iRequest,assetsPackage);
                if(assetsPackage.getPackageId()!=null&&!new Long(0L).equals(assetsPackage.getPackageId())){
                    assetsPackageService.updateByPrimaryKeySelective(iRequest,assetsPackage);
                }else{
                    assetsPackageService.insertSelective(iRequest,assetsPackage);
                }
            }
        }
        return assetsPack;
    }


    @Override
    public void cancelAssetsPack(IRequest iRequest, HlsCusAbsAssetsPack assetsPack) {
        assetsPack.setPackStatus(HlsCusConstant.WORKFLOW_STATUS.CANCEL);
        assetsPackMapper.updateByPrimaryKeySelective(assetsPack);
    }
    @Override
    public List<HlsCusAbsAssetsPack>  getLovData(HlsCusAbsAssetsPack assetsPack){
        return assetsPackMapper.selectProjectAssetsPack(assetsPack);
    }
}
