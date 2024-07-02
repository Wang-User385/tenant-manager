package hls.core.hls.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.csh.dto.HlsCusConDebtExemptionReqCf;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.mapper.HlsCusConDebtExemptionReqCfMapper;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.rpt.service.RptOffBalanceSheetService;
import com.hand.hls.service.HlsDayEndExecutor;
import com.hand.hls.utils.service.FakeRequestService;
import hls.core.fnd.dto.HlsPenaltyProfileDtl;
import hls.core.fnd.mapper.HlsPenaltyProfileDtlMapper;
import hls.core.hls.service.HlsDayEndCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class HlsOffBalanceSheetCalServiceImpl implements HlsDayEndExecutor {
    @Autowired
    HlsCusConContractMapper conContractMapper;
    @Autowired
    RptOffBalanceSheetService rptOffBalanceSheetService;
    @Autowired
    private FakeRequestService fakeRequestService;

    Logger logger = LoggerFactory.getLogger(getClass());

    private static final String DAY_END_TYPE = "OFF_BALANCE_SHEET";

    @Override
    public String getDayEndType() {
        return DAY_END_TYPE;
    }

    @Override
    public void process(Map map) {

        IRequest fakeRequest = fakeRequestService.createFakeRequest();
        try {
            if (map.get("contractId") != null && map.get("contractId") != "") {
                Long contractId = Long.valueOf(map.get("contractId").toString());
                rptOffBalanceSheetService.createRptOffBalanceSheet(fakeRequest, contractId);
            } else {
                HlsCusConContract contract = new HlsCusConContract();
                contract.setDataClass("NORMAL");
                contract.setInceptFlag("Y");
                List<HlsCusConContract> contractList = conContractMapper.select(contract);
                for (HlsCusConContract conContract : contractList) {
                    rptOffBalanceSheetService.createRptOffBalanceSheet(fakeRequest, conContract.getContractId());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }


}
