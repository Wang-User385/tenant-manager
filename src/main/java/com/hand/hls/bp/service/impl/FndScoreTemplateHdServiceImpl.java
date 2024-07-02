//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.FndScoreTemplateDbSource;
import com.hand.hls.bp.dto.FndScoreTemplateHd;
import com.hand.hls.bp.dto.FndScoreTemplateHdValue;
import com.hand.hls.bp.dto.FndScoreTemplateLn;
import com.hand.hls.bp.dto.FndScoreTemplateLnValue;
import com.hand.hls.bp.mapper.FndScoreTemplateDbSourceMapper;
import com.hand.hls.bp.mapper.FndScoreTemplateHdMapper;
import com.hand.hls.bp.mapper.FndScoreTemplateHdValueMapper;
import com.hand.hls.bp.mapper.FndScoreTemplateLnMapper;
import com.hand.hls.bp.mapper.FndScoreTemplateLnValueMapper;
import com.hand.hls.bp.service.IFndScoreTemplateHdService;
import com.hand.hls.fnd.dto.HlsDbDataSourceColumn;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FndScoreTemplateHdServiceImpl extends BaseServiceImpl<FndScoreTemplateHd> implements IFndScoreTemplateHdService {
    @Autowired
    private FndScoreTemplateHdMapper fndScoreTemplateHdMapper;
    @Autowired
    private FndScoreTemplateHdValueMapper fndScoreTemplateHdValueMapper;
    @Autowired
    private FndScoreTemplateLnMapper fndScoreTemplateLnMapper;
    @Autowired
    private FndScoreTemplateLnValueMapper fndScoreTemplateLnValueMapper;
    @Autowired
    private FndScoreTemplateDbSourceMapper fndScoreTemplateDbSourceMapper;

    public FndScoreTemplateHdServiceImpl() {
    }

    public List<FndScoreTemplateHd> selectList(FndScoreTemplateHd fndScoreTemplateHd) {
        return this.fndScoreTemplateHdMapper.selectList(fndScoreTemplateHd);
    }

    @Override
    public List<FndScoreTemplateHd> selectScoreTemplateHd() {
        return this.fndScoreTemplateHdMapper.selectScoreTemplateHd();
    }

    public List<HlsDbDataSourceColumn> dbCloumnData(FndScoreTemplateHd dto) {
        return this.fndScoreTemplateHdMapper.dbCloumnData(dto);
    }

    public List<FndScoreTemplateHd> query(IRequest request, FndScoreTemplateHd fndScoreTemplateHd, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return this.fndScoreTemplateHdMapper.query1(fndScoreTemplateHd);
    }

    public void deleteChild(List<FndScoreTemplateHd> fndScoreTemplateHds) {
        Iterator var2 = fndScoreTemplateHds.iterator();

        while(var2.hasNext()) {
            FndScoreTemplateHd f = (FndScoreTemplateHd)var2.next();
            FndScoreTemplateHdValue fth = new FndScoreTemplateHdValue();
            fth.setScoreTemplateHdId(f.getScoreTemplateHdId());
            Iterator var5 = this.fndScoreTemplateHdValueMapper.select(fth).iterator();

            while(var5.hasNext()) {
                FndScoreTemplateHdValue fsthv = (FndScoreTemplateHdValue)var5.next();
                this.fndScoreTemplateHdValueMapper.delete(fth);
            }

            FndScoreTemplateLn fstl = new FndScoreTemplateLn();
            fstl.setScoreTemplateHdId(f.getScoreTemplateHdId());
            Iterator var12 = this.fndScoreTemplateLnMapper.select(fstl).iterator();

            while(var12.hasNext()) {
                FndScoreTemplateLn ftl = (FndScoreTemplateLn)var12.next();
                FndScoreTemplateLnValue fstlv = new FndScoreTemplateLnValue();
                fstlv.setScoreTemplateLnId(ftl.getScoreTemplateLnId());
                Iterator var9 = this.fndScoreTemplateLnValueMapper.select(fstlv).iterator();

                while(var9.hasNext()) {
                    FndScoreTemplateLnValue fstlvs = (FndScoreTemplateLnValue)var9.next();
                    this.fndScoreTemplateLnValueMapper.delete(fstlvs);
                }

                this.fndScoreTemplateLnMapper.delete(ftl);
            }

            FndScoreTemplateDbSource fstds = new FndScoreTemplateDbSource();
            fstds.setScoreTemplateHdId(f.getScoreTemplateHdId());
            this.fndScoreTemplateDbSourceMapper.delete(fstds);
            this.fndScoreTemplateHdMapper.delete(f);
        }

    }
}
