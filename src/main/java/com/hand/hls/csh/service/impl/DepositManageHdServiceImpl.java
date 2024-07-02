package com.hand.hls.csh.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.StringUtil;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.csh.mapper.DepositManageHdMapper;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.wfl.service.IActivitiStartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.csh.dto.DepositManageHd;
import com.hand.hls.csh.service.IDepositManageHdService;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.hand.hls.cont.service.impl.ConFloatingCalcServiceImpl.logger;

@Service
@Transactional(rollbackFor = Exception.class)
public class DepositManageHdServiceImpl extends BaseServiceImpl<DepositManageHd> implements IDepositManageHdService{
    @Autowired
    private DepositManageHdMapper depositManageHdMapper;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private LoginUserInfoService loginUserInfoService;
    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private IDepositManageHdService depositManageHdService;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;

    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;
    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;



    @Override
    public List<DepositManageHd> selectManageHdById1(DepositManageHd depositManageHd) {
        return this.depositManageHdMapper.selectManageHdById1(depositManageHd);
    }

    /**
     * 提交审批
     *
     * @param requestCtx
     * @param dto
     * @return
     */
    @Override
    public DepositManageHd submitApproval(IRequest requestCtx, DepositManageHd dto,String workFlowType)throws Exception {
        //保存页面信息
//        HlsCusCreditProject hlsCusCreditProject = this.cascadeSubmit(requestCtx, dto);
        //获取申请人
//        HlsEmployee employee = hlsCusEmployeeMapper.getEmployeeCode(requestCtx.getUserId());
//        String employeeCode = employee.getEmployeeCode();
//        requestCtx.setEmployeeCode(requestCtx.getEmployeeCode());
        //获取需要的参数
        /*Long chanceId = dto.getManageHdId();
        DepositManageHd depositManage = depositManageHdMapper.selectByPrimaryKey(chanceId);*/
        List<DepositManageHd> depositManageHdListParam = this.depositManageHdMapper.selectManageHdById1(dto);
//        depositManageHdListParam.add(depositManage);
        DepositManageHd depositManage = depositManageHdListParam.get(0);

        HlsCusConContract hlsCusConContractNormal = new HlsCusConContract();
        hlsCusConContractNormal.setContractId(depositManage.getContractId());
        hlsCusConContractNormal = hlsCusConContractMapper.selectByPrimaryKey(hlsCusConContractNormal);

        if(!"INCEPT".equals(hlsCusConContractNormal.getContractStatus())){
            throw new HlsCusException("合同非起租状态，不能提交!");
        }

        if (depositManage.getExecutionResult() != null){
            if ("APPROVING".equalsIgnoreCase(depositManage.getExecutionResult()) || "APPROVED".equalsIgnoreCase(depositManage.getExecutionResult())
                    || "WRITE_OFF".equalsIgnoreCase(depositManage.getExecutionResult())|| "REJECTED".equalsIgnoreCase(depositManage.getExecutionResult())) {
                throw new HlsCusException("此管理数据执行结果非新建/取消/退回状态，不能再次提交!");
            }
        }

        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        //此次启动的工作流的唯一标识
        params.put("workFlowType", workFlowType);
        //工作流状态(执行结果(NEW-新建，APPROVING-审批中，APPROVED-审批通过，WRITE_OFF-已核销，CANCEL-取消，REJECTED-拒绝))
        params.put("executionResult", depositManage.getExecutionResult());
        //表单的主键
        params.put("manageHdId", depositManage.getManageHdId());
        params.put("contractId", depositManage.getContractId());
        params.put("businessKey", depositManage.getManageHdId());
//        Map<String, Object> evenParams = new HashMap<>();
        activitiStartService.start(requestCtx, depositManageHdListParam, params);

        //插入事件
        /*String userName = "";
        if (loginUserInfoService.queryUserInfo(requestCtx.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(requestCtx.getEmployeeCode())).get(0).getUserName();
        }
        //消息用于动态
        String msg = userName + "提交了一条保证金流程:" + depositManage.getContractNumber()+";方式:" + depositManage.getHandlingMethodN();
        evenParams.put("message", msg);
        evenParams.put("noticeTitle", depositManage.getHandlingMethodN());
        evenParams.put("url", "");
        evenParams.put("level", 2L);
        evenParams.put("noticeType", "NOTICE");
        evenParams.put("sourceUserId", requestCtx.getUserId());
        sysEventService.eventSave(requestCtx, depositManage.getChanceId(), depositManage.getDocumentCategory(), depositManage.getDocumentType(), "HLS_CREDIT_LINE_CHANCE", "HLS_CREDIT_LINE_CHANCE", "P2D", evenParams);
*/

        //设置审批中的状态(执行结果(NEW-新建，APPROVING-审批中，APPROVED-审批通过，WRITE_OFF-已核销，CANCEL-取消，REJECTED-拒绝))
        DepositManageHd chance = depositManageHdMapper.selectByPrimaryKey(dto.getManageHdId());
        chance.setExecutionResult("APPROVING");
        chance = self().updateByPrimaryKeySelective(requestCtx, chance);

        //当合同为起租状态时，更新NORMAL合同状态为暂挂
        if("INCEPT".equals(hlsCusConContractNormal.getContractStatus())) {
            hlsCusConContractNormal.setContractStatus("PENDING");//暂挂NORMAL合同
            hlsCusConContractMapper.updateByPrimaryKey(hlsCusConContractNormal);
        }
        if("BZJ_CLFSBG".equals(workFlowType)){
            //设置变更标记校验是否已经变更成功过或变更中
            hlsCusConContractNormal.setDepositDeductionChangedFlag("Y");
        }

        return chance;
    }


    @Override
    public List<DepositManageHd> selectDepositManageByField(IRequest iRequest, DepositManageHd dto, Integer page, Integer pageSize, String sortName, String sortOrder) {
        String orderBy = null;
        if(sortName!=null){
            if(orderBy==null){
                orderBy=sortName+" "+sortOrder;
            }else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }

        if (page != null && pageSize != null) {
            PageHelper.startPage(page, pageSize);
            if(net.logstash.logback.encoder.org.apache.commons.lang.StringUtils.isNotEmpty(orderBy)){
                PageHelper.orderBy(orderBy);
            }
        }
        return depositManageHdMapper.selectDepositManageByField(dto);
    }

    //保存后修改现金流
    @Override
    public DepositManageHd changeCashflow(IRequest request, DepositManageHd depositManageHd) throws Exception{

        Long chanceId = depositManageHd.getManageHdId();
        depositManageHd = depositManageHdMapper.selectByPrimaryKey(chanceId);

        //查询变更合同
        HlsCusConContract hlsCusConContractNew = new HlsCusConContract();
        hlsCusConContractNew.setContractId(depositManageHd.getChangeContractId());
        hlsCusConContractNew = hlsCusConContractMapper.selectByPrimaryKey(hlsCusConContractNew);

        //查询原合同的保证金金额
        HlsCusConContractCashflow contractCashflowFln = new HlsCusConContractCashflow();
        contractCashflowFln.setCfItem(52L);//保证金退还
        contractCashflowFln.setContractId(hlsCusConContractNew.getRefContractId());//原合同id
//        contractCashflowFln.setTimes(times);//小于等于多少期的
        Double depositAmt = hlsCusConContractCashflowMapper.querySubsectionCashflowDueAmount(contractCashflowFln);


        //查询需要计算变更的现金流(52保证金退还类型的现金流)
        HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
        hlsCusConContractCashflow.setContractId((Long) hlsCusConContractNew.getContractId());
        hlsCusConContractCashflow.setSubsectionStartTime(0L);
        hlsCusConContractCashflow.setSubsectionEndTime(9999L);

        //实际上就是对退还类型的现金流进行操作：抵扣（即倒数期数足额生成退还类型的现金流）；退还（即合并为最后一期退还类型的现金流）
        //PERIOD_FINAL_RETURN 保证金退还    PERIOD_FINAL_DEDUCTIBLE 保证金抵扣租金
        if ("PERIOD_FINAL_RETURN".equals(depositManageHd.getAfterChangeWay())){
//            hlsCusConContractCashflow.setCfItem(52L);//保证金退还
            hlsCusConContractCashflow.setCfItem(null);//查询请求参数置空
            List<HlsCusConContractCashflow> cashflowList = hlsCusConContractCashflowMapper.querySubsectionCashflow(hlsCusConContractCashflow);

            List<HlsCusConContractCashflow> cashflowListSec52 = cashflowList.stream().filter(cashflow-> cashflow.getCfItem().equals(52L)).sorted(Comparator.comparing(HlsCusConContractCashflow::getTimes).reversed()).collect(Collectors.toList());
            logger.debug("保证金退还类型现金流条数[{}]",cashflowListSec52.size());
            List<HlsCusConContractCashflow> cashflowListThir1Desc = cashflowList.stream().filter(cashflow-> cashflow.getCfItem().equals(1L)).sorted(Comparator.comparing(HlsCusConContractCashflow::getTimes).reversed()).collect(Collectors.toList());
            logger.debug("租金类型现金流条数[{}]",cashflowListThir1Desc.size());

            if(cashflowList.size()<1){
                throw new HlsCusException("不存在保证金退还类型的现金流，无法进行变更!");
            }

            //校验是否已经核销部分金额
            for (HlsCusConContractCashflow cashflow : cashflowListSec52) {
                if(cashflow.getReceivedAmount() != null && cashflow.getReceivedAmount()>0){
                    throw new HlsCusException("退还类型现金流已经存在部分核销或者完全核销，无法进行变更!");
                }
            }

            Long times = 0L;
            for (HlsCusConContractCashflow cashflow : cashflowList) {
                if(times<cashflow.getTimes()){
                    times = cashflow.getTimes();
                }
            }
            HlsCusConContractCashflow hlsCusConContractCashflowNew = hlsCusConContractCashflowMapper.selectByPrimaryKey(cashflowListSec52.get(0).getCashflowId());
            hlsCusConContractCashflowNew.setTimes(times);
            hlsCusConContractCashflowNew.setDueAmount(depositAmt);
            hlsCusConContractCashflowNew.setPrincipal(null);
            hlsCusConContractCashflowNew.setInterest(null);
            //设置为最后一期租金的日期，倒叙list
            hlsCusConContractCashflowNew.setDueDate(cashflowListThir1Desc.get(0).getDueDate());
            hlsCusConContractCashflowNew.setCalcDate(cashflowListThir1Desc.get(0).getCalcDate());
            hlsCusConContractCashflowNew.setFinIncomeDate(cashflowListThir1Desc.get(0).getFinIncomeDate());
            hlsCusConContractCashflowService.insertSelective(request, hlsCusConContractCashflowNew);
            for (HlsCusConContractCashflow cashflow : cashflowListSec52) {
                hlsCusConContractCashflowService.deleteByPrimaryKey(cashflow);
            }
        }
        //PERIOD_FINAL_DEDUCTIBLE 保证金抵扣租金
        if ("PERIOD_FINAL_DEDUCTIBLE".equals(depositManageHd.getAfterChangeWay())){
            hlsCusConContractCashflow.setCfItem(null);//查询请求参数置空
            List<HlsCusConContractCashflow> cashflowList = hlsCusConContractCashflowMapper.querySubsectionCashflow(hlsCusConContractCashflow);

            List<HlsCusConContractCashflow> cashflowListSec = cashflowList.stream().filter(cashflow-> cashflow.getCfItem().equals(52L)).sorted(Comparator.comparing(HlsCusConContractCashflow::getTimes).reversed()).collect(Collectors.toList());
            logger.debug("保证金退还类型现金流条数[{}]",cashflowListSec.size());
            List<HlsCusConContractCashflow> cashflowListThir = cashflowList.stream().filter(cashflow-> cashflow.getCfItem().equals(1L)).sorted(Comparator.comparing(HlsCusConContractCashflow::getTimes).reversed()).collect(Collectors.toList());
            logger.debug("租金类型现金流条数[{}]",cashflowListThir.size());
            //新增每一期的抵扣 退还类型的现金流
            loop:for (HlsCusConContractCashflow cashflow1 : cashflowListThir) {
                if(depositAmt > 0) {
                    HlsCusConContractCashflow hlsCusConContractCashflowThir = new HlsCusConContractCashflow();
                    hlsCusConContractCashflowThir = cashflow1;
//                hlsCusConContractCashflowThir.setDueAmount(cashflow.getUnReceivedAmount());
                    hlsCusConContractCashflowThir.setCfItem(52L);
                    //应是流出现金流
                    hlsCusConContractCashflowThir.setCfDirection("OUTFLOW");
                    if(depositAmt<cashflow1.getDueAmount()){
                        hlsCusConContractCashflowThir.setDueAmount(depositAmt);
                    }
                    hlsCusConContractCashflowService.insertSelective(request, hlsCusConContractCashflowThir);
                    depositAmt = depositAmt - cashflow1.getDueAmount();
                }else{
                    break loop;
                }
            }
            //删除退还现金流
            for (HlsCusConContractCashflow cashflow2 : cashflowListSec) {
                hlsCusConContractCashflowService.deleteByPrimaryKey(cashflow2);
            }


        }


        logger.debug("旧合同id[{}]",hlsCusConContractNew.getRefContractId());
        logger.debug("新合同id[{}]",hlsCusConContractNew.getContractId());
        //保存保证金管理表数据
//        depositManageHd.setChangeContractId(hlsCusConContractNew.getContractId());
//        depositManageHd=self().insertSelective(request,depositManageHd);
        return depositManageHd;

    }

    //期中代付 校验
    @Override
    public DepositManageHd changeCheck(IRequest request, DepositManageHd depositManageHd) throws Exception {

        //查询是否存在变更类型的保证金管理数据 根据合同id查询 1保证金处理方式变更；0保证金代付期中租金
        List<DepositManageHd> depositManageHdLists = depositManageHdMapper.selectManageHdById1(depositManageHd);
        for (DepositManageHd dmh : depositManageHdLists) {
            if (StringUtil.isNotEmpty(dmh.getHandlingMethod()) && "0".equals(dmh.getHandlingMethod())) {
                throw new HlsCusException("已经创建过保证金代付期中租金的方案，无法重复创建!");
            }
        }
        return depositManageHd;
    }

    //保存chang_req，创建变更数据
    @Override
    public DepositManageHd changeCreate(IRequest request, DepositManageHd depositManageHd) throws Exception{

        //查询是否存在变更类型的保证金管理数据 根据合同id查询 1保证金处理方式变更；0保证金代付期中租金
        List<DepositManageHd> depositManageHdLists = depositManageHdMapper.selectManageHdById1(depositManageHd);
        /*for(DepositManageHd dmh:depositManageHdLists){
            if(StringUtil.isNotEmpty(dmh.getHandlingMethod()) && ("1".equals(dmh.getHandlingMethod()) || "2".equals(dmh.getHandlingMethod())||"3".equals(dmh.getHandlingMethod()))){
                throw new HlsCusException("已经创建过保证金处理方式变更的方案，无法重复创建!");
            }
        }*/

        //查询合同是否存在退还类型的现金流
        HlsCusConContractCashflow contractCashflowFln = new HlsCusConContractCashflow();
        contractCashflowFln.setCfItem(52L);//保证金退还
        contractCashflowFln.setSubsectionStartTime(0L);
        contractCashflowFln.setSubsectionEndTime(9999L);
        contractCashflowFln.setContractId(depositManageHd.getContractId());//原合同id
        List<HlsCusConContractCashflow> cashflowList = hlsCusConContractCashflowMapper.querySubsectionCashflow(contractCashflowFln);
        if(cashflowList.size()<1){
            throw new HlsCusException("不存在保证金退还类型的现金流，无法进行变更!");
        }

//        Long chanceId = depositManageHd.getManageHdId();
//        DepositManageHd depositManage = depositManageHdMapper.selectByPrimaryKey(chanceId);

        //原始合同
        HlsCusConContract hlsCusConContractNormal = new HlsCusConContract();
        hlsCusConContractNormal.setContractId(depositManageHd.getContractId());
        hlsCusConContractNormal = hlsCusConContractMapper.selectByPrimaryKey(hlsCusConContractNormal);


        if(("2".equals(depositManageHd.getHandlingMethod()) || "3".equals(depositManageHd.getHandlingMethod())) && "Y".equals(hlsCusConContractNormal.getDepositDeductionChangedFlag())){
            throw new HlsCusException("已经变更过，无法再次变更!");
        }

        //创建变更合同
        //复制一份用作变更的数据, dataType:CHANGE_REQ
        /*depositManageHd = selectByPrimaryKey(request, depositManageHd);
        HlsCusConContract hlsCusConContractNew = new HlsCusConContract();
        Map<String, String> map1 = hlsBeanRefUtilService.getFieldValueMap(hlsCusConContractNormal);
        hlsBeanRefUtilService.setFieldValue(hlsCusConContractNew, map1);
        hlsCusConContractNew.setRefContractId(hlsCusConContractNormal.getContractId());*/
        HlsCusConContract hlsCusConContractNew = new HlsCusConContract();
        hlsCusConContractNew = hlsCusConContractMapper.selectByPrimaryKey(hlsCusConContractNormal);
        hlsCusConContractNew.setRefContractId(hlsCusConContractNormal.getContractId());
        hlsCusConContractNew.setContractId(null);
        hlsCusConContractNew.setDataClass("CHANGE_REQ");
        hlsCusConContractNew = hlsCusConContractService.insertSelective(request, hlsCusConContractNew);
        logger.debug("旧合同id[{}]",hlsCusConContractNew.getRefContractId());
        logger.debug("新合同id[{}]",hlsCusConContractNew.getContractId());
        //合同现金流备份
        chanceBackUp(request,hlsCusConContractNew);
//        hlsCusConContractCashflowService.cloneCashflow(normalContract, changeContract);
        //保存保证金管理表数据
        depositManageHd.setChangeContractId(hlsCusConContractNew.getContractId());
        depositManageHd.setExecutionResult("NEW");//设置新建状态
        if ("2".equals(depositManageHd.getHandlingMethod())) {
            depositManageHd.setAfterChangeWay("PERIOD_FINAL_RETURN");
        }
        if ("3".equals(depositManageHd.getHandlingMethod())) {
            depositManageHd.setAfterChangeWay("PERIOD_FINAL_DEDUCTIBLE");
        }
        depositManageHd=self().insertSelective(request,depositManageHd);
//        depositManageHd.setContractId(hlsCusConContractNew.getContractId());
        return depositManageHd;

    }
    //合同现金流备份
    public void chanceBackUp(IRequest iRequest, HlsCusConContract hlsCusConContract) throws Exception{
        HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
        hlsCusConContractCashflow.setContractId((Long) hlsCusConContract.getRefContractId());
        List<HlsCusConContractCashflow> cashflowList = hlsCusConContractCashflowService.queryContractRepCashflowByContractId(hlsCusConContractCashflow, 1, 99999);
        /*cashflowList = cashflowList.stream().filter(cashflow -> cashflow.getCfItem().equals(CF_ITEM_DUE_AMOUNT))
                .sorted(Comparator.comparing(HlsCusConContractCashflow::getDueDate)).collect(Collectors.toList());*/
        logger.debug("现金流条数[{}]",cashflowList.size());
        for (HlsCusConContractCashflow cashflow : cashflowList) {
            HlsCusConContractCashflow hlsCashflow = new HlsCusConContractCashflow();
            hlsCashflow = cashflow;
            hlsCashflow.setContractId(hlsCusConContract.getContractId());
            hlsCusConContractCashflowService.insertSelective(iRequest, hlsCashflow);
        }

    }
}