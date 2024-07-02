package com.hand.hls.plm.rc.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.plm.rc.dto.HlsCusRentCollectionRule;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface HlsCusRentCollectionRuleService extends IBaseService<HlsCusRentCollectionRule>, ProxySelf<HlsCusRentCollectionRuleService> {


    /**
     * 租金催收规则查询
     * @param iRequest
     * @param rentCollectionRule
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusRentCollectionRule> selectRentCollectionRuleData(IRequest iRequest, HlsCusRentCollectionRule rentCollectionRule, int page, int pageSize);



    /**
     * 当前合同催收记录个数
     * @param iRequest
     * @param rentCollectionRule
     * @return
     */
    int selectContractRentCount(IRequest iRequest, HlsCusRentCollectionRule rentCollectionRule);



    /**
     * 查询逾期合同且有催收规则的
     * @return
     */
    List<HlsCusRentCollectionRule> selectOverDateContract();


    /**
     * 更新最后日期
     * @param rentCollectionRule
     * @return
     */
    int updateLastCollectionDate(HlsCusRentCollectionRule rentCollectionRule);


    /**
     * 查询逾期超过30天的合同
     * @return
     */
    List<HlsCusRentCollectionRule> selectOverMonthContract();


    void exportRentCollectionRule(HttpServletRequest request, HttpServletResponse response, HlsCusRentCollectionRule rentCollectionRule) throws IOException,InvocationTargetException, IllegalAccessException ;



    /**
     * 查询逾期的合同
     * @return
     */
    List<HlsCusRentCollectionRule> selectOverTimesContract();
}