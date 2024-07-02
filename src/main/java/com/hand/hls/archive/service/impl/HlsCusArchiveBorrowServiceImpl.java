package com.hand.hls.archive.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.archive.dto.HlsCusArchive;
import com.hand.hls.archive.mapper.HlsCusArchiveMapper;
import hls.core.utils.exception.HlsCusException;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.wfl.service.IActivitiStartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.archive.dto.HlsCusArchiveBorrow;
import com.hand.hls.archive.service.HlsCusArchiveBorrowService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusArchiveBorrowServiceImpl extends BaseServiceImpl<HlsCusArchiveBorrow> implements HlsCusArchiveBorrowService{
    @Autowired
    private HlsEmployeeMapper employeeMapper;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private HlsCusArchiveMapper hlsCusArchiveMapper;
    @Override
    public void approvalSubmit(IRequest iRequest, HlsCusArchiveBorrow hlsCusArchiveBorrow) throws  HlsCusException{
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        if (ObjectUtils.isEmpty(employee)) {
            throw new HlsCusException("获取提交人失败");
        }
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);

        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "JC_BORROWING_WFL");
        List<HlsCusArchiveBorrow> list = new ArrayList<>();
        list.add(hlsCusArchiveBorrow);
        activitiStartService.start(iRequest, list, params);
    }

    @Override
    public void archiveBorrowReturn(IRequest iRequest, HlsCusArchiveBorrow hlsCusArchiveBorrow)  {
        hlsCusArchiveBorrow.setReturnDate(new Date());
        hlsCusArchiveBorrow.setBorrowReturnStatus("RETURNED");
        self().updateByPrimaryKeySelective(iRequest,hlsCusArchiveBorrow);
    }

    @Override
    public Boolean archiveBorrowCheck(IRequest iRequest, HlsCusArchive para)  throws HlsCusException{
        para.setArchivedFlag("Y");
        List<HlsCusArchive> list = hlsCusArchiveMapper.select(para);
        if(list.size()>0){
            return true;
        }else{
            throw new HlsCusException("已归类文件中无此类型文档");
        }
    }
}