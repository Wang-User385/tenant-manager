package com.hand.hls.ast.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.account.mapper.UserMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.ICodeService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.ast.dto.AssetClassHead;
import com.hand.hls.ast.dto.AssetClassLine;
import com.hand.hls.ast.mapper.AssetClassHeadMapper;
import com.hand.hls.ast.mapper.AssetClassLineMapper;
import com.hand.hls.ast.service.IAssetClassHeadService;
import com.hand.hls.ast.service.IAssetClassLineService;
import com.hand.hls.ast.service.ISendMessageService;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.prj.dto.HlsBpMaster;
import com.hand.hls.prj.mapper.BpMasterEmployeeMapper;
import com.hand.hls.prj.mapper.HlsBpMasterMapper;
import com.hand.hls.prj.service.HlsBpMasterService;
import com.hand.hls.utils.HlsConstantUtil;
import com.hand.hls.wfl.service.IActivitiStartService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.time.YearMonth;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class AssetClassHeadServiceImpl extends BaseServiceImpl<AssetClassHead> implements IAssetClassHeadService {

    private static final Logger logger = LoggerFactory.getLogger(AssetClassHeadServiceImpl.class);

    private static final String SEASON_ONE = "1";
    private static final String SEASON_TWO = "2";
    private static final String SEASON_THREE = "3";
    private static final String SEASON_FOUR = "4";
    private static final String SEASON_ONE_DESCRIPTION = "一";
    private static final String SEASON_TWO_DESCRIPTION = "二";
    private static final String SEASON_THREE_DESCRIPTION = "三";
    private static final String SEASON_FOUR_DESCRIPTION = "四";

    public static final String NORMAL_ONE = "NORMAL_ONE";
    public static final String NORMAL_TWO = "NORMAL_TWO";
    public static final String NORMAL_THREE = "NORMAL_THREE";
    public static final String NORMAL_FOUR = "NORMAL_FOUR";
    public static final String SPECIAL_MENTION_ONE = "SPECIAL_MENTION_ONE";
    public static final String SPECIAL_MENTION_TWO = "SPECIAL_MENTION_TWO";
    public static final String SPECIAL_MENTION_THREE = "SPECIAL_MENTION_THREE";
    public static final String SUBSTANDARD_ONE = "SUBSTANDARD_ONE";
    public static final String SUBSTANDARD_TWO = "SUBSTANDARD_TWO";
    public static final String DOUBTFUL_ONE = "DOUBTFUL_ONE";
    public static final String DOUBTFUL_TWO = "DOUBTFUL_TWO";
    public static final String LOSS = "LOSS";
    public static final String TEMPORARY = "TEMPORARY";
    public static final String PERIOD = "PERIOD";
    public static final String NORMAL = "NORMAL";

    private static final String ASSET_CLASSIFICATION_WORK_FLOW = "ASSET_CLASSIFICATION_WORK_FLOW";

    private static final String ASSET_CLASS_HEAD = "ASSET_CLASS_HEAD";

    private static final String SUBMIT_ERROR_MESSAGE = "该单据不可提交审批，请核对单据状态！";


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;

    @Autowired
    private AssetClassHeadMapper assetClassHeadMapper;

    @Autowired
    private AssetClassLineMapper assetClassLineMapper;

    @Autowired
    private IAssetClassLineService assetClassLineService;

    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private ICodeService iCodeService;
    @Autowired
    private HlsBpMasterService hlsBpMasterService;
    @Autowired
    private HlsBpMasterMapper hlsBpMasterMapper;
    @Autowired
    private BpMasterEmployeeMapper bpMasterEmployeeMapper;
//    @Autowired
//    private IMessageService messageService;
    @Autowired
    private ISendMessageService messageService;



    /**
     * 生成资产分类信息
     *
     * @param iRequest
     */
    public ResponseData createAssetClassInfo(IRequest iRequest) {

        //新增资产分类头表信息
        AssetClassHead assetClassHead = new AssetClassHead();
        Calendar calendar = Calendar.getInstance();
        assetClassHead.setClassYear(String.valueOf(calendar.get(Calendar.YEAR)));
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        if (currentMonth >= 1 && currentMonth <= 3) {
            assetClassHead.setClassSeason(SEASON_ONE);
        } else if (currentMonth >= 4 && currentMonth <= 6) {
            assetClassHead.setClassSeason(SEASON_TWO);
        } else if (currentMonth >= 7 && currentMonth <= 9) {
            assetClassHead.setClassSeason(SEASON_THREE);
        } else if (currentMonth >= 10 && currentMonth <= 12) {
            assetClassHead.setClassSeason(SEASON_FOUR);
        }

        assetClassHead.setApproveStatus(HlsConstantUtil.WorkFlowStatus.NEW);

        if (null == assetClassHeadMapper.queryMaxBatchNumber()) {
            assetClassHead.setBatchNum(1L);
        } else {
            assetClassHead.setBatchNum(assetClassHeadMapper.queryMaxBatchNumber() + 1L);
        }
        assetClassHead = self().insertSelective(iRequest, assetClassHead);

        //新增资产分类行表信息
        //查询出逾期的合同信息
        List<AssetClassLine> assetClassLines = assetClassLineMapper.queryOverdueContractInfo();

        for (AssetClassLine assetClassLine :
                assetClassLines) {
            assetClassLine.set__status(DTOStatus.ADD);
            assetClassLine.setClassHeadId(assetClassHead.getClassHeadId());

            if (assetClassLine.getOverdueDays() == null) {
                assetClassLine.setInitialLevel(NORMAL_THREE);
                assetClassLine.setReviewLevel(NORMAL_THREE);
                assetClassLine.setApproveReviewLevel(NORMAL_THREE);
            }else{
                if (assetClassLine.getOverdueDays() >= 1L && assetClassLine.getOverdueDays() <= 30L) {
                    assetClassLine.setInitialLevel(NORMAL_FOUR);
                    assetClassLine.setReviewLevel(NORMAL_FOUR);
                    assetClassLine.setApproveReviewLevel(NORMAL_FOUR);
                } else if (assetClassLine.getOverdueDays() >= 31L && assetClassLine.getOverdueDays() <= 60L) {
                    assetClassLine.setInitialLevel(SPECIAL_MENTION_ONE);
                    assetClassLine.setReviewLevel(SPECIAL_MENTION_ONE);
                    assetClassLine.setApproveReviewLevel(SPECIAL_MENTION_ONE);
                } else if (assetClassLine.getOverdueDays() >= 61L && assetClassLine.getOverdueDays() <= 90L) {
                    assetClassLine.setInitialLevel(SPECIAL_MENTION_TWO);
                    assetClassLine.setReviewLevel(SPECIAL_MENTION_TWO);
                    assetClassLine.setApproveReviewLevel(SPECIAL_MENTION_TWO);
                } else if (assetClassLine.getOverdueDays() >= 91L && assetClassLine.getOverdueDays() <= 180L) {
                    assetClassLine.setInitialLevel(SPECIAL_MENTION_THREE);
                    assetClassLine.setReviewLevel(SPECIAL_MENTION_THREE);
                    assetClassLine.setApproveReviewLevel(SPECIAL_MENTION_THREE);
                } else if (assetClassLine.getOverdueDays() >= 181L && assetClassLine.getOverdueDays() <= 270L) {
                    assetClassLine.setInitialLevel(SUBSTANDARD_ONE);
                    assetClassLine.setReviewLevel(SUBSTANDARD_ONE);
                    assetClassLine.setApproveReviewLevel(SUBSTANDARD_ONE);
                } else if (assetClassLine.getOverdueDays() >= 271L && assetClassLine.getOverdueDays() <= 360L) {
                    assetClassLine.setInitialLevel(SUBSTANDARD_TWO);
                    assetClassLine.setReviewLevel(SUBSTANDARD_TWO);
                    assetClassLine.setApproveReviewLevel(SUBSTANDARD_TWO);
                } else if (assetClassLine.getOverdueDays() >= 361L && assetClassLine.getOverdueDays() <= 720L) {
                    assetClassLine.setInitialLevel(DOUBTFUL_TWO);
                    assetClassLine.setReviewLevel(DOUBTFUL_TWO);
                    assetClassLine.setApproveReviewLevel(DOUBTFUL_TWO);
                } else if (assetClassLine.getOverdueDays() >= 721L) {
                    assetClassLine.setInitialLevel(LOSS);
                    assetClassLine.setReviewLevel(LOSS);
                    assetClassLine.setApproveReviewLevel(LOSS);
                }
            }


        }
        assetClassLineService.batchUpdate(iRequest, assetClassLines);

        return new ResponseData(true);
    }

    /**
     * 工作流启动过程
     * @param iRequest
     * @param classHead
     */
    public void assetClassWflProcedure(IRequest iRequest, AssetClassHead classHead){
        HlsCusBpMaster hlsBpMaster = new HlsCusBpMaster();
        hlsBpMaster.setBpId(classHead.getBpId());
//        hlsBpMaster = hlsBpMasterService.selectByPrimaryKey(iRequest, hlsBpMaster);
        hlsBpMaster= (HlsCusBpMaster) hlsBpMasterMapper.selectByPrimaryKey(hlsBpMaster);
        List<AssetClassHead> assetClassHeads = new ArrayList<>();
        assetClassHeads.add(classHead);
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(classHead));
        Map<String, Object> map = new HashMap();
        //临时分类，不经过业务经理审批节点
        if(StringUtils.equals(classHead.getDocumentType(),TEMPORARY)){
            map.put("manageProcess","Y");
        }else {
            map.put("manageProcess","N");
        }
        map.put("workFlowType", ASSET_CLASSIFICATION_WORK_FLOW);
        map.put("assetClassHead", jsonObject.toString());
        map.put("documentId", classHead.getClassHeadId());
        map.put("documentCategory", ASSET_CLASS_HEAD);
        // 云上越秀需要的参数
        map.put("documentType", iCodeService.getCodeMeaningByValue(iRequest, "HLS_ASSET_CLASS_HEAD_TYPE", classHead.getDocumentType()));
        // 云上越秀需要的参数
        map.put("documentName", hlsBpMaster.getBpName()+iCodeService.getCodeMeaningByValue(iRequest, "QUARTER", classHead.getClassSeason())+"资产分类");
        map.put("documentNumber", classHead.getBatchNum().toString());
        map.put("manufacturerId", classHead.getBpId());
        activitiStartService.start(iRequest, assetClassHeads, map);
    }


    public ResponseData assetClassWflStart(IRequest iRequest, AssetClassHead assetClassHead) {
        ResponseData responseData = new ResponseData(false);

        AssetClassHead classHead = assetClassHeadMapper.queryClassHeadInfoByHeadId(assetClassHead.getClassHeadId());

        if (StringUtils.equals(classHead.getApproveStatus(), HlsConstantUtil.WorkFlowStatus.APPROVING)
                || StringUtils.equals(classHead.getApproveStatus(), HlsConstantUtil.WorkFlowStatus.APPROVED)) {
            responseData.setMessage(SUBMIT_ERROR_MESSAGE);
            return responseData;
        }

        classHead.setApproveStatus(HlsConstantUtil.WorkFlowStatus.APPROVING);
        classHead.setSubmitBy(iRequest.getUserId());
        classHead.setUnitId(userMapper.queryInfoByUserId(iRequest.getUserId().toString()).getUnitId());
        self().updateByPrimaryKeySelective(iRequest, classHead);

        //调用工作流启动过程
        self().assetClassWflProcedure(iRequest,classHead);

        responseData.setSuccess(true);
        return responseData;
    }

    @Override
    public Long queryId(IRequest iRequest, AssetClassHead assetClassHead) {
        Long id = null;
        try{
           id=assetClassHeadMapper.queryId(assetClassHead.getBpId(),assetClassHead.getClassYear(),assetClassHead.getClassSeason()).getClassHeadId();
        }catch(NullPointerException e){
        }
        return id;
    }

    /**
     * 创建资产分类头表
     */
    @Override
    public ResponseData createAssetClassHead(IRequest iRequest,AssetClassHead assetClassHead) throws Exception{

        assetClassHead.setApproveStatus(HlsConstantUtil.WorkFlowStatus.NEW);
        if (null == assetClassHeadMapper.queryMaxBatchNumber()) {
            assetClassHead.setBatchNum(1L);
        } else {
            assetClassHead.setBatchNum(assetClassHeadMapper.queryMaxBatchNumber() + 1L);
        }
        assetClassHead.setSubmitBy(iRequest.getUserId());
        assetClassHead.setUnitId(userMapper.queryInfoByUserId(iRequest.getUserId().toString()).getUnitId());
        return new ResponseData(Arrays.asList(assetClassHead));
    }

    /**
     * 每季度自动创建资产分类信息
     * @param iRequest
     */
    public void periodCreateAssetClassInfo(IRequest iRequest){
        //去除权限
        iRequest.setAttribute("authorityRuleFlag", "N");
        //获取当前月份
        int nowMonth = YearMonth.now().getMonthValue();
        String season;
        String seasonDescription;
        if( 1 <= nowMonth && nowMonth <= 3){
            season = SEASON_FOUR;
            seasonDescription = SEASON_FOUR_DESCRIPTION;
        }else if(4 <= nowMonth && nowMonth <= 6){
            season = SEASON_ONE;
            seasonDescription = SEASON_ONE_DESCRIPTION;
        }else if(7 <= nowMonth && nowMonth <= 9){
            season = SEASON_TWO;
            seasonDescription = SEASON_TWO_DESCRIPTION;
        }else {
            season = SEASON_THREE;
            seasonDescription = SEASON_THREE_DESCRIPTION;
        }
        //获取当前年份
        int year = Year.now().getValue();

        if(StringUtils.equals(season,SEASON_FOUR)){
            year-- ;
        }

        //查询当前启用的所有的厂商
        List<HlsBpMaster> bpMasterList = hlsBpMasterMapper.queryManufacturerPartnerInfo();

        //将每个厂商下的合同自动资产分类
        for ( HlsBpMaster bpMaster : bpMasterList ) {
            //查询资产分类合同数据
            List<AssetClassLine> assetClassLineList = assetClassLineMapper.queryOverdueContractInfoByContractIds(null,bpMaster.getBpId());

            //如果资产分类行数据为空则不创建信息
            if(CollectionUtils.isEmpty(assetClassLineList)){
                continue;
            }

            //构造资产分类头数据
            AssetClassHead assetClassHead = new AssetClassHead();
            assetClassHead.setClassSeason(season);
            assetClassHead.setClassYear(String.valueOf(year));
            assetClassHead.setDocumentType("PERIOD");
            assetClassHead.setBpId(bpMaster.getBpId());

            // 如果已经创建过了则跳过
            List<AssetClassHead> select = assetClassHeadMapper.select(assetClassHead);
            if(CollectionUtils.isNotEmpty(select)){
                continue;
            }

            //获取批次号
            if (null == assetClassHeadMapper.queryMaxBatchNumber()) {
                assetClassHead.setBatchNum(1L);
            } else {
                assetClassHead.setBatchNum(assetClassHeadMapper.queryMaxBatchNumber() + 1L);
            }
            assetClassHead.setApproveStatus(HlsConstantUtil.WorkFlowStatus.NEW);
            assetClassHead = self().insertSelective(iRequest,assetClassHead);

            //将资产分类行表插入表中
            assetClassLineService.insertClassLineInfo(iRequest,assetClassHead.getClassHeadId(),assetClassLineList);
            //发送邮件
//            List<String> receiverList = new ArrayList<>();
//            Map<String, Object> params = new HashMap<>(3);
//            params.put("manufacturerName",bpMaster.getBpName());
//            params.put("year",String.valueOf(year));
//            params.put("season",seasonDescription);

//            BpMasterEmployee bpMasterEmployee = new BpMasterEmployee();
//            bpMasterEmployee.setBpId(bpMaster.getBpId());
//            bpMasterEmployee.setEnabledFlag("Y");
//            List<BpMasterEmployee> bpMasterEmployees = bpMasterEmployeeMapper.select(bpMasterEmployee);
//            for ( BpMasterEmployee employee : bpMasterEmployees ) {
//                //通过员工ID查询用户邮箱
//                User user = userMapper.queryByEmployeeId(employee.getEmployeeId());
//                //维护邮件接受对象
//                MessageReceiver mr = new MessageReceiver();
//                mr.setMessageAddress(user.getEmail());
//                mr.setMessageType(NORMAL);
//                receiverList.add(mr);
//            }
//            MessageReceiver mr = new MessageReceiver();
//            mr.setMessageAddress("1721466808@qq.com");
//            mr.setMessageType(NORMAL);
//            receiverList.add("19986287331");

//            try {
//                //发送邮件
//                messageService.sendMessage("ASSET_CLASS_PERIOD_REMIND", params.toString(), receiverList);
//            } catch (Exception e) {
//                logger.error("Interface retry send email error: ", e);
//            }
        }
        //发送邮件
        org.json.JSONObject jsonObject = new org.json.JSONObject();
        jsonObject.put("year",String.valueOf(year));
        jsonObject.put("season",seasonDescription);
        HlsEmployee hlsEmployee = new HlsEmployee();
        hlsEmployee.setCompanyId(Long.valueOf(248));
        List<HlsEmployee> hlsEmployeeList=hlsCusEmployeeMapper.select(hlsEmployee);
        for ( HlsEmployee employee : hlsEmployeeList ) {
            List<String> receiverList = new ArrayList<>();
            receiverList.add(employee.getMobile());
            try {
                //发送邮件
                messageService.sendMessage("ASSET_CLASS_PERIOD_REMIND", jsonObject.toString(), receiverList);
            } catch (Exception e) {
                logger.error("Interface retry send email error: ", e);
            }
        }




    }
}
