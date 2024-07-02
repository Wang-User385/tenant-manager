package com.hand.hls.fct.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fct.dto.HlsCusFctProjectBp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface HlsCusFctProjectBpService extends IBaseService<HlsCusFctProjectBp>, ProxySelf<FctProjectBpService> {

    List<HlsCusFctProjectBp> fctProjectBpQuery(HlsCusFctProjectBp hlsCusFctProjectBp, int page, int pagesize);

    List<HlsCusFctProjectBp> fctProjectBpInfoQuery(HlsCusFctProjectBp hlsCusFctProjectBp, int page, int pagesize);

    void fctProjectBpInfoDownloadExcel(HttpServletRequest request, HttpServletResponse response, HlsCusFctProjectBp hlsCusFctProjectBp) throws IOException, InvocationTargetException, IllegalAccessException;

    List<HlsCusFctProjectBp> deleteCreditor(HlsCusFctProjectBp dto, IRequest request);

    /**
     * 根据项目主键【projectId】去查询卖方【SELLER】的银行账户信息的bank_account_id
     * @param request
     * @param projectId
     */
    List<HlsCusFctProjectBp> selectSellerInfoByProjectId(IRequest request, Long projectId);

    /**
     * 查询合同变更的 bp信息
     * @param iRequest
     * @param dto
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusFctProjectBp> fctProjectBpQueryBef(IRequest iRequest, HlsCusFctProjectBp dto, int page, int pageSize);

    List<HlsCusFctProjectBp> selectSentBpName(HlsCusFctProjectBp hlsCusFctProjectBp, int page, int pagesize);

    List<HlsCusFctProjectBp> selectBpMasterNotSave(IRequest request, Long projectId);

    List<HlsCusFctProjectBp> fctProjectBpUpdate(IRequest requestCtx, List<HlsCusFctProjectBp> dto);

    List<HlsCusFctProjectBp> createSerialNumber(Long projectId, List<HlsCusFctProjectBp> dtos, String ROLETYPE);

    List<HlsCusFctProjectBp> updateExpiry(IRequest request, List<HlsCusFctProjectBp> dtos);

    /**
     * 查询授信方
     * @param projectId
     * @return
     */
    HlsCusFctProjectBp queryCreditGrantorByProjectId(Long projectId);
}