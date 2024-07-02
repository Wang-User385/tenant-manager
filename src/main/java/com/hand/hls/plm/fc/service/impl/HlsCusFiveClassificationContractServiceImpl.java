package com.hand.hls.plm.fc.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.plm.fc.dto.HlsCusFiveClassificationContract;
import com.hand.hls.plm.fc.mapper.HlsCusFiveClassificationContractMapper;
import com.hand.hls.plm.fc.service.HlsCusIFiveClassificationContractService;
import com.hand.hls.plm.pli.dto.PlmPliContract;
import oracle.sql.TIMESTAMP;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*import hls.core.plm.pli.dto.PlmPliContract;*/

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusFiveClassificationContractServiceImpl extends BaseServiceImpl<HlsCusFiveClassificationContract> implements HlsCusIFiveClassificationContractService {

    @Autowired
    private HlsCusFiveClassificationContractMapper mapper;
    private  NumberFormat numberFormat = new DecimalFormat("###,##0.00");

    public static final String NORMAL = "NORMAL";
    public static final String ATTENTION = "ATTENTION";
    public static final String SECONDARY = "SECONDARY";
    public static final String SUSPICIOUS = "SUSPICIOUS";
    public static final String LOSS = "LOSS";

    public static final Long QUARTER_DAYS = 90L;

    public static final Long MONTH_DAYS = 30L;

    public static final Long WEEK_DAYS = 7L;

    public static final Long HALF_YEAR_DAYS = 180L;

    public static final Integer HUNDRED = 100;

    private static final String[] DEFAULT_FIELDS = new String[]{"isInterManageAbnormal", "isProductionLineStopped",
            "isProductStructureChange", "isMarketPriceLeasing", "isOtherCases", "isMajorPlan", "isExecutivePersonnelChange",
            "isExternalEnvironment", "isMajorProceedings", "isMajorPenalty", "isMajorAccidents", "isMajorOtherCases", "isValueReduced",
            "guarantorFinancialStatus", "otherGuaranteesSituation", "operatingProfitStatus", "revenueComparisonStatus", "operCfChangeStatus",
            "netOperatingCashflowStatus", "otherFinancialSituation", "leaseRentSituation", "otherInstOverdueSituation", "twoYearsLossesStatus",
            "creditFormulaSystemStatus", "otherCasesSituation"};

    /**
     * @Description:五级分类查询所有的起租合同信息
     * @Author: Wty
     * @Date: Created om 15:02 2018/5/17
     */
    @Override
    public List<HlsCusFiveClassificationContract> selectAllContracts(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract, int page, int pageSize) {
        fiveClassificationContract.setCompanyId(iRequest.getCompanyId());
        List<HlsCusFiveClassificationContract> list = mapper.selectAllContracts(fiveClassificationContract);
        //合同号去重
//        return getDistinctContractNumber(list);
        return list;
    }

    /**
     * @Description:去掉重复的合同编号
     * @Author: Wty
     * @Date: Created om 11:19 2018/6/8
     */
    private List<HlsCusFiveClassificationContract> getDistinctContractNumber(List<HlsCusFiveClassificationContract> list) {
        List<HlsCusFiveClassificationContract> returnList = new ArrayList<>();//返回的list
        List<Integer> intList = new ArrayList<>();//用来保存已经判断过的list位置
        //用来判断查找出的合同是否属于同一批大合同
        for (int i = 0; i < list.size(); i++) {
            //租赁合同
//            list.get(i).setFiveClassifyConId(Long.parseLong(new Integer(i).toString()));
            if ("CON".equals(list.get(i).getContractType())) {
                //如果当前合同的位置intList中有则说明判断过直接跳过
                if (intList.contains(i)) {
                    continue;
                }
                String contractNum = list.get(i).getContractNumber().split("-")[0];
                //保存大合同号
                list.get(i).setContractNumber(contractNum);
                //从第i+1个进行判断
                for (int j = i + 1; j < list.size(); j++) {
                    String contractNumBak = list.get(j).getContractNumber().split("-")[0];
                    //如果能找到对应的就说明时同一批
                    if (contractNum.equals(contractNumBak)) {
                        intList.add(j);
                    }
                }
                returnList.add(list.get(i));
            } else {
                //保理合同就直接添加
                returnList.add(list.get(i));
            }
        }
        return returnList;
    }

    /**
     * @Description:五级分类查找页面对应的合同信息
     * @Author: Wty
     * @Date: Created om 20:34 2018/5/17
     */
    @Override
    public List<HlsCusFiveClassificationContract> selectFcContracts(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract, int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        return mapper.selectFiveClassificationContracts(fiveClassificationContract);
    }

    /**
     * 为结果添加五级分类建议分类
     * @param list
     * @return
     */
    private List<HlsCusFiveClassificationContract> getFiveClass(List<HlsCusFiveClassificationContract> list) {
        for (HlsCusFiveClassificationContract dt : list) {
            Long fineDate = dt.getFineDays();
            if (fineDate == null || fineDate <= 30) {
                dt.setSuggestedClassification(NORMAL);
            } else if (fineDate > 30 && fineDate <= 180) {
                dt.setSuggestedClassification(ATTENTION);
            } else if (fineDate > 180 && fineDate <= 365) {
                dt.setSuggestedClassification(SECONDARY);
            } else if (fineDate > 365 && fineDate <= 730) {
                dt.setSuggestedClassification(SUSPICIOUS);
            } else if (fineDate > 730 ) {
                dt.setSuggestedClassification(LOSS);
            }

        }
        return list;
    }

    /**
     * @Description:获取新的合同list
     * @Author: Wty
     * @Date: Created om 15:38 2018/6/13
     */
    private void getNewContractList(IRequest iRequest, List<HlsCusFiveClassificationContract> list, HlsCusFiveClassificationContract fiveClassificationContract) {
        fiveClassificationContract.setCompanyId(iRequest.getCompanyId());
        String[] contractIds = fiveClassificationContract.getContractIds();
        if (!ArrayUtils.isEmpty(contractIds)) {
            List<Long> conContractIdsList = new ArrayList<>();
            List<Long> fctContractIdsList = new ArrayList<>();
            for (int i = 0; i < contractIds.length; i++) {
                //租赁合同
                if (contractIds[i].startsWith("CON")) {
                    conContractIdsList.add(Long.parseLong(contractIds[i].split("-")[1]));
                } else if (contractIds[i].startsWith("FCT")) {
                    //保理合同
                    fctContractIdsList.add(Long.parseLong(contractIds[i].split("-")[1]));
                }
            }
            if (CollectionUtils.isNotEmpty(conContractIdsList)) {
                Long[] conContractIds = new Long[conContractIdsList.size()];
                conContractIdsList.toArray(conContractIds);
                fiveClassificationContract.setConContractIds(conContractIds);
                List<HlsCusFiveClassificationContract> conContractsList = mapper.selectConContracts(fiveClassificationContract);
                if (CollectionUtils.isNotEmpty(conContractsList)) {
                    //添加投放总额和剩余本金 以及对应的上期分类结果/时间，初步分类结果/时间
                    list.addAll(setAmountAndResult(iRequest.getCompanyId(), "CON", conContractsList, fiveClassificationContract.getFiveClassificationType()));
                }
            }
            if (CollectionUtils.isNotEmpty(fctContractIdsList)) {
                Long[] fctContractIds = new Long[fctContractIdsList.size()];
                fctContractIdsList.toArray(fctContractIds);
                fiveClassificationContract.setFctContractIds(fctContractIds);
                List<HlsCusFiveClassificationContract> fctContractsList = mapper.selectFctContracts(fiveClassificationContract);
                if (CollectionUtils.isNotEmpty(fctContractsList)) {
                    //添加投放总额和剩余本金 以及对应的上期分类结果/时间，初步分类结果/时间
                    list.addAll(setAmountAndResult(iRequest.getCompanyId(), "FCT", fctContractsList, fiveClassificationContract.getFiveClassificationType()));
                }
            }
            list.get(0).setContractIds(contractIds);
        }
        //给合同设置默认值
        setNewContractsDefaultValue(list, iRequest.getCompanyId());
    }

    /**
     * @param list : 合同清单
     * @Description: 给新建合同添加分析默认值
     * @Author: Wty
     * @Date: Created in 上午10:37 2018/8/9
     * @return: void
     */
    private void setNewContractsDefaultValue(List<HlsCusFiveClassificationContract> list, Long companyId) {
        if (CollectionUtils.isNotEmpty(list)) {
            HlsCusFiveClassificationContract contract = new HlsCusFiveClassificationContract();
            contract.setCompanyId(companyId);
            for (HlsCusFiveClassificationContract c : list) {
                contract.setContractNumber(c.getContractNumber());
                //查找合同默认值
                List<HlsCusFiveClassificationContract> contracts = mapper.selectNewContractDefaultValue(contract);
                if (CollectionUtils.isNotEmpty(contracts)) {
                    setDefaultValue(c, contracts.get(0));
                }
            }
        }
    }

    /**
     * @param setContract   : 需要赋值的合同
     * @param valueContract : 取值的合同
     * @Description:给合同赋值分析结果
     * @Author: Wty
     * @Date: Created in 下午1:41 2018/8/9
     * @return: void
     */
    private void setDefaultValue(HlsCusFiveClassificationContract setContract, HlsCusFiveClassificationContract valueContract) {
        try {
            Class clazz = HlsCusFiveClassificationContract.class;
            String firstUpperFiled;
            String fieldGetMethodName;
            String fieldSetMethodName;
            for (int i = 0; i < DEFAULT_FIELDS.length; i++) {
                firstUpperFiled = DEFAULT_FIELDS[i].substring(0, 1).toUpperCase() + DEFAULT_FIELDS[i].substring(1);
                fieldGetMethodName = "get" + firstUpperFiled;
                Method getMethod = clazz.getMethod(fieldGetMethodName);
                Object valueObject = getMethod.invoke(valueContract);
                if (valueObject != null) {
                    fieldSetMethodName = "set" + firstUpperFiled;
                    Method setMethod = clazz.getMethod(fieldSetMethodName, String.class);
                    setMethod.invoke(setContract, (String) valueObject);
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("未知错误");
        }

    }

    /**
     * @Description:五级分类给每个合同添加投放总额和剩余本金 以及对应的上期分类结果/时间，初步分类结果/时间
     * @Author: Wty
     * @Date: Created om 16:38 2018/5/22
     * @param: [companyId ,type, list ,fiveClassificationType] companyId:公司id type:CON 租赁,FCT保理  list合同集合 fiveClassificationType五级分类类型
     */
    private List<HlsCusFiveClassificationContract> setAmountAndResult(Long companyId, String type, List<HlsCusFiveClassificationContract> list, String fiveClassificationType) {
        //循环查找对应的合同的剩余本金和投放总额
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setCompanyId(companyId);
            //查询上期分类结果和初步分类结果
            if ("CON".equals(type)) {
                list.get(i).setContractNumber(list.get(i).getContractNumber().split("-")[0]);
            }
            List<HlsCusFiveClassificationContract> resultList = mapper.selectLastAndFirstResult(list.get(i));
            if (CollectionUtils.isNotEmpty(resultList)) {
                //保存上次分类结果
                if ("FC".equals(resultList.get(0).getFiveClassificationType())) {
                    //如果上一次是五级分类，则获取最终分类结果
                    list.get(i).setLastClassificationResult(resultList.get(0).getFinalClassificationResult());
                } else {
                    //否则获取最初分类结果
                    list.get(i).setLastClassificationResult(resultList.get(0).getFirstClassificationResult());
                }
                //倒序获取他的初步分类结果
                if ("FC".equals(fiveClassificationType)) {
                    for (int k = resultList.size() - 1; k >= 0; k--) {
                        if (!"FC".equals(resultList.get(k).getFiveClassificationType())) {
                            list.get(i).setFirstClassificationResult(resultList.get(k).getFirstClassificationResult());
                            list.get(i).setFirstClassificationTime(resultList.get(k).getFirstClassificationTime());
                        }
                    }
                }


            }
            //设置金额
            setAmount(type, list.get(i), companyId);
        }

        return list;
    }

    /**
     * @Description:五级分类给每个合同添加投放总额和剩余本金
     * @Author: Wty
     * @Date: Created om 19:50 2018/6/20
     * @param: [type, contract, companyId] type:CON 租赁,FCT保理  contract五级分类合同 companyId:公司id
     * @return: void
     */
    @Override
    public void setAmount(String type, HlsCusFiveClassificationContract contract, Long companyId) {
        contract.setCompanyId(companyId);
        List<HlsCusFiveClassificationContract> amountList;
        //用来存储租赁拆分合同
        List<String> conNumberList = new ArrayList<>();
        if ("CON".equals(type)) {
            //得到大合同号
            contract.setContractNumber(contract.getContractNumber().split("-")[0]);
            amountList = mapper.selectConContractAmount(contract);
        } else {
            amountList = mapper.selectFctContractAmount(contract);
        }
        if (CollectionUtils.isNotEmpty(amountList)) {
            Double financeAmountAll = 0D;
            Double receivedPrincipal = 0D;
            Double leasItemAmount = 0D;//租赁的投放总额
            for (int j = 0; j < amountList.size(); j++) {
                if ("CON".equals(type)) {
                    //租赁
                    String conNumber = amountList.get(j).getContractNumber();
                    if (!conNumberList.contains(conNumber)) {
                        //判断list里面是否已经存在拆分合同 如果不存在则加上他的financeAmount
                        financeAmountAll += amountList.get(j).getFinanceAmount();
                        leasItemAmount += amountList.get(j).getLeaseItemAmount();
                        conNumberList.add(conNumber);
                    }
                    receivedPrincipal += amountList.get(j).getReceivedPrincipal();
                } else {
                    //保理
                    financeAmountAll += amountList.get(j).getFinanceAmount();
                    receivedPrincipal += amountList.get(j).getReceivedPrincipal();
                }

            }
            if ("CON".equals(type)) {
                contract.setContractAmount(leasItemAmount);
            } else {
                contract.setContractAmount(financeAmountAll);
            }
            contract.setContractRemainingPrincipal(financeAmountAll - receivedPrincipal);
        }
    }

    /**
     * @Description:工作流查询指定人的合同
     * @Author: Wty
     * @Date: Created om 上午11:01 2018/7/9
     */
    @Override
    public List<HlsCusFiveClassificationContract> selectFcActivitiContracts(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract, int page, int pageSize) {
        List<Long> userList = mapper.selectUserIdByUserName(fiveClassificationContract.getUserName());
        if (CollectionUtils.isNotEmpty(userList)) {
            fiveClassificationContract.setUserId(userList.get(0));
        } else {
            fiveClassificationContract.setUserId(iRequest.getUserId());
        }
        fiveClassificationContract.setCompanyId(iRequest.getCompanyId());
        List<HlsCusFiveClassificationContract> list = mapper.selectFiveClassificationContracts(fiveClassificationContract);
        List<HlsCusFiveClassificationContract> returnList = new ArrayList<>();
        List<Long> conContractIds = new ArrayList<>();
        List<Long> fctContractIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (HlsCusFiveClassificationContract f : list) {
                if ("CON".equals(f.getContractType())) {
                    conContractIds.add(f.getContractId());
                } else {
                    fctContractIds.add(f.getContractId());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(conContractIds)) {
            Long[] conIds = new Long[conContractIds.size()];
            fiveClassificationContract.setConContractIds(conContractIds.toArray(conIds));
            List<HlsCusFiveClassificationContract> conList = mapper.selectFiveClassificationActivitiConContracts(fiveClassificationContract);
            if (CollectionUtils.isNotEmpty(conList)) {
                returnList.addAll(conList);
            }
        }
        if (CollectionUtils.isNotEmpty(fctContractIds)) {
            Long[] fctIds = new Long[conContractIds.size()];
            fiveClassificationContract.setConContractIds(fctContractIds.toArray(fctIds));
            List<HlsCusFiveClassificationContract> fctList = mapper.selectFiveClassificationActivitiFctContracts(fiveClassificationContract);
            if (CollectionUtils.isNotEmpty(fctList)) {
                returnList.addAll(fctList);
            }
        }

        return returnList;

    }

    /**
     * @Description:返回copy集合
     * @Author: Wty
     * @Date: Created om 上午10:55 2018/7/11
     */
    @Override
    public List<HlsCusFiveClassificationContract> getCopyContracts(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract) {
        if (fiveClassificationContract.getContractNumberList() != null) {
            String[] arr = new String[fiveClassificationContract.getContractNumberList().size()];
            fiveClassificationContract.setContractNumberArray(fiveClassificationContract.getContractNumberList().toArray(arr));
        }
        List<HlsCusFiveClassificationContract> list = mapper.selectFiveClassificationContracts(fiveClassificationContract);
        if (CollectionUtils.isNotEmpty(list)) {
            return copyFiveClassificationContracts(list);
        }
        return new ArrayList<>();
    }

    /**
     * @Description:工作流中保存意见
     * @Author: Wty
     * @Date: Created om 下午5:32 2018/7/11
     * @param: [iRequest, list, approvalNode] list意见集合,approvalNode审批节点
     * @return: java.util.List<hls.core.plm.fc.dto.HlsCusFiveClassificationContract>
     */
    @Override
    public List<HlsCusFiveClassificationContract> saveOpinions(IRequest iRequest, List<HlsCusFiveClassificationContract> list, String approvalNode) {
        List<HlsCusFiveClassificationContract> fives = new ArrayList<>();
        HlsCusFiveClassificationContract contract = new HlsCusFiveClassificationContract();
        contract.setFiveClassificationId(list.get(0).getFiveClassificationId());
        contract.setApprovalNode(approvalNode);
        fives = self().select(iRequest, contract, 1, 99999);
        Boolean flag = true;
        List<HlsCusFiveClassificationContract> fiveClassificationContracts = new ArrayList<>();
        for (HlsCusFiveClassificationContract c : list) {
            c.set__status("add");
            c.setApprovalNode(approvalNode);
            //temp:前台保存写在了确认框弹出之前(which is a bug)，此处判断，若已插入改合同在该审批节点的数据，就不在此插入
            for (HlsCusFiveClassificationContract five : fives) {
                if (c.getContractId().equals(five.getContractId())) {
                    flag = false;
                }
            }
            if (flag) {
                fiveClassificationContracts.add(c);
            }
        }
        return this.batchUpdate(iRequest, fiveClassificationContracts);
    }

    /**
     * 查询当前用户的unitCode 如果='003'则false
     * @param requestContext
     * @return
     */
    @Override
    public Boolean shouldButtonShow(IRequest requestContext) {
        boolean flag=true;
        if (null!=requestContext.getEmployeeCode()&&mapper.selectCountUnitNo(requestContext.getEmployeeCode(),"003")>0){
            flag=false;
        }
        return flag;
    }

    /**
     * @Description:五级分类首页chart查询
     * @Author: Wty
     * @Date: Created om 18:00 2018/5/22
     */
    @Override
    public List<HlsCusFiveClassificationContract> homeChartQuery(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract) {
        //fiveClassificationContract.setCompanyId(iRequest.getCompanyId());
        List<HlsCusFiveClassificationContract> list = mapper.homeChartQuery(fiveClassificationContract);
        if (CollectionUtils.isNotEmpty(list)) {
            Long allResultCount = 0L;
            Long currentPercent = 0L;
            Double allRemainAmount = 0D;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getFinalClassificationResult() != null) {
                    allResultCount += list.get(i).getResultCount();
                    allRemainAmount += list.get(i).getRemainAmount();
                }
            }

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getFinalClassificationResult() != null) {
                    if (i == (list.size() - 1)) {
                        list.get(i).setAllResultCount(allResultCount);
                        list.get(i).setAllRemainAmount(allRemainAmount);
                        list.get(i).setCountPercent(Long.parseLong(HUNDRED.toString()) - currentPercent);
                    } else {
                        list.get(i).setAllResultCount(allResultCount);
                        list.get(i).setAllRemainAmount(allRemainAmount);
                        //Long countPercent = Math.round(((double) list.get(i).getResultCount() / (double) allResultCount) * HUNDRED);
                        Long countPercent = Math.round(((double) list.get(i).getRemainAmount() / allRemainAmount) * HUNDRED);
                        currentPercent += countPercent;
                        list.get(i).setCountPercent(countPercent);
                    }

                }
            }
        }
        return list;
    }

    /**
     * @Description:风险预警通过bpId查找所有保理租赁合同
     * @Author: Wty
     * @Date: Created om 19:26 2018/5/31
     */
    @Override
    public List<PlmPliContract> selectRwContractsByBpId(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract, int page, int pageSize) {
        if (fiveClassificationContract.getBpId() != null) {
            PageHelper.startPage(page, pageSize);
            if (!(fiveClassificationContract.getCompanyId() != null && fiveClassificationContract.getCompanyId() != 0)) {
                fiveClassificationContract.setCompanyId(iRequest.getCompanyId());
            }
            return mapper.selectRwContractsByBpId2(fiveClassificationContract);
        } else {
            return new ArrayList<>();
        }

    }

    /**
     * @Description:租金催收首页rollTable查询
     * @Author: Wty
     * @Date: Created om 13:27 2018/6/11
     */
    @Override
    public List<Map> rcHomeRollTableQuery(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract, int page, int pageSize) {
        fiveClassificationContract.setCompanyId(iRequest.getCompanyId());
        if (fiveClassificationContract.getIsSettleStatus() != null && !"".equals(fiveClassificationContract.getIsSettleStatus())) {
            fiveClassificationContract.setIsSettleArray(fiveClassificationContract.getIsSettleStatus().split(","));
        }
        Map map = new HashMap();
        map.put("companyId", iRequest.getCompanyId());
        map.put("positionCode", "110");
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        List<Long> userIds = mapper.selectRcUserIds(map);
        List<Map> maps = mapper.rcHomeRollTableQuery(fiveClassificationContract);
//        List<Map> returnMap = new ArrayList<>();
//        if (CollectionUtils.isNotEmpty(userIds)) {
//            if (userIds.contains(iRequest.getUserId())) {
//                for (Map m : maps) {
//                    if (Long.parseLong( m.get("overdueDays").toString())  >= QUARTER_DAYS) {
//                        returnMap.add(m);
//                    }
//                }
//                return returnMap;
//            }
//        }
//        for (Map m : maps) {
//            if (Long.parseLong( m.get("overdueDays").toString()) < QUARTER_DAYS) {
//                returnMap.add(m);
//            }
//        }
        formatDate(sdf, maps);
        return maps;
    }

    private void formatDate(SimpleDateFormat sdf, List<Map> returnMap) {
        returnMap.forEach(m -> {
            if (null != m.get("validFrom")) {
                TIMESTAMP timestamp = (TIMESTAMP) m.get("validFrom");
                try {
                    String date = sdf.format(timestamp.dateValue());
                    m.put("validFrom", date);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @Description:租金催收合同信息具体信息查询
     * @Author: Wty
     * @Date: Created om 13:29 2018/6/11
     */
    @Override
    public List<Map> rcSelectOverdueContractsInfo(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract, int page, int pageSize) {
        fiveClassificationContract.setCompanyId(iRequest.getCompanyId());
        PageHelper.startPage(page, pageSize);
        //PageHelper.orderBy("cs.times");
        if ("CON".equals(fiveClassificationContract.getContractType())) {
            //租赁
            List<Map> res=mapper.selectOverdueConContractData(fiveClassificationContract);
            return res;
        } else {
            //保理
            List<Map> res= mapper.selectOverdueFctContractData(fiveClassificationContract);
            return res;
        }
    }

    /**
     * @Description:租金催收合同基本信息查询
     * @Author: Wty
     * @Date: Created om 16:32 2018/6/11
     */
    @Override
    public List<Map> rcSelectBaseInfo(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract, int page, int pageSize) {
        fiveClassificationContract.setCompanyId(iRequest.getCompanyId());
        if ("CON".equals(fiveClassificationContract.getContractType())) {
            //租赁
            return mapper.rcConContractBaseInfoQuery(fiveClassificationContract);
        } else {
            //保理
            return mapper.rcFctContractBaseInfoQuery(fiveClassificationContract);
        }
    }

    /**
     * @Description:租金催收firstChartQuery
     * @Author: Wty
     * @Date: Created om 13:16 2018/6/12
     */
    @Override
    public List<Map> rcHomeFirstChartQuery(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract, int page, int pageSize) {
        BigDecimal allDueAmount = BigDecimal.ZERO;//所有应收金额
        BigDecimal allOverdueAmount = BigDecimal.ZERO;//所有逾期金额
        BigDecimal allReceivedAmount = BigDecimal.ZERO;
        Map<String,String> map = new HashMap();
        List<Map> allDueAmountList = mapper.rcHomeChartAllDueAmountQuery(fiveClassificationContract);
        if (CollectionUtils.isNotEmpty(allDueAmountList)) {
            allDueAmount = new BigDecimal(allDueAmountList.get(0).get("allDueAmount").toString());
            allReceivedAmount =new BigDecimal(allDueAmountList.get(0).get("allReceivedAmount").toString());
            allOverdueAmount =new BigDecimal(allDueAmountList.get(0).get("allOverdueAmount").toString());
        }
        map.put("allDueAmount", numberFormat.format(allDueAmount));
        map.put("allOverdueAmount", numberFormat.format(allOverdueAmount));
        map.put("allNotOverdueAmount", numberFormat.format(allDueAmount.subtract(allOverdueAmount)));
        if(allDueAmount.compareTo(new BigDecimal(0))==0) {
            map.put("allOverPre", "0%");
            map.put("allReceivedPre", "0%");
        }else{
            map.put("allOverPre", ((allOverdueAmount.multiply(BigDecimal.valueOf(100))).divide(allDueAmount,2,BigDecimal.ROUND_UP)).toPlainString()+"%");
            map.put("allReceivedPre",((new BigDecimal(allReceivedAmount.toString()).multiply(BigDecimal.valueOf(100))).divide(allDueAmount,2,BigDecimal.ROUND_UP)).toPlainString()+"%");
        }

        List<Map> list = new ArrayList<>();
        list.add(map);
        return list;
    }

    /**
     * @Description:租金催收firstChartQuery
     * @Author: Wty
     * @Date: Created om 14:11 2018/6/12
     */
    @Override
    public List<Map> rcHomeSecondChartQuery(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract, int page, int pageSize) {
        fiveClassificationContract.setCompanyId(iRequest.getCompanyId());
        List<Map> overdueAmountList = mapper.rcHomeSecondChartQuery(fiveClassificationContract);
        Map<String,String> map = new HashMap();
        //180天以上
        map.put("halfYearOverDueAmount", numberFormat.format(overdueAmountList.get(0).get("halfYearUpOverdueAmount")));
        //91-180天
        map.put("halfYearDueAmount", numberFormat.format(overdueAmountList.get(0).get("quarterOverdueAmount")));
        //31-90天
        map.put("quarterDueAmount", numberFormat.format(overdueAmountList.get(0).get("quarterOverdueAmount")));
        //30天内
        map.put("monthdDueAmount", numberFormat.format(overdueAmountList.get(0).get("monthOverdueAmount")));

        List<Map> list = new ArrayList<>();
        list.add(map);
        return list;
    }

    /**
     * @Description:copy合同list
     * @Author: Wty
     * @Date: Created om 下午7:02 2018/7/10
     * @param: [contracts]合同LIst
     * @return: java.util.List<hls.core.plm.fc.dto.HlsCusFiveClassificationContract>
     */
    private List<HlsCusFiveClassificationContract> copyFiveClassificationContracts(List<HlsCusFiveClassificationContract> contracts) {
        List<HlsCusFiveClassificationContract> list = new ArrayList<>();
        for (HlsCusFiveClassificationContract c : contracts) {
            list.add(copyFiveClassificationContract(c));
        }
        return list;
    }

    /**
     * @Description:copy单个合同 不copy主键
     * @Author: Wty
     * @Date: Created om 下午7:03 2018/7/10
     * @param: [contract]合同
     * @return: hls.core.plm.fc.dto.HlsCusFiveClassificationContract
     */
    private HlsCusFiveClassificationContract copyFiveClassificationContract(HlsCusFiveClassificationContract contract) {
        HlsCusFiveClassificationContract newContract = new HlsCusFiveClassificationContract();
        BeanUtils.copyProperties(contract, newContract);
        newContract.setFiveClassifyConId(null);
        return newContract;
    }
}
