package com.hand.hls.sys.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.sys.dto.HlsSystemNotice;
import com.hand.hls.sys.dto.HlsSystemNoticeOwner;
import com.hand.hls.sys.mapper.HlsSystemNoticeMapper;
import com.hand.hls.sys.mapper.HlsSystemNoticeOwnerMapper;
import com.hand.hls.sys.service.IHlsSystemNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HlsSystemNoticeServiceImpl extends BaseServiceImpl<HlsSystemNotice> implements IHlsSystemNoticeService {

    @Autowired
    HlsSystemNoticeMapper hsnm;
    @Autowired
    HlsSystemNoticeOwnerMapper ownerMapper;
    @Autowired
    HlsSystemNoticeOwnerServiceImpl ownerService;

    @Override
    public List<HlsSystemNotice> selectCurUserMsg(IRequest iRequest, HlsSystemNotice hsn, int pagenum, int pagesize) {
        // TODO Auto-generated method stub
        PageHelper.startPage(pagenum,pagesize);
        return hsnm.selectUserNotice(iRequest.getAttribute("allocationId"),hsn);
    }
    @Override
    public List<HlsSystemNotice> selectCurUserAllMsg(IRequest iRequest, HlsSystemNotice hsn, int pagenum, int pagesize) {
        // TODO Auto-generated method stub
        PageHelper.startPage(pagenum,pagesize);
        return hsnm.selectUserAllNotice(iRequest.getAttribute("allocationId"),hsn);
    }
    @Override
    public List<HlsSystemNotice> selectCurUserTodo(IRequest iRequest, HlsSystemNotice hsn, int pagenum, int pagesize) {
        // TODO Auto-generated method stub
        PageHelper.startPage(pagenum,pagesize);
        return hsnm.selectUserTodo(iRequest.getAttribute("allocationId"),hsn);
    }
    @Override
    public List<HlsSystemNotice> setAllRead(IRequest iRequest) {
        HlsSystemNotice dto = new HlsSystemNotice();
        dto.setSourceUserId(iRequest.getUserId());
        dto.setNoticeType("NOTICE");
        dto.setReadFlag("N");
        List<HlsSystemNotice> hlsSystemNoticeList = hsnm.selectUserNotice(iRequest.getAttribute("allocationId"),dto);
        List<HlsSystemNoticeOwner> hlsSystemNoticeOwnerList = new ArrayList<>();
        if(hlsSystemNoticeList.size()>0){
            for(int i=0;i<hlsSystemNoticeList.size();i++){
                HlsSystemNoticeOwner hlsSystemNoticeOwner = new HlsSystemNoticeOwner();
                hlsSystemNoticeOwner.setNotice_id(hlsSystemNoticeList.get(i).getNoticeId());
                hlsSystemNoticeOwner.setOwner_allocation_id(hlsSystemNoticeList.get(i).getSourceAllocationId());
                hlsSystemNoticeOwner.setOwner_user_id(hlsSystemNoticeList.get(i).getSourceUserId());
                List<HlsSystemNoticeOwner> owners = ownerMapper.queryOwnerNoticeById(hlsSystemNoticeOwner);
                hlsSystemNoticeOwner.setRead_flag("Y");
                if(owners.size()>0){
                    hlsSystemNoticeOwner.set__status("update");
                    ownerMapper.updateHlsOwner(hlsSystemNoticeOwner);
                }else {
                    hlsSystemNoticeOwner.set__status("insert");
                    hlsSystemNoticeOwnerList.add(hlsSystemNoticeOwner);
                }
            }
            ownerService.batchUpdate(iRequest,hlsSystemNoticeOwnerList);
        }
        return hlsSystemNoticeList;
    }
    @Override
    public List<HlsSystemNotice> queryNoticeByTodoAndDocId(Long source_document_id) {
        return hsnm.queryNoticeByTodoAndDocId(source_document_id);
    }
//    @Override
//    public List<HlsSystemNotice> selectSendNotice(HlsSystemNotice hlsSystemNotice) {
//        // TODO Auto-generated method stub
//        return hsnm.selectSendNotice(hlsSystemNotice);
//    }

    @Override
    public List<HlsSystemNotice> setRead(IRequest iRequest, HlsSystemNotice hlsSystemNotice) {
        List<HlsSystemNotice> hlsSystemNoticeList = hsnm.selectUserNotice(iRequest.getAttribute("allocationId"),hlsSystemNotice);
        List<HlsSystemNoticeOwner> hlsSystemNoticeOwnerList = new ArrayList<>();
        if(hlsSystemNoticeList.size()>0){
            for(int i=0;i<hlsSystemNoticeList.size();i++){
                HlsSystemNoticeOwner hlsSystemNoticeOwner = new HlsSystemNoticeOwner();
                hlsSystemNoticeOwner.setNotice_id(hlsSystemNoticeList.get(i).getNoticeId());
                hlsSystemNoticeOwner.setOwner_allocation_id(hlsSystemNoticeList.get(i).getSourceAllocationId());
                List<HlsSystemNoticeOwner> owners = ownerMapper.queryOwnerNoticeById(hlsSystemNoticeOwner);
                hlsSystemNoticeOwner.setRead_flag("Y");
                if(owners.size()>0){
                    hlsSystemNoticeOwner.set__status("update");
                    hlsSystemNoticeOwner.setRead_flag("Y");
                    ownerMapper.updateByPrimaryKey(hlsSystemNoticeOwner);
//                    ownerMapper.updateHlsOwner(hlsSystemNoticeOwner);
                }else {
                    hlsSystemNoticeOwner.set__status("insert");
                    hlsSystemNoticeOwnerList.add(hlsSystemNoticeOwner);
                }
            }
            ownerService.batchUpdate(iRequest,hlsSystemNoticeOwnerList);
        }
        return hlsSystemNoticeList;
    }

    @Override
    public List<HlsSystemNotice> userUnreadCount(IRequest iRequest, HlsSystemNotice hsn, int pagenum, int pagesize) {
        return hsnm.userUnreadCount(iRequest.getAttribute("allocationId"),hsn);
    }

}
