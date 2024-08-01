package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.BpMasterChangeReq;
import com.hand.hls.sys.dto.SysDocumentHistoryDetail;
import leaf.service.validation.ParameterNullException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface IBpMasterChangeReqService extends IBaseService<BpMasterChangeReq>, ProxySelf<IBpMasterChangeReqService> {

    boolean submit(IRequest iRequest, BpMasterChangeReq bpChangeReq) throws ParameterNullException;


    void approveWfl(IRequest iRequest, BpMasterChangeReq bpChangeReq, Long allocationId, String workflowType) throws Exception;

    List<Map<String, Object>> getDatas(Long bpId) throws ParameterNullException;

    void leaveHistory(IRequest iRequest, BpMasterChangeReq changeReq, Long processInstanceId) throws Exception;

    Boolean bpChangeValidate(IRequest iRequest, BpMasterChangeReq bpChangeReq) throws Exception;

    String getBpMasterChangeWflType(IRequest iRequest, BpMasterChangeReq bpChangeReq) throws Exception;

    /**
     * 经销商同步接口初始化商业伙伴变更信息
     * @param iRequest
     * @throws ParameterNullException
     */
    void initBpChangeInfo(IRequest iRequest,BpMasterChangeReq bbpChangeReqpId) throws ParameterNullException;

    /**
     * 商业伙伴创建变更
     * @param documentId
     * @param documentCategory
     * @param bpId
     * @throws ParameterNullException
     */
    void createHistory(Long documentId, String documentCategory, Long bpId) throws ParameterNullException;


    List<Long> count(Long bpId);
}