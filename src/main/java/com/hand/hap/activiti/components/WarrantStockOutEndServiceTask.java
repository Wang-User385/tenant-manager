package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.pam.dto.HlsCusHlsLeaseItemWarrant;
import com.hand.hls.pam.dto.HlsWarrantStockHd;
import com.hand.hls.pam.mapper.HlsCusHlsLeaseItemWarrantMapper;
import com.hand.hls.pam.mapper.HlsWarrantStockHdMapper;
import com.hand.hls.pam.service.HlsCusHlsLeaseItemWarrantService;
import com.hand.hls.pam.service.IHlsWarrantStockHdService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Qian Yuanfeng
 * @date 2020/7/28 - 14:25
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class WarrantStockOutEndServiceTask implements JavaDelegate, IActivitiBean {

    private static final String APPROVED = "APPROVED";
    private static final String APPROVED_RETURN = "APPROVED_RETURN";
    private static final String REJECTED = "REJECTED";

    //临时出库
    private static final String TEMP_OUT_STOCK = "TEMP_OUT_STOCK";
    //永久出库
    private static final String LAST_OUT_STOCK = "LAST_OUT_STOCK";


    private static final String WARRANT_STATUS_TEMP = "临时出库";
    private static final String WARRANT_STATUS_LAST = "永久出库";



    @Autowired
    private HlsWarrantStockHdMapper hlsWarrantStockHdMapper;

    @Autowired
    private IHlsWarrantStockHdService hlsWarrantStockHdService;

    @Autowired
    private HlsCusHlsLeaseItemWarrantMapper hlsCusHlsLeaseItemWarrantMapper;

    @Autowired
    private HlsCusHlsLeaseItemWarrantService hlsCusHlsLeaseItemWarrantService;


    @Autowired
    private DatabaseLockProvider databaseLockProvider;


    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long warrantStockId = (Long) delegateExecution.getVariable("warrantStockId");
        String stockType = String.valueOf(delegateExecution.getVariable("stockType"));

        HlsWarrantStockHd hlsWarrantStockHd = new HlsWarrantStockHd() ;
        hlsWarrantStockHd.setWarrantStockId(warrantStockId);
        hlsWarrantStockHd = hlsWarrantStockHdMapper.selectByPrimaryKey(hlsWarrantStockHd);

        List<HlsCusHlsLeaseItemWarrant> hlsCusHlsLeaseItemWarrantList =  hlsCusHlsLeaseItemWarrantMapper.queryWarrantByStock(hlsWarrantStockHd);

        databaseLockProvider.lock(hlsWarrantStockHd);
        //databaseLockProvider.lock(hlsCusHlsLeaseItemWarrantList);

        String warrantStatus = WARRANT_STATUS_TEMP;
        if(TEMP_OUT_STOCK.equalsIgnoreCase(stockType)){
            warrantStatus = WARRANT_STATUS_TEMP;
        }else if (LAST_OUT_STOCK.equalsIgnoreCase(stockType)){
            warrantStatus = WARRANT_STATUS_LAST;
        }

        if (APPROVED.equalsIgnoreCase(result)) {

            hlsWarrantStockHd.setApproveStatus(APPROVED);
            hlsWarrantStockHd.setStockManager(requestCtx.getUserId());
            hlsWarrantStockHdService.updateByPrimaryKey(requestCtx , hlsWarrantStockHd);

            for( int i = 0 ; i < hlsCusHlsLeaseItemWarrantList.size() ; i++ ){
                HlsCusHlsLeaseItemWarrant warrant = new HlsCusHlsLeaseItemWarrant();
                warrant.setWarrantId(hlsCusHlsLeaseItemWarrantList.get(i).getWarrantId());
                warrant =  hlsCusHlsLeaseItemWarrantMapper.selectByPrimaryKey(warrant);
                warrant.setWarrantStatus(warrantStatus);
                hlsCusHlsLeaseItemWarrantService.updateByPrimaryKeySelective(requestCtx ,  warrant);
            }

        } else if (REJECTED.equalsIgnoreCase(result)) {
            hlsWarrantStockHd.setApproveStatus(REJECTED);
            hlsWarrantStockHdService.updateByPrimaryKey(requestCtx , hlsWarrantStockHd);

        }

    }
}
