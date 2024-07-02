package com.hand.hls.fnd.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.fnd.dto.FndRiskRatio;
import com.hand.hls.fnd.mapper.FndRiskRatioMapper;
import com.hand.hls.fnd.mapper.FndRiskRatioSetMapper;
import com.hand.hls.fnd.service.IFndRiskRatioService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.fnd.dto.FndRiskRatioSet;
import com.hand.hls.fnd.service.IFndRiskRatioSetService;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class FndRiskRatioSetServiceImpl extends BaseServiceImpl<FndRiskRatioSet> implements IFndRiskRatioSetService {
    @Autowired
    FndRiskRatioSetMapper fndRiskRatioSetMapper;
    @Autowired
    FndRiskRatioMapper fndRiskRatioMapper;
    @Autowired
    IFndRiskRatioService fndRiskRatioService;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    @Override
    public void saveHeadAndLine(IRequest iRequest, List<FndRiskRatioSet> fndRiskRatioSets) {
        List<FndRiskRatioSet> datas = self().selectAll(iRequest);
        List<FndRiskRatio> fndRiskRatios = new ArrayList();

        for (FndRiskRatioSet fndRiskRatioSet : fndRiskRatioSets) {
            if (fndRiskRatioSet.getFndRiskRatio() != null) {
                for (FndRiskRatio item : fndRiskRatioSet.getFndRiskRatio()) {
                    FndRiskRatio ratio = item;
                    fndRiskRatios.add(ratio);
                }
            }
            fndRiskRatioSet.setEnabledFlag("Y");
            batchChildUpdate(iRequest, fndRiskRatioSet, fndRiskRatios, datas);
        }
    }

    @Override
    public void copyHeadAndLine(IRequest iRequest, List<FndRiskRatioSet> fndRiskRatioSets) {
        FndRiskRatioSet baseRiskRatioSet = new FndRiskRatioSet();
        baseRiskRatioSet.setRiskRatioSetCode(fndRiskRatioSets.get(0).getRiskRatioSetCode());
        baseRiskRatioSet = selectByPrimaryKey(iRequest, baseRiskRatioSet);

        //复制头
        if (baseRiskRatioSet != null && baseRiskRatioSet.getValidTo() == null) {
            FndRiskRatioSet riskRatioSetNew = new FndRiskRatioSet();
            Map<String, String> mapCsh = hlsBeanRefUtilService.getFieldValueMap(baseRiskRatioSet);
            hlsBeanRefUtilService.setFieldValue(riskRatioSetNew, mapCsh);

            SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            Date beginDate = calendar.getTime();
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
            Date endDate = calendar.getTime();
            baseRiskRatioSet.setValidTo(endDate);
            self().updateByPrimaryKeySelective(iRequest,baseRiskRatioSet);

            String beginDateStr = dft.format(beginDate);
            riskRatioSetNew.setRiskRatioSetCode("RISK" + beginDateStr);
            riskRatioSetNew.setDescription("风险准备金率集" + beginDateStr);
            riskRatioSetNew.setValidFrom(beginDate);
            riskRatioSetNew.setValidTo(null);
            riskRatioSetNew.setEnabledFlag("Y");
            riskRatioSetNew = self().insertSelective(iRequest, riskRatioSetNew);

            //复制明细
            FndRiskRatio riskRatioQuery = new FndRiskRatio();
            riskRatioQuery.setRiskRatioSetCode(baseRiskRatioSet.getRiskRatioSetCode());
            List<FndRiskRatio> fndRiskRatioList = fndRiskRatioMapper.select(riskRatioQuery);
            for (FndRiskRatio item : fndRiskRatioList) {
                FndRiskRatio ratioInsert = item;
                ratioInsert.setRiskRatioId(null);
                ratioInsert.setRiskRatioSetCode(riskRatioSetNew.getRiskRatioSetCode());
                fndRiskRatioService.insertSelective(iRequest, ratioInsert);
            }
        }
    }

    @Override
    public void batchChildUpdate(IRequest iRequest, FndRiskRatioSet
            fndRiskRatioSet, List<FndRiskRatio> fndRiskRatio, List<FndRiskRatioSet> datas) {
        if (fndRiskRatioSet != null) {
            //更新和插入头表
            boolean check = false;

            for (FndRiskRatioSet data : datas) {
                if (data.getRiskRatioSetCode().equals(fndRiskRatioSet.getRiskRatioSetCode())) {
                    check = true;
                } else {
                    if (data.getValidTo() == null) {
                        Date beginDate = fndRiskRatioSet.getValidFrom();
                        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(beginDate);
                        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
                        Date endDate = calendar.getTime();
                        data.setValidTo(endDate);
                        data.set__status("update");
                        self().updateByPrimaryKey(iRequest, data);
                    }
                }
            }

            if (!check) {
                fndRiskRatioSet.setEnabledFlag("Y");
                self().insertSelective(iRequest, fndRiskRatioSet);
            } else {
                self().updateByPrimaryKey(iRequest, fndRiskRatioSet);
            }

            //更新和插入行表
            if (fndRiskRatioSet.getFndRiskRatio() != null) {
                for (FndRiskRatio item : fndRiskRatio) {
                    if (item.getRiskRatioId() == null) {
                        item.setRiskRatioSetCode(fndRiskRatioSet.getRiskRatioSetCode());
                        fndRiskRatioService.insertSelective(iRequest, item);
                    } else {
                        item.setRiskRatioSetCode(fndRiskRatioSet.getRiskRatioSetCode());
                        fndRiskRatioService.updateByPrimaryKey(iRequest, item);
                    }
                }
            }
        }
    }

    @Override
    public void batchDeleteLine(List<FndRiskRatioSet> fndRiskRatioSetList) {
        Example example = new Example(FndRiskRatio.class);
        if (CollectionUtils.isNotEmpty(fndRiskRatioSetList)) {
            fndRiskRatioSetList.forEach((item) -> {
                example.createCriteria().andEqualTo("riskRatioSetCode", item.getRiskRatioSetCode());
                fndRiskRatioMapper.deleteByExample(example);
            });
        }

    }

}