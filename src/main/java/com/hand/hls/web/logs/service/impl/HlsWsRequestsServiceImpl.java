package com.hand.hls.web.logs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.app.utils.generalUtils.AppConstantUtils;
import com.hand.hls.app.utils.generalUtils.AppResponseData;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.sys.dto.SysUserAllocation;
import com.hand.hls.sys.dto.SysUserAuthorityRule;
import com.hand.hls.sys.mapper.SysUserAllocationMapper;
import com.hand.hls.sys.mapper.SysUserAuthorityRuleMapper;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import com.hand.hls.web.logs.mapper.HlsWsRequestsMapper;
import com.hand.hls.web.logs.service.IHlsWsRequestsService;
import net.sf.json.JSONArray;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

/**
 * @author liao
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsWsRequestsServiceImpl extends BaseServiceImpl<HlsWsRequests> implements IHlsWsRequestsService{
    @Autowired
    private HlsWsRequestsMapper hlsWsRequestsMapper;
    @Autowired
    private SysUserAuthorityRuleMapper sysUserAuthorityRuleMapper;
    @Autowired
    private SysUserAllocationMapper sysUserAllocationMapper;


    private static Long userId = -1L;
    private static String ALLOCATION_ID = "allocationId";
    private static String COMPANY_ID = "companyId";

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public HlsWsRequests interfaceSaveAll(HlsWsRequests dto , HttpServletRequest request,IRequest iRequest)  throws Exception {

        // 修改当前session 数据
        if( dto.getRequestJson() != null){
            String requestJson = dto.getRequestJson();
            JSONObject jsonObject = JSONObject.parseObject(requestJson);
            // 不需要权限时 将此方法注释
            updateRequest(iRequest,jsonObject);
        }


        //预留 功能
        // saveInterfaceAll( dto ,  request);
         //获取 请求地址
        dto.setRequestWsdlUrl(request.getRequestURL().toString());
        /// 请求体   信息保存
        dto = interfaceSave(dto ,iRequest);
        return dto;
    }
     /**
     *权限设置
      * 将app 用户、权限 信息 设置到session中
      *
     */
    private void updateRequest(IRequest iRequest, JSONObject jsonObject) {

        SysUserAuthorityRule sysUserAuthorityRule = jsonObject.toJavaObject(SysUserAuthorityRule.class);
        // 将allocationId 及权限信息 手动设置到 当前请求session 中
        if(jsonObject.getString(ALLOCATION_ID)  != null && jsonObject.getString(COMPANY_ID)  != null){
            List<SysUserAuthorityRule> sysUserAuthorityRules = sysUserAuthorityRuleMapper.queryUserRule(sysUserAuthorityRule);

            List<SysUserAllocation> allocation = sysUserAllocationMapper.selectUserInfo(jsonObject.getLong("allocationId"));

            iRequest.setAttribute("MDC.allocationId",jsonObject.getString("allocationId"));
            iRequest.setAttribute("allocationId",jsonObject.getLong("allocationId"));
            iRequest.setAttribute("authorityRuleFlag","Y");
            iRequest.setAttribute("userRules",sysUserAuthorityRules);
            iRequest.setAttribute("employeeId",allocation.get(0).getEmployeeId());
            iRequest.setCompanyId(jsonObject.getLong("companyId"));
            iRequest.setSubject("APP");
            iRequest.setRoleId(allocation.get(0).getRoleId());
            iRequest.setEmployeeCode(allocation.get(0).getEmployeeCode());
            iRequest.setUserId(allocation.get(0).getUserId());
            // 待办 历史流程 不走权限
            if(jsonObject.getString("functionCode") != null){
                if("WFL".equals(jsonObject.getString("functionCode").toUpperCase())  ){
                    iRequest.setAttribute("wflRuleControlFlag", "Y");
                }
            }
        }


    }

    /***
 * 保留项目
 * 如果需要将 session 、请求头、等一堆 信息 存表记录
 * 可在这里进行 操作 ！
 *
 * */
    private void saveInterfaceAll(HlsWsRequests dto, HttpServletRequest request) {
        String t0 = request.getServerName();

        HttpSession t1 =   request.getSession();
        String t2 = request.getContextPath();
        Cookie[] t3 =  request.getCookies();
        String t4 = request.getServletPath();
        String t5 = request.getRequestURI();
        StringBuffer t6 = request.getRequestURL();



    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public HlsWsRequests interfaceSave(HlsWsRequests dto , IRequest iRequest){
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if(dto.getRecordId()!= null ){
            hlsWsRequests =hlsWsRequestsMapper.selectByPrimaryKey(dto.getRecordId()) ;
            hlsWsRequests.setResponsedDate(new Date());
            hlsWsRequests.setReturnStatus(dto.getReturnStatus());
            hlsWsRequests.setResponseJson(dto.getResponseJson());
            hlsWsRequests.setResponseXml(dto.getResponseXml());
            hlsWsRequests.setLastUpdateDate(new Date());
            if(iRequest.getUserId() != null) {
                hlsWsRequests.setLastUpdatedBy(iRequest.getUserId());
            }else{
                hlsWsRequests.setLastUpdatedBy(userId);
            }
            hlsWsRequestsMapper.updateByPrimaryKey(hlsWsRequests);
        }else {
            BeanUtils.copyProperties(dto, hlsWsRequests);
            if(iRequest.getUserId() != null){
                hlsWsRequests.setUserId(iRequest.getUserId());
                hlsWsRequests.setCreatedBy(iRequest.getUserId());
            }else{
                hlsWsRequests.setUserId(userId);
                hlsWsRequests.setCreatedBy(userId);
            }
            hlsWsRequests.setRequestDate(new Date());
            hlsWsRequests.setCreationDate(new Date());
            hlsWsRequests.setLastUpdateDate(new Date());
            hlsWsRequests.setStatusDate(new Date());
            hlsWsRequests.setStatusCode("NEW");
            hlsWsRequestsMapper.insert(hlsWsRequests);
        }
        return hlsWsRequests;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateResult(ResponseData result, HlsWsRequests hlsWsRequests, IRequest iRequest, String dataType) throws Exception {
        if(result.isSuccess()){
            hlsWsRequests.setReturnStatus("S");
            hlsWsRequests.setResponsedDate(new Date());
            if(result.getRows()!= null  ) {
                if(dataType.equals(AppConstantUtils.Interface.DATA_JSON)) {
                    //JSONArray.fromObject(list).toString()
                    try {
                        hlsWsRequests.setResponseJson(JSONArray.fromObject(result.getRows()).toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    //JSONArray.fromObject(list).toString()
                    hlsWsRequests.setResponseXml(result.getRows().toString());

                }

            }
        }else {
            hlsWsRequests.setReturnStatus("E");
            hlsWsRequests.setResponsedDate(new Date());
            if(result.getMessage()!= null) {
                hlsWsRequests.setResponseJson(result.getMessage());
                hlsWsRequests.setResponsedDate(new Date());
            }
        }
        interfaceSave(hlsWsRequests ,iRequest);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateResult(AppResponseData result, HlsWsRequests hlsWsRequests, IRequest iRequest, String dataType) throws Exception {
        if(result.isSuccess()){
            hlsWsRequests.setReturnStatus("S");
            hlsWsRequests.setResponsedDate(new Date());
            if(result.getRows()!= null  ) {
                if(dataType.equals(AppConstantUtils.Interface.DATA_JSON)) {
                    //JSONArray.fromObject(list).toString()
                    hlsWsRequests.setResponseJson(JSONArray.fromObject(result.getRows()).toString());
                }else {
                    //JSONArray.fromObject(list).toString()
                    hlsWsRequests.setResponseXml(result.getRows().toString());

                }

            }
        }else {
            hlsWsRequests.setReturnStatus("E");
            hlsWsRequests.setResponsedDate(new Date());
            if(result.getMessage()!= null) {
                hlsWsRequests.setResponseJson(result.getMessage());
                hlsWsRequests.setResponsedDate(new Date());
            }
        }
        interfaceSave(hlsWsRequests ,iRequest);
    }

    @Override
    public void updateResultFile(ResponseEntity<byte[]> result, HlsWsRequests hlsWsRequests, IRequest iRequest) throws Exception {
        int statusCodeValue = result.getStatusCodeValue();
        HttpStatus statusCode = result.getStatusCode();
        if(result.getStatusCodeValue() == AppConstantUtils.Interface.STATUS_CODE_200){
            hlsWsRequests.setReturnStatus("S");
            hlsWsRequests.setResponsedDate(new Date());
            hlsWsRequests.setResponseJson("success");

        }else if(result.getStatusCodeValue() == AppConstantUtils.Interface.STATUS_CODE_404) {
                hlsWsRequests.setResponseJson(result.toString());
                hlsWsRequests.setReturnStatus("E");
                hlsWsRequests.setResponsedDate(new Date());
                hlsWsRequests.setResponseJson("404 Not found");
        }else {
            hlsWsRequests.setResponseJson(result.toString());
            hlsWsRequests.setReturnStatus("E");
            hlsWsRequests.setResponsedDate(new Date());
            hlsWsRequests.setResponseJson("下载失败 请联系管理员");
        }
        interfaceSave(hlsWsRequests ,iRequest);
    }
    @Override
    public String getFileInfo(HttpServletRequest request ) throws Exception {
        JSONObject jsonObject = new JSONObject();
         try{
             MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;

             String sourceType = multiRequest.getParameter("sourceType");
             if( sourceType != null){
                 jsonObject.put("sourceType",sourceType);
             }
             String pkValue = multiRequest.getParameter("pkvalue");
             if(pkValue != null){
                  jsonObject.put("pkvalue",pkValue);
             }

             String filename = multiRequest.getParameter("filename");
             if(filename != null ){
                 jsonObject.put("filename", URLDecoder.decode(filename, "UTF-8"));
             }

             return jsonObject.toJSONString();
         }catch (Exception e){
             throw new HlsCusException(e.getMessage());
         }

    }
}