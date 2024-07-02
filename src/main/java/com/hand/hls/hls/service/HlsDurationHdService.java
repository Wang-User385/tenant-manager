package com.hand.hls.hls.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.hls.dto.HlsDurationHd;
import com.hand.hls.hls.dto.HlsDurationLn;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.utils.ResMessageException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.List;

public interface HlsDurationHdService extends IBaseService<HlsDurationHd>, ProxySelf<HlsDurationHdService> {
    List<HlsDurationHd> hlsDurationSave(IRequest iRequest, HlsDurationHd hd) throws ResMessageException;

    List<HlsDurationHd> hlsDurationReplySave(IRequest iRequest, HttpSession session, HlsDurationHd hd) throws ParseException;

    List<HlsDurationHd> submitWfl(IRequest iRequest, List<HlsDurationHd> hdList) throws ResMessageException;

    HlsDurationHd submitReplyWfl(IRequest iRequest, HlsDurationHd hdList) throws ResMessageException;

    List<HlsDurationLn> etSave(IRequest iRequest, HlsDurationHd hd) throws ResMessageException;

    List<HlsDurationLn> projectSave(IRequest iRequest, HttpSession session, HlsDurationHd hd) throws ParseException;

    void contractCheck(Long contractId, HlsDurationHd hd) throws ResMessageException;

    void contractCheckList(List<Long> contractIds, HlsDurationHd hd, String durationType) throws ResMessageException;

    HlsDurationHd executeCreate(IRequest iRequest, HttpSession session, HlsDurationHd hd) throws ParseException;

    void executeCancel(HlsDurationHd hd);

    void executeUpdate(IRequest iRequest, HlsDurationHd hd);

    List<HlsDurationHd> executeSubmit(IRequest iRequest, List<HlsDurationHd> hdList) throws ResMessageException;

    void executeCheck(Long hdId) throws ResMessageException;

    void conCashCheckList(HlsDurationLn hlsDurationln, String var) throws ResMessageException;

    List<HlsDurationHd> queryHlsDurationHdForPrjLov(IRequest iRequest, HlsDurationHd hd, int pagenum, int pagesize);

    String prjContractLeaseItemCheck(IRequest iRequest, HttpSession session, HlsDurationHd hd);

    List<HlsCusConContract> executeCashflow(IRequest iRequest, HlsDurationHd hd);

    void executeLeaseItem(IRequest requestCtx, HlsDurationHd hd);

    void executeBpInfo(IRequest requestCtx, HlsDurationHd hd);

    List<FndAttachmentMulti> contextCreateMultiple(IRequest request, String code, String hdId, HttpServletResponse response) throws Exception;

    void executeEt(IRequest request, HlsDurationHd hd);

    void executeEnd(IRequest request, HlsDurationHd hd);

    HlsCusPrjProject durationCreate(IRequest request, HlsDurationHd hd);
}

