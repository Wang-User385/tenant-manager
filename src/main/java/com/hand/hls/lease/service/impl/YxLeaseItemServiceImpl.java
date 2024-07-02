package com.hand.hls.lease.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.mail.dto.SysMessageEmailRule;
import com.hand.hap.mail.dto.SysMessageEmailRuleLn;
import com.hand.hap.mail.mapper.SysMessageEmailRuleLnMapper;
import com.hand.hap.mail.mapper.SysMessageEmailRuleMapper;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.fnd.service.IInterfaceErrorMsgService;
import com.hand.hls.lease.dto.YxLeaseItemClassify;
import com.hand.hls.lease.dto.YxLeaseItemClassifyLog;
import com.hand.hls.lease.dto.YxLeaseItemManuLog;
import com.hand.hls.lease.dto.YxLeaseItemManufacturer;
import com.hand.hls.lease.mapper.YxLeaseItemClassifyLogMapper;
import com.hand.hls.lease.mapper.YxLeaseItemClassifyMapper;
import com.hand.hls.lease.mapper.YxLeaseItemManuLogMapper;
import com.hand.hls.lease.mapper.YxLeaseItemManufacturerMapper;
import com.hand.hls.lease.service.YxLeaseItemService;
/*import com.hand.hls.org.dto.*;*/
/*import com.hand.hls.org.mapper.*;*/
import com.hand.hls.prj.dto.HlsBpMaster;
import com.hand.hls.prj.dto.HlsBpMasterRole;
import com.hand.hls.prj.mapper.HlsBpMasterMapper;
import com.hand.hls.prj.mapper.HlsBpMasterRoleMapper;
import com.hand.hls.sys.mapper.SysUserAllocationMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * OrgSignController class
 *
 * @author zengyuxuan
 * @date 2020/12/14
 */
@Service
@Transactional(
        rollbackFor = {Exception.class}
)
public class YxLeaseItemServiceImpl implements YxLeaseItemService {
    private static Logger logger = LoggerFactory.getLogger(YxLeaseItemServiceImpl.class);

    private static final Long READ_LINE = 0L;
    private static final String PARAM_NOT_FOUND = "参数未找到";
    private static final String SHEET = "sheet1";
    private static final String YX_LEASE_ITEM_CLASSIFY = "YX_LEASE_ITEM_CLASSIFY";
    private static final String YX_LEASE_ITEM_MANUFACTURER = "YX_LEASE_ITEM_MANUFACTURER";

    @Autowired
    private YxLeaseItemClassifyMapper yxLeaseItemClassifyMapper;

    @Autowired
    private YxLeaseItemManufacturerMapper yxLeaseItemManufacturerMapper;

    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;

    @Autowired
    private IInterfaceErrorMsgService interfaceErrorMsgService;

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Autowired
    private HlsBpMasterMapper hlsBpMasterMapper;

    @Autowired
    private HlsBpMasterRoleMapper hlsBpMasterRoleMapper;

    @Autowired
    private YxLeaseItemClassifyLogMapper yxLeaseItemClassifyLogMapper;

    @Autowired
    private YxLeaseItemManuLogMapper yxLeaseItemManuLogMapper;

    @Autowired
    private SysUserAllocationMapper userAllocationMapper;
    @Autowired
    private SysMessageEmailRuleMapper messageEmailRuleMapper;
    @Autowired
    private SysMessageEmailRuleLnMapper messageEmailRuleLnMapper;

    @Override
    public List<YxLeaseItemClassify> yxLeaseItemClassifyTreeQuery(YxLeaseItemClassify yxLeaseItemClassify) {
        return yxLeaseItemClassifyMapper.yxLeaseItemClassifyTreeQuery(yxLeaseItemClassify);
    }

    @Override
    public List<YxLeaseItemClassify> yxLeaseItemClassifyForLov(YxLeaseItemClassify yxLeaseItemClassify) {
        return yxLeaseItemClassifyMapper.yxLeaseItemClassifyForLov(yxLeaseItemClassify);
    }


    /**
     * 查询租赁物产品可比价格
     *
     * @return
     */
    @Override
    public List<Map> queryComparePrice(IRequest iRequest, Map params) {
        JSONObject paramJson = JSONObject.parseObject(params.get("_request_data").toString()).getJSONObject("parameter");
        Long classifyId = paramJson.getLong("classifyId");
        return yxLeaseItemClassifyMapper.queryComparePrice(classifyId);
    }

    @Override
    public void leaseItemExcelBatchImport(IRequest iRequest, Long headerId) throws HlsCusException {
        if (headerId == null) {
            throw new HlsCusException(PARAM_NOT_FOUND);
        }

        //租赁物产品信息
        FndInterfaceLines fndInterfaceLines = new FndInterfaceLines();
        fndInterfaceLines.setHeaderId(headerId);
        fndInterfaceLines.setReadLine(READ_LINE);
        fndInterfaceLines.setSheetName(SHEET);
        List<FndInterfaceLines> leaseItems = fndInterfaceLinesMapper.fndInterfaceLinesDetailQuery(fndInterfaceLines);

        //租赁物产品信息不能为空
        if(CollectionUtils.isEmpty(leaseItems)){
            throw new HlsCusException(new StringBuffer(SHEET).append("不能为空！").toString());
        }

        //校验导入数据与系统中数据不能重复
        for(int i = 0;i < leaseItems.size();i++){
            try{
                if(StringUtils.isEmpty(leaseItems.get(i).getAttributes_1())){
                    throw new HlsCusException(new StringBuffer("第").append(i+1).append("行标签必填，请检查！").toString());
                }
                if(StringUtils.isEmpty(leaseItems.get(i).getAttributes_2())){
                    throw new HlsCusException(new StringBuffer("第").append(i+1).append("行属性必填，请检查！").toString());
                }
                if(StringUtils.isEmpty(leaseItems.get(i).getAttributes_3())){
                    throw new HlsCusException(new StringBuffer("第").append(i+1).append("行属性值必填，请检查！").toString());
                }
                for(int j = 0;j < leaseItems.size();j++){
                    if(i!=j&&leaseItems.get(i).getAttributes_1() == leaseItems.get(j).getAttributes_1()&&leaseItems.get(i).getAttributes_2() == leaseItems.get(j).getAttributes_2()&&leaseItems.get(i).getAttributes_3() == leaseItems.get(j).getAttributes_3()&&leaseItems.get(i).getAttributes_5() == leaseItems.get(j).getAttributes_5()){
                        throw new HlsCusException(new StringBuffer("第").append(i+1).append("行租赁物产品重复，请检查！").toString());
                    }
                }
                YxLeaseItemClassify yxLeaseItemClassify = new YxLeaseItemClassify();
                Map labelMap = new HashMap();
                labelMap.put("code","LEASE_ITEM_LABEL");
                labelMap.put("codeName",leaseItems.get(i).getAttributes_1());
                List<Map> labelMaps = yxLeaseItemClassifyMapper.querySysCodeByValue(labelMap);
                if(labelMaps.size() == 0L){
                    throw new HlsCusException(new StringBuffer("第").append(i+1).append("行未匹配到对应标签，请先在系统代码定义中进行维护！").toString());
                }else if(labelMaps.size() > 1L){
                    throw new HlsCusException(new StringBuffer("第").append(i+1).append("行匹配到多个对应标签，请先在系统代码定义中进行维护！").toString());
                }else{
                    yxLeaseItemClassify.setLabel(labelMaps.get(0).get("codeValue").toString());
                }
                Map attributeMap = new HashMap();
                attributeMap.put("code","LEASE_ITEM_ATTRIBUTE");
                attributeMap.put("codeName",leaseItems.get(i).getAttributes_2());
                List<Map> attributeMaps = yxLeaseItemClassifyMapper.querySysCodeByValue(attributeMap);
                if(attributeMaps.size() == 0L){
                    throw new HlsCusException(new StringBuffer("第").append(i+1).append("行未匹配到对应属性，请先在系统代码定义中进行维护！").toString());
                }else if(attributeMaps.size() > 1L){
                    throw new HlsCusException(new StringBuffer("第").append(i+1).append("行匹配到多个对应属性，请先在系统代码定义中进行维护！").toString());
                }else{
                    yxLeaseItemClassify.setAttribute(attributeMaps.get(0).get("codeValue").toString());
                }
                yxLeaseItemClassify.setAttributeValue(leaseItems.get(i).getAttributes_3());
                if(!StringUtils.isEmpty(leaseItems.get(i).getAttributes_5())){
                    YxLeaseItemManufacturer yxLeaseItemManufacturer = new YxLeaseItemManufacturer();
                    yxLeaseItemManufacturer.setManufacturerName(leaseItems.get(i).getAttributes_5());
                    List<YxLeaseItemManufacturer> yxLeaseItemManufacturers = yxLeaseItemManufacturerMapper.select(yxLeaseItemManufacturer);
                    if(yxLeaseItemManufacturers.size() == 0L){
                        throw new HlsCusException(new StringBuffer("第").append(i+1).append("行未匹配到生产厂商，请先在生产厂商定义中进行维护！").toString());
                    }else if(yxLeaseItemManufacturers.size() > 1L){
                        throw new HlsCusException(new StringBuffer("第").append(i+1).append("行匹配到多个生产厂商，请检查！").toString());
                    }
                    yxLeaseItemClassify.setManufacturerId(yxLeaseItemManufacturers.get(0).getManufacturerId());
                }
                List<YxLeaseItemClassify> yxLeaseItemClassifies = yxLeaseItemClassifyMapper.select(yxLeaseItemClassify);
                if(yxLeaseItemClassifies.size() > 0L){
                    throw new HlsCusException(new StringBuffer("第").append(i+1).append("行租赁物产品与系统中租赁物产品重复，请检查！").toString());
                }
                Map params = new HashMap();
                String classifyCode = fndCodingRuleValuesService.getCodeRuleValue(iRequest, "LEASE_ITEM_CLASSIFY", "LEASE_ITEM_CLASSIFY", "LEASE_ITEM_CLASSIFY", params);
                if(!StringUtils.isEmpty(leaseItems.get(i).getAttributes_4())){
                    Map map = new HashMap();
                    map.put("attributeValue",leaseItems.get(i).getAttributes_4());
                    map.put("manufacturerIdN",leaseItems.get(i).getAttributes_5());
                    List<YxLeaseItemClassify> yxLeaseItemClassifyList = yxLeaseItemClassifyMapper.yxLeaseItemClassifyQueryForImport(map);
                    if(yxLeaseItemClassifyList.size() == 0L){
                        throw new HlsCusException(new StringBuffer("第").append(i+1).append("行未匹配到关联上级，请检查！").toString());
                    }else if(yxLeaseItemClassifyList.size() > 1L){
                        throw new HlsCusException(new StringBuffer("第").append(i+1).append("行匹配到产品库中多行数据，请检查！").toString());
                    }
                    yxLeaseItemClassify.setSuperiorClassifyId(yxLeaseItemClassifyList.get(0).getClassifyId());
                }
                yxLeaseItemClassify.setAdvancedEquipmentFlag("N");
                yxLeaseItemClassify.setClassifyCode(classifyCode);
                yxLeaseItemClassify.setEnabledFlag("Y");
                yxLeaseItemClassify.setCreatedBy(iRequest.getUserId());
                yxLeaseItemClassify.setCreationDate(new Date());
                yxLeaseItemClassify.setLastUpdatedBy(iRequest.getUserId());
                yxLeaseItemClassify.setLastUpdateDate(new Date());
                yxLeaseItemClassifyMapper.insert(yxLeaseItemClassify);
            }catch (HlsCusException e){
                logger.error("project import error:{}",e);
                interfaceErrorMsgService.insertInterfaceErrorMessage(iRequest,headerId,YX_LEASE_ITEM_CLASSIFY,e.getMessage());
                continue;
            }
        }
    }

    @Override
    public void manufacturerExcelBatchImport(IRequest iRequest, Long headerId) throws HlsCusException {
        if (headerId == null) {
            throw new HlsCusException(PARAM_NOT_FOUND);
        }

        //租赁物产品信息
        FndInterfaceLines fndInterfaceLines = new FndInterfaceLines();
        fndInterfaceLines.setHeaderId(headerId);
        fndInterfaceLines.setReadLine(READ_LINE);
        fndInterfaceLines.setSheetName(SHEET);
        List<FndInterfaceLines> manufacturers = fndInterfaceLinesMapper.fndInterfaceLinesDetailQuery(fndInterfaceLines);

        //租赁物产品信息不能为空
        if(CollectionUtils.isEmpty(manufacturers)){
            throw new HlsCusException(new StringBuffer(SHEET).append("不能为空！").toString());
        }

        //校验导入数据与系统中数据不能重复
        for(int i = 0;i < manufacturers.size();i++){
            try{
                if(StringUtils.isEmpty(manufacturers.get(i).getAttributes_1())){
                    throw new HlsCusException(new StringBuffer("第").append(i+1).append("行合作方必填，请检查！").toString());
                }
                if(StringUtils.isEmpty(manufacturers.get(i).getAttributes_2())){
                    throw new HlsCusException(new StringBuffer("第").append(i+1).append("行生产厂商必填，请检查！").toString());
                }
                for(int j = 0;j < manufacturers.size();j++){
                    if(i!=j&&manufacturers.get(i).getAttributes_1() == manufacturers.get(j).getAttributes_1()&&manufacturers.get(i).getAttributes_2() == manufacturers.get(j).getAttributes_2()){
                        throw new HlsCusException(new StringBuffer("第").append(i+1).append("行合作方重复，请检查！").toString());
                    }
                }
                /*HlsBpMaster hlsBpMaster = new HlsBpMaster();
                hlsBpMaster.setBpName(manufacturers.get(i).getAttributes_1());
                List<HlsBpMaster> hlsBpMasters = hlsBpMasterMapper.select(hlsBpMaster);*/
                HlsCusBpMaster hlsBpMaster = new HlsCusBpMaster();
                hlsBpMaster.setBpName(manufacturers.get(i).getAttributes_1());
                List<HlsCusBpMaster> hlsBpMasters = hlsBpMasterMapper.select(hlsBpMaster);
                if(hlsBpMasters.size() == 0L){
                    throw new HlsCusException(new StringBuffer("第").append(i+1).append("行未匹配到商业伙伴，请检查！").toString());
                }else if(hlsBpMasters.size() > 1L){
                    throw new HlsCusException(new StringBuffer("第").append(i+1).append("行匹配到多个商业伙伴，请检查！").toString());
                }else{
                    Map map = new HashMap();
                    map.put("bpId",hlsBpMasters.get(0).getBpId());
                    List<HlsBpMasterRole> hlsBpMasterRoles = hlsBpMasterRoleMapper.query2(map);
                    long count = hlsBpMasterRoles.stream().filter(h -> h.getBpCategory().equals("PARTNER")||h.getBpCategory().equals("MANUFACTURER")).count();
                    if(count == 0L){
                        throw new HlsCusException(new StringBuffer("第").append(i+1).append("行未匹配到商业伙伴，请检查！").toString());
                    }
                }
                YxLeaseItemManufacturer yxLeaseItemManufacturer = new YxLeaseItemManufacturer();
                yxLeaseItemManufacturer.setBpId(hlsBpMasters.get(0).getBpId());
                yxLeaseItemManufacturer.setManufacturerName(manufacturers.get(i).getAttributes_2());
                List<YxLeaseItemManufacturer> yxLeaseItemManufacturers = yxLeaseItemManufacturerMapper.select(yxLeaseItemManufacturer);
                if(yxLeaseItemManufacturers.size() > 0L){
                    throw new HlsCusException(new StringBuffer("第").append(i+1).append("行合作方重复，请检查！").toString());
                }
                yxLeaseItemManufacturer.setEnabledFlag("Y");
                yxLeaseItemManufacturer.setCreatedBy(iRequest.getUserId());
                yxLeaseItemManufacturer.setCreationDate(new Date());
                yxLeaseItemManufacturer.setLastUpdatedBy(iRequest.getUserId());
                yxLeaseItemManufacturer.setLastUpdateDate(new Date());
                yxLeaseItemManufacturerMapper.insert(yxLeaseItemManufacturer);
            }catch (HlsCusException e){
                logger.error("project import error:{}",e);
                interfaceErrorMsgService.insertInterfaceErrorMessage(iRequest,headerId,YX_LEASE_ITEM_MANUFACTURER,e.getMessage());
                continue;
            }
        }
    }

    @Override
    public void leaseItemLogCreate(IRequest iRequest, Map params) {
        JSONObject paramJson = JSONObject.parseObject(params.get("_request_data").toString()).getJSONObject("parameter");
        String logType = paramJson.getString("logType");
        JSONArray changeList = paramJson.getJSONArray("changeList");
        if(StringUtils.equals(logType,"classify")){
            for(int i=0;i < changeList.size();i++){
                YxLeaseItemClassifyLog yxLeaseItemClassifyLog = JSONObject.parseObject(changeList.getJSONObject(i).toJSONString(),YxLeaseItemClassifyLog.class);
                yxLeaseItemClassifyLog.setLogCreationDate(new Date());
                yxLeaseItemClassifyLog.setLogCreatedBy(11139L);
                yxLeaseItemClassifyLog.setLogLastUpdateDate(new Date());
                yxLeaseItemClassifyLog.setLogLastUpdatedBy(11139L);
                yxLeaseItemClassifyLogMapper.insert(yxLeaseItemClassifyLog);
            }
        }else if(StringUtils.equals(logType,"manufacturer")){
            for(int i=0;i < changeList.size();i++){
                YxLeaseItemManuLog yxLeaseItemManuLog = JSONObject.parseObject(changeList.getJSONObject(i).toJSONString(),YxLeaseItemManuLog.class);
                yxLeaseItemManuLog.setLogCreationDate(new Date());
                yxLeaseItemManuLog.setLogCreatedBy(11139L);
                yxLeaseItemManuLog.setLogLastUpdateDate(new Date());
                yxLeaseItemManuLog.setLogLastUpdatedBy(11139L);
                yxLeaseItemManuLogMapper.insert(yxLeaseItemManuLog);
            }
        }
    }

    @Override
    public String leaseItemCheckBeforeSubmit(IRequest iRequest, Map params) {
        JSONObject paramJson = JSONObject.parseObject(params.get("_request_data").toString()).getJSONObject("parameter");
        JSONArray checkData = paramJson.getJSONArray("checkData");
        for(int i=0;i < checkData.size();i++){
            Map map = new HashMap();
            map.put("classifyId",checkData.getJSONObject(i).get("classify_id"));
            map.put("label",checkData.getJSONObject(i).get("label"));
            map.put("attribute",checkData.getJSONObject(i).get("attribute"));
            map.put("attributeValue",checkData.getJSONObject(i).get("attribute_value"));
            map.put("manufacturerId",checkData.getJSONObject(i).get("manufacturer_id"));
            map.put("superiorClassifyId",checkData.getJSONObject(i).get("superior_classify_id"));
            Long count = yxLeaseItemClassifyMapper.queryRepeatData(map);
            if(count > 0L){
                if(StringUtils.isEmpty(checkData.getJSONObject(i).getString("classify_code"))){
                    return "NEW";
                }
                return checkData.getJSONObject(i).getString("classify_code");
            }
        }
        return "NONE";
    }

    @Override
    public void updateAdvancedEquipmentFlag(Long classifyId, String advancedEquipmentFlag) {
        yxLeaseItemClassifyMapper.updateAdvancedEquipmentFlag(classifyId,advancedEquipmentFlag);
        //获取子集
        Example example = new Example(YxLeaseItemClassify.class);
        example.createCriteria().andEqualTo("superiorClassifyId",classifyId);
        List<YxLeaseItemClassify> yxLeaseItemClassifies = yxLeaseItemClassifyMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(yxLeaseItemClassifies)) {
            for (YxLeaseItemClassify yxLeaseItemClassify : yxLeaseItemClassifies) {
                updateAdvancedEquipmentFlag(yxLeaseItemClassify.getClassifyId(),advancedEquipmentFlag);
            }
        }
    }

    @Override
    public boolean updateLeaseItemCheck(IRequest iRequest) throws HlsCusException {
        SysMessageEmailRule sysMessageEmailRule = messageEmailRuleMapper.queryForSendEmailByRuleCode("LEASE_PRODUCT_FILL_IN");
        if (Objects.isNull(sysMessageEmailRule)) {
            throw new HlsCusException("邮件调用规则定义未配置，请检查");
        }
        List<SysMessageEmailRuleLn> messageEmailRuleLns = messageEmailRuleLnMapper.queryData(sysMessageEmailRule.getRuleId());
        if (CollectionUtil.isEmpty(messageEmailRuleLns)) {
            throw new HlsCusException("邮件收件人未配置，请检查");
        }
        List<Long> userIds = messageEmailRuleLns.stream().map(SysMessageEmailRuleLn::getUserId).collect(Collectors.toList());
        if (userIds.contains(iRequest.getUserId())) {
            return true;
        }
        return false;
    }
}
