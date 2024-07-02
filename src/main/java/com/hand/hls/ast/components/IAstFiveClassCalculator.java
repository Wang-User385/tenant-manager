//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.ast.components;

import com.hand.hls.ast.dto.AstFcEstimate;
import com.hand.hls.ast.dto.FiveClassRunnerResult;
import com.hand.hls.cont.dto.ConContract;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;

public interface IAstFiveClassCalculator {
    void load(String var1);

    FiveClassRunnerResult execute(HlsCusConContract var1);

    /**
     * 计算逻辑 传参修改，原框架逻辑不满足条件
     *
     * @param var1  请求对象
     * @return  返回结果集
     * */
    FiveClassRunnerResult executeNew(HlsCusConContractCashflow var1, AstFcEstimate astFcEstimate);
}
