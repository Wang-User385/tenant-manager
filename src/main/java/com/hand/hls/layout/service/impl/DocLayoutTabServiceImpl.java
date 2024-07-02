package com.hand.hls.layout.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.layout.dto.DocLayoutConfig;
import com.hand.hls.layout.dto.DocLayoutConfigLov;
import com.hand.hls.layout.dto.DocLayoutScreen;
import com.hand.hls.layout.dto.DocLayoutTab;
import com.hand.hls.layout.mapper.*;
import com.hand.hls.layout.service.IDocLayoutTabService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uncertain.composite.CompositeMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class DocLayoutTabServiceImpl extends BaseServiceImpl<DocLayoutTab> implements IDocLayoutTabService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DocLayoutTabMapper docLayoutTabMapper;
    @Autowired
    private DocLayoutScreenMapper docLayoutScreenMapper;
    @Autowired
    private DocLayoutTabButtonMapper docLayoutTabButtonMapper;
    @Autowired
    private DocLayoutConfigLovMapper docLayoutConfigLovMapper;
    @Autowired
    private DocLayoutConfigMapper docLayoutConfigMapper;
    @Autowired
    private DocLayoutTreeMapper docLayoutTreeMapper;

    @Override
    public List<DocLayoutTab> selectDocLayoutTab(Map<String, Object> paramMap, int page, int pageSize) {
//        PageHelper.startPage(page, pageSize);
        return docLayoutTabMapper.selectDocLayoutTab(paramMap);
    }

    @Override
    public void deleteDocLayoutTab(List<DocLayoutTab> dto) {
        dto.forEach(docLayoutTab -> {
            if (StringUtils.isNotBlank(docLayoutTab.getTabCode()) && StringUtils.isNotBlank(docLayoutTab.getLayoutCode())) {
                docLayoutTabMapper.deleteByLayoutCodeAndTabCode(docLayoutTab);
                docLayoutScreenMapper.deleteByLayoutCodeAndTabCode(docLayoutTab);
                docLayoutTabButtonMapper.deleteByLayoutCodeAndTabCode(docLayoutTab);
                docLayoutConfigLovMapper.deleteByLayoutCodeAndTabCode(docLayoutTab);
                docLayoutConfigMapper.deleteByLayoutCodeAndTabCode(docLayoutTab);
            }
        });
    }

    @Override
    public void loadDocLayoutTab(IRequest requestContext, DocLayoutTab layoutTab) {
        List<DocLayoutTab> list = docLayoutTabMapper.select(layoutTab);
        if (list == null || list.size() != 1) {
            logger.debug("Get list by {} ==> {}", layoutTab, list);
            throw new RuntimeException();
        }
        DocLayoutTab docLayoutTab = list.get(0);
        List<DocLayoutConfig> configs = docLayoutTabMapper.selectCatalogColumn(docLayoutTab);

        Long userId = requestContext.getUserId();
        Date now = new Date(System.currentTimeMillis());
//      load config
        for (DocLayoutConfig config : configs) {
            config.setConfigId(null);
            config.setCreatedBy(userId);
            config.setLastUpdatedBy(userId);
            config.setCreationDate(now);
            config.setLastUpdateDate(now);
            docLayoutConfigMapper.insertSelective(config);
        }

    }

    @Override
    public ResponseData selectLayoutTabForTabConfig(CompositeMap map, String whereStr) {
        List<CompositeMap> list = docLayoutTabMapper.selectLayoutTabForTabConfig(map);
        HttpServletRequest request = (HttpServletRequest) map.get("_instance.javax.servlet.http.HttpServletRequest");
        Long count = null;
        if (list != null && list.size() > 0) {
            count = (Long) list.get(0).get("total_count");
        }
        request.setAttribute("screen_config_record_count", count);
        return new ResponseData(list);
    }


    @Override
    public ResponseData layoutTabCopy(IRequest requestContext, String fromLayoutCode, String toLayoutCode, String fromTabCode, String toTabCode, String toTabDesc, String TabOnly, String parentTabCode) {
        Long userId = requestContext.getUserId();
        DocLayoutTab docLayoutTab = new DocLayoutTab();
        docLayoutTab.setLayoutCode(fromLayoutCode);
        docLayoutTab.setTabCode(fromTabCode);
        //查询指定源layoutCode和TabCode的Tab
        List<DocLayoutTab> tabs = docLayoutTabMapper.select(docLayoutTab);
        if (tabs == null || tabs.size() == 0) {
            return new ResponseData(false, "为查询到源组件，请检查参数");
        }

        //如果已经存在目标layoutCode ,目标tabCode 的对象，删除
        DocLayoutTab layOutTabToDelete = new DocLayoutTab();
        layOutTabToDelete.setLayoutCode(toLayoutCode);
        layOutTabToDelete.setTabCode(toTabCode);
        docLayoutTabMapper.delete(layOutTabToDelete);

        for (DocLayoutTab layoutTab : tabs) {
            //根据原LayoutTab信息，修改后插入表,
            DocLayoutTab layout_tab_copy = layoutTab;
            layout_tab_copy.setLayoutCode(toLayoutCode);
            layout_tab_copy.setTabCode(toTabCode);
            layout_tab_copy.setTabDesc(toTabDesc);
            layout_tab_copy.setLastUpdatedBy(userId);
            if(parentTabCode != null) {
                layout_tab_copy.setParentTabCode(parentTabCode);
            }
            layout_tab_copy.setLastUpdateDate(new Date());
            this.insertSelective(requestContext, layout_tab_copy);

            if (TabOnly.equals("N")) {
                //表中已经存在要复制的，删除screen
                DocLayoutScreen screenToDelete = new DocLayoutScreen();
                screenToDelete.setLayoutCode(toLayoutCode);
                screenToDelete.setTabCode(toTabCode);
                docLayoutScreenMapper.delete(screenToDelete);

                //删除hls_doc_layout_config 和对应的lov
                Map<String, Object> param = new HashMap<>();
                param.put("layout_code", toLayoutCode);
                param.put("tab_code", toTabCode);
                List<DocLayoutConfig> configs = docLayoutConfigMapper.selectDocLayoutConfig(param);
                for (DocLayoutConfig config : configs) {
                    //删除hls_doc_layout_config_lov
                    DocLayoutConfigLov docLayoutConfigLov = new DocLayoutConfigLov();
                    if (config.getConfigId() != null) {
                        docLayoutConfigLov.setConfigId(config.getConfigId());
                        docLayoutConfigLovMapper.delete(docLayoutConfigLov);
                    }
                    docLayoutConfigMapper.delete(config);
                }

                //复制hls_doc_layout_config和lov
                param.put("layout_code", fromLayoutCode);
                param.put("tab_code", fromTabCode);
                List<DocLayoutConfig> configList = docLayoutConfigMapper.selectDocLayoutConfig(param);

                for (DocLayoutConfig config : configList) {
                    //复制hls_doc_layout_config_lov
                    DocLayoutConfigLov docLayoutConfigLov = new DocLayoutConfigLov();
                    docLayoutConfigLov.setConfigId(config.getConfigId());
                    List<DocLayoutConfigLov> configLovs = docLayoutConfigLovMapper.select(docLayoutConfigLov);
                    config.setLayoutCode(toLayoutCode);
                    config.setTabCode(toTabCode);
                    //清除config原主键
                    config.setConfigId(null);
                    docLayoutConfigMapper.insert(config);
                    for (DocLayoutConfigLov configLov : configLovs) {
                        //清除config原主键
                        configLov.setLovConfigId(null);
                        configLov.setConfigId(config.getConfigId());
                        docLayoutConfigLovMapper.insert(configLov);
                    }
                }

                //复制screen
                DocLayoutScreen screenToCopy = new DocLayoutScreen();
                screenToCopy.setLayoutCode(fromLayoutCode);
                screenToCopy.setTabCode(fromTabCode);
                List<DocLayoutScreen> screens = docLayoutScreenMapper.select(screenToCopy);
                for (DocLayoutScreen screen : screens) {
                    screen.setLayoutCode(toLayoutCode);
                    screen.setTabCode(toTabCode);
                    docLayoutScreenMapper.insert(screen);
                }
            }
        }
        return new ResponseData(true);
    }


}