package com.hand.hls.app.user.service.impl;

import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.app.user.dto.HlsCusVersion;
import com.hand.hls.app.user.mapper.HlsCusVersionMapper;
import com.hand.hls.app.user.service.IHlsCusVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author liao
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusHlsCusVersionServiceImpl extends BaseServiceImpl<HlsCusVersion> implements IHlsCusVersionService {
    @Autowired
    private HlsCusVersionMapper hlsCusVersionMapper;


    @Override
    public List<HlsCusVersion> selectVersion(HlsCusVersion hlsCusVersion) {

        Example example = new Example(HlsCusVersion.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(HlsCusVersion.FIELD_ENABLE_FLAG, "Y") ;
        List<HlsCusVersion> hlsCusVersionList =   hlsCusVersionMapper.selectByExample(example);

        return hlsCusVersionList;
    }

    @Override
    public void updateVersion(HlsCusVersion hlsCusVersion) {
        try{


            hlsCusVersionMapper.updateAppVersion(hlsCusVersion.getAppVersion());
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }
}