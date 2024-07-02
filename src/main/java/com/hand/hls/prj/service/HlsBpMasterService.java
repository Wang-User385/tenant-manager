package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.BpCategoryInfoLov;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.prj.dto.BpDealerInfoLov;
import com.hand.hls.prj.dto.BpManufacturerInfoLov;
import com.hand.hls.prj.dto.BpVenderInfoLov;
import com.hand.hls.prj.dto.HlsBpMaster;

import java.util.List;


public interface HlsBpMasterService extends IBaseService<HlsBpMaster> {

    /**
     * 二期功能：保证金的付款对象查询
     * @param requestCtx
     * @param transactionIds
     * @param refundId
     * @param pagenum
     * @param pagesize
     * @return
     */
    List<HlsBpMaster> queryPaymentBpMasterLov(IRequest requestCtx, String transactionIds, String refundId, int pagenum, int pagesize);


    List<HlsBpMaster> queryBpMasterLov(IRequest requestCtx, HlsBpMaster hlsBpMaster, int pagenum, int pagesize);

    List<HlsCusBpMaster> validBpNameQuery(IRequest requestCtx, String bpName);

    List<HlsBpMaster> queryBpMasterVenderLov(IRequest var1, HlsBpMaster var2, int var3, int var4);

    List<HlsBpMaster> queryBpMasterVenderLovNew(IRequest var1, HlsBpMaster hlsBpMaster, int var3, int var4);


    List<HlsBpMaster> queryBpMasterLov2(IRequest requestCtx, HlsBpMaster hlsBpMaster, int pagenum, int pagesize);

    List<HlsBpMaster> bpMasterHomeQuery(IRequest requestCtx, HlsBpMaster hlsBpMaster, int pagenum, int pagesize);

    List<HlsBpMaster> selectForLovIf(HlsBpMaster dto, int pagenum, int pagesize);

    List<HlsBpMaster> lonCreditBpLovQuery(IRequest requestCtx, HlsBpMaster hlsBpMaster, int pageNum, int pageSize);

    /**
     * 查询厂商/合作方lov框
     * @param requestCtx
     * @param bpManufacturerInfoLov
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<BpManufacturerInfoLov> bpManufacturerInfoQuery(IRequest requestCtx, BpManufacturerInfoLov bpManufacturerInfoLov, int pageNum, int pageSize);


    /**
     * 查询主机厂/合作方lov框
     * @param requestCtx
     * @param bpVenderInfoLov
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<BpVenderInfoLov> bpVenderInfoQuery(IRequest requestCtx, BpVenderInfoLov bpVenderInfoLov, int pageNum, int pageSize);

    /**
     * 查询经销商
     * @param requestCtx
     * @param bpDealerInfoLov
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<BpDealerInfoLov> bpDealerInfoQuery(IRequest requestCtx, BpDealerInfoLov bpManufacturerInfoLov, int pageNum, int pageSize);
    /**
     * 校验登记注册号码
     *
     * @param bpId
     * @param regNumber
     * @return
     */
    Boolean validRegNumber(IRequest iRequest, Long bpId, String regNumber);
    Boolean validRegNumber1(IRequest var1, Long var2, String var3);

    Boolean validIdCardNo(IRequest iRequest, Long bpId, String idCardNo);

    Boolean validBpName(IRequest iRequest, Long bpId, String bpNme);
    Boolean validBpName2(IRequest var1, Long var2, String var3);

    List<BpCategoryInfoLov> bpTypeInfoQuery(IRequest var1, BpCategoryInfoLov var2, int var3, int var4);
    /**
     * 查询商业伙伴
     */
    HlsBpMaster queryByBpId(long bpId);
    String getAuthorityString(IRequest iRequest);
    List<HlsBpMaster> bpManufacturerPartnerLovQuery();
}
