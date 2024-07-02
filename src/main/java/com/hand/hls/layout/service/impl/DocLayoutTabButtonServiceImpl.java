package com.hand.hls.layout.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.layout.dto.DocLayoutTabButTmpltDto;
import com.hand.hls.layout.dto.DocLayoutTabButton;
import com.hand.hls.layout.mapper.DocLayoutTabButTmpltMapper;
import com.hand.hls.layout.mapper.DocLayoutTabButtonMapper;
import com.hand.hls.layout.service.IDocLayoutTabButtonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class DocLayoutTabButtonServiceImpl extends BaseServiceImpl<DocLayoutTabButton> implements IDocLayoutTabButtonService {

    @Autowired
    private DocLayoutTabButtonMapper docLayoutTabButtonMapper;

    @Autowired
    private DocLayoutTabButTmpltMapper docLayoutTabButTmpltMapper;

    @Override
    public List<DocLayoutTabButton> selectDocLayoutTabButton(Map map, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return docLayoutTabButtonMapper.selectDocLayoutTabButton(map);
    }

    @Override
    public List<DocLayoutTabButton> queryDocLayoutTabButton(DocLayoutTabButton param, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return docLayoutTabButtonMapper.queryDocLayoutTabButton(param);
    }

    @Override
    public ResponseData tabButtonConfigLoad(DocLayoutTabButton docLayoutTabButton) {
        List<DocLayoutTabButTmpltDto> all = docLayoutTabButTmpltMapper.selectAll();
        Example example = new Example(DocLayoutTabButton.class);
        example.createCriteria().andEqualTo(DocLayoutTabButton.FIELD_FUNCTION_CODE, docLayoutTabButton.getFunctionCode())
                .andEqualTo(DocLayoutTabButton.FIELD_LAYOUT_CODE, docLayoutTabButton.getLayoutCode())
                .andEqualTo(DocLayoutTabButton.FIELD_TAB_CODE, docLayoutTabButton.getTabCode());
        List<DocLayoutTabButton> have = docLayoutTabButtonMapper.selectByExample(example);
        //已经拥有的buttonCode
        List<String> havaButtonList = have.stream().map(DocLayoutTabButton::getButtonCode).collect(Collectors.toList());

        List<DocLayoutTabButTmpltDto> collect = all.stream().filter(dto -> !havaButtonList.contains(dto.getButtonCode())).collect(Collectors.toList());
        collect.forEach(dto -> {
            DocLayoutTabButton button = docLayoutTabButton;
            button.setButtonCode(dto.getButtonCode());
            button.setPrompt(dto.getPrompt());
            button.setSaveDataFirst(dto.getSaveDataFirst());
            button.setSystemFlag(dto.getSystemFlag());
            button.setDisplayFlag(dto.getDisplayFlag());
            button.setDisplayOrder(dto.getDisplayOrder());
            button.setJavascript(dto.getJavascript());
            button.setEnabledFlag(dto.getEnabledFlag());
            docLayoutTabButtonMapper.insert(button);
        });
        return new ResponseData(true);
    }

    @Override
    public ResponseData tabButtonConfigReload(List<DocLayoutTabButton> docLayoutTabButton) {
        List<DocLayoutTabButTmpltDto> all = docLayoutTabButTmpltMapper.selectAll();
        Map<String, DocLayoutTabButTmpltDto> allMap = all.stream().collect(Collectors.toMap(DocLayoutTabButTmpltDto::getButtonCode, Function.identity()));

        List<String> collect = all.stream().map(DocLayoutTabButTmpltDto::getButtonCode).collect(Collectors.toList());
        List<DocLayoutTabButton> buttonToReload = docLayoutTabButton.stream().filter(button -> !collect.equals(button.getButtonCode())).collect(Collectors.toList());

        buttonToReload.forEach(dto -> {
            DocLayoutTabButTmpltDto docLayoutTabButTmpltDto = allMap.get(dto.getButtonCode());
            if (docLayoutTabButTmpltDto != null) {
                dto.setPrompt(docLayoutTabButTmpltDto.getPrompt());
                dto.setSaveDataFirst(docLayoutTabButTmpltDto.getSaveDataFirst());
                dto.setSystemFlag(docLayoutTabButTmpltDto.getSystemFlag());
                dto.setDisplayOrder(docLayoutTabButTmpltDto.getDisplayOrder());
                dto.setDisplayFlag(docLayoutTabButTmpltDto.getDisplayFlag());
                dto.setJavascript(docLayoutTabButTmpltDto.getJavascript());
                dto.setEnabledFlag(docLayoutTabButTmpltDto.getEnabledFlag());
                docLayoutTabButtonMapper.updateByPrimaryKey(dto);
            }
        });

        return new ResponseData(true);
    }

}