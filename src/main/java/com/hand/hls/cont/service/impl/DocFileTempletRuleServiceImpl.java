package com.hand.hls.cont.service.impl;

import java.util.List;

import com.hand.hls.cont.dto.DocFileTempletType;
import com.hand.hls.cont.mapper.DocFileTempletTypeMapper;
import com.hand.hls.ruleengine.dto.RuleEngineType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.DocFileTempletRule;
import com.hand.hls.cont.mapper.DocFileTempletRuleMapper;
import com.hand.hls.cont.service.IDocFileTempletRuleService;
import com.hand.hls.ruleengine.dto.HlsRuleEngine;
import com.hand.hls.ruleengine.mapper.HlsRuleEngineMapper;
import com.hand.hls.ruleengine.service.IHlsRuleEngineService;

@Service
@Transactional
public class DocFileTempletRuleServiceImpl extends BaseServiceImpl<DocFileTempletRule> implements IDocFileTempletRuleService {


    @Autowired
    private IHlsRuleEngineService hlsRuleEngineService;

    @Autowired
    private HlsRuleEngineMapper ruleEngineMapper;

    @Autowired
    private DocFileTempletRuleMapper docFileTempletRuleMapper;

    @Autowired
    private DocFileTempletTypeMapper templetTypeMapper;

    public List<DocFileTempletType> selectTempletTypeLov(DocFileTempletType dto) {
        return this.templetTypeMapper.selectFileTempletTypeLov(dto);
    }

    @Override
    public List<DocFileTempletRule> batchUpdate(IRequest request, List<DocFileTempletRule> list) {
        for (DocFileTempletRule fileTempletRule : list) {
            HlsRuleEngine ruleEngine = new HlsRuleEngine();
            //获取绑定的类型
            String templetType = fileTempletRule.getRuleEngineType();
            switch (fileTempletRule.get__status()) {
                case DTOStatus.ADD:
                    ruleEngine.setRuleEngineCode("FILE_" + fileTempletRule.getCompanyId().toString() + "_" + fileTempletRule.getTempletType());
                    ruleEngine.setRuleEngineName("FILE_" + fileTempletRule.getCompanyId().toString() + "_" + fileTempletRule.getTempletType());
//                    ruleEngine.setRuleEngineType("CON.CONTRACT_FILE");
                    ruleEngine.setRuleEngineType(templetType);
                    ruleEngine.setEnabledFlag("Y");
                    ruleEngine.setDataSourceId(fileTempletRule.getDataSourceId());
                    ruleEngine = hlsRuleEngineService.self().insertSelective(request, ruleEngine);
                    fileTempletRule.setRuleEngineId(ruleEngine.getRuleEngineId());
                    insertSelective(request, fileTempletRule);
                    break;
                case DTOStatus.UPDATE:
                    ruleEngine.setRuleEngineId(fileTempletRule.getRuleEngineId());
                    ruleEngine = ruleEngineMapper.selectOne(ruleEngine);
                    ruleEngine.setRuleEngineType(templetType);
                    ruleEngine.setEnabledFlag("Y");
                    ruleEngine.setDataSourceId(fileTempletRule.getDataSourceId());
                    hlsRuleEngineService.updateByPrimaryKeySelective(request, ruleEngine);
                    updateByPrimaryKeySelective(request, fileTempletRule);
                    break;
                case DTOStatus.DELETE:
                    ruleEngine.setRuleEngineId(fileTempletRule.getRuleEngineId());
                    hlsRuleEngineService.deleteByPrimaryKey(ruleEngine);
                    deleteByPrimaryKey(fileTempletRule);
                    break;
                default:
                    break;
            }
        }
        return list;
    }

    @Override
    public List<DocFileTempletRule> selectByFontCondition(IRequest request, DocFileTempletRule docFileTempletRule, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return docFileTempletRuleMapper.selectByFontCondition(docFileTempletRule);
    }

    @Override
    public void updateRuleEngineTypleByRuleEngineId(DocFileTempletRule docFileTempletRule) {
        docFileTempletRuleMapper.updateRuleEngineTypleByRuleEngineId(docFileTempletRule);
    }

}