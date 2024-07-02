//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.FndScoreTemplateLn;
import com.hand.hls.bp.dto.FndScoreTemplateLnValue;
import com.hand.hls.bp.mapper.FndScoreTemplateLnMapper;
import com.hand.hls.bp.mapper.FndScoreTemplateLnValueMapper;
import com.hand.hls.bp.service.IFndScoreTemplateLnService;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FndScoreTemplateLnServiceImpl extends BaseServiceImpl<FndScoreTemplateLn> implements IFndScoreTemplateLnService {
    @Autowired
    private FndScoreTemplateLnValueMapper fndScoreTemplateLnValueMapper;
    @Autowired
    private FndScoreTemplateLnMapper fndScoreTemplateLnMapper;

    public FndScoreTemplateLnServiceImpl() {
    }

    public List<FndScoreTemplateLn> select(IRequest request, FndScoreTemplateLn condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return this.fndScoreTemplateLnMapper.select(condition);
    }

    public List<FndScoreTemplateLn> selectLevelOne(IRequest requestContext, FndScoreTemplateLn condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return this.fndScoreTemplateLnMapper.selectLevelOne(condition);
    }

    public void deleteChild(List<FndScoreTemplateLn> fndScoreTemplateLns) {
        Iterator var2 = fndScoreTemplateLns.iterator();

        while(var2.hasNext()) {
            FndScoreTemplateLn fstl = (FndScoreTemplateLn)var2.next();
            FndScoreTemplateLnValue fstlv = new FndScoreTemplateLnValue();
            fstlv.setScoreTemplateLnId(fstl.getScoreTemplateLnId());
            Iterator var5 = this.fndScoreTemplateLnValueMapper.select(fstlv).iterator();

            while(var5.hasNext()) {
                FndScoreTemplateLnValue fstlvs = (FndScoreTemplateLnValue)var5.next();
                this.fndScoreTemplateLnValueMapper.delete(fstlv);
            }

            this.fndScoreTemplateLnMapper.delete(fstl);
        }

    }

    public List<FndScoreTemplateLn> queryScoreTemplateLn(FndScoreTemplateLn fndScoreTemplateLn) {
        List<FndScoreTemplateLn> list = this.fndScoreTemplateLnMapper.queryScoreTemplateLn1(fndScoreTemplateLn);
        List<FndScoreTemplateLn> list2 = new ArrayList();
        String TempTargetName = null;
        List<FndScoreTemplateLn> list3 = recordTreeToList(list, list2, (String)TempTargetName);
        List<FndScoreTemplateLn> result = new ArrayList();
        Iterator var6 = list3.iterator();

        while(var6.hasNext()) {
            FndScoreTemplateLn fnd = (FndScoreTemplateLn)var6.next();
            if (fnd.getSummaryFlag() != null && fnd.getSummaryFlag().equals("N")) {
                result.add(fnd);
            }
        }

        return result;
    }

    @Override
    public List<FndScoreTemplateLn> queryScoreTemplateLn2(Long[] var1, Long var2) {
        HashMap map = new HashMap();
        map.put("scoreTemplateLnId",var1);
        map.put("scoreResultId",var2);
        List<FndScoreTemplateLn> list = this.fndScoreTemplateLnMapper.queryScoreTemplateLn2(map);
        List<FndScoreTemplateLn> list2 = new ArrayList();
        String TempTargetName = null;
        List<FndScoreTemplateLn> list3 = recordTreeToList(list, list2, (String)TempTargetName);
        List<FndScoreTemplateLn> result = new ArrayList();
        Iterator var6 = list3.iterator();

        while(var6.hasNext()) {
            FndScoreTemplateLn fnd = (FndScoreTemplateLn)var6.next();
            if (fnd.getSummaryFlag() != null && fnd.getSummaryFlag().equals("N")) {
                result.add(fnd);
            }
        }

        return result;
    }


    private static List<FndScoreTemplateLn> recordTreeToList(List<FndScoreTemplateLn> list, List<FndScoreTemplateLn> relist, String targetName) {
        Iterator var3 = list.iterator();

        while(var3.hasNext()) {
            FndScoreTemplateLn fnd = (FndScoreTemplateLn)var3.next();
            if (fnd.getScoreTemplateLn().size() >= 0) {
                List<FndScoreTemplateLn> fndArray = fnd.getScoreTemplateLn();
                fnd.setTargetName(targetName);
                relist.add(fnd);
                recordTreeToList(fndArray, relist, fnd.getFirstTargetName());
            }
        }

        return relist;
    }

    public List<FndScoreTemplateLn> selectLnRoot(Long scoreTemplateHdId) {
        return this.fndScoreTemplateLnMapper.selectLnRoot1(scoreTemplateHdId);
    }
}
