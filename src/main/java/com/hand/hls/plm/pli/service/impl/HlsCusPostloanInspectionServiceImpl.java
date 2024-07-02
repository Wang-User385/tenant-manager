package com.hand.hls.plm.pli.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.service.ICodeService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.plm.dto.HlsCusPlmAttachment;
import com.hand.hls.plm.fc.dto.HlsCusFiveClassificationContract;
import com.hand.hls.plm.fc.mapper.HlsCusFiveClassificationContractMapper;
import com.hand.hls.plm.fc.mapper.HlsCusFiveClassificationMapper;
import com.hand.hls.plm.fc.service.HlsCusIFiveClassificationService;
import com.hand.hls.plm.pli.dto.HlsCusPostloanInspection;
import com.hand.hls.plm.pli.dto.PlmPliCheckItem;
import com.hand.hls.plm.pli.dto.PlmPliUpcoming;
import com.hand.hls.plm.pli.mapper.HlsCusPostloanInspectionMapper;
import com.hand.hls.plm.pli.mapper.PlmPliUpcomingMapper;
import com.hand.hls.plm.pli.service.HlsCusIPostloanInspectionConclusionHService;
import com.hand.hls.plm.pli.service.HlsCusIPostloanInspectionService;
import com.hand.hls.plm.pli.service.PlmPliCheckItemService;
import com.hand.hls.plm.pli.service.PlmPliFrequencySetUtil;
import com.hand.hls.plm.service.HlsCusPlmIAttachmentService;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:贷后检查serviceImpl
 * @Author: Wty
 * @Date: Created om 16:26 2018/5/24
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPostloanInspectionServiceImpl extends BaseServiceImpl<HlsCusPostloanInspection> implements HlsCusIPostloanInspectionService {

    @Autowired
    private HlsCusFiveClassificationContractMapper ficationContractMapper;

    @Autowired
    private HlsCusIFiveClassificationService fiveClassificationService;

    @Autowired
    private HlsCusFiveClassificationMapper fiveClassificationMapper;

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Autowired
    private HlsCusIPostloanInspectionConclusionHService conclusionHService;

    @Autowired
    private HlsCusPostloanInspectionMapper mapper;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private LoginUserInfoService loginUserInfoService;

    @Autowired
    private HlsEmployeeMapper employeeMapper;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Autowired
    private PlmPliUpcomingMapper plmPliUpcomingMapper;

    @Autowired
    private PlmPliCheckItemService plmPliCheckItemService;

    @Autowired
    private HlsCusPlmIAttachmentService plmIAttachmentService;

    @Autowired
    private PlmPliFrequencySetUtil setUtil;

    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private HlsCusBpMasterMapper bpMasterMapper;
    @Autowired
    private ICodeService codeService;

    private static final String REGULAR = "REGULAR";
    private static final String IRREGULAR = "IRREGULAR";

    private static final String FLAG_Y = "Y";
    private static final String FLAG_N = "N";


    /**
     * @Description: 贷后检查保存, 前面已经在选择时就创建了，这里对其更新
     * @Author: Wty
     * @Date: Created om 16:11 2018/5/25
     */
    @Override
    public HlsCusPostloanInspection savePostLoan(IRequest iRequest, HlsCusPostloanInspection postloanInspection) {
        HlsCusPostloanInspection returnInspection;

        //更新贷后检查
        postloanInspection.setDocumentCategory("POSTLOAN_INSPECTION");
        postloanInspection.setDocumentType("POSTLOAN_INSPECTION");
        postloanInspection.setBusinessType("POSTLOAN_INSPECTION");
        postloanInspection.setCompanyId(iRequest.getCompanyId());

        //生成单据编号
        Map<String, String> params = new HashMap<>();
        postloanInspection.setPostloanInspectionNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest, postloanInspection.getDocumentCategory(), postloanInspection.getDocumentType(), postloanInspection.getBusinessType(), params));

        returnInspection = self().updateByPrimaryKeySelective(iRequest, postloanInspection);
        return returnInspection;
    }

    /**
     * @Description:重写查询方法 顺带查出他对应的五级分类id
     * @Author: Wty
     * @Date: Created om 17:21 2018/5/25
     */
    @Override
    public HlsCusPostloanInspection selectPostloadInspection(IRequest iRequest, HlsCusPostloanInspection postloanInspection) {
        //postloanInspection.setCompanyId(iRequest.getCompanyId());
        return mapper.selectInspection(postloanInspection);
    }

    /**
     * @Description:首页rollTabel查询
     * @Author: Wty
     * @Date: Created om 17:44 2018/5/25
     */
    @Override
    public List<HlsCusPostloanInspection> homeRollTableQuery(IRequest iRequest, HlsCusPostloanInspection postloanInspection, int page, int pageSize) {
        if (postloanInspection.getInspectionFrequencyList() != null && !"".equals(postloanInspection.getInspectionFrequencyList())) {
            postloanInspection.setInspectionFrequencyArray(postloanInspection.getInspectionFrequencyList().split(","));
        }
        postloanInspection.setCompanyId(iRequest.getCompanyId());
        PageHelper.startPage(page, pageSize);
        PageHelper.orderBy("ppi.inspection_date desc");
        List<HlsCusPostloanInspection> list = mapper.homeRollTableQuery(postloanInspection);
        return list;
        //通过用户过滤，暂时去掉
//        return filterListByUser(iRequest, list);
    }

    /**
     * @Description:判断商业伙伴贷后检查频率
     * @Author: Wty
     * @Date: Created om 17:26 2018/5/28
     */
    @Override
    public List<HlsCusPostloanInspection> selectContractFrequency(IRequest iRequest, HlsCusPostloanInspection postloanInspection) {
        return selectContractFrequency(iRequest.getCompanyId(), postloanInspection);
    }

    /**
     * @Description:重载selectContractFrequency方法，根据companyId来判断商业伙伴贷后检查频率
     * @Author: Wty
     * @Date: Created om 16:59 2018/6/19
     */
    private List<HlsCusPostloanInspection> selectContractFrequency(Long companyId, HlsCusPostloanInspection postloanInspection) {
        List<HlsCusPostloanInspection> returnList = new ArrayList<>();
        HlsCusPostloanInspection inspection = new HlsCusPostloanInspection();
        postloanInspection.setCompanyId(companyId);
        if (postloanInspection.getPostloanInspectionId() != null && postloanInspection.getPostloanInspectionId() != 0) {
            returnList.add(mapper.selectInspection(postloanInspection));
        } else {
            HlsCusFiveClassificationContract fiveClassificationContract = new HlsCusFiveClassificationContract();
            fiveClassificationContract.setCompanyId(companyId);
            fiveClassificationContract.setContractBpId(postloanInspection.getBpId());
            List<HlsCusFiveClassificationContract> list = ficationContractMapper.selectContractFrequency(fiveClassificationContract);
            int monthSize = 0;
            int quarterSize = 0;
            int yearSize = 0;
            int halfYearSize = 0;
            String frequency;
            for (int i = 0; i < list.size(); i++) {
                frequency = list.get(i).getContractFrequency();
                if (frequency != null) {
                    switch (frequency) {
                        case "MONTH":
                            monthSize++;
                            break;
                        case "QUARTER":
                            quarterSize++;
                            break;
                        case "YEAR":
                            yearSize++;
                            break;
                        case "HALF_A_YEAR":
                            halfYearSize++;
                            break;
                        default:
                            break;
                    }
                }
            }
            String[] frequencyArray = new String[]{"MONTH", "QUARTER", "YEAR", "HALF_A_YEAR"};
            int[] array = new int[]{monthSize, quarterSize, yearSize, halfYearSize};
            int maxSize = -1;
            for (int i = 0; i < array.length; i++) {
                if (array[i] > maxSize) {
                    maxSize = i;
                }
            }
            inspection.setInspectFrenquency(frequencyArray[maxSize]);
            returnList.add(inspection);
        }
        return returnList;
    }

    /**
     * @Description: 重载查询待检查清单方法
     * @Author: Wty
     * @Date: Created om 17:11 2018/6/19
     */
    @Override
    public List<HlsCusPostloanInspection> selectCheckList(Long companyId, String MODEL) {
        HlsCusFiveClassificationContract fiveClassificationContract = new HlsCusFiveClassificationContract();
        //查找当前用户下面起租的合同以及对应
        fiveClassificationContract.setCompanyId(companyId);
        List<HlsCusFiveClassificationContract> contractList = ficationContractMapper.selectBpInceptionLease(fiveClassificationContract);
        List<HlsCusFiveClassificationContract> contractListPlan=contractList.stream().filter(contract->"PLANE".equalsIgnoreCase(contract.getItemType())).collect(Collectors.toList());
        List<HlsCusFiveClassificationContract> contractListNotPlan=contractList.stream().filter(contract->!"PLANE".equalsIgnoreCase(contract.getItemType())).collect(Collectors.toList());
        List<HlsCusPostloanInspection> checkList = new ArrayList<>();

        String FREQUENCY_TYPE = "GDXF_PLI_FREQUENCY";
        setUtil.createUtil(FREQUENCY_TYPE,MODEL);

        if (CollectionUtils.isNotEmpty(contractListNotPlan)) {
            for (HlsCusFiveClassificationContract hlsCusFiveClassificationContract : contractListNotPlan) {

                PlmPliUpcoming plmPliUpcoming = new PlmPliUpcoming();
                plmPliUpcoming.setBpId(hlsCusFiveClassificationContract.getBpId());
                List<PlmPliUpcoming> plmPliUpcomings = plmPliUpcomingMapper.select(plmPliUpcoming)
                        .stream().filter(upcoming->!"OFF_SITE_INSPECT_PLAN".equalsIgnoreCase(upcoming.getInspectionType())).collect(Collectors.toList());

                //如果有检查过，则根据最新检查的那一条进行处理
                Map<String, Date> mapOn = null;
                Map<String, Date> mapOff = null;

                if (hlsCusFiveClassificationContract.getInceptionOfLease() != null) {
                    try {
                        if (plmPliUpcomings.size() == 1) {
                            for (PlmPliUpcoming dt : plmPliUpcomings) {
                                if (PlmPliFrequencySetUtilImpl.ON_SITE_INSPECT.equalsIgnoreCase(dt.getInspectionType())) {
                                    mapOn = setUtil.getFrequencyDate(PlmPliFrequencySetUtilImpl.ON_SITE_INSPECT, hlsCusFiveClassificationContract.getInceptionOfLease(), dt.getAppointedDate());
                                    mapOff = setUtil.getFrequencyDate(PlmPliFrequencySetUtilImpl.OFF_SITE_INSPECT, hlsCusFiveClassificationContract.getInceptionOfLease(), hlsCusFiveClassificationContract.getInceptionOfLease());

                                    copyPlmPliUpcoming(mapOn, dt, null, MODEL);
                                    copyPlmPliUpcoming(mapOff, dt, PlmPliFrequencySetUtilImpl.OFF_SITE_INSPECT, MODEL);
                                }
                                if (PlmPliFrequencySetUtilImpl.OFF_SITE_INSPECT.equalsIgnoreCase(dt.getInspectionType())) {
                                    mapOff = setUtil.getFrequencyDate(PlmPliFrequencySetUtilImpl.OFF_SITE_INSPECT, hlsCusFiveClassificationContract.getInceptionOfLease(), dt.getAppointedDate());
                                    mapOn = setUtil.getFrequencyDate(PlmPliFrequencySetUtilImpl.ON_SITE_INSPECT, hlsCusFiveClassificationContract.getInceptionOfLease(), hlsCusFiveClassificationContract.getInceptionOfLease());

                                    copyPlmPliUpcoming(mapOn, dt, PlmPliFrequencySetUtilImpl.ON_SITE_INSPECT, MODEL);
                                    copyPlmPliUpcoming(mapOff, dt, null, MODEL);
                                }
                            }
                        } else if (plmPliUpcomings.size() == 2) {
                            for (PlmPliUpcoming dt : plmPliUpcomings) {
                                if (PlmPliFrequencySetUtilImpl.ON_SITE_INSPECT.equalsIgnoreCase(dt.getInspectionType())) {
                                    mapOn = setUtil.getFrequencyDate(PlmPliFrequencySetUtilImpl.ON_SITE_INSPECT, hlsCusFiveClassificationContract.getInceptionOfLease(), dt.getAppointedDate());
                                    copyPlmPliUpcoming(mapOn, dt, null, MODEL);
                                }
                                if (PlmPliFrequencySetUtilImpl.OFF_SITE_INSPECT.equalsIgnoreCase(dt.getInspectionType())) {
                                    mapOff = setUtil.getFrequencyDate(PlmPliFrequencySetUtilImpl.OFF_SITE_INSPECT, hlsCusFiveClassificationContract.getInceptionOfLease(), dt.getAppointedDate());
                                    copyPlmPliUpcoming(mapOff, dt, null, MODEL);
                                }
                            }
                        } else if (plmPliUpcomings.size() == 0) {
                            mapOn = setUtil.getFrequencyDate(PlmPliFrequencySetUtilImpl.ON_SITE_INSPECT, hlsCusFiveClassificationContract.getInceptionOfLease(), hlsCusFiveClassificationContract.getAppointedDate());
                            mapOff = setUtil.getFrequencyDate(PlmPliFrequencySetUtilImpl.OFF_SITE_INSPECT, hlsCusFiveClassificationContract.getInceptionOfLease(), hlsCusFiveClassificationContract.getAppointedDate());

                            PlmPliUpcoming dt2 = new PlmPliUpcoming();
                            dt2.setBpId(hlsCusFiveClassificationContract.getBpId());
                            dt2.setBpName(hlsCusFiveClassificationContract.getContractBpName());
                            copyPlmPliUpcoming(mapOn, dt2, PlmPliFrequencySetUtilImpl.ON_SITE_INSPECT, MODEL);
                            copyPlmPliUpcoming(mapOff, dt2, PlmPliFrequencySetUtilImpl.OFF_SITE_INSPECT, MODEL);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(contractListPlan)) {
            for (HlsCusFiveClassificationContract hlsCusFiveClassificationContract : contractListPlan) {

                PlmPliUpcoming plmPliUpcoming = new PlmPliUpcoming();
                plmPliUpcoming.setBpId(hlsCusFiveClassificationContract.getBpId());
                plmPliUpcoming.setInspectionType("OFF_SITE_INSPECT_PLAN");
                List<PlmPliUpcoming> plmPliUpcomings = plmPliUpcomingMapper.select(plmPliUpcoming);
                //如果有检查过，则根据最新检查的那一条进行处理
                Map<String, Date> mapOn = null;
                Map<String, Date> mapOff = null;

                if (hlsCusFiveClassificationContract.getInceptionOfLease() != null) {
                    try {
                        if (plmPliUpcomings.size() == 1) {
                            for (PlmPliUpcoming dt : plmPliUpcomings) {
                                    mapOff = setUtil.getFrequencyDate(PlmPliFrequencySetUtilImpl.OFF_SITE_INSPECT, hlsCusFiveClassificationContract.getInceptionOfLease(), dt.getAppointedDate());
                                    copyPlmPliUpcoming(mapOff, dt, null, MODEL);
                            }
                        } else if (plmPliUpcomings.size() == 0) {
                            mapOff = setUtil.getFrequencyDate(PlmPliFrequencySetUtilImpl.OFF_SITE_INSPECT, hlsCusFiveClassificationContract.getInceptionOfLease(), hlsCusFiveClassificationContract.getAppointedDate());

                            PlmPliUpcoming dt2 = new PlmPliUpcoming();
                            dt2.setBpId(hlsCusFiveClassificationContract.getBpId());
                            dt2.setBpName(hlsCusFiveClassificationContract.getContractBpName());
                            copyPlmPliUpcoming(mapOff, dt2, PlmPliFrequencySetUtilImpl.OFF_SITE_INSPECT_PLAN, MODEL);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return checkList;
    }

    /**
     * 创建贷后检查
     *
     * @param iRequest
     * @param hlsCusPostloanInspection
     * @return
     */
    @Override
    public List<HlsCusPostloanInspection> createPostloanInspection(IRequest iRequest, HlsCusPostloanInspection hlsCusPostloanInspection) {
        List<HlsCusPostloanInspection> list = new ArrayList<>();
        boolean flag = false;

        //选择检查方案，默认按现场检查处理
        String type = null;
        Date appointedDate = new Date();
        //定期检查需要修改待检查清单
        if (REGULAR.equalsIgnoreCase(hlsCusPostloanInspection.getInspectionMethod())) {
            //修改待检查清单
            PlmPliUpcoming plmPliUpcoming = new PlmPliUpcoming();
            plmPliUpcoming.setBpId(hlsCusPostloanInspection.getBpId());
            plmPliUpcoming.setEnableFlag("Y");
            plmPliUpcoming.setStatus("Y");
            if (PlmPliFrequencySetUtilImpl.ON_SITE_INSPECT.equalsIgnoreCase(hlsCusPostloanInspection.getInspectionType())
                    || PlmPliFrequencySetUtilImpl.OFF_SITE_INSPECT.equalsIgnoreCase(hlsCusPostloanInspection.getInspectionType())
                    ||PlmPliFrequencySetUtilImpl.OFF_SITE_INSPECT_PLAN.equalsIgnoreCase(hlsCusPostloanInspection.getInspectionType())) {
                plmPliUpcoming.setInspectionType(hlsCusPostloanInspection.getInspectionType());
                List<PlmPliUpcoming> plmPliUpcomings = plmPliUpcomingMapper.select(plmPliUpcoming);
                if (plmPliUpcomings.size() > 0) {
                    plmPliUpcoming = plmPliUpcomings.get(0);
                    //这条记录已经使用
                    plmPliUpcoming.setStatus(FLAG_N);
                    //这条记录正在占用，当审批流程通过后释放
                    plmPliUpcoming.setEnableFlag(FLAG_N);
                    plmPliUpcoming.set__status(HlsCusConstant.DATA_STATUS.STATUS_UPDATE);

                    plmPliUpcomingMapper.updateByPrimaryKeySelective(plmPliUpcoming);
                    type = hlsCusPostloanInspection.getInspectionType();
                    appointedDate = plmPliUpcoming.getAppointedDate();
                    flag = true;
                }
            } else if (PlmPliFrequencySetUtilImpl.ALL.equalsIgnoreCase(hlsCusPostloanInspection.getInspectionType())) {
                List<PlmPliUpcoming> plmPliUpcomings = plmPliUpcomingMapper.select(plmPliUpcoming);
                if (plmPliUpcomings.size() == 2) {
                    for (PlmPliUpcoming dt : plmPliUpcomings) {
                        //这条记录已经使用
                        dt.setStatus(FLAG_N);
                        //这条记录正在占用，当审批流程通过后释放
                        dt.setEnableFlag(FLAG_N);
                        dt.set__status(HlsCusConstant.DATA_STATUS.STATUS_UPDATE);

                        plmPliUpcomingMapper.updateByPrimaryKeySelective(dt);

                        if (PlmPliFrequencySetUtilImpl.ON_SITE_INSPECT.equalsIgnoreCase(dt.getInspectionType())) {
                            appointedDate = dt.getAppointedDate();
                        }
                    }
                    type = setUtil.getPriorityInspect(PlmPliFrequencySetUtilImpl.ON_SITE_INSPECT);
                    flag = true;
                }
            }
        }
        //不定期检查不需要修改待检查清单
        else if (IRREGULAR.equalsIgnoreCase(hlsCusPostloanInspection.getInspectionMethod())) {
            type = hlsCusPostloanInspection.getInspectionType();
            flag = true;
        }

        //待检查清单修改后创建贷后检查和附件清单
        if (flag) {
            //创建贷后检查
            hlsCusPostloanInspection.setCreationUserId(iRequest.getUserId());
            hlsCusPostloanInspection.setPostloanInspectionCreatetime(new Date());
            hlsCusPostloanInspection.setAppointedDate(appointedDate);
            hlsCusPostloanInspection.setAgreementInspectionDate(appointedDate);
            hlsCusPostloanInspection.setExpectedCompletionDate(appointedDate);
            hlsCusPostloanInspection.setStatus("NEW");
            hlsCusPostloanInspection.set__status(HlsCusConstant.DATA_STATUS.STATUS_ADD);

            //获取初始化的检查年月
            Calendar c = Calendar.getInstance();
            c.setTime(appointedDate);
            hlsCusPostloanInspection.setInspectionYear((long) c.get(Calendar.YEAR));
            hlsCusPostloanInspection.setInspectionMonth((long) c.get(Calendar.MONTH)+1);

            mapper.insertSelective(hlsCusPostloanInspection);

            PlmPliCheckItem plmPliCheckItem = new PlmPliCheckItem();
            plmPliCheckItem.setPostloanInspectionId(hlsCusPostloanInspection.getPostloanInspectionId());
            plmPliCheckItem.setListCategory("PLM");
            //初始化检查列表 飞机检查不初始化
            //现场+非飞机
            if (PlmPliFrequencySetUtilImpl.ON_SITE_INSPECT.equalsIgnoreCase(type) && hlsCusPostloanInspection.getIsPlaneCheck().equalsIgnoreCase("N")) {
                String[] ListType = {"SURVEY","LESSEE","GUARANTOR","LEASE","PAWN"};
                plmPliCheckItemService.createItemForPli(plmPliCheckItem, ListType);
            }
            //非现场+非飞机
            else if (PlmPliFrequencySetUtilImpl.OFF_SITE_INSPECT.equalsIgnoreCase(type)&& hlsCusPostloanInspection.getIsPlaneCheck().equalsIgnoreCase("N")) {
                String[] ListType = {"OFF_SURVEY"};
                plmPliCheckItemService.createItemForPli(plmPliCheckItem, ListType);
            }
            //创建的时候不带出来
//            //现场+飞机
//            else if ((PlmPliFrequencySetUtilImpl.ON_SITE_INSPECT_PLANE.equalsIgnoreCase(type)
//                    ) && hlsCusPostloanInspection.getIsPlaneCheck().equalsIgnoreCase("Y"))
//            {
//                String[] ListType = {"ON_SURVEY_PLANE"};
//                plmPliCheckItemService.createItemForPli(plmPliCheckItem, ListType);
//            }
            //初始化附件信息========================================================================
            initAccessory(iRequest,hlsCusPostloanInspection);
            //初始化附件信息========================================================================

            list.add(hlsCusPostloanInspection);
        }
        return list;
    }

    /**
     * 初始化附件信息
     * @param iRequest
     * @param hlsCusPostloanInspection
     */
    @Transactional(rollbackFor = Exception.class)
    public void initAccessory(IRequest iRequest, HlsCusPostloanInspection hlsCusPostloanInspection){
        List<String>fileList=new ArrayList<>(16);
        if (StringUtils.isNotEmpty(hlsCusPostloanInspection.getAttribute1())){
            //查询维护好的数据(费用科目编码和名称的对应关系)
            List<CodeValue> codeValues = codeService.selectCodeValuesByCodeName(iRequest,hlsCusPostloanInspection.getAttribute1());
            if (CollectionUtils.isNotEmpty(codeValues))
            {
                fileList=codeValues.stream().map(CodeValue::getMeaning).collect(Collectors.toList());
            }
        }
        if (CollectionUtils.isEmpty(fileList)){
            fileList.add("财务数据报表");
            fileList.add("近三个月征信报告");
        }
        for (String fileName : fileList) {
            HlsCusPlmAttachment plmAttachment = new HlsCusPlmAttachment();
            plmAttachment.setPlmId(hlsCusPostloanInspection.getPostloanInspectionId());
            plmAttachment.setPlmType("PLI");
            plmAttachment.setPlmAttachmentCategory("PLM_PLI_ATTACHMENT");
            plmAttachment.setPlmSourceType("PLM_PLI_ATTACHMENT");
            plmAttachment.setSourceType("PLM_PLI_ATTACHMENT");
            plmAttachment.setChangeIq("NORMAL");
            plmAttachment.setMakeUp("NORMAL");
            plmAttachment.setDocumentName(fileName);
            plmAttachment.setPostloanInspectionId(hlsCusPostloanInspection.getPostloanInspectionId());
            plmAttachment.set__status("insert");

            plmIAttachmentService.insertSelective(iRequest, plmAttachment);
        }
    }



    /**
     * @Description:工作流提交
     * @Author: Wty
     * @Date: Created om 10:45 2018/5/30
     */
    @Override
    public HlsCusPostloanInspection submitWfl(IRequest iRequest, HlsCusPostloanInspection hlsCusPostloanInspection) {
        HlsCusPostloanInspection dto = savePostLoan(iRequest, hlsCusPostloanInspection);
        databaseLockProvider.lock(dto);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode ="";
        if (null!=employee){
            employee.getEmployeeCode();
        }
        iRequest.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<>();
        List<HlsCusPostloanInspection> list = new ArrayList<>();
        list.add(dto);
        params.put("workFlowType", "PLM_PLI_WORK_FLOW");
        activitiStartService.start(iRequest, list, params);
        dto.setStatus("APPROVING");
        self().updateByPrimaryKeySelective(iRequest, dto);

        dto = self().selectByPrimaryKey(iRequest,dto);
        //获取客户名称
        HlsCusBpMaster bpMaster = new HlsCusBpMaster();
        bpMaster.setBpId(dto.getBpId());
        bpMaster = bpMasterMapper.selectByPrimaryKey(dto);
        //消息参数，插入动态消息
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        String noticeTitle = bpMaster.getBpName();
        if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
        }
        String msg = userName.concat("新增了").concat(dto.getInspectionYear()+"年").concat(dto.getInspectionMonth()+"月").concat("贷后检查动态") ;
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", noticeTitle);
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(iRequest, hlsCusPostloanInspection.getPostloanInspectionId(), hlsCusPostloanInspection.getDocumentCategory(), hlsCusPostloanInspection.getDocumentType(), "PLM_PLI", "PLI_SUBMIT", "P2D", paramsEvent);

        return dto;
    }

    /**
     * @Description:得到提醒日和约定检查日
     * @Author: Wty
     * @Date: Created om 10:05 2018/5/30
     * @param: [frequency, inspectionDate]  frequency:待检查频率 , InspectionDate:检查日期
     * @return: java.util.Map<java.lang.String                                                               ,                                                               java.util.Date>
     */
    private Map<String, Date> getRemindAndInspectionDate(String frequency, Date inspectionDate) {
        Map<String, Date> map = new HashMap<>();
        //设置约定检查日
        GregorianCalendar remindDateGc = new GregorianCalendar();
        GregorianCalendar agreementInspectionDateGc = new GregorianCalendar();
        agreementInspectionDateGc.setTime(inspectionDate);
        if ("MONTH".equals(frequency)) {
            agreementInspectionDateGc.add(GregorianCalendar.MONTH, +1);
        } else if ("QUARTER".equals(frequency)) {
            agreementInspectionDateGc.add(GregorianCalendar.MONTH, +3);
        } else if ("YEAR".equals(frequency)) {
            agreementInspectionDateGc.add(GregorianCalendar.YEAR, +1);
        } else if ("HALF_A_YEAR".equals(frequency)) {
            agreementInspectionDateGc.add(GregorianCalendar.MONTH, +6);
        }
        map.put("agreementInspectionDate", agreementInspectionDateGc.getTime());

        //设置开始提醒日为约定检查日前一个月
        remindDateGc.setTime(agreementInspectionDateGc.getTime());
        remindDateGc.add(GregorianCalendar.MONTH, -1);
        map.put("remindDate", remindDateGc.getTime());

        return map;
    }

    /**
     * @Description:校验当天是否大于提醒日
     * @Author: Wty
     * @Date: Created om 10:35 2018/5/30
     * @param: [map, contractBpName， bpId]  map:存放提醒日和约定检查日期,contractBpName:存放客户名称,bpId:存放客户ID
     * @return: hls.core.plm.pli.dto.HlsCusPostloanInspection
     */
    private HlsCusPostloanInspection checkRemindDate(Map<String, Date> map, String contractBpName, Long bpId) {
        Date date = new Date();
        if (DateUtils.isSameDay(map.get("remindDate"), date) || map.get("remindDate").before(date)) {
            HlsCusPostloanInspection postloanInspection = new HlsCusPostloanInspection();
            postloanInspection.setCheckContain(contractBpName + "贷后检查时间将近，请及时检查");
            postloanInspection.setBpName(contractBpName);
            postloanInspection.setRemindDate(map.get("remindDate"));
            postloanInspection.setAgreementInspectionDate(map.get("agreementInspectionDate"));
            postloanInspection.setBpId(bpId);
            return postloanInspection;
        } else {
            return null;
        }
    }

    /**
     * @Description:通过userId来过滤数据
     * @Author: Wty
     * @Date: Created om 15:31 2018/6/19
     */
    private List<HlsCusPostloanInspection> filterListByUser(IRequest iRequest, List<HlsCusPostloanInspection> list) {
        Boolean isShowAll = false;
        Map<String, Object> map = new HashMap<>();
        map.put("userId", iRequest.getUserId());
        map.put("companyId", iRequest.getCompanyId());
        //判断是否是admin/风控/公司领导
        if (CollectionUtils.isNotEmpty(mapper.selectUserInfo(map))) {
            isShowAll = true;
        }
        if (isShowAll) {
            //如果是admin/风控/公司领导 则返回所有的数据
            return list;
        } else {
            String bpName;
            //如果不是则判断，根据当前商业伙伴下是否有当前用户创建的合同
            List<String> approvedBpNameList = new ArrayList<>();//保存判断通过的用户
            List<String> unApprovedBpNameList = new ArrayList<>();//保存判断不通过的用户
            List<HlsCusPostloanInspection> returnList = new ArrayList<>();
            for (HlsCusPostloanInspection p : list) {
                bpName = p.getBpName();
                if (approvedBpNameList.contains(bpName)) {
                    returnList.add(p);
                } else {
                    if (!unApprovedBpNameList.contains(bpName)) {
                        map.put("bpId", p.getBpId());
                        if (CollectionUtils.isNotEmpty(mapper.selectBpContractInfo(map))) {
                            //如果不为空,则给返回集合添加
                            returnList.add(p);
                            approvedBpNameList.add(bpName);
                        } else {
                            unApprovedBpNameList.add(bpName);
                        }
                    }
                }

            }
            return returnList;
        }
    }

    private void copyPlmPliUpcoming(Map<String, Date> map, PlmPliUpcoming dt, String flag, String MODEL) {
        String FLAG_Y = "Y";
        String FLAG_N = "N";

        if (MODEL.equalsIgnoreCase("JOB")) {
            if (FLAG_Y.equalsIgnoreCase(dt.getEnableFlag())
                    && FLAG_N.equalsIgnoreCase(dt.getStatus())
                    && StringUtils.isBlank(flag)) {
                dt.setAppointedDate(map.get(PlmPliFrequencySetUtilImpl.INSPECTION_DATE));
                dt.setRemindDate(map.get(PlmPliFrequencySetUtilImpl.REMIND_DATE));
                dt.setStatus(FLAG_Y);
                dt.set__status(HlsCusConstant.DATA_STATUS.STATUS_UPDATE);
                plmPliUpcomingMapper.updateByPrimaryKeySelective(dt);
            } else if (StringUtils.isNotBlank(flag)) {
                PlmPliUpcoming dt2 = new PlmPliUpcoming();
                dt2.setBpId(dt.getBpId());
                dt2.setBpName(dt.getBpName());
                dt2.setAppointedDate(map.get(PlmPliFrequencySetUtilImpl.INSPECTION_DATE));
                dt2.setRemindDate(map.get(PlmPliFrequencySetUtilImpl.REMIND_DATE));
                dt2.setStatus(FLAG_Y);
                dt2.setEnableFlag(FLAG_Y);
                dt2.setInspectionType(flag);
                dt2.set__status(HlsCusConstant.DATA_STATUS.STATUS_ADD);

                plmPliUpcomingMapper.insertSelective(dt2);
            }
        } else if (MODEL.equalsIgnoreCase("UPDATE")) {
            if (FLAG_Y.equalsIgnoreCase(dt.getEnableFlag())
                    && FLAG_Y.equalsIgnoreCase(dt.getStatus())
                    && StringUtils.isBlank(flag)) {
                dt.setAppointedDate(map.get(PlmPliFrequencySetUtilImpl.INSPECTION_DATE));
                dt.setRemindDate(map.get(PlmPliFrequencySetUtilImpl.REMIND_DATE));
                dt.set__status(HlsCusConstant.DATA_STATUS.STATUS_UPDATE);
                plmPliUpcomingMapper.updateByPrimaryKeySelective(dt);
            } else if (StringUtils.isNotBlank(flag)) {
                PlmPliUpcoming dt2 = new PlmPliUpcoming();
                dt2.setBpId(dt.getBpId());
                dt2.setBpName(dt.getBpName());
                dt2.setAppointedDate(map.get(PlmPliFrequencySetUtilImpl.INSPECTION_DATE));
                dt2.setRemindDate(map.get(PlmPliFrequencySetUtilImpl.REMIND_DATE));
                dt2.setEnableFlag(FLAG_Y);
                dt2.setStatus(FLAG_Y);
                dt2.setInspectionType(flag);
                dt2.set__status(HlsCusConstant.DATA_STATUS.STATUS_ADD);

                plmPliUpcomingMapper.insertSelective(dt2);
            }
        }


    }
}
