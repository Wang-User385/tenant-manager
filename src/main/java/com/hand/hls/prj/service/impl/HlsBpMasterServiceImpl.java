package com.hand.hls.prj.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.BpCategoryInfoLov;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.csh.mapper.HlsCusCshTransactionRefundMapper;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.HlsBpMasterMapper;
import com.hand.hls.prj.mapper.ZxBpOrgbaseMapper;
import com.hand.hls.prj.service.HlsBpMasterService;
import com.hand.hls.sys.service.IFndCompanyService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HlsBpMasterServiceImpl extends BaseServiceImpl<HlsBpMaster> implements HlsBpMasterService {

    @Autowired
    private HlsBpMasterMapper hlsBpMasterMapper;
    @Autowired
    private HlsBpMasterService hlsBpMasterService;
    @Autowired
    private IFndCompanyService fndCompanyService;
    @Autowired
    private HlsCusBpMasterService hlsCusBpMasterService;

    @Autowired
    private ZxBpOrgbaseMapper zxBpOrgbaseMapper;
    @Autowired
    private HlsCusCshTransactionRefundMapper transactionRefundMapper;

    /**
     * 二期功能：保证金的付款对象查询
     *
     * @param requestCtx
     * @param transactionIds
     * @param refundId
     * @param pagenum
     * @param pagesize
     * @return
     */
    @Override
    public List<HlsBpMaster> queryPaymentBpMasterLov(IRequest requestCtx, String transactionIds, String refundId, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        List<String> transactionIdList = new ArrayList<>();
        List<Map> transactionIdMap = new ArrayList<>();

        if(!transactionIds.isEmpty()){
            String[] transactionId  = transactionIds.split(",");
            transactionIdList = Arrays.asList(transactionId);
        }else{
            transactionIdMap  = transactionRefundMapper.queryRefundTransactionId(refundId);
            for(int i = 0; i < transactionIdMap.size(); i++){
                transactionIdList.add(transactionIdMap.get(i).get("transactionId").toString());
            }
        }

        List<HlsBpMaster> intersectList = new ArrayList<>();
        for (int i = 0; i < transactionIdList.size(); i++) {
            if(i == 0){
                intersectList = hlsBpMasterMapper.queryPaymentBpMaster(transactionIdList.get(i));
            }else{
                List<HlsBpMaster> paymentBpList = hlsBpMasterMapper.queryPaymentBpMaster(transactionIdList.get(i));
                intersectList = intersectList.stream().filter(id -> paymentBpList.contains(id)).collect(Collectors.toList());
            }
        }
        return intersectList;
    }

    @Override
    public List<HlsBpMaster> queryBpMasterLov(IRequest requestCtx, HlsBpMaster hlsBpMaster, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return hlsBpMasterMapper.queryBpMasterLov(hlsBpMaster);
    }

    @Override
    public List<HlsCusBpMaster> validBpNameQuery(IRequest requestCtx, String bpName) {
//        PageHelper.startPage(pagenum, pagesize);
        return hlsBpMasterMapper.validBpName(bpName);
    }

    @Override
    public List<HlsBpMaster> queryBpMasterVenderLov(IRequest var1, HlsBpMaster hlsBpMaster, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return this.hlsBpMasterMapper.queryBpMasterVenderLov(hlsBpMaster);
    }

    @Override
    public List<HlsBpMaster> queryBpMasterVenderLovNew(IRequest var1, HlsBpMaster hlsBpMaster, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return this.hlsBpMasterMapper.queryBpMasterVenderLovNew(hlsBpMaster);
    }

    @Override
    public List<HlsBpMaster> queryBpMasterLov2(IRequest requestCtx, HlsBpMaster hlsBpMaster, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return hlsBpMasterMapper.queryBpMasterLov2(hlsBpMaster);
    }

    @Override
    public List<HlsBpMaster> bpMasterHomeQuery(IRequest requestCtx, HlsBpMaster hlsBpMaster, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return hlsBpMasterMapper.bpMasterHomeQuery(hlsBpMaster);
    }

    @Override
    public List<HlsBpMaster> selectForLovIf(HlsBpMaster dto, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return hlsBpMasterMapper.selectForLovIf(dto);
    }

    @Override
    public List<HlsBpMaster> lonCreditBpLovQuery(IRequest requestCtx, HlsBpMaster hlsBpMaster, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return hlsBpMasterMapper.lonCreditBpLovQuery(hlsBpMaster);
    }

    /**
     * 查询厂商/合作方lov框
     *
     * @param requestCtx
     * @param bpManufacturerInfoLov
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<BpManufacturerInfoLov> bpManufacturerInfoQuery(IRequest requestCtx, BpManufacturerInfoLov bpManufacturerInfoLov, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return this.hlsBpMasterMapper.bpManufacturerInfoQuery(bpManufacturerInfoLov);
    }

    /**
     * 查询主机厂/合作方lov框
     *
     * @param requestCtx
     * @param bpVenderInfoLov
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<BpVenderInfoLov> bpVenderInfoQuery(IRequest requestCtx, BpVenderInfoLov bpVenderInfoLov, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return this.hlsBpMasterMapper.bpVenderInfoQuery(bpVenderInfoLov);
    }

    @Override
    public List<BpDealerInfoLov> bpDealerInfoQuery(IRequest requestCtx, BpDealerInfoLov bpDealerInfoLov, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return this.hlsBpMasterMapper.bpDealerInfoQuery(bpDealerInfoLov);
    }

    @Override
    public Boolean validRegNumber(IRequest iRequest, Long bpId, String regNumber) {
        Example example = new Example(HlsCusBpMaster.class);
        example.createCriteria().andEqualTo("registerCertNum", regNumber);
        List<HlsCusBpMaster> hlsCusBpMaster = hlsBpMasterMapper.selectByExample(example);
        if (hlsCusBpMaster.size() > 0) {
            if (bpId == -1L) {
                return false;
            } else {
                HlsCusBpMaster zxBpOrgbase = hlsCusBpMaster.get(0);
                if (zxBpOrgbase.getBpId() != bpId.intValue()) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public Boolean validRegCertNumber(IRequest iRequest, Long bpId, String regNumber) {
        Example example = new Example(HlsCusBpMaster.class);
        example.createCriteria().andEqualTo("registerCertNum", regNumber);
        List<HlsCusBpMaster> hlsCusBpMaster = hlsBpMasterMapper.selectByExample(example);
        if (hlsCusBpMaster.size() > 1)return false;
        return hlsCusBpMaster.size() == 0 || hlsCusBpMaster.get(0).getBpId().longValue() == bpId.longValue();
    }

    @Override
    public Boolean validRegNumber1(IRequest var1, Long bpId, String regNumber) {
        Example example = new Example(ZxBpOrgbase.class);
        example.createCriteria().andEqualTo("regno", regNumber);
        List<ZxBpOrgbase> zxBpOrgbases = this.zxBpOrgbaseMapper.selectByExample(example);
        if (zxBpOrgbases.size() > 0) {
            if (bpId == -1L) {
                return false;
            }

            ZxBpOrgbase zxBpOrgbase = zxBpOrgbases.get(0);
            if (zxBpOrgbase.getBpId() != bpId.intValue()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Boolean validIdCardNo(IRequest iRequest, Long bpId, String idCardNo) {
        Example example = new Example(HlsCusBpMaster.class);
        example.createCriteria().andEqualTo("idCardNo", idCardNo);
        List<HlsCusBpMaster> hlsCusBpMaster = hlsBpMasterMapper.selectByExample(example);
        if (hlsCusBpMaster.size() > 0) {
            if (bpId == -1L) {
                return false;
            } else {
                HlsCusBpMaster zxBpOrgbase = hlsCusBpMaster.get(0);
                if (zxBpOrgbase.getBpId() != bpId.intValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Boolean validBpName(IRequest iRequest, Long bpId, String bpName) {
        bpName = bpName.replace(" ","");
        iRequest.setAttribute("wflRuleControlFlag", "Y");
        List<HlsCusBpMaster> hlsCusBpMasterList = hlsBpMasterService.validBpNameQuery(iRequest, bpName);
        if(hlsCusBpMasterList.size() > 0){
            if (bpId == -1L) {
                return false;
            } else {
                HlsCusBpMaster zxBpOrgbase = hlsCusBpMasterList.get(0);
                if (zxBpOrgbase.getBpId() != bpId.intValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Boolean validBpName2(IRequest var1, Long bpId, String bpName) {
        Example example = new Example(HlsBpMaster.class);
        example.createCriteria().andEqualTo("bpName", bpName);
        List<HlsBpMaster> hlsBpMasters = this.hlsBpMasterMapper.selectByExample(example);
        if (hlsBpMasters.size() > 0) {
            if (bpId == -1L) {
                return false;
            }

            for (HlsBpMaster hlsBpMaster: hlsBpMasters) {
                if(hlsBpMaster.getBpId() != bpId.intValue()){
                    return false;
                }
            }
        }

        return true;
    }

    public List<BpCategoryInfoLov> bpTypeInfoQuery(IRequest requestCtx, BpCategoryInfoLov category, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return this.hlsBpMasterMapper.bpTypeInfoQuery(category);
    }

    @Override
    public HlsBpMaster queryByBpId(long bpId) {
        return hlsBpMasterMapper.queryByBpId(bpId);
    }

    @Override
    public String getAuthorityString(IRequest iRequest) {
        FndCompany fndCompany = new FndCompany();
        fndCompany.setCompanyId(iRequest.getCompanyId());
        fndCompany = fndCompanyService.selectByPrimaryKey(iRequest, fndCompany);
        String employeeCode = iRequest.getEmployeeCode();
        String positionCode = iRequest.getAttribute("positionCode") == null ? "" : (String) iRequest.getAttribute("positionCode");
        String unitCode = iRequest.getAttribute("unitCode") == null ? "" : (String) iRequest.getAttribute("unitCode");
        String authorityRuleString = '"' + fndCompany.getCompanyCode() + '"' + "." + '"' + unitCode + '"' + "." + '"' + '"' + "." + '"' + '"' + "." + '"' + '"' + "." + '"' + positionCode + '"' + "." + '"' + employeeCode + '"';
        return authorityRuleString;
    }
    @Override
    public List<HlsBpMaster> bpManufacturerPartnerLovQuery() {
        return hlsBpMasterMapper.bpManufacturerPartnerLovQuery();
    }




}
