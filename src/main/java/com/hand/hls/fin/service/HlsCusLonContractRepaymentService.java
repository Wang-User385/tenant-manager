package com.hand.hls.fin.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReqLn;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fin.dto.HlsCusContractRepaymentLn;
import com.hand.hls.fin.dto.HlsCusLonContractRepayment;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import com.hand.hls.fnd.service.HlsCusImportInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface HlsCusLonContractRepaymentService extends IBaseService<HlsCusLonContractRepayment>, ProxySelf<HlsCusLonContractRepaymentService>, HlsCusImportInterface {
    /**
     * 融资产品还款计划报表
     *
     * 以还款为维度，同一笔提款下相同日期合并
     *
     * @param request 请求参数
     * @param map 查询参数集合
     * @return 返回报表数据
     */
    List<Map<String,Object>> queryReport(IRequest request, Map<String, Object> map);

    List<HlsCusLonContractRepayment> selectLonContractRep(IRequest request, HlsCusLonContractRepayment hlsCusLonContractRepayment, int page, int pageSize, String model);

    void confirmRepayment(IRequest request, List<HlsCusLonContractWithdraw> lonContractWithdraws)  throws HlsCusException, hls.core.utils.exception.HlsCusException;

    /*首页还款明细查询*/
//    List<HlsCusLonContractRepayment> queryChartRep(IRequest request, HlsCusLonContractRepayment hlsCusLonContractRepayment, int page, int pageSize);

    /*首页还款明细查询*/
    List<Map> queryRep(IRequest request, HlsCusLonContractRepayment hlsCusLonContractRepayment, int page, int pageSize);


    List<HlsCusLonContractRepayment> queryList(IRequest request, HlsCusLonContractRepayment hlsCusLonContractRepayment, int page, int pageSize);

//    List<HlsCusLonContractRepayment> rateChangeDetailCfCompare(IRequest request, HlsCusConFloatingRateReqLn dto);

//    List<HlsCusLonContractRepayment> unitQueryRep(IRequest request, HlsCusLonContractRepayment hlsCusLonContractRepayment, int page, int pageSize);

//    void unitQueryRepDownloadExcel(IRequest iRequest, HttpServletRequest request, HttpServletResponse httpServletResponse, HlsCusLonContractRepayment hlsCusLonContractRepayment) throws IOException, InvocationTargetException, IllegalAccessException;

    void batchDeleteRepayment(IRequest request, List<HlsCusLonContractRepayment> dto) throws HlsCusException;

//    List<HlsCusLonContractRepayment> queryDebtMaturityStructureChart(IRequest request, HlsCusLonContractRepayment hlsCusLonContractRepayment, int page, int pageSize);

//    void exportRepaymentReport(HttpServletRequest request, HttpServletResponse response, HlsCusLonContractRepayment hlsCusLonContractRepayment)throws IOException, InvocationTargetException, IllegalAccessException;

    List<HlsCusLonContractRepayment> selectList(IRequest iRequest, HlsCusLonContractRepayment lonContractRepayment, Integer page, Integer pageSize);

    /**
     * 查询 融资提款的还款本金，或者还款还款利息之和
     */
    Double cfItemAmountSum(IRequest request, HlsCusLonContractRepayment hlsCusLonContractRepayment);

    /**
     * 资金调拨界面进行还款
     * @param request
     * @param repaymentLn
     * @throws HlsCusException
     */
    void confirmFundRepayment(IRequest request, HlsCusContractRepaymentLn repaymentLn) throws HlsCusException;

//    List<HlsCusLonContractRepayment> selectLonContractChangeAfterRep(IRequest request, HlsCusLonContractRepayment lonContractRepayment, int page, int pageSize, String model);

    List<HlsCusLonContractRepayment> selectLonContractRepAndFin(HlsCusLonContractRepayment hlsCusLonContractRepayment);
}