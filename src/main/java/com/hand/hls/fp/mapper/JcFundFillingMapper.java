package com.hand.hls.fp.mapper;

import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fp.dto.JcFundFilling;
import com.hand.hls.fp.dto.JcFundFillingDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JcFundFillingMapper extends Mapper<JcFundFilling> {
    List<JcFundFilling> queryAll(JcFundFilling jcFundFilling);

    List<JcFundFilling> queryAllByUnit(JcFundFilling jcFundFilling);

    List<JcFundFilling> queryAllByUnitReq(JcFundFilling jcFundFilling);

    List<JcFundFilling> queryNormal(JcFundFilling jcFundFilling);

    List<JcFundFilling> queryAllNew(JcFundFilling jcFundFilling);

    List<JcFundFilling> queryUserInfo();

    List<JcFundFilling> queryReq(JcFundFilling jcFundFilling);

    void deleteFundLn(JcFundFilling jcFundFilling);

    void deleteFundDetail(JcFundFilling jcFundFilling);

    List<JcFundFilling> queryCheckApproved(JcFundFilling jcFundFilling);
}