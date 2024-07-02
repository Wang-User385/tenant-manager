package com.hand.hls.prj.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.mapper.HlsCusPrjSurviveRequireMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.prj.dto.HlsCusPrjSurviveRequire;
import com.hand.hls.prj.service.HlsCusPrjSurviveRequireService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjSurviveRequireServiceImpl extends BaseServiceImpl<HlsCusPrjSurviveRequire> implements HlsCusPrjSurviveRequireService{

    @Autowired
    private HlsCusPrjSurviveRequireMapper mapper;

    private static final String BLACK_WORD = "、";
    private static final String BRACKETS = ")";
    private static final String NO_CONTENT = "\"无\"";
    private static final String FROM_WORD = "";
    private static final String TO_WORD = "变更为";
    private static final String ENTER = "\n";
    private static final String FRONT_STR = "\"";
    private static final String NO_CHANGE = "未变更";

    @Override
    public String getPrjSurviveRequireChangeInfo(IRequest iRequest, Long currentProjectId, Long historyProjectId) {
        /*
         * 分为四个部分，
         * 第一个当前项目中没有，历史数据有的是数据，删除的数据
         * 第二个未变化数据
         * 第三个当前项目中有，历史数据有的数据，数据差异对比
         * 第四个当前项目中有，历史数据没有的数据，即新增的数据
         * */

        StringBuilder content = new StringBuilder();
        Long seq = 1L;
        HlsCusPrjSurviveRequire condition = new HlsCusPrjSurviveRequire();
        condition.setProjectId(currentProjectId);
        condition.setHisProjectId(historyProjectId);

        List<HlsCusPrjSurviveRequire> removeConditionList = mapper.selectChangeRemoveInfo(condition);
        for(int i = 0; i < removeConditionList.size();i++){
            content.append(seq.toString());
            content.append(BRACKETS);
//            content.append(BLACK_WORD);
            content.append(FROM_WORD);

            content.append(FRONT_STR);
            content.append(removeConditionList.get(i).getManageRequirements());
            content.append(FRONT_STR);

            content.append(TO_WORD);
            content.append(NO_CONTENT);
            content.append(ENTER);
            seq++;
        }


        List<HlsCusPrjSurviveRequire> conditionList = mapper.selectNotChangeInfo(condition);
        for(int i = 0; i < conditionList.size();i++){
            content.append(seq.toString());
            content.append(BRACKETS);
//            content.append(BLACK_WORD);

            content.append(FRONT_STR);
            content.append(conditionList.get(i).getManageRequirements());
            content.append(FRONT_STR);
            content.append(NO_CHANGE);

            content.append(ENTER);
            seq++;
        }

        List<HlsCusPrjSurviveRequire> diffConditionList = mapper.selectChangeDiffInfo(condition);
        for(int i = 0; i < diffConditionList.size();i++){
            content.append(seq.toString());
            content.append(BRACKETS);
//            content.append(BLACK_WORD);
            content.append(FROM_WORD);
            content.append(FRONT_STR);
            content.append(diffConditionList.get(i).getHisManageRequirements());
            content.append(FRONT_STR);
            content.append(TO_WORD);
            content.append(FRONT_STR);
            content.append(diffConditionList.get(i).getManageRequirements());
            content.append(FRONT_STR);
            content.append(ENTER);
            seq++;
        }

        List<HlsCusPrjSurviveRequire> addConditionList = mapper.selectChangeAddInfo(condition);
        for(int i = 0; i < addConditionList.size();i++){
            content.append(seq.toString());
            content.append(BRACKETS);
//            content.append(BLACK_WORD);
            content.append(FROM_WORD);
            content.append(NO_CONTENT);
            content.append(TO_WORD);
            content.append(FRONT_STR);
            content.append(addConditionList.get(i).getManageRequirements());
            content.append(FRONT_STR);
            content.append(ENTER);
            seq++;
        }

        return content.toString();
    }
}