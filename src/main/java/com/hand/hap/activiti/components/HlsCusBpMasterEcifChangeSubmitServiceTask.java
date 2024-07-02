package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusBpMasterAddress;
import com.hand.hls.bp.service.HlsBpMasterAddressService;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.ecif.dto.HlsCusBpMasterRequestRecords;
import com.hand.hls.ecif.dto.HlsCusEcifBpMasterChange;
import com.hand.hls.ecif.service.HlsCusBpMasterRequestRecordsService;
import com.hand.hls.ecif.service.HlsCusEcifBpMasterChangeService;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 客户变更工作流结束事件
 * @Author: wangchao
 * @Date: Created in 16:51 2020/4/27
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusBpMasterEcifChangeSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private HlsCusEcifBpMasterChangeService service;

    @Autowired
    private HlsCusBpMasterService hlsBpMasterService;

    @Autowired
    private HlsBpMasterAddressService hlsBpMasterAddressService;

    @Autowired
    private HlsCusBpMasterRequestRecordsService hlsCusBpMasterRequestRecordsService;




    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest iRequest = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long ecifChangeId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        HlsCusEcifBpMasterChange newDto = new HlsCusEcifBpMasterChange();
        newDto.setEcifChangeId(ecifChangeId);
        newDto.setWflStatus(result);

        HlsCusEcifBpMasterChange hlsCusEcifBpMasterChange=  service.selectByPrimaryKey(iRequest,newDto);
        if ("APPROVED".equalsIgnoreCase(result)) {
            //回写商业伙伴表
            hlsCusBpMasterRequestRecordsService.dataBackBpMaster(iRequest,hlsCusEcifBpMasterChange);
            //调用批量创建接口
            HlsCusBpMasterRequestRecords hlsCusBpMasterRequestRecords= new HlsCusBpMasterRequestRecords();
            hlsCusBpMasterRequestRecords.setBpId(hlsCusEcifBpMasterChange.getBpId());
            hlsCusBpMasterRequestRecords.setDataType("BP");
            hlsCusBpMasterRequestRecords= hlsCusBpMasterRequestRecordsService.wsEcifBatchCreateUpdate(iRequest,hlsCusBpMasterRequestRecords);

            newDto.setMsg(hlsCusBpMasterRequestRecords.getMsg());

        } else if ("APPROVED_RETURN".equalsIgnoreCase(result)) {

        }
        service.updateByPrimaryKeySelective(iRequest, newDto);
    }


}
