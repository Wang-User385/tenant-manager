package com.hand.hls.fnd.service.impl;

import com.hand.hap.core.BaseConstants;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.InterfaceErrorMsg;
import com.hand.hls.fnd.mapper.InterfaceErrorMsgMapper;
import com.hand.hls.fnd.service.IInterfaceErrorMsgService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class InterfaceErrorMsgServiceImpl extends BaseServiceImpl<InterfaceErrorMsg> implements IInterfaceErrorMsgService{

    @Autowired
    private InterfaceErrorMsgMapper interfaceErrorMsgMapper;

    /**
     * 导入异常信息存表
     * @param iRequest
     * @param headId
     * @param documentCategory
     * @param errorMessage
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void insertInterfaceErrorMessage(IRequest iRequest,Long headId,String documentCategory,String errorMessage){
        InterfaceErrorMsg interfaceErrorMsg = new InterfaceErrorMsg();
        interfaceErrorMsg.setHeadId(headId);
        interfaceErrorMsg.setDocumentCategory(documentCategory);
        interfaceErrorMsg.setErrorMsg(errorMessage);
        self().insertSelective(iRequest,interfaceErrorMsg);
    }

    /**
     * 查询导入是否有异常信息
     * @param headId
     * @param documentCategory
     * @return
     */
    @Override
    public String checkImportErrorMessageExist(Long headId,String documentCategory){
        //如果导入头ID为空，返回false
        if(headId == null){
            return BaseConstants.NO;
        }

        InterfaceErrorMsg interfaceErrorMsg = new InterfaceErrorMsg();
        interfaceErrorMsg.setHeadId(headId);
        interfaceErrorMsg.setDocumentCategory(documentCategory);
        List<InterfaceErrorMsg> interfaceErrorMsgList = interfaceErrorMsgMapper.select(interfaceErrorMsg);

        if(CollectionUtils.isEmpty(interfaceErrorMsgList)){
            return BaseConstants.YES;
        }else {
            return BaseConstants.NO;
        }
    }

}