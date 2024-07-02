//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hand.hls.bp.dto.BPRelation;
import com.hand.hls.bp.dto.HlsCusBpMasterRelation;
import com.hand.hls.bp.mapper.BPRelationMapper;
import com.hand.hls.bp.mapper.HlsCusBpMasterRelationMapper;
import com.hand.hls.bp.service.HlsBpMasterRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HlsBpMasterRelationServiceImpl extends BaseServiceImpl<HlsCusBpMasterRelation> implements HlsBpMasterRelationService {

    @Autowired
    HlsCusBpMasterRelationMapper mapper;

    @Autowired
    BPRelationMapper relationMapper;

    HlsCusBpMasterRelation relation;

    public HlsBpMasterRelationServiceImpl() {
    }

    @Override
    public List<HlsCusBpMasterRelation> selectRelationType(Long bpId) {
        return this.mapper.selectRelationType(bpId);
    }

    @Override
    public List<HlsCusBpMasterRelation> selectRelationCode(Long bpId) {
        return this.mapper.selectRelationCode(bpId);
    }

    @Override
    public Long queryBpId(String bpCode, String bpName) {
        return this.mapper.queryBpId(bpCode, bpName);
    }

    @Override
    public List<HlsCusBpMasterRelation> queryAll(IRequest requestContext, HlsCusBpMasterRelation bpMasterRelation, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return this.mapper.queryAll(bpMasterRelation);
    }

    @Override
    public List<HlsCusBpMasterRelation> queryAllType(IRequest requestContext, HlsCusBpMasterRelation bpMasterRelation, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return this.mapper.queryAllType(bpMasterRelation);
    }

    @Override
    public void batchRelationDelete(List<HlsCusBpMasterRelation> lists) {
        if (lists != null) {
            Iterator var2 = lists.iterator();

            while(var2.hasNext()) {
                HlsCusBpMasterRelation list = (HlsCusBpMasterRelation)var2.next();
                this.relation = new HlsCusBpMasterRelation();
                this.relation.setBpId(list.getRelationBpId());
                this.relation.setRelationBpId(list.getBpId());
                this.relation.setBpRelationType(list.getBpRelationType());
                this.mapper.deleteByPrimaryKey(list);
                this.mapper.deleteByPrimaryKey(this.relation);
            }
        }

    }

    @Override
    public List<HlsCusBpMasterRelation> batchRelationUpdate(IRequest requestContext, List<HlsCusBpMasterRelation> bpMasterRelations) {
        List<HlsCusBpMasterRelation> brs = new ArrayList();
        if (bpMasterRelations != null) {
            Iterator var4 = bpMasterRelations.iterator();

            while(var4.hasNext()) {
                HlsCusBpMasterRelation list = (HlsCusBpMasterRelation)var4.next();
                this.relation = new HlsCusBpMasterRelation();
                this.relation.setBpId(list.getRelationBpId());
                this.relation.setRelationBpId(list.getBpId());
                BPRelation rm = (BPRelation)this.relationMapper.selectByPrimaryKey(list.getBpRelationType());
                this.relation.setBpRelationType(rm.getBpRelationTypeOp());
                ((HlsBpMasterRelationService)this.self()).insertSelective(requestContext, list);
                ((HlsBpMasterRelationService)this.self()).insertSelective(requestContext, this.relation);
                brs.add(list);
            }
        }

        return brs;
    }

    @Override
    public List<HlsCusBpMasterRelation> selectBpRelationType(IRequest requestContext, HlsCusBpMasterRelation bpMasterRelation) {
        return this.mapper.selectBpRelationType(bpMasterRelation);
    }
}
