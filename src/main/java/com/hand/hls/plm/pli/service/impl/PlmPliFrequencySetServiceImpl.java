package com.hand.hls.plm.pli.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.plm.pli.dto.PlmPliFrequencySet;
import com.hand.hls.plm.pli.mapper.PlmPliFrequencySetMapper;
import com.hand.hls.plm.pli.service.HlsCusIPostloanInspectionService;
import com.hand.hls.plm.pli.service.PlmPliFrequencySetService;
import com.hand.hls.utils.HlsCusConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class PlmPliFrequencySetServiceImpl extends BaseServiceImpl<PlmPliFrequencySet> implements PlmPliFrequencySetService {

    public static final String[] FREQUENCY_TYPE_LIST = {"GDXF_PLI_FREQUENCY"};

    @Autowired
    private PlmPliFrequencySetMapper mapper;

    @Autowired
    private HlsCusIPostloanInspectionService postloanInspectionService;

    @Override
    public ResponseData batchUpdate2(IRequest iRequest, List<PlmPliFrequencySet> dtos) {
        ResponseData responseData = new ResponseData();
        responseData.setSuccess(false);
        boolean flag = false;
        this.batchUpdate(iRequest, dtos);

        for (String str : FREQUENCY_TYPE_LIST){
            PlmPliFrequencySet pp = new PlmPliFrequencySet();
            pp.setFrequencyType(str);
            List<PlmPliFrequencySet> dtos_new = mapper.select(pp);
            int isY = 0;
            for (PlmPliFrequencySet dt : dtos_new) {
                if (HlsCusConstant.FLAG.Y.equalsIgnoreCase(dt.getEnableFlag())) {
                    isY++;
                }
            }

            if (isY == 1) {
                flag = true;
            } else if (isY == 0) {
                flag = false;
                responseData.setSuccess(false);
                PlmPliFrequencySet dt = new PlmPliFrequencySet();
                dt = dtos_new.get(0);
                dt.setEnableFlag(HlsCusConstant.FLAG.Y);
                mapper.updateByPrimaryKeySelective(dt);
                responseData.setMessage("启用的规则有且必须只有一条");
            } else {
                flag = false;
                responseData.setSuccess(false);
                for (int i = 0 ;i<(dtos_new.size()-1);i++) {
                    dtos_new.get(i).setEnableFlag(HlsCusConstant.FLAG.N);
                    mapper.updateByPrimaryKeySelective(dtos_new.get(i));
                }

                dtos_new.get(dtos_new.size()-1).setEnableFlag(HlsCusConstant.FLAG.Y);
                mapper.updateByPrimaryKeySelective(dtos_new.get(dtos_new.size()-1));
                responseData.setMessage("启用的规则有且必须只有一条");
            }
        }
        if (flag){
            postloanInspectionService.selectCheckList(iRequest.getCompanyId(),"UPDATE");
        }
        responseData.setRows(mapper.selectAll());
        responseData.setSuccess(flag);
        return responseData;
    }

    private void listRefresh(){

    }
}
