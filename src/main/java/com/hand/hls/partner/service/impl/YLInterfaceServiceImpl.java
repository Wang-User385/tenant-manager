package com.hand.hls.partner.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.mybatis.provider.ExampleProvider;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusBpMasterBankAccount;
import com.hand.hls.bp.mapper.HlsCusBpMasterBankAccountMapper;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.csh.dto.CshPaymentReqHd;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.mapper.CshTransactionMapper;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.hls.dto.HlsDurationLn;
import com.hand.hls.lease.mapper.LeaseItemInsuranceMapper;
import com.hand.hls.partner.dto.*;
import com.hand.hls.partner.service.YLInterfaceService;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.*;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import com.hand.hls.web.logs.mapper.HlsWsRequestsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class YLInterfaceServiceImpl implements YLInterfaceService {

    @Autowired
    private HlsCusPrjProjectMapper prjProjectMapper;
    @Autowired
    private HlsCusConContractMapper conContractMapper;
    @Autowired
    private HlsWsRequestsMapper hlsWsRequestsMapper;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private HlsCusCshTransactionMapper hlsCusCshTransactionMapper;
    @Autowired
    private HlsCusPrjProjectLeaseItemMapper hlsCusPrjProjectLeaseItemMapper;
    @Autowired
    private PrjLeaseItemInsuranceMapper prjLeaseItemInsuranceMapper;
    @Autowired
    private HlsCusBpMasterBankAccountMapper hlsCusBpMasterBankAccountMapper;
    @Autowired
    private ProjectLeaseItemMortgageMapper projectLeaseItemMortgageMapper;
    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;
    @Autowired
    private ProjectLeaseItemSalesMapper projectLeaseItemSalesMapper;
    @Autowired
    private HlsCusPrjProjectBpMapper hlsCusPrjProjectBpMapper;
    @Autowired
    private ProjectLeaseItemConditionMapper projectLeaseItemConditionMapper;

    @Override
    public ResponseData placeOrder(PlaceOrderDTO placeOrderDTO, HttpServletRequest request, IRequest iRequest) {

        //保存日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
//        获取请求路径
        String requestURI = request.getRequestURI();
        hlsWsRequests.setRequestWsdlUrl(requestURI);
        //请求日期
        hlsWsRequests.setRequestDate(new Date());
//        功能名称
        hlsWsRequests.setFunctionName("下单");
//        状态变更日期
        hlsWsRequests.setStatusDate(new Date());
//        user_id
        String userId = request.getParameter("user_id");
        if (userId!=null){
            hlsWsRequests.setUserId(Long.valueOf(userId));
        }
//        请求状态
        hlsWsRequests.setStatusCode("200");
//        参数类型
        hlsWsRequests.setParameterType("JSON");
        // 请求体
        String s = JSONObject.toJSONString(placeOrderDTO);
        hlsWsRequests.setRequestJson(s);


        ResponseData responseData = new ResponseData();

        //判断该客户存不存在
        //如果存在，判断名称和电话一不一致，不一致就修改
        //如果不存在，就新增
        HlsCusPrjProjectBp hlsCusPrjProjectBp = null;
        HlsCusBpMaster bpMaster = hlsCusBpMasterMapper.selectMasterByIdCardNo(placeOrderDTO.getIdCardNo());
        if (bpMaster==null){
            bpMaster = new HlsCusBpMaster();
            hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
            Map<String, String> params = new HashMap<String, String>();
            String codeRuleValue = fndCodingRuleValuesService.getCodeRuleValue(iRequest, "HLS_BP_MASTER", "NP", "NP", params);
            bpMaster.setBpCode(codeRuleValue);
            bpMaster.setBpName(placeOrderDTO.getName());
            bpMaster.setIdCardNo(placeOrderDTO.getIdCardNo());
            bpMaster.setPhone(placeOrderDTO.getMobile());
            bpMaster.setBpClass("NP");
            hlsCusBpMasterMapper.insert(bpMaster);
            hlsCusPrjProjectBp.setBpId(bpMaster.getBpId());

        }else{
            if (!placeOrderDTO.getName().equals(bpMaster.getBpName())){
                bpMaster.setBpName(placeOrderDTO.getName());
            }
            if (!placeOrderDTO.getMobile().equals(bpMaster.getPhone())){
                bpMaster.setPhone(placeOrderDTO.getMobile());
            }
            hlsCusBpMasterMapper.updateByPrimaryKey(bpMaster);
        }

//        获取当前客户所有的项目，判断项目状态
        List<HlsCusPrjProject> list = prjProjectMapper.selectProjectByIdCardNo(placeOrderDTO.getIdCardNo());
        list.stream().forEach(x->{
//            如果项目是取消、拒绝、结束允许下单，否则不允许
            if (!"CLOSED".equals(x.getProjectStatus())||!"CANCEL".equals(x.getProjectStatus())||
                    !"REJECTED".equals(x.getProjectStatus())){
                responseData.setCode("400");
                responseData.setMessage("存在在途单");
            }
//            else{
////                如果项目不是取消、拒绝、结束，则判断合同状态是否为结束、关闭、终止，如果是则允许下单，否则不允许下单
//                HlsCusConContract hlsCusConContract = conContractMapper.selectByRefProjectId(x.getProjectId());
//                if (!"TERMINATE".equals(hlsCusConContract.getContractStatus())||
//                        !"CANCEL".equals(hlsCusConContract.getContractStatus())||
//                        !"END".equals(hlsCusConContract.getContractStatus())){
//                    responseData.setCode("400");
//                    responseData.setMessage("存在在途单");
//                }
//            }
        });
//        判断循环之后的结果，如果不允许创建，返回信息
        if ("400".equals(responseData.getCode())){
            hlsWsRequests.setReturnStatus("E");
            hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
            hlsWsRequestsMapper.insert(hlsWsRequests);
            return responseData;
        }

        //获取订单编号,将订单编号入库
        /*编码规则*/
        Map<String, String> params = new HashMap<String, String>();
        params.put("PARAMETER_01","YL");
        String codeRuleValue = fndCodingRuleValuesService.getCodeRuleValue(iRequest,"PRJ_PROJECT_IMPORT", "PRJLB", "LEASEBACK", params);

        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectNumber(codeRuleValue);
        hlsCusPrjProject.setCompanyId(1L);
        hlsCusPrjProject.setTenantId(bpMaster.getBpId());
        hlsCusPrjProject.setProjectStatus("NEW");
        prjProjectMapper.insert(hlsCusPrjProject);

        if (hlsCusPrjProjectBp!=null){
            hlsCusPrjProjectBp.setProjectId(hlsCusPrjProject.getProjectId());
            hlsCusPrjProjectBpMapper.insert(hlsCusPrjProjectBp);
        }

//        设置返回信息
        List<String> stringList = new ArrayList<>();
        stringList.add(codeRuleValue);
        responseData.setCode("200");
        responseData.setRows(stringList);
        responseData.setMessage("下单成功");
        hlsWsRequests.setReturnStatus("S");
        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
        hlsWsRequestsMapper.insert(hlsWsRequests);
        return responseData;
    }

    @Override
    public ResponseData closeOrder(CloseOrderDTO closeOrderDTO, HttpServletRequest request) {

        //保存日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
//        获取请求路径
        String requestURI = request.getRequestURI();
        hlsWsRequests.setRequestWsdlUrl(requestURI);
        //请求日期
        hlsWsRequests.setRequestDate(new Date());
//        功能名称
        hlsWsRequests.setFunctionName("关单");
//        状态变更日期
        hlsWsRequests.setStatusDate(new Date());
//        user_id
        String userId = request.getParameter("user_id");
        if (userId!=null){
            hlsWsRequests.setUserId(Long.valueOf(userId));
        }
//        请求状态
        hlsWsRequests.setStatusCode("200");
//        参数类型
        hlsWsRequests.setParameterType("JSON");
        // 请求体
        String s = JSONObject.toJSONString(closeOrderDTO);
        hlsWsRequests.setRequestJson(s);

        ResponseData responseData = new ResponseData();

        HlsCusPrjProject hlsCusPrjProject = prjProjectMapper.selectProjectByOrderNo(closeOrderDTO.getOrderNo());
        CshPaymentReqHd cshPaymentReqHd =prjProjectMapper.selectPaymentByOrderNo(closeOrderDTO.getOrderNo());
        if (hlsCusPrjProject==null){
            responseData.setCode("100003");
            responseData.setMessage("订单不存在");
            hlsWsRequests.setReturnStatus("E");
            hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
            hlsWsRequestsMapper.insert(hlsWsRequests);
            return responseData;
        }else{
            String projectStatus = hlsCusPrjProject.getProjectStatus();
            if ("APPROVING".equals(projectStatus)){
                responseData.setCode("100101");
                responseData.setMessage("订单状态和操作不相符");
                hlsWsRequests.setReturnStatus("E");
                hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                hlsWsRequestsMapper.insert(hlsWsRequests);
                return responseData;
            }
            if ("Y".equals(hlsCusPrjProject.getLoanInitialLease())){
                responseData.setCode("100101");
                responseData.setMessage("订单状态和操作不相符");
                hlsWsRequests.setReturnStatus("E");
                hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                hlsWsRequestsMapper.insert(hlsWsRequests);
                return responseData;
            }
            if ("APPROVING".equals(cshPaymentReqHd.getPaymentReqStatusDesc())){
                responseData.setCode("100101");
                responseData.setMessage("订单状态和操作不相符");
                hlsWsRequests.setReturnStatus("E");
                hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                hlsWsRequestsMapper.insert(hlsWsRequests);
                return responseData;
            }
        }
        //修改订单状态
        hlsCusPrjProject.setProjectStatus("END");
        prjProjectMapper.updateByPrimaryKey(hlsCusPrjProject);
        responseData.setCode("200");
        responseData.setMessage("取消成功");
        hlsWsRequests.setReturnStatus("S");
        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
        hlsWsRequestsMapper.insert(hlsWsRequests);
        return responseData;
    }

    @Override
    public ResponseData queryOrder(QueryOrderDTO queryOrderDTO, HttpServletRequest request) {

        //保存日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
//        获取请求路径
        String requestURI = request.getRequestURI();
        hlsWsRequests.setRequestWsdlUrl(requestURI);
        //请求日期
        hlsWsRequests.setRequestDate(new Date());
//        功能名称
        hlsWsRequests.setFunctionName("账单查询");
//        状态变更日期
        hlsWsRequests.setStatusDate(new Date());
//        user_id
        String userId = request.getParameter("user_id");
        if (userId!=null){
            hlsWsRequests.setUserId(Long.valueOf(userId));
        }
//        请求状态
        hlsWsRequests.setStatusCode("200");
//        参数类型
        hlsWsRequests.setParameterType("JSON");
        // 请求体
        String s = JSONObject.toJSONString(queryOrderDTO);
        hlsWsRequests.setRequestJson(s);


        ResponseData responseData = new ResponseData();
        //根据订单编号查询相对应的还款信息,判断订单存不存在，不存在直接返回
        List<RepayPlanTermInfoDTO> repayPlanTermInfoDTOList = prjProjectMapper.selectRepayPlanByOrderNo(queryOrderDTO.getOrderNo());
        QueryOrder queryOrder = prjProjectMapper.selectQueryOrderByOrderNo(queryOrderDTO.getOrderNo());
        if (queryOrder==null){
            responseData.setCode("100003");
            responseData.setMessage("订单不存在");
            hlsWsRequests.setReturnStatus("E");
            hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
            hlsWsRequestsMapper.insert(hlsWsRequests);
            return responseData;
        }else{
            queryOrder.setRepayPlanTermInfoDTOList(repayPlanTermInfoDTOList);
            queryOrder.setStatus("NORMAL");
//            订单存在，判断合同状态是否为起租后状态、结清状态
//            如果是，则返回数据，如果不是，返回错误
            String contractStatus = queryOrder.getContractStatus();
            if ("ET".equals(contractStatus)  || "INCEPT".equals(contractStatus)){
                responseData.setCode("200");
                responseData.setMessage("查询成功");
                hlsWsRequests.setReturnStatus("S");
                hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                hlsWsRequestsMapper.insert(hlsWsRequests);
                List<QueryOrder> queryOrderList = new ArrayList<>();
                queryOrderList.add(queryOrder);
                responseData.setRows(queryOrderList);
                return responseData;
            }else{
                responseData.setCode("100101");
                responseData.setMessage("订单状态和操作不相符");
                hlsWsRequests.setReturnStatus("E");
                hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                hlsWsRequestsMapper.insert(hlsWsRequests);
                return responseData;
            }
        }
    }

    @Override
    public ResponseData repayment(RepayMent repayMent, HttpServletRequest request) {

        //保存日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
//        获取请求路径
        String requestURI = request.getRequestURI();
        hlsWsRequests.setRequestWsdlUrl(requestURI);
        //请求日期
        hlsWsRequests.setRequestDate(new Date());
//        功能名称
        hlsWsRequests.setFunctionName("还款");
//        状态变更日期
        hlsWsRequests.setStatusDate(new Date());
//        user_id
        String userId = request.getParameter("user_id");
        if (userId!=null){
            hlsWsRequests.setUserId(Long.valueOf(userId));
        }
//        请求状态
        hlsWsRequests.setStatusCode("200");
//        参数类型
        hlsWsRequests.setParameterType("JSON");
        // 请求体
        String s = JSONObject.toJSONString(repayMent);
        hlsWsRequests.setRequestJson(s);

        ResponseData responseData = new ResponseData();

        //根据订单编号查询数据
        List<HlsCusCshTransaction> hlsCusCshTransactionList = prjProjectMapper.selectTranSactionByOrderNo(repayMent.getOrderNo());
        if (hlsCusCshTransactionList.size()==0){
            responseData.setCode("400");
            responseData.setMessage("查询数据为空");
            hlsWsRequests.setReturnStatus("E");
            hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
            hlsWsRequestsMapper.insert(hlsWsRequests);
            return responseData;
        }
        List<TermRepayDetailApplyDTO> termRepayDetailApplyDTOList = repayMent.getTermRepayDetailApplyDTOList();
        //保存数据到事务表
        for (TermRepayDetailApplyDTO termRepayDetailApplyDTO : termRepayDetailApplyDTOList) {
            //判断还款方式是否为蚂蚁链代扣，如果是则判断结算单号、代扣交易单号是否为空
            if ("蚂蚁链代扣".equals(repayMent.getRepayType())){
                if (termRepayDetailApplyDTO.getTransactionNo()==null){
                    responseData.setCode("400");
                    responseData.setMessage("结算单号为空");
                    hlsWsRequests.setReturnStatus("E");
                    hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                    hlsWsRequestsMapper.insert(hlsWsRequests);
                    return responseData;
                }
                if ("蚂蚁链代扣".equals(termRepayDetailApplyDTO.getExternalDeductNo())){
                    responseData.setCode("400");
                    responseData.setMessage("代扣交易单号为空");
                    hlsWsRequests.setReturnStatus("E");
                    hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                    hlsWsRequestsMapper.insert(hlsWsRequests);
                    return responseData;
                }
            }
//            将数据保存入库
            for (HlsCusCshTransaction hlsCusCshTransaction : hlsCusCshTransactionList) {
                if (hlsCusCshTransaction.getTermNo().equals(termRepayDetailApplyDTO.getTermNo())){
                    hlsCusCshTransaction.setRepayPrincipal(termRepayDetailApplyDTO.getRepayPrincipal());
                    hlsCusCshTransaction.setRepayInterest(termRepayDetailApplyDTO.getRepayInterest());
                    hlsCusCshTransaction.setRepayAmount(termRepayDetailApplyDTO.getRepayAmount());
                    hlsCusCshTransaction.setPaymentMethod(repayMent.getRepayType());
                    hlsCusCshTransaction.setTransactionNo(termRepayDetailApplyDTO.getTransactionNo());
                    hlsCusCshTransaction.setExternalDeductNo(termRepayDetailApplyDTO.getExternalDeductNo());
                    hlsCusCshTransactionMapper.insert(hlsCusCshTransaction);
                }
            }
        }



        //设置返回状态
        responseData.setCode("200");
        responseData.setMessage("还款成功");
        hlsWsRequests.setReturnStatus("S");
        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
        hlsWsRequestsMapper.insert(hlsWsRequests);

        return responseData;
    }

    @Override
    public ResponseData compensatoryTrialCalculation(CompensatoryTrialCalculationDTO compensatoryTrialCalculation, HttpServletRequest request) {

        //保存日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
//        获取请求路径
        String requestURI = request.getRequestURI();
        hlsWsRequests.setRequestWsdlUrl(requestURI);
        //请求日期
        hlsWsRequests.setRequestDate(new Date());
//        功能名称
        hlsWsRequests.setFunctionName("代偿试算");
//        状态变更日期
        hlsWsRequests.setStatusDate(new Date());
//        user_id
        String userId = request.getParameter("user_id");
        if (userId!=null){
            hlsWsRequests.setUserId(Long.valueOf(userId));
        }
//        请求状态
        hlsWsRequests.setStatusCode("200");
//        参数类型
        hlsWsRequests.setParameterType("JSON");
        // 请求体
        String s = JSONObject.toJSONString(compensatoryTrialCalculation);
        hlsWsRequests.setRequestJson(s);

        ResponseData responseData = new ResponseData();

//            计算本金、利息、罚息、应付金额
            CompensatoryTrialCalculationDTO compensatoryTrialCalculation1 = prjProjectMapper.selectCTCByOrderNo(compensatoryTrialCalculation);

            if (compensatoryTrialCalculation1==null){
                responseData.setCode("400");
                responseData.setMessage("订单不存在");
                hlsWsRequests.setReturnStatus("E");
                hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                hlsWsRequestsMapper.insert(hlsWsRequests);
                return responseData;
            }else{
                //            判断传入时间是否为空，如果为空则用现在时间，如果不为空，则用传入时间
                if (compensatoryTrialCalculation.getTrialTime()==null){
                    compensatoryTrialCalculation1.setTrialTime(String.valueOf(new Date()));
                }else{
                    compensatoryTrialCalculation1.setTrialTime(compensatoryTrialCalculation.getTrialTime());
                }
                //设置返回订单号
                compensatoryTrialCalculation1.setOrderNo(compensatoryTrialCalculation.getOrderNo());
//            设置返回期次号
                compensatoryTrialCalculation1.setTermNo(compensatoryTrialCalculation.getTermNo());
//            设置返回数据
                List<CompensatoryTrialCalculationDTO> compensatoryTrialCalculationList = new ArrayList<>();
                compensatoryTrialCalculationList.add(compensatoryTrialCalculation1);
                responseData.setRows(compensatoryTrialCalculationList);

//            设置返回状态
                responseData.setCode("200");
                responseData.setMessage("还款成功");
                hlsWsRequests.setReturnStatus("S");
                hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                hlsWsRequestsMapper.insert(hlsWsRequests);
                return responseData;
            }
    }

    @Override
    public ResponseData claimsSubrogation(ClaimsSubrogationDTO claimsSubrogationDTO, HttpServletRequest request) {

        //保存日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
//        获取请求路径
        String requestURI = request.getRequestURI();
        hlsWsRequests.setRequestWsdlUrl(requestURI);
        //请求日期
        hlsWsRequests.setRequestDate(new Date());
//        功能名称
        hlsWsRequests.setFunctionName("代偿请求");
//        状态变更日期
        hlsWsRequests.setStatusDate(new Date());
//        user_id
        String userId = request.getParameter("user_id");
        if (userId!=null){
            hlsWsRequests.setUserId(Long.valueOf(userId));
        }
//        请求状态
        hlsWsRequests.setStatusCode("200");
//        参数类型
        hlsWsRequests.setParameterType("JSON");
        // 请求体
        String s = JSONObject.toJSONString(claimsSubrogationDTO);
        hlsWsRequests.setRequestJson(s);

        ResponseData responseData = new ResponseData();

        //根据订单编号查询数据
        List<HlsCusCshTransaction> hlsCusCshTransactionList = prjProjectMapper.selectTranSactionByOrderNo(claimsSubrogationDTO.getOrderNo());
//            将数据保存入库
        for (HlsCusCshTransaction hlsCusCshTransaction : hlsCusCshTransactionList) {
            if (hlsCusCshTransaction.getTermNo().equals(claimsSubrogationDTO.getTermNo())){
                hlsCusCshTransaction.setRepayAmount(claimsSubrogationDTO.getSubstituteAmount());
                hlsCusCshTransaction.setPaymentMethod("代偿");
                hlsCusCshTransactionMapper.insert(hlsCusCshTransaction);
            }
        }


        //            设置返回状态
        responseData.setCode("200");
        responseData.setMessage("还款成功");
        hlsWsRequests.setReturnStatus("S");
        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
        hlsWsRequestsMapper.insert(hlsWsRequests);
        return responseData;
    }

    @Override
    public ResponseData advancesSettleTrialCalculation(AdvancesSettleComputeDTO advancesSettleComputeDTO, HttpServletRequest request) {

        //保存日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
//        获取请求路径
        String requestURI = request.getRequestURI();
        hlsWsRequests.setRequestWsdlUrl(requestURI);
        //请求日期
        hlsWsRequests.setRequestDate(new Date());
//        功能名称
        hlsWsRequests.setFunctionName("提前结清试算");
//        状态变更日期
        hlsWsRequests.setStatusDate(new Date());
//        user_id
        String userId = request.getParameter("user_id");
        if (userId!=null){
            hlsWsRequests.setUserId(Long.valueOf(userId));
        }
//        请求状态
        hlsWsRequests.setStatusCode("200");
//        参数类型
        hlsWsRequests.setParameterType("JSON");
        // 请求体
        String s = JSONObject.toJSONString(advancesSettleComputeDTO);
        hlsWsRequests.setRequestJson(s);


        ResponseData responseData = new ResponseData();


//            查询结算金额
        AdvancesSettleComputeDTO advancesSettleComputeDTO1 = new AdvancesSettleComputeDTO();
        advancesSettleComputeDTO1.setOrderNo(advancesSettleComputeDTO.getOrderNo());
        //            判断试算日期是否为空，如果为空则使用当前日期，如果有，则使用传入日期
        if (advancesSettleComputeDTO.getTrialTime()==null){
            advancesSettleComputeDTO1.setTrialTime(String.valueOf(new Date()));
        }
        advancesSettleComputeDTO1.setTrialTime(advancesSettleComputeDTO.getTrialTime());
        //            设置返回状态
        responseData.setCode("200");
        responseData.setMessage("试算成功");
        hlsWsRequests.setReturnStatus("S");
        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
        hlsWsRequestsMapper.insert(hlsWsRequests);
        return responseData;
    }

    @Override
    public ResponseData advancesSettleRequest(AdvancesSettleComputeDTO advancesSettleComputeDTO, HttpServletRequest request) {

        //保存日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
//        获取请求路径
        String requestURI = request.getRequestURI();
        hlsWsRequests.setRequestWsdlUrl(requestURI);
        //请求日期
        hlsWsRequests.setRequestDate(new Date());
//        功能名称
        hlsWsRequests.setFunctionName("提前结清请求");
//        状态变更日期
        hlsWsRequests.setStatusDate(new Date());
//        user_id
        String userId = request.getParameter("user_id");
        if (userId!=null){
            hlsWsRequests.setUserId(Long.valueOf(userId));
        }
//        请求状态
        hlsWsRequests.setStatusCode("200");
//        参数类型
        hlsWsRequests.setParameterType("JSON");
        // 请求体
        String s = JSONObject.toJSONString(advancesSettleComputeDTO);
        hlsWsRequests.setRequestJson(s);


        ResponseData responseData = new ResponseData();

//            保存还款金额


        //            设置返回状态
        responseData.setCode("200");
        responseData.setMessage("还款成功");
        hlsWsRequests.setReturnStatus("S");
        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
        hlsWsRequestsMapper.insert(hlsWsRequests);
        return responseData;
    }

    @Override
    @Transactional
    public ResponseData dataAcquisition(DataAcquisitionDTO dataAcquisitionDTO, HttpServletRequest request) {
        //保存日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
//        获取请求路径
        String requestURI = request.getRequestURI();
        hlsWsRequests.setRequestWsdlUrl(requestURI);
        //请求日期
        hlsWsRequests.setRequestDate(new Date());
//        功能名称
        hlsWsRequests.setFunctionName("数据采集");
//        状态变更日期
        hlsWsRequests.setStatusDate(new Date());
//        user_id
        String userId = request.getParameter("user_id");
        if (userId!=null){
            hlsWsRequests.setUserId(Long.valueOf(userId));
        }
//        请求状态
        hlsWsRequests.setStatusCode("200");
//        参数类型
        hlsWsRequests.setParameterType("JSON");
        // 请求体
        String s = JSONObject.toJSONString(dataAcquisitionDTO);
        hlsWsRequests.setRequestJson(s);


        ResponseData responseData = new ResponseData();

//        首先判断订单状态
//        Example durationLnExample = new Example(HlsCusPrjProject.class);
//        durationLnExample.createCriteria().
//                andEqualTo("projectNumber", dataAcquisitionDTO.getOrderNo());
//        prjProjectMapper.selectByExample(durationLnExample)
        HlsCusPrjProject hlsCusPrjProject = prjProjectMapper.selectProjectByOrderNo(dataAcquisitionDTO.getOrderNo());
        if (hlsCusPrjProject==null){
            responseData.setCode("400");
            responseData.setMessage("订单不存在");
            hlsWsRequests.setReturnStatus("E");
            hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
            hlsWsRequestsMapper.insert(hlsWsRequests);
            return responseData;
        }
        //根据项目id获取租赁物信息,如果为空，那么直接入库，如果不为空，判断订单状态
        List<HlsCusPrjProjectLeaseItem> hlsCusPrjProjectLeaseItemList = hlsCusPrjProjectLeaseItemMapper.selectLeaseItemByProjectId(hlsCusPrjProject.getProjectId());

        //租赁物信息
        HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem1 = null;
        //融资方案相关信息
        FinanceInfo financeInfo = dataAcquisitionDTO.getFinanceInfo();
//            销售信息
        SaleInfo saleInfo = dataAcquisitionDTO.getSaleInfo();
//            风控审核相关数据
        String riskInfo = dataAcquisitionDTO.getRiskInfo();
        PreRiskAuditData preRiskAuditData = null;
        if (riskInfo!=null){
            preRiskAuditData  = JSONObject.parseObject(riskInfo, PreRiskAuditData.class);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        CarInfo carInfo = dataAcquisitionDTO.getCarInfo();


        if (hlsCusPrjProjectLeaseItemList.size()>0){
            hlsCusPrjProjectLeaseItem1 = hlsCusPrjProjectLeaseItemList.get(0);
        }else{
            hlsCusPrjProjectLeaseItem1 = new HlsCusPrjProjectLeaseItem();
        }
        //项目id
        hlsCusPrjProjectLeaseItem1.setProjectId(hlsCusPrjProject.getProjectId());
//            车架号
        hlsCusPrjProjectLeaseItem1.setFrameNumber(carInfo.getVin());
//            车辆出厂日期
        try {
            Date productDate = simpleDateFormat.parse(carInfo.getCarProductionDate());
            hlsCusPrjProjectLeaseItem1.setProductDate(productDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//            发动机号
        hlsCusPrjProjectLeaseItem1.setEngineNumber(carInfo.getEngineNumber());
        //车辆指导价
        hlsCusPrjProjectLeaseItem1.setListPrice(financeInfo.getCarGuidePrice().doubleValue()/100);
//            车辆售价
        hlsCusPrjProjectLeaseItem1.setSellingPrice(financeInfo.getCarSalePrice().doubleValue()/100);
//            申请融资额
        hlsCusPrjProjectLeaseItem1.setFinanceAmount(financeInfo.getApplyLoanAmount().doubleValue()/100);


        prjProjectMapper.updateByPrimaryKey(hlsCusPrjProject);
        if (hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId()!=null){
            hlsCusPrjProjectLeaseItemMapper.updateByPrimaryKey(hlsCusPrjProjectLeaseItem1);
        }else{
            hlsCusPrjProjectLeaseItemMapper.insert(hlsCusPrjProjectLeaseItem1);
        }


        PrjProjectLeaseItemSales prjProjectLeaseItemSales = null;
        if (hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId()!=null){
            List<PrjProjectLeaseItemSales> prjProjectLeaseItemSales1 = projectLeaseItemSalesMapper.prjProjectLeaseItemSalesQuery(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());
            if (prjProjectLeaseItemSales1.size()>0){
                prjProjectLeaseItemSales = prjProjectLeaseItemSales1.get(0);
            }else{
                prjProjectLeaseItemSales = new PrjProjectLeaseItemSales();
            }
        }else{
            prjProjectLeaseItemSales = new PrjProjectLeaseItemSales();
        }
//            销售方统一社会信用代码
        prjProjectLeaseItemSales.setUnifiedSocialCreditCode(saleInfo.getSellerCode());
//            销售方统一社会信用代码名称
        prjProjectLeaseItemSales.setSalesName(saleInfo.getSellerName());
//            上牌主体社会代码
        prjProjectLeaseItemSales.setRegisterSocialCreditCode(saleInfo.getLicensePlateOwnerCode());
//            上牌主体名称
        prjProjectLeaseItemSales.setRegisterName(saleInfo.getLicensePlateOwnerName());
//            抵押人社会代码
        prjProjectLeaseItemSales.setMortgageSocialCreditCode(saleInfo.getMortgagorCode());
//            抵押人名称
        prjProjectLeaseItemSales.setMortgageName(saleInfo.getMortgagorName());
//            抵押城市名称
        prjProjectLeaseItemSales.setMortgageCity(saleInfo.getMortgageCityName());
        prjProjectLeaseItemSales.setProjectLeaseItemId(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());


        //强制保险金额
        PrjLeaseItemInsurance prjLeaseItemInsurance = null;
        if (hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId()!=null){
            prjLeaseItemInsurance = prjLeaseItemInsuranceMapper.selectInsByLeaseItemId(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());
        }else{
            prjLeaseItemInsurance = new PrjLeaseItemInsurance();
        }
        if (prjLeaseItemInsurance==null){
            prjLeaseItemInsurance = new PrjLeaseItemInsurance();
        }
        prjLeaseItemInsurance.setProjectLeaseItemId(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());
        double compulsoryAmount = carInfo.getMandatoryInsuranceAmount().doubleValue();
        prjLeaseItemInsurance.setCompulsoryAmount(compulsoryAmount / 100);
        //商业保险类型
        prjLeaseItemInsurance.setCommercialInsurance(carInfo.getCommercialInsuranceType());


        if (prjProjectLeaseItemSales.getSalesId()!=null){
            projectLeaseItemSalesMapper.updateByPrimaryKey(prjProjectLeaseItemSales);
        }else{
            projectLeaseItemSalesMapper.insert(prjProjectLeaseItemSales);
        }
        prjLeaseItemInsurance.setProjectLeaseItemId(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());
        if (prjLeaseItemInsurance.getInsuranceId()!=null){
            prjLeaseItemInsuranceMapper.updateByPrimaryKey(prjLeaseItemInsurance);
        }else{
            prjLeaseItemInsuranceMapper.insert(prjLeaseItemInsurance);
        }

        String projectStatus = hlsCusPrjProject.getProjectStatus();
        //        如果订单状态为放款之后，不允许修改
        if ("放款之后".equals(projectStatus)){
            responseData.setCode("400");
            responseData.setMessage("订单已放款，不允许修改");
            hlsWsRequests.setReturnStatus("E");
            hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
            hlsWsRequestsMapper.insert(hlsWsRequests);
            return responseData;
        }
        //如果订单为业务申请之后，放款之前，对字段进行校验
        //品牌名称、车系名称、车型名称、车辆颜色
        if ("申请之后,放款之前".equals(projectStatus)){
            if (preRiskAuditData!=null){
                if (hlsCusPrjProjectLeaseItemList.size()>0){
                    hlsCusPrjProjectLeaseItem1 = hlsCusPrjProjectLeaseItemList.get(0);
                    //判断车系名称与riskInfo中的值是否相同
                    CarInformation carInformation = preRiskAuditData.getCarInformation();
                    if (!hlsCusPrjProjectLeaseItem1.getBrandC().equals(carInformation.getCarbrand2())){
                        responseData.setCode("400");
                        responseData.setMessage("品牌名称与riskInfo中的值不同");
                        hlsWsRequests.setReturnStatus("E");
                        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                        hlsWsRequestsMapper.insert(hlsWsRequests);
                        return responseData;
                    }
                    if (hlsCusPrjProjectLeaseItem1.getSeriesC().equals(carInformation.getChexi())){
                        responseData.setCode("400");
                        responseData.setMessage("车系名称与riskInfo中的值不同");
                        hlsWsRequests.setReturnStatus("E");
                        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                        hlsWsRequestsMapper.insert(hlsWsRequests);
                        return responseData;
                    }
                    if (hlsCusPrjProjectLeaseItem1.getModelC().equals(carInformation.getCartype())){
                        responseData.setCode("400");
                        responseData.setMessage("车型名称与riskInfo中的值不同");
                        hlsWsRequests.setReturnStatus("E");
                        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                        hlsWsRequestsMapper.insert(hlsWsRequests);
                        return responseData;
                    }
                    if (hlsCusPrjProjectLeaseItem1.getColorC().equals(carInformation.getCarcolor())){
                        responseData.setCode("400");
                        responseData.setMessage("车辆颜色与riskInfo中的值不同");
                        hlsWsRequests.setReturnStatus("E");
                        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                        hlsWsRequestsMapper.insert(hlsWsRequests);
                        return responseData;
                    }
                }
            }
        }

        //获取租赁物相关信息，并入库





//            if ("申请之前".equals(projectStatus)){

                HlsCusPrjQuotation prjQuotation = null;
                if (hlsCusPrjProject.getProjectId()!=null){
                    List<HlsCusPrjQuotation> hlsCusPrjQuotations = hlsCusPrjQuotationMapper.selectQuoByProjectId(hlsCusPrjProject.getProjectId());
                    if (hlsCusPrjQuotations.size()>0){
                        prjQuotation = hlsCusPrjQuotations.get(0);
                    }
                }
                if (prjQuotation==null){
                    prjQuotation = new HlsCusPrjQuotation();
                    //期次
                    prjQuotation.setLeaseTimes(Long.valueOf(financeInfo.getTermCount()));
//            月租
                    prjQuotation.setPmt(financeInfo.getMonthPayment().doubleValue()/100);
//            利率
                    prjQuotation.setIntRate(financeInfo.getRate().doubleValue());
//            首付款
                    prjQuotation.setDownPayment(financeInfo.getFirstPayment().doubleValue()/100);
                    //剩余车辆价款 (分)
                    prjQuotation.setSurplusAmount(financeInfo.getCarRestPrice().doubleValue()/100);
                }else{
                    if (!prjQuotation.getLeaseTimes().equals(Long.valueOf(financeInfo.getTermCount()))&&
                            !prjQuotation.getPmt().equals(financeInfo.getMonthPayment().doubleValue()/100)&&
                            !prjQuotation.getIntRate().equals(financeInfo.getRate().doubleValue()/100)&&
                            prjQuotation.getDownPayment().equals(financeInfo.getFirstPayment().doubleValue()/100) &&
                            prjQuotation.getSurplusAmount().equals(financeInfo.getCarRestPrice().doubleValue()/100)
                    ){
                        //期次
                        prjQuotation.setLeaseTimes(Long.valueOf(financeInfo.getTermCount()));
//            月租
                        prjQuotation.setPmt(financeInfo.getMonthPayment().doubleValue()/100);
//            利率
                        prjQuotation.setIntRate(financeInfo.getRate().doubleValue());
//            首付款
                        prjQuotation.setDownPayment(financeInfo.getFirstPayment().doubleValue()/100);
                        //剩余车辆价款 (分)
                        prjQuotation.setSurplusAmount(financeInfo.getCarRestPrice().doubleValue()/100);
                    }
                }


                //品牌
                hlsCusPrjProjectLeaseItem1.setBrandC(carInfo.getBrandName());
//            车系
                hlsCusPrjProjectLeaseItem1.setSeriesC(carInfo.getSeriesName());
//            车型
                hlsCusPrjProjectLeaseItem1.setModelC(carInfo.getModelName());
                //            车辆颜色
                hlsCusPrjProjectLeaseItem1.setColorC(carInfo.getColor());
                if (preRiskAuditData!=null){
                    //进件信息
                    hlsCusPrjProject.setDivision(preRiskAuditData.getProline());

                    //承租人基本信息
                    BasicCustomerInformation basicCustomerInformation = preRiskAuditData.getBasicCustomerInformation();
                    //承租人职业信息
                    BasicCustomerJobInformation basicCustomerJobInformation = preRiskAuditData.getBasicCustomerJobInformation();
                    //            关联人信息
                    AssociatedPersonInformation associatedPersonInformation = preRiskAuditData.getAssociatedPersonInformation();
                    //车辆信息
                    CarInformation carInformation = preRiskAuditData.getCarInformation();
                    HlsCusBpMaster hlsCusBpMaster = hlsCusBpMasterMapper.selectByProjectId(hlsCusPrjProject.getProjectId());
                    //性别
                    hlsCusBpMaster.setGender(basicCustomerInformation.getSex());
                    //民族
                    hlsCusBpMaster.setEthnicity(basicCustomerInformation.getNation());
                    //出生日期
                    Date dateOfBirth = null;
                    try {
                        dateOfBirth = simpleDateFormat.parse(basicCustomerInformation.getBirthdate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    hlsCusBpMaster.setDateOfBirth(dateOfBirth);
                    //年龄
                    hlsCusBpMaster.setAge(Long.valueOf(basicCustomerInformation.getAge()));
                    //国籍
                    hlsCusBpMaster.setNationality(basicCustomerInformation.getNationality());
                    //户籍所属省份
                    hlsCusBpMaster.setDomicileProvince(basicCustomerInformation.getDomicileshen());
                    //户籍所属市
                    hlsCusBpMaster.setDomicileCity(basicCustomerInformation.getHomeaddresspcity());
                    //户籍所属区
                    hlsCusBpMaster.setDomicileDistrict(basicCustomerInformation.getDomicilequ());
                    //户籍地址
                    hlsCusBpMaster.setDomicileAddress(basicCustomerInformation.getDomicileaddress());
                    //是否本地户籍
                    hlsCusBpMaster.setDomicileLocalFlag(basicCustomerInformation.getIslocaldomicile());
                    //签发机关
                    hlsCusBpMaster.setIdIssueOrgan(basicCustomerInformation.getIssuegov());
                    //是否长期有效
                    hlsCusBpMaster.setIdLongTerm(basicCustomerInformation.getIsenable());
                    //居住地址省
                    hlsCusBpMaster.setHouseProvince(basicCustomerInformation.getHomeaddressprovince());
                    //居住地址市
                    hlsCusBpMaster.setHouseCity(basicCustomerInformation.getHomeaddresspcity());
                    //居住地址区县
                    hlsCusBpMaster.setHouseDistrict(basicCustomerInformation.getJzdzqx());
                    //居住地址
                    hlsCusBpMaster.setHouseAddress(basicCustomerInformation.getHomeaddress());
                    //房产类型
                    hlsCusBpMaster.setHouseType(basicCustomerInformation.getHousetype());
                    //婚姻状况
                    hlsCusBpMaster.setMaritalStatus(basicCustomerInformation.getMarriage());
                    //子女人数
                    hlsCusBpMaster.setNumberOfChildren(Long.valueOf(basicCustomerInformation.getChildnum()));
                    //有无驾照
                    hlsCusBpMaster.setDriverLicenseFlag(basicCustomerInformation.getIsdriverlicence());
                    //驾照类型
                    hlsCusBpMaster.setDriverLicenseType(basicCustomerInformation.getDriverlicencetype());
                    //驾照状态
                    hlsCusBpMaster.setDriverLicenseStatus(basicCustomerInformation.getDriverstatus());
                    //驾照截止日期
                    Date driverLicenseDeadline = null;
                    try {
                        driverLicenseDeadline = simpleDateFormat.parse(basicCustomerInformation.getJzjzrq());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    hlsCusBpMaster.setDriverLicenseDeadline(driverLicenseDeadline);
                    //违章分数
                    hlsCusBpMaster.setViolationScore(Long.valueOf(basicCustomerInformation.getWzfs()));
                    //违章罚款
                    hlsCusBpMaster.setViolationFines(Long.valueOf(basicCustomerInformation.getWzfk()));
                    //学历
                    hlsCusBpMaster.setHighestDegree(basicCustomerInformation.getDiploma());
                    //单位名称
                    hlsCusBpMaster.setWorkingCompany(basicCustomerJobInformation.getCompany());
                    //所属行业
                    hlsCusBpMaster.setEconomicInduClassify(basicCustomerJobInformation.getIndustry());
                    //单位性质
                    hlsCusBpMaster.setCompanyNature(basicCustomerJobInformation.getCorpnprop());
                    //职业
                    hlsCusBpMaster.setProfession(basicCustomerJobInformation.getOccu());
                    //当前职位
                    hlsCusBpMaster.setJobTitle(basicCustomerJobInformation.getPosition());
                    //个人月收入
//            hlsCusBpMaster.setMonthlyIncome(Long.valueOf(basicCustomerJobInformation.getSalary()));
                    //单位电话
                    hlsCusBpMaster.setWorkPhone(basicCustomerJobInformation.getCompanyphone());
                    //公司所属省份
                    hlsCusBpMaster.setCompanyProvince(basicCustomerJobInformation.getCompanyshen());
                    //公司所属市
                    hlsCusBpMaster.setCompanyCity(basicCustomerJobInformation.getCompanyshi());
                    //公司所属区
                    hlsCusBpMaster.setCompanyDistrict(basicCustomerJobInformation.getCompanyqu());
                    //公司地址
                    hlsCusBpMaster.setCompanyAddress(basicCustomerJobInformation.getCompaddr());


                    HlsCusBpMasterBankAccount hlsCusBpMasterBankAccount = null;
                    if (hlsCusBpMaster.getBpId()!=null){
                        hlsCusBpMasterBankAccount = hlsCusBpMasterBankAccountMapper.selectBankByBpId(hlsCusBpMaster.getBpId());
                    }
                    if (hlsCusBpMasterBankAccount==null){
                        hlsCusBpMasterBankAccount = new HlsCusBpMasterBankAccount();
                    }
                    //银行卡号
                    hlsCusBpMasterBankAccount.setBankAccountNum(basicCustomerInformation.getCardno());
                    //实际驾驶人与申请人关系
                    hlsCusPrjProject.setDriverAndApplicant(associatedPersonInformation.getSjjsrysqrgx());
                    //配偶姓名
                    hlsCusBpMaster.setBpNameSp(associatedPersonInformation.getSpousename());
//            配偶性别
                    hlsCusBpMaster.setGenderSp(associatedPersonInformation.getSpouseidcard());
                    //配偶出生日期
                    Date dateOfBirthSp = null;
                    try {
                        dateOfBirthSp = simpleDateFormat.parse(associatedPersonInformation.getSpousebir());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    hlsCusBpMaster.setDateOfBirthSp(dateOfBirthSp);
                    //配偶证件号码
                    hlsCusBpMaster.setIdCardNoSp(associatedPersonInformation.getSpouseidcard());
                    //配偶单位地址
                    hlsCusBpMaster.setAddressSp(associatedPersonInformation.getSpoucecompaddr());
                    //配偶联系电话
                    hlsCusBpMaster.setSpousePhone(Long.valueOf(associatedPersonInformation.getSpousephone()));
                    //配偶公司名称
                    hlsCusBpMaster.setSpouseJobsUnit(associatedPersonInformation.getSpousecomp());
                    //配偶公司所属行业
                    hlsCusBpMaster.setInduClassifySp(associatedPersonInformation.getSpousecompind());
                    //配偶公司性质
                    hlsCusBpMaster.setCompanyNatureSp(associatedPersonInformation.getSpousecomptype());
                    //配偶职业类型
                    hlsCusBpMaster.setProfessionSp(associatedPersonInformation.getSpousezylx());
                    //配偶职位
                    hlsCusBpMaster.setJobTitleSp(associatedPersonInformation.getSpousezw());

                    //制造商
                    hlsCusPrjProjectLeaseItem1.setManufacturer(carInformation.getCarfac());
                    //车辆类型
                    hlsCusPrjProjectLeaseItem1.setVoitureType(carInformation.getVehicletype());
                    //车辆准载(定员)
                    hlsCusPrjProjectLeaseItem1.setVehicleCapacity(carInformation.getCarzkcount());
                    //是否进口
                    hlsCusPrjProjectLeaseItem1.setIsImport(carInformation.getSfjk());
                    //燃料类型
                    hlsCusPrjProjectLeaseItem1.setFuelType(carInformation.getRllx());
                    //车辆评估价格
                    hlsCusPrjProjectLeaseItem1.setEvaluationValue(Double.valueOf(carInformation.getClpgjg()));
                    //首次登记日期
                    //转让登记日期
                    Date firstRegistrationDate = null;
                    Date transferRegistrationDate = null;
                    try {
                        firstRegistrationDate = simpleDateFormat.parse(carInformation.getScdjrq());
                        transferRegistrationDate = simpleDateFormat.parse(carInformation.getTransferencedate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    hlsCusPrjProjectLeaseItem1.setFirstRegistrationDate(firstRegistrationDate);
                    hlsCusPrjProjectLeaseItem1.setTransferRegistrationDate(transferRegistrationDate);
                    //车牌号
                    hlsCusPrjProjectLeaseItem1.setLicensePlateNumber(carInformation.getChepaihao());
                    //车辆使用性质
                    hlsCusPrjProjectLeaseItem1.setNatureOfVehicle(carInformation.getCarnatureofuse());
//            上牌城市
                    hlsCusPrjProjectLeaseItem1.setCityCode(carInformation.getRegisteredcity());
                    //首次上牌日
                    Date firstPlateDate = null;
                    try {
                        firstPlateDate = simpleDateFormat.parse(carInformation.getScspr());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    hlsCusPrjProjectLeaseItem1.setFirstPlateDate(firstPlateDate);
                    //表显里程
                    hlsCusPrjProjectLeaseItem1.setOdometerReading(Integer.valueOf(carInformation.getBxlc()));
                    //车辆年限
                    hlsCusPrjProjectLeaseItem1.setVehicleAge(Integer.valueOf(carInformation.getCarlife()));
                    //车辆所有人
                    hlsCusPrjProjectLeaseItem1.setPropPerson(carInformation.getCaraffiliation());


                    PrjProjectLeaseItemMortgage prjProjectLeaseItemMortgage = null;
                    if (hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId()!=null){
                        List<PrjProjectLeaseItemMortgage> prjProjectLeaseItemMortgages = projectLeaseItemMortgageMapper.prjProjectLeaseItemMortgageByLeaseItemId(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());
                        if (prjProjectLeaseItemMortgages.size()>0){
                            prjProjectLeaseItemMortgage = prjProjectLeaseItemMortgages.get(0);
                        }
                    }
                    if (prjProjectLeaseItemMortgage==null){
                        prjProjectLeaseItemMortgage = new PrjProjectLeaseItemMortgage();
                    }
                    //抵押次数
                    prjProjectLeaseItemMortgage.setNumberOfMortgages(Integer.valueOf(carInformation.getDycs()));
                    //过户次数
                    prjProjectLeaseItemMortgage.setNumberOfTransfers(Integer.valueOf(carInformation.getGhjcs()));
                    //近1年抵押次数
                    prjProjectLeaseItemMortgage.setNumberOfMortgagesOne(Integer.valueOf(carInformation.getJyndics()));
                    //近1年过户次数
                    prjProjectLeaseItemMortgage.setNumberOfTransfersOne(Integer.valueOf(carInformation.getLast1yearguohucount()));
                    //近2年过户次数
                    prjProjectLeaseItemMortgage.setNumberOfTransfersTwo(Integer.valueOf(carInformation.getLast2yearguohucount()));
                    //是否有车辆登记证补领记录
                    prjProjectLeaseItemMortgage.setIsRenewalRecord(carInformation.getIsregister());
                    //近半年是否有车辆登记证补领记录
                    prjProjectLeaseItemMortgage.setIsHalfRenewalRecord(carInformation.getIsregisterhy());
                    //上一次抵押登记日期
                    //最近一次解押日期
                    Date lastTransfersDate = null;
                    Date recentlyTransfersDate = null;
                    try {
                        lastTransfersDate = simpleDateFormat.parse(carInformation.getLastmortgagedate());
                        recentlyTransfersDate = simpleDateFormat.parse(carInformation.getLastdtecompressiondate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    prjProjectLeaseItemMortgage.setLastTransfersDate(lastTransfersDate);
                    prjProjectLeaseItemMortgage.setRecentlyTransfersDate(recentlyTransfersDate);
                    //抵押状态
                    prjProjectLeaseItemMortgage.setTransfersStatus(carInformation.getMortgagestatus());
                    //解押天数
                    prjProjectLeaseItemMortgage.setDaysToRelease(Integer.valueOf(carInformation.getJyts()));

                    //是否有交强险
                    prjLeaseItemInsurance.setIsCompulsoryInsurance(carInformation.getSfyjqx());
                    //交强险到期日期
                    Date compulsoryEndDate = null;
                    //车损险到期日期
                    Date vehicleEndDate = null;
                    //第三者责任险到期日期
                    Date thirdEndDate = null;
                    try {
                        compulsoryEndDate = simpleDateFormat.parse(carInformation.getJqxdqrq());
                        vehicleEndDate = simpleDateFormat.parse(carInformation.getCsxdqrq());
                        thirdEndDate = simpleDateFormat.parse(carInformation.getSzxdqrq());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    prjLeaseItemInsurance.setCompulsoryEndDate(compulsoryEndDate);
                    //是否有车损险
                    prjLeaseItemInsurance.setIsVehicleDamage(carInformation.getSfycsx());

                    prjLeaseItemInsurance.setVehicleEndDate(vehicleEndDate);
                    //是否有第三者责任险
                    prjLeaseItemInsurance.setIsThirdParty(carInformation.getSfyszx());
                    prjLeaseItemInsurance.setThirdEndDate(thirdEndDate);


                    PrjProjectLeaseItemCondition prjProjectLeaseItemCondition = null;
                    if (hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId()!=null){
                        List<PrjProjectLeaseItemCondition> prjProjectLeaseItemConditions = projectLeaseItemConditionMapper.prjProjectLeaseItemConditionByLeaseItemId(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());
                        if (prjProjectLeaseItemConditions.size()>0){
                            prjProjectLeaseItemCondition = prjProjectLeaseItemConditions.get(0);
                        }
                    }
                    if (prjProjectLeaseItemCondition==null){
                        prjProjectLeaseItemCondition = new PrjProjectLeaseItemCondition();
                    }
                    //车况信息
                    //是否年检
                    prjProjectLeaseItemCondition.setIsAnnualInspection(carInformation.getIscheckyea());
                    //是否安装GPS
                    hlsCusPrjProjectLeaseItem1.setGpsIsInstallation(carInformation.getSfazgps());
                    //贷款用途
                    hlsCusPrjProjectLeaseItem1.setLoanPurpose(carInformation.getUsage());
                    //上牌类型
                    hlsCusPrjProjectLeaseItem1.setPlateType(carInformation.getSptype());
                    //牌照归属
                    hlsCusPrjProjectLeaseItem1.setLicensePlateOwnership(carInformation.getPaizhaogs());
                    //车辆交易价
                    hlsCusPrjProjectLeaseItem1.setPrice(Double.valueOf(carInformation.getCljyjg())/100);
                    //购置税
                    hlsCusPrjProjectLeaseItem1.setPurchaseTax(Double.valueOf(carInformation.getGouzhis())/100);
                    //车辆保险金额
                    hlsCusPrjProjectLeaseItem1.setInsurancePremium(Double.valueOf(carInformation.getClbxje())/100);
                    //GPS费用
                    hlsCusPrjProjectLeaseItem1.setGpsFee(Double.valueOf(carInformation.getGpsfy())/100);
                    //上牌发票金额
                    hlsCusPrjProjectLeaseItem1.setPlateInvoiceAmount(Double.valueOf(carInformation.getCtac())/100);
                    //装饰品金额
                    hlsCusPrjProjectLeaseItem1.setAccessoryAmount(Double.valueOf(carInformation.getZspje())/100);
                    //车辆税额
                    hlsCusPrjProjectLeaseItem1.setVehicleTax(Double.valueOf(carInformation.getCheliangse())/100);
                    //是否水泡
                    prjProjectLeaseItemCondition.setIsWaterDamaged(carInformation.getSfsp());
                    //是否营转非
                    prjProjectLeaseItemCondition.setIsConversion(carInformation.getSfyzf());
                    //是否重大改装车
                    prjProjectLeaseItemCondition.setIsSignificantlyModified(carInformation.getSfzdgzc());
                    //原车主证件类型
                    prjProjectLeaseItemCondition.setOriginalOwnerCardType(carInformation.getYuanchezzjlx());
                    //原车主姓名
                    prjProjectLeaseItemCondition.setOriginalOwnerName(carInformation.getYuanczxm());
                    //原车主证件号
                    prjProjectLeaseItemCondition.setOriginalOwnerCardNum(carInformation.getYuanchezzjhm());
                    //原车主户籍所在地址
                    prjProjectLeaseItemCondition.setOriginalOwnerAddress(
                            carInformation.getYuanchezhuhujishengfen()
                                    +carInformation.getYuanchezhuhujishi()+carInformation.getCarownersdomicilelast());
                    //维修保养情况
                    prjProjectLeaseItemCondition.setMaintenanceInfo(carInformation.getBywxqk());
                    //精品加装
                    prjProjectLeaseItemCondition.setPremiumAddOn(carInformation.getJpjz());

                    //首付比例
                    prjQuotation.setDownPaymentRatio(Double.valueOf(carInformation.getPaymentratio()));
                    //尾款比例

                    prjProjectMapper.updateByPrimaryKey(hlsCusPrjProject);
                    prjQuotation.setSourceDocumentId(hlsCusPrjProject.getProjectId());
                    if (prjQuotation.getQuotationId()!=null){
                        hlsCusPrjQuotationMapper.updateByPrimaryKey(prjQuotation);
                    }else{
                        hlsCusPrjQuotationMapper.insert(prjQuotation);
                    }
                    if (hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId()!=null){
                        hlsCusPrjProjectLeaseItemMapper.updateByPrimaryKey(hlsCusPrjProjectLeaseItem1);
                    }else{
                        hlsCusPrjProjectLeaseItemMapper.insert(hlsCusPrjProjectLeaseItem1);
                    }
                    prjProjectLeaseItemMortgage.setProjectLeaseItemId(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());
                    if (prjProjectLeaseItemMortgage.getMortgageId()!=null){
                        projectLeaseItemMortgageMapper.updateByPrimaryKey(prjProjectLeaseItemMortgage);
                    }else{
                        projectLeaseItemMortgageMapper.insert(prjProjectLeaseItemMortgage);
                    }
                    prjProjectLeaseItemSales.setProjectLeaseItemId(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());
                    if (prjProjectLeaseItemSales.getSalesId()!=null){
                        projectLeaseItemSalesMapper.updateByPrimaryKey(prjProjectLeaseItemSales);
                    }else{
                        projectLeaseItemSalesMapper.insert(prjProjectLeaseItemSales);
                    }
                    prjProjectLeaseItemCondition.setProjectLeaseItemId(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());
                    if (prjProjectLeaseItemCondition.getConditionId()!=null){
                        projectLeaseItemConditionMapper.updateByPrimaryKey(prjProjectLeaseItemCondition);
                    }else{
                        projectLeaseItemConditionMapper.insert(prjProjectLeaseItemCondition);
                    }
                    prjLeaseItemInsurance.setProjectLeaseItemId(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());
                    if (prjLeaseItemInsurance.getInsuranceId()!=null){
                        prjLeaseItemInsuranceMapper.updateByPrimaryKey(prjLeaseItemInsurance);
                    }else{
                        prjLeaseItemInsuranceMapper.insert(prjLeaseItemInsurance);
                    }
                    hlsCusBpMasterMapper.updateByPrimaryKey(hlsCusBpMaster);
                    hlsCusBpMasterBankAccount.setBpId(hlsCusBpMaster.getBpId());
                    if (hlsCusBpMasterBankAccount.getBankAccountId()!=null){
                        hlsCusBpMasterBankAccountMapper.updateByPrimaryKey(hlsCusBpMasterBankAccount);
                    }else{
                        hlsCusBpMasterBankAccountMapper.insert(hlsCusBpMasterBankAccount);
                    }
                    hlsCusBpMasterMapper.updateByPrimaryKey(hlsCusBpMaster);
                }else{
                    prjProjectMapper.updateByPrimaryKey(hlsCusPrjProject);
                    prjQuotation.setSourceDocumentId(hlsCusPrjProject.getProjectId());
                    if (prjQuotation.getQuotationId()!=null){
                        hlsCusPrjQuotationMapper.updateByPrimaryKey(prjQuotation);
                    }else{
                        hlsCusPrjQuotationMapper.insert(prjQuotation);
                    }
                    hlsCusPrjProjectLeaseItemMapper.updateByPrimaryKey(hlsCusPrjProjectLeaseItem1);
                }
//            }




        //            设置返回状态
        responseData.setCode("200");
        responseData.setMessage("数据采集成功");
        hlsWsRequests.setReturnStatus("S");
        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
        hlsWsRequestsMapper.insert(hlsWsRequests);
        return responseData;
    }

    @Override
    public ResponseData overdueRepurchaseTrialCalculation(OverdueRepurchaseTrialCalculationDTO overdueRepurchaseTrialCalculationDTO, HttpServletRequest request) {
        //保存日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
//        获取请求路径
        String requestURI = request.getRequestURI();
        hlsWsRequests.setRequestWsdlUrl(requestURI);
        //请求日期
        hlsWsRequests.setRequestDate(new Date());
//        功能名称
        hlsWsRequests.setFunctionName("逾期回购试算");
//        状态变更日期
        hlsWsRequests.setStatusDate(new Date());
//        user_id
        String userId = request.getParameter("user_id");
        if (userId!=null){
            hlsWsRequests.setUserId(Long.valueOf(userId));
        }
//        请求状态
        hlsWsRequests.setStatusCode("200");
//        参数类型
        hlsWsRequests.setParameterType("JSON");
        // 请求体
        String s = JSONObject.toJSONString(overdueRepurchaseTrialCalculationDTO);
        hlsWsRequests.setRequestJson(s);


        ResponseData responseData = new ResponseData();

        //根据订单编号查询数据


        //            设置返回状态
        responseData.setCode("200");
        responseData.setMessage("试算成功");
        hlsWsRequests.setReturnStatus("S");
        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
        hlsWsRequestsMapper.insert(hlsWsRequests);
        return responseData;
    }

    @Override
    public ResponseData overdueRepurchaseRequest(OverdueRepurchaseRequestDTO overdueRepurchaseRequestDTO, HttpServletRequest request) {
        //保存日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
//        获取请求路径
        String requestURI = request.getRequestURI();
        hlsWsRequests.setRequestWsdlUrl(requestURI);
        //请求日期
        hlsWsRequests.setRequestDate(new Date());
//        功能名称
        hlsWsRequests.setFunctionName("逾期回购请求");
//        状态变更日期
        hlsWsRequests.setStatusDate(new Date());
//        user_id
        String userId = request.getParameter("user_id");
        if (userId!=null){
            hlsWsRequests.setUserId(Long.valueOf(userId));
        }
//        请求状态
        hlsWsRequests.setStatusCode("200");
//        参数类型
        hlsWsRequests.setParameterType("JSON");
        // 请求体
        String s = JSONObject.toJSONString(overdueRepurchaseRequestDTO);
        hlsWsRequests.setRequestJson(s);


        ResponseData responseData = new ResponseData();

        //将数据入库


        //            设置返回状态
        responseData.setCode("200");
        responseData.setMessage("逾期回购成功");
        hlsWsRequests.setReturnStatus("S");
        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
        hlsWsRequestsMapper.insert(hlsWsRequests);
        return responseData;
    }

    @Override
    public ResponseData queryWithholdingState(QueryWithholdingStateDTO queryWithholdingStateDTO, HttpServletRequest request) {
        //保存日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
//        获取请求路径
        String requestURI = request.getRequestURI();
        hlsWsRequests.setRequestWsdlUrl(requestURI);
        //请求日期
        hlsWsRequests.setRequestDate(new Date());
//        功能名称
        hlsWsRequests.setFunctionName("代扣状态查询");
//        状态变更日期
        hlsWsRequests.setStatusDate(new Date());
//        user_id
        String userId = request.getParameter("user_id");
        if (userId!=null){
            hlsWsRequests.setUserId(Long.valueOf(userId));
        }
//        请求状态
        hlsWsRequests.setStatusCode("200");
//        参数类型
        hlsWsRequests.setParameterType("JSON");
        // 请求体
        String s = JSONObject.toJSONString(queryWithholdingStateDTO);
        hlsWsRequests.setRequestJson(s);


        ResponseData responseData = new ResponseData();

        //根据订单编号查询数据


        //            设置返回状态
        responseData.setCode("200");
        responseData.setMessage("查询成功");
        hlsWsRequests.setReturnStatus("S");
        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
        hlsWsRequestsMapper.insert(hlsWsRequests);
        return responseData;
    }

    @Override
    public ResponseData stopWithholding(StopWithholdingDTO stopWithholdingDTO, HttpServletRequest request) {
        //保存日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
//        获取请求路径
        String requestURI = request.getRequestURI();
        hlsWsRequests.setRequestWsdlUrl(requestURI);
        //请求日期
        hlsWsRequests.setRequestDate(new Date());
//        功能名称
        hlsWsRequests.setFunctionName("暂停代扣");
//        状态变更日期
        hlsWsRequests.setStatusDate(new Date());
//        user_id
        String userId = request.getParameter("user_id");
        if (userId!=null){
            hlsWsRequests.setUserId(Long.valueOf(userId));
        }
//        请求状态
        hlsWsRequests.setStatusCode("200");
//        参数类型
        hlsWsRequests.setParameterType("JSON");
        // 请求体
        String s = JSONObject.toJSONString(stopWithholdingDTO);
        hlsWsRequests.setRequestJson(s);


        ResponseData responseData = new ResponseData();

        //根据订单编号和期次号，修改状态


        //            设置返回状态
        responseData.setCode("200");
        responseData.setMessage("暂停代扣成功");
        hlsWsRequests.setReturnStatus("S");
        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
        hlsWsRequestsMapper.insert(hlsWsRequests);
        return responseData;
    }

    @Override
    public ResponseData recoverWithholding(RecoverWithholdingDTO recoverWithholdingDTO, HttpServletRequest request) {
        //保存日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
//        获取请求路径
        String requestURI = request.getRequestURI();
        hlsWsRequests.setRequestWsdlUrl(requestURI);
        //请求日期
        hlsWsRequests.setRequestDate(new Date());
//        功能名称
        hlsWsRequests.setFunctionName("恢复代扣");
//        状态变更日期
        hlsWsRequests.setStatusDate(new Date());
//        user_id
        String userId = request.getParameter("user_id");
        if (userId!=null){
            hlsWsRequests.setUserId(Long.valueOf(userId));
        }
//        请求状态
        hlsWsRequests.setStatusCode("200");
//        参数类型
        hlsWsRequests.setParameterType("JSON");
        // 请求体
        String s = JSONObject.toJSONString(recoverWithholdingDTO);
        hlsWsRequests.setRequestJson(s);


        ResponseData responseData = new ResponseData();

        //根据订单编号和期次号，修改状态


        //            设置返回状态
        responseData.setCode("200");
        responseData.setMessage("暂停代扣成功");
        hlsWsRequests.setReturnStatus("S");
        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
        hlsWsRequestsMapper.insert(hlsWsRequests);
        return responseData;
    }
}