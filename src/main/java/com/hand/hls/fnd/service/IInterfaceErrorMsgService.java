package com.hand.hls.fnd.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fnd.dto.InterfaceErrorMsg;

public interface IInterfaceErrorMsgService extends IBaseService<InterfaceErrorMsg>, ProxySelf<IInterfaceErrorMsgService>{

    /**
     * 导入异常信息存表
     * @param iRequest
     * @param headId
     * @param documentCategory
     * @param errorMessage
     */
    void insertInterfaceErrorMessage(IRequest iRequest, Long headId, String documentCategory, String errorMessage);

    /**
     * 查询导入是否有异常信息
     * @param headId
     * @param documentCategory
     * @return
     */
    String checkImportErrorMessageExist(Long headId, String documentCategory);

}