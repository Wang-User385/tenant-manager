package com.hand.hls.layout.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.layout.dto.DocLayoutButton;
import com.hand.hls.layout.dto.DocLayoutButtonProcDto;
import com.hand.hls.layout.dto.LovDocLayoutButtonDto;
import com.hand.hls.layout.mapper.DocLayoutButtonMapper;
import com.hand.hls.layout.mapper.DocLayoutButtonProcMapper;
import com.hand.hls.layout.service.IDocLayoutButtonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uncertain.composite.CompositeMap;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marshal
 * @date 2019-01-12 10:54
 * @description
 */
@Service
public class DocLayoutButtonServiceImpl extends BaseServiceImpl<DocLayoutButton> implements IDocLayoutButtonService {

    @Autowired
    private DocLayoutButtonMapper docLayoutButtonMapper;
    @Autowired
    private DocLayoutButtonProcMapper docLayoutButtonProcMapper;

    @Override
    public List<DocLayoutButton> load(String functionCode) {
        List<DocLayoutButton> list = docLayoutButtonMapper.selectBtnFromTplt(functionCode);
        list.forEach(item -> {
            item.setFunctionCode(functionCode);
            item.setCreationDate(new Date());
            item.setLastUpdateDate(new Date());
            docLayoutButtonMapper.insertSelective(item);
        });
        return list;
    }

    @Override
    public List<LovDocLayoutButtonDto> queryForLov(Map param, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return docLayoutButtonMapper.queryForLov(param);
    }

    @Override
    public ResponseData queryLovByfunctionCode(CompositeMap map, String whereStr) {
        Map parameter = (Map) map.get("parameter");
        Map param = new HashMap();
        if (parameter != null) {
            Object function_code = parameter.get("function_code");
            param.put("function_code", function_code.toString());
        }
        return new ResponseData(docLayoutButtonMapper.queryLovByfunctionCode(param));
    }

    @Override
    public void reload(IRequest requestContext, List<DocLayoutButton> listToModify) {
        Long userId = requestContext.getUserId();
        listToModify.forEach(dto -> {
            //删除原有数据
            DocLayoutButton docLayoutButton = new DocLayoutButton();
            docLayoutButton.setButtonCode(dto.getButtonCode());
            docLayoutButton.setFunctionCode(dto.getFunctionCode());
            docLayoutButtonMapper.deleteByPrimaryKey(docLayoutButton);

            Example example = new Example(DocLayoutButtonProcDto.class);
            example.createCriteria().andEqualTo(DocLayoutButtonProcDto.FIELD_FUNCTION_CODE, dto.getFunctionCode())
                    .andEqualTo(DocLayoutButtonProcDto.FIELD_BUTTON_CODE, dto.getButtonCode());
            docLayoutButtonProcMapper.deleteByExample(example);

            dto.setLastUpdatedBy(userId);
            dto.setLastUpdateDate(new Date());
            docLayoutButtonMapper.insertByTemplate(dto);
            docLayoutButtonProcMapper.insertByTemplate(dto);
        });
    }

}
