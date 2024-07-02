package com.hand.hls.ast.service;

import com.hand.hls.ast.dto.AstFcEstimate;
import com.hand.hls.cont.dto.ConContract;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;

import java.util.Map;

public interface IAstFiveClassTargetValueAdapter {
    Map<String, Object> getFiveClassTargetValueMapByContract(HlsCusConContract var1);


    Map<String, Object> getFiveClassTargetValueMapByContractNew(HlsCusConContractCashflow var1, AstFcEstimate astFcEstimate);
}