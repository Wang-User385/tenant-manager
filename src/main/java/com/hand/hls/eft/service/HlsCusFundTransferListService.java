package com.hand.hls.eft.service;

import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.eft.dto.HlsCusFundTransfer;
import com.hand.hls.eft.dto.HlsCusFundTransferList;
import com.hand.hls.exception.HlsCusException;
import org.docx4j.openpackaging.exceptions.Docx4JException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;

public interface HlsCusFundTransferListService extends IBaseService<HlsCusFundTransferList>, ProxySelf<HlsCusFundTransferListService> {


    /**
     * 调拨单查询
     *
     * @param fundTransferList
     * @return
     */
    List<HlsCusFundTransferList> selectFundTransferList(IRequest iRequest, HlsCusFundTransferList fundTransferList, int page, int pageSize);


    /**
     * 资金主页查询调拨单详情
     *
     * @param fundTransferList
     * @return
     */
    List<HlsCusFundTransferList> selectFundTransferListDeatil(IRequest iRequest, HlsCusFundTransferList fundTransferList, int page, int pageSize);


    /**
     * 财务查询的调拨单
     *
     * @param fundTransferList
     * @return
     */
    List<HlsCusFundTransferList> selectFundTransferFinanceList(IRequest iRequest, HlsCusFundTransferList fundTransferList, int page, int pageSize);


    /**
     * 复制调拨单查询
     *
     * @param fundTransferList
     * @return
     */
    List<HlsCusFundTransferList> selectCopyFundTransferList(IRequest iRequest, HlsCusFundTransferList fundTransferList, int page, int pageSize);


    /**
     * 调拨审批单查询
     *
     * @param fundTransferList
     * @return
     */
    List<HlsCusFundTransferList> selectApproveTransferList(IRequest iRequest, HlsCusFundTransferList fundTransferList, int page, int pageSize);


    /**
     * 更新/新增 调拨单
     *
     * @param iRequest
     * @param hlsCusFundTransferLists
     * @return
     */
    List<HlsCusFundTransferList> batchUpdateTransferList(IRequest iRequest, List<HlsCusFundTransferList> hlsCusFundTransferLists);


    /**
     * 应还代办
     *
     * @param fundTransferList
     * @return
     */
    List<HlsCusFundTransferList> selectTransferTaskList(IRequest iRequest, HlsCusFundTransferList fundTransferList, int page, int pageSize);


    /**
     * 创建调拨单
     *
     * @param iRequest
     * @param hlsCusFundTransferLists
     * @return
     */
    List<HlsCusFundTransferList> createTransferListFund(IRequest iRequest, List<HlsCusFundTransferList> hlsCusFundTransferLists) throws HlsCusException;


    /**
     * 导出
     *
     * @param request
     * @param response
     * @param fundTransferList
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    void exportTransferList(HttpServletRequest request, HttpServletResponse response, HlsCusFundTransferList fundTransferList) throws IOException, InvocationTargetException, IllegalAccessException;


    /**
     * 创建调拨单
     *
     * @param iRequest
     * @param hlsCusFundTransferLists
     * @return
     */
    HlsCusFundTransfer createTransferListFinance(IRequest iRequest, List<HlsCusFundTransferList> hlsCusFundTransferLists) throws HlsCusException;


    /**
     * 作废调拨单
     *
     * @param iRequest
     * @param hlsCusFundTransferLists
     */
    void cancelTransferListFinance(IRequest iRequest, List<HlsCusFundTransferList> hlsCusFundTransferLists) throws HlsCusException;


    /**
     * 未来三十天账户余额
     *
     * @param fundTransferList
     * @return
     */
    List<HlsCusFundTransferList> selectAccountBalanceData(HlsCusFundTransferList fundTransferList);


    /**
     * 创建调拨单
     *
     * @param iRequest
     * @param hlsCusFundTransferLists
     * @return
     */
    List<HlsCusFundTransferList> createTransferListGap(IRequest iRequest, List<HlsCusFundTransferList> hlsCusFundTransferLists) throws HlsCusException;


    /**
     * 未来账户收付明细
     *
     * @param fundTransferList
     * @return
     */
    List<HlsCusFundTransferList> selectAccountBalanceDetail(IRequest iRequest, HlsCusFundTransferList fundTransferList, int page, int pageSize);


    /**
     * 查询账户详情
     *
     * @param fundTransferList
     * @return
     */
    List<HlsCusFundTransferList> selectBankAccountData(IRequest iRequest, HlsCusFundTransferList fundTransferList);


    /**
     * 删除调拨单 或者 财务取消
     *
     * @param iRequest
     * @param hlsCusFundTransferLists
     */
    void batchDeleteTransferList(IRequest iRequest, List<HlsCusFundTransferList> hlsCusFundTransferLists);


    /**
     * 导出收支明细
     *
     * @param request
     * @param response
     * @param fundTransferList
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    void exportBalanceDetail(HttpServletRequest request, HttpServletResponse response, HlsCusFundTransferList fundTransferList) throws IOException, InvocationTargetException, IllegalAccessException;


    /**
     * 查询实际支付金额是否大于剩余金额
     *
     * @param iRequest
     * @param sourceDocCategory
     * @param sourceDocLineId
     * @return
     */
    BigDecimal selectActualPaySurplusAmount(IRequest iRequest, String sourceDocCategory, Long sourceDocLineId);


    /**
     * 下载调拨审批书
     *
     * @param jsonStr
     * @param request
     * @param response
     * @throws TokenException
     * @throws FileReadIOException
     * @throws Docx4JException
     * @throws HlsCusException
     */
    void downloadTransferList(List<HlsCusFundTransferList> fundTransferLists, HttpServletRequest request, HttpServletResponse response) throws Exception;


    /**
     * 查询单据引用数量
     *
     * @param sourceDocCategory
     * @param sourceDocLineId
     * @return
     */
    int selectSourceDocumentCount(String sourceDocCategory, Long sourceDocId, Long sourceDocLineId);


    /**
     * 查询核销情况
     *
     * @param iRequest
     * @param fundTransferList
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusFundTransferList> selectWriteOffDeatil(IRequest iRequest, HlsCusFundTransferList fundTransferList, int page, int pageSize);


    /**
     * 调拨单号流水
     *
     * @param transferId
     * @return
     */
    String selectTransferNumberMax(Long transferId);

    /**
     * 作废调拨单
     *
     * @param iRequest
     * @param hlsCusFundTransferLists
     */
    void cancelTaskListCancel(IRequest iRequest, List<HlsCusFundTransfer> hlsCusFundTransferLists);
}