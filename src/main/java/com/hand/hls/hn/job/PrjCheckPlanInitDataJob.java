package com.hand.hls.hn.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractBp;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqHdMapper;
import com.hand.hls.eas.service.IHlsCusEasLoginService;
import com.hand.hls.hn.dto.CheckPlanConContract;
import com.hand.hls.hn.dto.PrjCheckPlan;
import com.hand.hls.hn.mapper.PrjCheckPlanMapper;
import com.hand.hls.hn.mapper.SelectAllConContractMapper;
import com.hand.hls.hn.service.IPrjCheckPlanService;
import com.hand.hls.hn.service.ISelectAllConContractService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.sys.dto.SysUserAllocation;
import com.hand.hls.sys.mapper.SysUserAllocationMapper;
import hls.core.sys.event.service.SysEventService;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * description
 * 租后检查计划初始化数据
 *
 * @author wangchao 2020/05/14 5:56 PM
 */
public class PrjCheckPlanInitDataJob extends AbstractJob {


    @Autowired
    private ISelectAllConContractService selectAllConContractService;

    @Autowired
    private PrjCheckPlanMapper prjCheckPlanMapper;

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private IPrjCheckPlanService prjCheckPlanService;
    @Autowired
    private SysUserAllocationMapper sysUserAllocationMapper;
    @Autowired
    private SysEventService sysEventService;

    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {
        IRequest iRequest = (IRequest) jobExecutionContext.getMergedJobDataMap().get("requestContext");

        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectStatus("APPROVED");
        hlsCusPrjProject.setDataClass("VIRTUAL_CON");
        //测试
        //hlsCusPrjProject.setProjectId(7591L);
        List<HlsCusPrjProject> hlsCusPrjProjectList = hlsCusPrjProjectMapper.queryAllApproveContract(hlsCusPrjProject);

        for (int i = 0; i < hlsCusPrjProjectList.size(); i++) {
            HlsCusPrjProject HlsCusPrjProject1 = (HlsCusPrjProject) hlsCusPrjProjectList.get(i);
            //后续操作
            PrjCheckPlan prjCheckPlan0 = new PrjCheckPlan();
            prjCheckPlan0.setContractId(HlsCusPrjProject1.getProjectId());
            //在这里进行
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String dateNowStr = sdf.format(d);
            String planNumber = "";
            String finalPossCategory = "NORMAL";
            Date newDate = null;
            Calendar curr = Calendar.getInstance();
            List<PrjCheckPlan> prjCheckPlan1 = prjCheckPlanMapper.selectPrjCheckPlanByContractId(prjCheckPlan0);
            //目前设置 finalPossCategory  最终资产分类

            //第一次预警：
            //如果最终资产分类结果为：正常类或者为空  起租日加俩个月
            //如果最终资产分类结果为：非正常类        起租日
            //第二次预警：
            //如果最终资产分类结果为：正常类或者为空  计划检查时间加三个月
            //如果最终资产分类结果为：非正常类        计划检查时间加一个月

            //第一次警示（插入）
            if (prjCheckPlan1.size() == 0) {

                for (int j = 0; j < 2; j++) {
                    PrjCheckPlan prjCheckPlan = new PrjCheckPlan();
                    prjCheckPlan.setContractId(HlsCusPrjProject1.getProjectId());
                    prjCheckPlan.setContractNumber(HlsCusPrjProject1.getContractNumber());
                    prjCheckPlan.setContractStartDate(HlsCusPrjProject1.getFirstPayDate());
                    //prjCheckPlan.setEmployeeId(HlsCusPrjProject1.getEmployeeId());
                    prjCheckPlan.setEmployeeId(HlsCusPrjProject1.getHostProjectManager());
                    List<PrjCheckPlan> prjCheckPlan2 = prjCheckPlanMapper.selectPrjCheckPlan(new PrjCheckPlan());
                    if (prjCheckPlan2.size() == 0) {
                        planNumber = "ZHJH-" + dateNowStr + "-0001";
                    } else {
                        String tempNumber = prjCheckPlan2.get(0).getPlanNumber();
                        int tmp = Integer.valueOf(tempNumber.substring(tempNumber.length() - 4)).intValue() + 1;
                        String code = String.format("%04d", tmp);
                        planNumber = "ZHJH-" + dateNowStr + "-" + code;
                    }
                    prjCheckPlan.setPlanNumber(planNumber);

                    if (j == 0) {
                        prjCheckPlan.setPrjRegularAssign("prj_check");
                    } else {
                        prjCheckPlan.setPrjRegularAssign("regular_check");
                    }

                    if (HlsCusPrjProject1.getFirstPayDate() != null) {
                        curr.setTime(HlsCusPrjProject1.getFirstPayDate());
                        if ("NORMAL".equals(finalPossCategory) || finalPossCategory == null || "".equals(finalPossCategory)) {
                            curr.set(Calendar.MONTH, curr.get(Calendar.MONTH) + 2);
                            newDate = curr.getTime();
                        } else {
                            curr.set(Calendar.MONTH, curr.get(Calendar.MONTH));
                            newDate = curr.getTime();
                        }
                        Date date = new Date();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String nowDate = dateFormat.format(date);
                        if (dateFormat.format(newDate).equals(nowDate)) {
//                        Long data1 = date.getTime();
//                        Long data2 = newDate.getTime();
//                        if (data2 < data1) {
                            prjCheckPlan.setContractStartDate(HlsCusPrjProject1.getFirstPayDate());
                            //add by 20211123 再加一个月作为计划日期  有问题请咨询涂广新项目经理
                            curr.set(Calendar.MONTH, curr.get(Calendar.MONTH) + 1);
                            newDate = curr.getTime();
                            prjCheckPlan.setPlanCheckDate(newDate);
                            prjCheckPlan.setAssignStatus("unachieve"); //任务状态---未完成
                            PrjCheckPlan checkPlan = prjCheckPlanService.insert(iRequest, prjCheckPlan);

                            //
                            SysUserAllocation sysUserAllocation = new SysUserAllocation();
                            sysUserAllocation.setUserId(HlsCusPrjProject1.getHostProjectManager());
                            List<SysUserAllocation> sysUserAllocations = sysUserAllocationMapper.select(sysUserAllocation);
                            for (SysUserAllocation people : sysUserAllocations) {
                                Map<String, Object> evenParams = new HashMap();
                                evenParams.put("message", "需对编号为" + checkPlan.getContractNumber() + "的合同进行资产分类与租后检查处理！");
                                evenParams.put("noticeTitle", "合同结束通知");
                                evenParams.put("url", "");
                                evenParams.put("level", 1L);
                                evenParams.put("noticeType", "NOTICE");
                                evenParams.put("allocation_id", people.getAllocationId());

                                String documentCategory = "RENT_CHECK_WFL";
                                String documentType = "RENT_CHECK_WFL";
                                iRequest.setUserId(people.getUserId());
                                this.sysEventService.createEvent(iRequest, checkPlan.getPlanId(), documentCategory, documentType, evenParams);

                            }

                        }
                    }


                }


                //已经进行过警示（更新）
            } else {

                for (PrjCheckPlan checkPlan : prjCheckPlan1) {
                    PrjCheckPlan prjCheckPlan = new PrjCheckPlan();
                    prjCheckPlan.setContractId(HlsCusPrjProject1.getProjectId());
                    prjCheckPlan.setContractNumber(HlsCusPrjProject1.getContractNumber());
                    prjCheckPlan.setContractStartDate(HlsCusPrjProject1.getFirstPayDate());
                    //prjCheckPlan.setEmployeeId(HlsCusPrjProject1.getEmployeeId());
                    prjCheckPlan.setEmployeeId(HlsCusPrjProject1.getHostProjectManager());
                    List<PrjCheckPlan> prjCheckPlan2 = prjCheckPlanMapper.selectPrjCheckPlan(new PrjCheckPlan());
                    String tempNumber = prjCheckPlan2.get(0).getPlanNumber();
                    int tmp = Integer.valueOf(tempNumber.substring(tempNumber.length() - 4)).intValue() + 1;
                    String code = String.format("%04d", tmp);
                    planNumber = "ZHJH-" + dateNowStr + "-" + code;
                    prjCheckPlan.setPlanNumber(planNumber);

                    if (HlsCusPrjProject1.getFirstPayDate() != null && checkPlan.getPlanCheckDate() != null) {
                        curr.setTime(checkPlan.getPlanCheckDate());
                        if ("NORMAL".equals(finalPossCategory) || finalPossCategory == null || "".equals(finalPossCategory)) {
                            curr.set(Calendar.MONTH, curr.get(Calendar.MONTH) + 2);
                            newDate = curr.getTime();
                        } else {
                            curr.set(Calendar.MONTH, curr.get(Calendar.MONTH));
                            newDate = curr.getTime();
                        }
                        Date date = new Date();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String nowDate = dateFormat.format(date);
                        if (dateFormat.format(newDate).equals(nowDate)) {
                            prjCheckPlan.setContractStartDate(HlsCusPrjProject1.getFirstPayDate());
                            curr.set(Calendar.MONTH, curr.get(Calendar.MONTH) + 1);
                            newDate = curr.getTime();
                            prjCheckPlan.setPlanCheckDate(newDate);
                            prjCheckPlan.setAssignStatus("unachieve"); //任务状态---未完成
                            prjCheckPlanService.insertSelective(iRequest, prjCheckPlan);

                            SysUserAllocation sysUserAllocation = new SysUserAllocation();
                            sysUserAllocation.setUserId(HlsCusPrjProject1.getHostProjectManager());
                            List<SysUserAllocation> sysUserAllocations = sysUserAllocationMapper.select(sysUserAllocation);
                            for (SysUserAllocation people : sysUserAllocations) {
                                Map<String, Object> evenParams = new HashMap();
                                evenParams.put("message", "需对编号为" + checkPlan.getContractNumber() + "的合同进行资产分类与租后检查处理！");
                                evenParams.put("noticeTitle", "合同结束通知");
                                evenParams.put("url", "");
                                evenParams.put("level", 1L);
                                evenParams.put("noticeType", "NOTICE");
                                evenParams.put("allocation_id", people.getAllocationId());

                                String documentCategory = "RENT_CHECK_WFL";
                                String documentType = "RENT_CHECK_WFL";
                                iRequest.setUserId(people.getUserId());
                                this.sysEventService.createEvent(iRequest, checkPlan.getPlanId(), documentCategory, documentType, evenParams);

                            }
                        }
                    }
                }
            }
        }

        //租后检查计划的JOB需要调整，添加条件：当同一个项目下任意一个合同的【租后检查、资产分类评级】审批通过，自动更新项目下所有合同的任务状态为已完成。
        //查询已完成的计划任务的项目Id,任务类型
        List<PrjCheckPlan> prjCheckPlans = prjCheckPlanMapper.selectAchievedPrjCheckPlan();
        for (PrjCheckPlan prjCheckPlan : prjCheckPlans) {
            //更新项目ID为projectId下的所有的合同检查计划为已完成
            //prjCheckPlanMapper.updateAchievedPrjCheckPlanByProject(prjCheckPlan.getProjectId(), prjCheckPlan.getPrjRegularAssign(),prjCheckPlan.getActualFinishDate());
            //更新同一客户的合同检查计划为已完成
            prjCheckPlanMapper.updateAchievedPrjCheckPlanByBpId(prjCheckPlan.getBpId(),prjCheckPlan.getActualFinishDate());
        }
    }
}
