package com.hand.hls.plm.pli.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.plm.pli.dto.PlmPliCheckItem;
import com.hand.hls.plm.pli.dto.PlmPliCheckList;
import com.hand.hls.plm.pli.mapper.PlmPliCheckItemMapper;
import com.hand.hls.plm.pli.mapper.PlmPliCheckListMapper;
import com.hand.hls.plm.pli.service.PlmPliCheckItemService;
import com.hand.hls.utils.HlsCusConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class PlmPliCheckItemServiceImpl extends BaseServiceImpl<PlmPliCheckItem> implements PlmPliCheckItemService {

    @Autowired
    private PlmPliCheckListMapper plmPliCheckListMapper;
    @Autowired
    private PlmPliCheckItemMapper mapper;

    /**
     * 初始化贷后检查事项列表
     * @param dto
     * @return
     */
    @Override
    public List<PlmPliCheckItem> createItemForPli(PlmPliCheckItem dto,String[] listType) {

        PlmPliCheckList plmPliCheckList = new PlmPliCheckList();
        plmPliCheckList.setEnableFlag(HlsCusConstant.FLAG.Y);
        plmPliCheckList.setListCategory(dto.getListCategory());
        List<PlmPliCheckList> plmPliCheckLists = new ArrayList<>();
        for(String dt : listType){
            plmPliCheckList.setListType(dt);
            List<PlmPliCheckList> lists = plmPliCheckListMapper.select(plmPliCheckList);
            if (lists.size()>0){
                plmPliCheckLists.addAll(lists);
            }
        }

        for(PlmPliCheckList po : plmPliCheckLists){
            PlmPliCheckItem dt = new PlmPliCheckItem();
            dt.setPostloanInspectionId(dto.getPostloanInspectionId());
            if(dto.getPlmPliItemId()!=null){
                dt.setPlmPliItemId(dto.getPlmPliItemId());
            }else {
                dt.setPlmPliItemId(-1L);
            }
            dt.setListType(po.getListType());
            dt.setMatter(po.getMatter());
            dt.setValueKey(po.getValueKey());
            dt.setListCategory(po.getListCategory());
            dt.set__status("insert");

            mapper.insertSelective(dt);
        }

        PlmPliCheckItem sel = new PlmPliCheckItem();
        sel.setPostloanInspectionId(dto.getPostloanInspectionId());
        return mapper.select(sel);
    }

    @Override
    public int delete(PlmPliCheckItem dto) {
        return this.mapper.delete(dto);
    }
}
