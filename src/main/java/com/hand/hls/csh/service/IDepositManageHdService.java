package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.DepositManageHd;

import java.util.List;

public interface IDepositManageHdService extends IBaseService<DepositManageHd>, ProxySelf<IDepositManageHdService>{

    DepositManageHd submitApproval(IRequest requestCtx, DepositManageHd dto,String workFlowType) throws Exception;

    List<DepositManageHd> selectDepositManageByField(IRequest iRequest, DepositManageHd dto, Integer page, Integer pageSize, String sortName, String sortOrder);

    List<DepositManageHd> selectManageHdById1(DepositManageHd depositManageHd);
    //期中代付 校验
    DepositManageHd changeCheck(IRequest request, DepositManageHd depositManageHd) throws Exception;

    //保存chang_req
    DepositManageHd changeCreate(IRequest request, DepositManageHd depositManageHd) throws Exception;
    //修改计算现金流
    DepositManageHd changeCashflow(IRequest request, DepositManageHd depositManageHd) throws Exception;

}