package com.hand.hls.ast.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.ast.dto.NoticeManage;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.fct.dto.HlsCusFctQuotationCashflow;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface INoticeManageService extends IBaseService<NoticeManage>, ProxySelf<INoticeManageService>{

    List<NoticeManage> cashflowInfo(NoticeManage noticeManage, IRequest requestContext, Integer page, Integer pageSize);

    List<NoticeManage> queryContractCashflowLov(IRequest var1, NoticeManage var2, int var3, int var4);

    List<NoticeManage> queryNoticeTempLov(IRequest var1, NoticeManage var2, int var3, int var4);

    ResponseData downloadNoticePrintFile(List<NoticeManage> noticeManageList, IRequest requestContext, HttpServletRequest request, HttpServletResponse response);

    List<NoticeManage> queryContractCashflowItemLov(IRequest var1, NoticeManage var2, int var3, int var4);

}