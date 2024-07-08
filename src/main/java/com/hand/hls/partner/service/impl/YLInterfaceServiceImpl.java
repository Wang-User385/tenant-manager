package com.hand.hls.partner.service.impl;

import com.alibaba.fastjson.JSON;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.csh.dto.CshPaymentReqHd;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.partner.dto.*;
import com.hand.hls.partner.service.YLInterfaceService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import com.hand.hls.web.logs.mapper.HlsWsRequestsMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

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
        // 读取请求体
        try {
            String jsonBody = request.getInputStream().toString();
            hlsWsRequests.setRequestJson(jsonBody);
        } catch (IOException e) {
            e.printStackTrace();
        }


        ResponseData responseData = new ResponseData();
        //判断姓名、客户身份证号、手机号、产品码、合作机构单号、证件签发日期、证件到期日期是否为空
        if (placeOrderDTO.getName()==null){
            responseData.setCode("400");
            responseData.setMessage("客户姓名为空");
            hlsWsRequests.setReturnStatus("E");
            hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
            hlsWsRequestsMapper.insert(hlsWsRequests);
            return responseData;
        }
        if (placeOrderDTO.getIdCardNo()==null){
            responseData.setCode("400");
            responseData.setMessage("客户身份证号为空");
            hlsWsRequests.setReturnStatus("E");
            hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
            hlsWsRequestsMapper.insert(hlsWsRequests);
            return responseData;
        }
        if (placeOrderDTO.getMobile()==null){
            responseData.setCode("400");
            responseData.setMessage("手机号为空");
            hlsWsRequests.setReturnStatus("E");
            hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
            hlsWsRequestsMapper.insert(hlsWsRequests);
            return responseData;
        }
        if (placeOrderDTO.getProductCode()==null){
            responseData.setCode("400");
            responseData.setMessage("产品码为空");
            hlsWsRequests.setReturnStatus("E");
            hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
            hlsWsRequestsMapper.insert(hlsWsRequests);
            return responseData;
        }
        if (placeOrderDTO.getOutBizNo()==null){
            responseData.setCode("400");
            responseData.setMessage("合作机构单号为空");
            hlsWsRequests.setReturnStatus("E");
            hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
            hlsWsRequestsMapper.insert(hlsWsRequests);
            return responseData;
        }
        if (placeOrderDTO.getIdissue()==null){
            responseData.setCode("400");
            responseData.setMessage("证件签发日期为空");
            hlsWsRequests.setReturnStatus("E");
            hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
            hlsWsRequestsMapper.insert(hlsWsRequests);
            return responseData;
        }
        if (placeOrderDTO.getIdexp()==null){
            responseData.setCode("400");
            responseData.setMessage("证件到期日期为空");
            hlsWsRequests.setReturnStatus("E");
            hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
            hlsWsRequestsMapper.insert(hlsWsRequests);
            return responseData;
        }

        //判断该客户存不存在
        //如果存在，判断名称和电话一不一致，不一致就修改
        //如果不存在，就新增
        HlsCusBpMaster bpMaster = hlsCusBpMasterMapper.selectMasterByIdCardNo(placeOrderDTO.getIdCardNo());
        if (bpMaster==null){
            HlsCusBpMaster bpMaster1 = new HlsCusBpMaster();
            bpMaster1.setBpName(placeOrderDTO.getName());
            bpMaster1.setIdCardNo(placeOrderDTO.getIdCardNo());
            bpMaster1.setPhone(placeOrderDTO.getMobile());
            bpMaster1.setBpClass("NP");
            hlsCusBpMasterMapper.insert(bpMaster1);
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
            }else{
//                如果项目不是取消、拒绝、结束，则判断合同状态是否为结束、关闭、终止，如果是则允许下单，否则不允许下单
                HlsCusConContract hlsCusConContract = conContractMapper.selectByRefProjectId(x.getProjectId());
                if (!"TERMINATE".equals(hlsCusConContract.getContractStatus())||
                        !"CANCEL".equals(hlsCusConContract.getContractStatus())||
                        !"END".equals(hlsCusConContract.getContractStatus())){
                    responseData.setCode("400");
                    responseData.setMessage("存在在途单");
                }
            }
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
        String codeRuleValue = fndCodingRuleValuesService.getCodeRuleValue(iRequest, HlsCusConstant.ORDER_CODE, HlsCusConstant.ORDER_CODE, HlsCusConstant.ORDER_CODE, params);

        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectNumber(codeRuleValue);
        hlsCusPrjProject.setCompanyId(1L);
        hlsCusPrjProject.setProjectStatus("NEW");
        prjProjectMapper.insert(hlsCusPrjProject);


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
        // 读取请求体
        try {
            String jsonBody = request.getInputStream().toString();
            hlsWsRequests.setRequestJson(jsonBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        responseData.setCode("200");
        responseData.setMessage("取消成功");
        hlsWsRequests.setReturnStatus("S");
        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
        hlsWsRequestsMapper.insert(hlsWsRequests);
        return responseData;
    }

    @Override
    public ResponseData queryOrder(String orderNo, HttpServletRequest request) {

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
        // 读取请求体
        try {
            String jsonBody = request.getInputStream().toString();
            hlsWsRequests.setRequestJson(jsonBody);
        } catch (IOException e) {
            e.printStackTrace();
        }


        ResponseData responseData = new ResponseData();
        //根据订单编号查询相对应的还款信息,判断订单存不存在，不存在直接返回
        List<RepayPlanTermInfoDTO> repayPlanTermInfoDTOList = prjProjectMapper.selectRepayPlanByOrderNo(orderNo);
        QueryOrder queryOrder = prjProjectMapper.selectQueryOrderByOrderNo(orderNo);
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
        // 读取请求体
        try {
            String jsonBody = request.getInputStream().toString();
            hlsWsRequests.setRequestJson(jsonBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ResponseData responseData = new ResponseData();
//        判断传入参数是否为空，为空直接返回
        if (repayMent==null){
            responseData.setCode("400");
            responseData.setMessage("请求参数为空");
            hlsWsRequests.setReturnStatus("E");
            hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
            hlsWsRequestsMapper.insert(hlsWsRequests);
            return responseData;
        }else{
            //判断订单编号是否为空，为空直接返回
            if (repayMent.getOrderNo()==null){
                responseData.setCode("400");
                responseData.setMessage("订单编号为空");
                hlsWsRequests.setReturnStatus("E");
                hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                hlsWsRequestsMapper.insert(hlsWsRequests);
                return responseData;
            }
            //判断还款方式是否为空，为空直接返回
            if (repayMent.getRepayType()==null){
                responseData.setCode("400");
                responseData.setMessage("还款方式为空");
                hlsWsRequests.setReturnStatus("E");
                hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                hlsWsRequestsMapper.insert(hlsWsRequests);
                return responseData;
            }
            //判断请求号是否为空，为空直接返回
            if (repayMent.getRequestNo()==null){
                responseData.setCode("400");
                responseData.setMessage("请求号为空");
                hlsWsRequests.setReturnStatus("E");
                hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                hlsWsRequestsMapper.insert(hlsWsRequests);
                return responseData;
            }
            List<TermRepayDetailApplyDTO> termRepayDetailApplyDTOList = repayMent.getTermRepayDetailApplyDTOList();
            //判断期次还款信息是否为空，为空直接返回
            if (termRepayDetailApplyDTOList==null){
                responseData.setCode("400");
                responseData.setMessage("期次还款信息为空");
                hlsWsRequests.setReturnStatus("E");
                hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                hlsWsRequestsMapper.insert(hlsWsRequests);
                return responseData;
            }else{
                //循环判断实体信息是否为空
                for (TermRepayDetailApplyDTO termRepayDetailApplyDTO : termRepayDetailApplyDTOList) {
                    //判断期次号是否为空，为空直接返回
                    if (termRepayDetailApplyDTO.getTermNo()==null){
                        responseData.setCode("400");
                        responseData.setMessage("期次号为空");
                        hlsWsRequests.setReturnStatus("E");
                        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                        hlsWsRequestsMapper.insert(hlsWsRequests);
                        return responseData;
                    }
                    //判断实际还款本金是否为空，为空直接返回
                    if (termRepayDetailApplyDTO.getRepayPrincipal()==null){
                        responseData.setCode("400");
                        responseData.setMessage("实际还款本金为空");
                        hlsWsRequests.setReturnStatus("E");
                        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                        hlsWsRequestsMapper.insert(hlsWsRequests);
                        return responseData;
                    }
                    //判断实际还款利息是否为空，为空直接返回
                    if (termRepayDetailApplyDTO.getRepayInterest()==null){
                        responseData.setCode("400");
                        responseData.setMessage("实际还款利息为空");
                        hlsWsRequests.setReturnStatus("E");
                        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                        hlsWsRequestsMapper.insert(hlsWsRequests);
                        return responseData;
                    }
                    //判断实还罚息是否为空，为空直接返回
                    if (termRepayDetailApplyDTO.getRepayPrincipalPenalty()==null){
                        responseData.setCode("400");
                        responseData.setMessage("实还罚息为空");
                        hlsWsRequests.setReturnStatus("E");
                        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                        hlsWsRequestsMapper.insert(hlsWsRequests);
                        return responseData;
                    }
                    //判断实际还款总金额是否为空，为空直接返回
                    if (termRepayDetailApplyDTO.getRepayAmount()==null){
                        responseData.setCode("400");
                        responseData.setMessage("实际还款总金额为空");
                        hlsWsRequests.setReturnStatus("E");
                        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                        hlsWsRequestsMapper.insert(hlsWsRequests);
                        return responseData;
                    }
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
                }
            }
        }

        //保存数据到事务表，待完成


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
        // 读取请求体
        try {
            String jsonBody = request.getInputStream().toString();
            hlsWsRequests.setRequestJson(jsonBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ResponseData responseData = new ResponseData();

        //        判断传入参数是否为空，为空直接返回
        if (compensatoryTrialCalculation==null){
            responseData.setCode("400");
            responseData.setMessage("请求参数为空");
            hlsWsRequests.setReturnStatus("E");
            hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
            hlsWsRequestsMapper.insert(hlsWsRequests);
            return responseData;
        }else{
            String orderNo = compensatoryTrialCalculation.getOrderNo();
            Integer termNo = compensatoryTrialCalculation.getTermNo();
            //        判断期次是否为空，为空直接返回
            if (termNo==null){
                responseData.setCode("400");
                responseData.setMessage("期次为空");
                hlsWsRequests.setReturnStatus("E");
                hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                hlsWsRequestsMapper.insert(hlsWsRequests);
                return responseData;
            }
            //        判断订单编号是否为空，为空直接返回
            if (orderNo==null){
                responseData.setCode("400");
                responseData.setMessage("订单编号为空");
                hlsWsRequests.setReturnStatus("E");
                hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                hlsWsRequestsMapper.insert(hlsWsRequests);
                return responseData;
            }
//            计算本金、利息、罚息、应付金额
            CompensatoryTrialCalculationDTO compensatoryTrialCalculation1 = prjProjectMapper.selectCTCByOrderNo(compensatoryTrialCalculation);

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
        // 读取请求体
        try {
            String jsonBody = request.getInputStream().toString();
            hlsWsRequests.setRequestJson(jsonBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ResponseData responseData = new ResponseData();

        //        判断传入参数是否为空，为空直接返回
        if (claimsSubrogationDTO==null){
            responseData.setCode("400");
            responseData.setMessage("请求参数为空");
            hlsWsRequests.setReturnStatus("E");
            hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
            hlsWsRequestsMapper.insert(hlsWsRequests);
            return responseData;
        }else{
            //        判断期次是否为空，为空直接返回
            if (claimsSubrogationDTO.getTermNo()==null){
                responseData.setCode("400");
                responseData.setMessage("请求参数为空");
                hlsWsRequests.setReturnStatus("E");
                hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                hlsWsRequestsMapper.insert(hlsWsRequests);
                return responseData;
            }
            //        判断请求号是否为空，为空直接返回
            if (claimsSubrogationDTO.getRequestNo()==null){
                responseData.setCode("400");
                responseData.setMessage("请求参数为空");
                hlsWsRequests.setReturnStatus("E");
                hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                hlsWsRequestsMapper.insert(hlsWsRequests);
                return responseData;
            }
            //        判断代偿金额是否为空，为空直接返回
            if (claimsSubrogationDTO.getSubstituteAmount()==null){
                responseData.setCode("400");
                responseData.setMessage("请求参数为空");
                hlsWsRequests.setReturnStatus("E");
                hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                hlsWsRequestsMapper.insert(hlsWsRequests);
                return responseData;
            }
            //        判断订单编号是否为空，为空直接返回
            if (claimsSubrogationDTO.getOrderNo()==null){
                responseData.setCode("400");
                responseData.setMessage("请求参数为空");
                hlsWsRequests.setReturnStatus("E");
                hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
                hlsWsRequestsMapper.insert(hlsWsRequests);
                return responseData;
            }

            //保存代偿金额

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
        // 读取请求体
        try {
            String jsonBody = request.getInputStream().toString();
            hlsWsRequests.setRequestJson(jsonBody);
        } catch (IOException e) {
            e.printStackTrace();
        }


        ResponseData responseData = new ResponseData();

        //判断所传参数是否为空,如果为空直接返回
        if (advancesSettleComputeDTO==null){
            responseData.setCode("400");
            responseData.setMessage("请求参数为空");
            hlsWsRequests.setReturnStatus("E");
            hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
            hlsWsRequestsMapper.insert(hlsWsRequests);
            return responseData;
        }else{

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
        // 读取请求体
        try {
            String jsonBody = request.getInputStream().toString();
            hlsWsRequests.setRequestJson(jsonBody);
        } catch (IOException e) {
            e.printStackTrace();
        }


        ResponseData responseData = new ResponseData();

        //判断所传参数是否为空,如果为空直接返回
        if (advancesSettleComputeDTO==null){
            responseData.setCode("400");
            responseData.setMessage("请求参数为空");
            hlsWsRequests.setReturnStatus("E");
            hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
            hlsWsRequestsMapper.insert(hlsWsRequests);
            return responseData;
        }else{
//            保存还款金额


            //            设置返回状态
            responseData.setCode("200");
            responseData.setMessage("还款成功");
            hlsWsRequests.setReturnStatus("S");
            hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
            hlsWsRequestsMapper.insert(hlsWsRequests);
            return responseData;
        }
    }
}