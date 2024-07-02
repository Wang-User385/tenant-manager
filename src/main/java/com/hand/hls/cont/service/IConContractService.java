package com.hand.hls.cont.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.sun.istack.NotNull;


import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IConContractService extends IBaseService<HlsCusConContract>, ProxySelf<IConContractService> {

    /**
     * 业务确认函批量打印信息创建
     */
    List<Long> createBusinessConfirm(IRequest iRequest, List<Long> list) throws Exception;

    /**
     * 二期功能：投放审查申请工作流审批通过后，自动生成合同数据
     * @param iRequest
     * @param prjProjectParameter
     */
    void saveConContractFromPrjProjectSign(IRequest iRequest, HlsCusPrjProject prjProjectParameter);

    /**
     * 二期功能：付款申请-取消合同按钮更新合同/项目表字段
     * @param iRequest
     * @param contractIds
     * @param returnDate
     * @param returnReason
     * @param returnDescription
     */
    void updatePaymentReturn(IRequest iRequest, Long[] contractIds, Date returnDate, String returnReason, String returnDescription);

    /**
     * 二期功能：修改合同or项目编号后缀加上 _取消
     * @param iRequest
     * @param documentCategory
     * @param documentId
     * @throws HlsCusException
     */
    void documentNumberCancel(IRequest iRequest, String documentCategory, Long documentId) throws HlsCusException;
    /**
     * 经销商业务确认函创建
     */
    List<Long> createDealerBusinessConfirm(IRequest iRequest, List<Long> list) throws Exception;

    /**
     * 判断是否为对应的角色
     */
    Boolean isCompanyManageRole(IRequest iRequest,@NotNull String roleCode);

    /**
     * 零售业务合同结束
     */
    void terminate(IRequest iRequest, HlsCusConContract hlsCusConContract) throws HlsCusException;

    /**
     * 大单部分提前还本不规则报价现金流导入
     */
    ResponseData conContractChangePrepaymentCashflowExcelImport(IRequest iRequest, Long headerId,Long changeReqId) throws Exception;
}
