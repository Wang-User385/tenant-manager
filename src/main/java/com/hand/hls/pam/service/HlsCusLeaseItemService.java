package com.hand.hls.pam.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fct.dto.HlsCusCreditProject;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import com.hand.hls.pam.dto.HlsCusLeaseItem;
import com.hand.hls.pam.dto.HlsCusLeaseItemManage;
import com.hand.hls.utils.ResMessageException;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

/**
 * @author Qian Yuanfeng
 * @date 2020/4/14 - 14:13
 */
public interface HlsCusLeaseItemService extends IBaseService<HlsCusLeaseItem>, ProxySelf<HlsCusLeaseItemService>  {


    HlsCusLeaseItemManage leaseSaveSubmit(IRequest requestCtx, HlsCusLeaseItemManage dto);

    //租赁物主页面查询
    List<HlsCusLeaseItem> selectPamLeaseItem(IRequest iRequest, HlsCusLeaseItem hlsCusLeaseItem, Integer page, Integer pageSize);

    /**
     * 租赁物跳转详细页面查询
     * @param iRequest
     * @param hlsCusLeaseItem
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusLeaseItem> selectModelByCondition(IRequest iRequest, HlsCusLeaseItem hlsCusLeaseItem, Integer page, Integer pageSize);


    List<HlsCusLeaseItem> invalidLeaseItem(IRequest iRequest, List<HlsCusLeaseItem> hlsCusLeaseItems) throws ResMessageException;

    /**
     * 租赁物作废
     * @param iRequest
     * @param hlsCusLeaseItems
     * @return
     * @throws ResMessageException
     */
    List<HlsCusLeaseItem> invalidLeaseItemRental(IRequest iRequest, List<HlsCusLeaseItem> hlsCusLeaseItems) throws ResMessageException;


    void receiptImportPledgeDc(IRequest iRequest, Long hdId,Long leaseItemId,String patternDet) throws ExcelException, SQLException, ParseException;

    String generateAuthorityString(IRequest iRequest);
}
