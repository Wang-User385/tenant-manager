package com.hand.hls.fp.mapper;

import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fp.dto.JcFundFillingLn;
import uncertain.composite.CompositeMap;

import java.util.List;

public interface JcFundFillingLnMapper extends Mapper<JcFundFillingLn> {
    List<JcFundFillingLn> queryAll(JcFundFillingLn jcFundFillingLn);

    List<JcFundFillingLn> queryAllForUpdateAmount(JcFundFillingLn jcFundFillingLn);

    List<JcFundFillingLn> queryAllReq(JcFundFillingLn jcFundFillingLn);

    List<JcFundFillingLn> queryDetail(JcFundFillingLn jcFundFillingLn);

    List<JcFundFillingLn> queryAllDetailNew(JcFundFillingLn jcFundFillingLn);

    List<JcFundFillingLn> queryAllNew(JcFundFillingLn jcFundFillingLn);

    List<JcFundFillingLn> queryFiledPrompt(JcFundFillingLn jcFundFillingLn);

    List<JcFundFillingLn> queryWeekFiledPrompt(JcFundFillingLn jcFundFillingLn);

    void updateByItemCode(IRequest request, JcFundFillingLn jcFundFillingLn);

    List<JcFundFillingLn> queryByItemCode(JcFundFillingLn jcFundFillingLn);

    JcFundFillingLn queryEndBalanceAmountSxK(JcFundFillingLn jcFundFillingLn);

    JcFundFillingLn queryEndBalanceAmountSxB(JcFundFillingLn jcFundFillingLn);

    JcFundFillingLn queryEndBalanceAmountTjK(JcFundFillingLn jcFundFillingLn);

    JcFundFillingLn queryEndBalanceAmountTjB(JcFundFillingLn jcFundFillingLn);

    void deleteFillingLn(JcFundFillingLn jcFundFillingLn);

    void updateFillingDetailUnitId(JcFundFillingLn jcFundFillingLn);
}