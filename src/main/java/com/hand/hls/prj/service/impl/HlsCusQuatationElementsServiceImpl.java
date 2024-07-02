package com.hand.hls.prj.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.mapper.HlsCusQuatationElementsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.prj.dto.HlsCusPrjQuotationElements;
import com.hand.hls.prj.service.IHlsCusQuatationElementsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusQuatationElementsServiceImpl extends BaseServiceImpl<HlsCusPrjQuotationElements> implements IHlsCusQuatationElementsService{

    @Autowired
    HlsCusQuatationElementsMapper mapper;

    private static final String NO_CONTENT = "\"无\"";
    private static final String FROM_WORD = "由";
    private static final String TO_WORD = "变更为";
    private static final String ENTER = "\n";
    private static final String FRONT_STR = "\"";
    private static final String BRACKETS = ")";
    private static final String NO_CHANGE = "未变更";
    private static final String COLON = ":";
    private static final String ELEMENTS = "项目要素";
    private static final String SEMICOLON = "；";

    @Autowired
    private  HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;

    @Override
    public String getQuatationElementChangeInfo(IRequest iRequest, Long currentProjectId, Long historyProjectId) {
        /*
         * 分为三个部分，
         * 第一个当前项目中没有，历史数据有的是数据，删除的数据
         * 第三个当前项目中有，历史数据有的数据，数据差异对比
         * 第四个当前项目中有，历史数据没有的数据，即新增的数据
         * */


        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setSourceDocumentId(currentProjectId);
        List<HlsCusPrjQuotation> hlsCusPrjQuotationList = hlsCusPrjQuotationMapper.queryRefProjectQuotation(hlsCusPrjQuotation);
        StringBuilder contentAll = new StringBuilder();
        Long seqInitial = 1L;
        for(HlsCusPrjQuotation cusPrjQuotation : hlsCusPrjQuotationList ){
            Long newQuotationId =  cusPrjQuotation.getQuotationId();
            Long oldQuotationId =  cusPrjQuotation.getChangeRefQuotationId();


            StringBuilder content = new StringBuilder();

            content.append(ELEMENTS);
            content.append(toChinese(seqInitial.toString()));
            content.append(COLON);
            content.append(ENTER);

            Long seq = 1L;
            HlsCusPrjQuotationElements elements = new HlsCusPrjQuotationElements();
            elements.setProjectId(currentProjectId);
            elements.setHisProjectId(historyProjectId);
            elements.setNewQuotationId(newQuotationId);
            elements.setOldQuotationId(oldQuotationId);

            List<HlsCusPrjQuotationElements> removeList = mapper.selectQuotationElementRemoveInfo(elements);
            for(int i = 0; i < removeList.size();i++){
                content.append(seq.toString());
                content.append(BRACKETS);

                content.append(FRONT_STR);
                content.append(removeList.get(i).getProjectElements());
                content.append(FRONT_STR);

                content.append(FROM_WORD);
                content.append(FRONT_STR);
                content.append(removeList.get(i).getElementsDescribe());
                content.append(FRONT_STR);
                content.append(TO_WORD);
                content.append(NO_CONTENT);
                content.append(ENTER);
                seq++;
            }


            List<HlsCusPrjQuotationElements> diffList = mapper.selectQuotationElementDiffInfo(elements);
            for(int i = 0; i < diffList.size();i++){
                content.append(seq.toString());
                content.append(BRACKETS);

                content.append(FRONT_STR);
                content.append(diffList.get(i).getProjectElements());
                content.append(FRONT_STR);

                content.append(FROM_WORD);
                content.append(FRONT_STR);
                content.append(diffList.get(i).getHisElementsDescribe());
                content.append(FRONT_STR);
                content.append(TO_WORD);
                content.append(FRONT_STR);
                content.append(diffList.get(i).getElementsDescribe());
                content.append(FRONT_STR);
                content.append(ENTER);
                seq++;
            }

            List<HlsCusPrjQuotationElements> addList = mapper.selectQuotationElementAddInfo(elements);
            for(int i = 0; i < addList.size();i++){
                content.append(seq.toString());
                content.append(BRACKETS);

                content.append(FRONT_STR);
                content.append(addList.get(i).getProjectElements());
                content.append(FRONT_STR);

                content.append(FROM_WORD);
                content.append(NO_CONTENT);
                content.append(TO_WORD);
                content.append(FRONT_STR);
                content.append(addList.get(i).getElementsDescribe());
                content.append(FRONT_STR);
                content.append(ENTER);
                seq++;
            }


            if(removeList.size() == 0 && diffList.size() == 0 && addList.size() == 0  ){
                content.append(NO_CHANGE);
            }

            content.append(ENTER);
            seqInitial++;

            contentAll.append(content);

        }


        return contentAll.toString();

    }


    private static String toChinese(String str) {
        String[] s1 = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
        String[] s2 = { "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千" };
        String result = "(";
        int n = str.length();
        for (int i = 0; i < n; i++) {
            int num = str.charAt(i) - '0';
            if (i != n - 1 && num != 0) {
                result += s1[num] + s2[n - 2 - i];
            } else {
                result += s1[num];
            }
        }
        result += ")";
        return result;
    }

    @Override
    public String getRentalPlanChangeInfo(IRequest iRequest, Long currentProjectId, Long historyProjectId) {
        /*
         * 分为三个部分，
         * 第一个当前项目中没有，历史数据有的是数据，删除的数据
         * 第三个当前项目中有，历史数据有的数据，数据差异对比
         * 第四个当前项目中有，历史数据没有的数据，即新增的数据
         * */

        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setSourceDocumentId(currentProjectId);
        List<HlsCusPrjQuotation> hlsCusPrjQuotationList = hlsCusPrjQuotationMapper.queryRefProjectQuotation(hlsCusPrjQuotation);
        StringBuilder contentAll = new StringBuilder();
        Long seqInitial = 1L;
        for(HlsCusPrjQuotation cusPrjQuotation : hlsCusPrjQuotationList ){
            Long newQuotationId =  cusPrjQuotation.getQuotationId();
            Long oldQuotationId =  cusPrjQuotation.getChangeRefQuotationId();

            StringBuilder content = new StringBuilder();

            Long seq = 1L;
            HlsCusPrjQuotationElements elements = new HlsCusPrjQuotationElements();
            elements.setProjectId(currentProjectId);
            elements.setHisProjectId(historyProjectId);
            elements.setNewQuotationId(newQuotationId);
            elements.setOldQuotationId(oldQuotationId);

            List<HlsCusPrjQuotationElements> removeList = mapper.selectRentalRemoveInfo(elements);
            for(int i = 0; i < removeList.size();i++){


                content.append(FRONT_STR);
                content.append(removeList.get(i).getElementsDescribe());
                content.append(FRONT_STR);
                content.append(TO_WORD);
                content.append(NO_CONTENT);
                content.append(SEMICOLON);
                seq++;
            }


            List<HlsCusPrjQuotationElements> diffList = mapper.selectRentalDiffInfo(elements);
            for(int i = 0; i < diffList.size();i++){


                content.append(FRONT_STR);
                content.append(diffList.get(i).getHisElementsDescribe());
                content.append(FRONT_STR);
                content.append(TO_WORD);
                content.append(FRONT_STR);
                content.append(diffList.get(i).getElementsDescribe());
                content.append(FRONT_STR);
                content.append(SEMICOLON);
                seq++;
            }

            List<HlsCusPrjQuotationElements> addList = mapper.selectRentalAddInfo(elements);
            for(int i = 0; i < addList.size();i++){

                content.append(FRONT_STR);
                content.append(NO_CONTENT);
                content.append(TO_WORD);
                content.append(FRONT_STR);
                content.append(addList.get(i).getElementsDescribe());
                content.append(FRONT_STR);
                content.append(SEMICOLON);
                seq++;
            }


            if(removeList.size() == 0 && diffList.size() == 0 && addList.size() == 0  ){
                content.append(NO_CHANGE);
                content.append(SEMICOLON);

            }

            seqInitial++;

            contentAll.append(content);

        }


        return contentAll.toString();

    }

}