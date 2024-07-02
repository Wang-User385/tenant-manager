package com.hand.hls.check.components;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.cont.dto.ContentNumberHead;
import com.hand.hls.cont.mapper.ContentNumberHeadMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.utils.SpringContextHolder;
import leaf.database.service.IDatabaseServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uncertain.composite.CompositeMap;

import java.util.HashMap;
import java.util.Map;

@Component
public class ContentNumSave {

    private static String RECORD = "record";
    private static String HEAD_TAB_CODE = "CONT304F1_F_BASIN_INFO_con_content_number_head";
    private static String HEAD_ID = "head_id";

    private static final String CONTENT_NUMBER_REQ = "CONTENT_NUMBER_REQ";
    private static final String NEW = "NEW";

    private static final String NUM = "NUM";
    private static final String COMMON_STR = "-";

    public void saveReqNumber(CompositeMap qMap, IDatabaseServiceFactory factory, CompositeMap root){
        if(root != null && root.getChild(RECORD) != null && root.getChild(RECORD).getChild(HEAD_TAB_CODE) != null) {
            CompositeMap map = root.getChild(RECORD).getChild(HEAD_TAB_CODE);
            if (map.getChilds() != null && map.getChilds().size() > 0) {
                if (map.getChilds().get(0).get(HEAD_ID) != null) {

                    Long headId = Long.valueOf(map.getChilds().get(0).get(HEAD_ID).toString());
                    IRequest iRequest = RequestHelper.getCurrentRequest();
                    Map params = new HashMap();

                    ContentNumberHeadMapper contentNumberHeadMapper = SpringContextHolder.getBean(ContentNumberHeadMapper.class);
                    HlsCusPrjProjectMapper prjProjectMapper = SpringContextHolder.getBean(HlsCusPrjProjectMapper.class);
                    FndCodingRuleValuesService codingRuleValuesService = SpringContextHolder.getBean(FndCodingRuleValuesService.class);



                    ContentNumberHead contentNumberHead = new ContentNumberHead();
                    contentNumberHead.setHeadId(headId);
                    contentNumberHead = contentNumberHeadMapper.selectByPrimaryKey(contentNumberHead);
                    if(contentNumberHead.getReqNumber() == null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(NUM);

                        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
                        hlsCusPrjProject.setProjectId(contentNumberHead.getProjectId());
                        hlsCusPrjProject = prjProjectMapper.selectByPrimaryKey(hlsCusPrjProject);
                        sb.append(COMMON_STR);
                        sb.append(hlsCusPrjProject.getProjectNumber());

                        String reqNumber = codingRuleValuesService.getCodeRuleValue(iRequest, CONTENT_NUMBER_REQ,
                                CONTENT_NUMBER_REQ, CONTENT_NUMBER_REQ, params);
                        sb.append(COMMON_STR);
                        sb.append(reqNumber);

                        contentNumberHead.setReqNumber(sb.toString());
                        contentNumberHead.setStatus(NEW);
                        contentNumberHeadMapper.updateByPrimaryKeySelective(contentNumberHead);
                    }
                }
            }
        }

    }

}
