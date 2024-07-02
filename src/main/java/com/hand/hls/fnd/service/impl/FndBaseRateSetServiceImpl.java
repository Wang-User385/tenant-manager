package com.hand.hls.fnd.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.FndBaseRate;
import com.hand.hls.fnd.dto.FndBaseRateSet;
import com.hand.hls.fnd.mapper.FndBaseRateMapper;
import com.hand.hls.fnd.mapper.FndBaseRateSetMapper;
import com.hand.hls.fnd.service.FndBaseRateService;
import com.hand.hls.fnd.service.FndBaseRateSetService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


@Service
@Transactional
public class FndBaseRateSetServiceImpl extends BaseServiceImpl<FndBaseRateSet> implements FndBaseRateSetService {
    @Autowired
    FndBaseRateSetMapper fndBaseRateSetMapper;
    @Autowired
    FndBaseRateMapper fndBaseRateMapper;
    @Autowired
    FndBaseRateService fndBaseRateService;

    public FndBaseRateSetServiceImpl() {
    }

    public List<FndBaseRateSet> getCount() {
        return this.fndBaseRateSetMapper.getCount();
    }

    public List<FndBaseRateSet> queryAll() {
        return this.fndBaseRateSetMapper.selectAll();
    }

    public List<FndBaseRateSet> selectByQuery(FndBaseRateSet fndBaseRateSet, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return this.fndBaseRateSetMapper.selectByQuery(fndBaseRateSet);
    }

    public void batchChildUpdate(IRequest iRequest, FndBaseRateSet fndBaseRateSet, List<FndBaseRate> fndBaseRate, List<FndBaseRateSet> datas) {
        FndBaseRateSet oldFndBaseRateSet = new FndBaseRateSet();
        if (fndBaseRateSet != null) {
            //更新和插入头表
            boolean check = false;

            for (FndBaseRateSet data : datas) {
                if (data.getBaseRateSet().equals(fndBaseRateSet.getBaseRateSet())) {
                    check = true;
                } else if (data.getBaseRateType().equals(fndBaseRateSet.getBaseRateType())) {
                    if (data.getValidTo() == null) {
                        Date beginDate = fndBaseRateSet.getValidFrom();
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
                if (data.getBaseRateSet().equals(fndBaseRateSet.getBaseRateSetOld())) {
                    //如果是修改的话  获取原旧数据
                    oldFndBaseRateSet = data;
                }
            }

            if (!check) {
                //按照原本的逻辑 无论新增还是修改都是插入操作  所以修改时应该删除旧数据
                fndBaseRateSet.setEnabledFlag("Y");
                self().insertSelective(iRequest, fndBaseRateSet);
                if (oldFndBaseRateSet.getBaseRateSet() != null) {
                    self().deleteByPrimaryKey(oldFndBaseRateSet);
                }

            } else {
                self().updateByPrimaryKey(iRequest, fndBaseRateSet);
            }

            //更新和插入行表
            if (fndBaseRateSet.getFndBaseRate() != null) {
                for (FndBaseRate fndbaserate : fndBaseRate) {
                    if (fndbaserate.getBaseRateId() == null) {
                        fndbaserate.setBaseRateSet(fndBaseRateSet.getBaseRateSet());
                        fndBaseRateService.insertSelective(iRequest, fndbaserate);
                    } else {
                        fndbaserate.setBaseRateSet(fndBaseRateSet.getBaseRateSet());
                        fndBaseRateService.updateByPrimaryKey(iRequest, fndbaserate);
                    }
                }
            }


        }


    }

    public List<FndBaseRateSet> queryForFinanceContract(FndBaseRateSet fndBaseRateSet) {
        return this.fndBaseRateSetMapper.queryForFinanceContract(fndBaseRateSet);
    }

    public void batchDeleteLine(List<FndBaseRateSet> fndBaseRateSetList) {
        Example example = new Example(FndBaseRate.class);
        if (CollectionUtils.isNotEmpty(fndBaseRateSetList)) {
            fndBaseRateSetList.forEach((fndBaseRateSet) -> {
                example.createCriteria().andEqualTo("baseRateSet", fndBaseRateSet.getBaseRateSet());
                this.fndBaseRateMapper.deleteByExample(example);
            });
        }

    }

}
