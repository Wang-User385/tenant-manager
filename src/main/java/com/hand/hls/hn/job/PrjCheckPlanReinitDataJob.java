package com.hand.hls.hn.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.hn.dto.PrjCheckPlan;
import com.hand.hls.hn.mapper.PrjCheckPlanMapper;
import com.hand.hls.hn.service.IPrjCheckPlanService;
import com.hand.hls.hn.service.ISelectAllConContractService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.sys.dto.SysUserAllocation;
import com.hand.hls.sys.mapper.SysUserAllocationMapper;
import com.hand.hls.utils.MathUtil;
import hls.core.sys.event.service.SysEventService;
import org.docx4j.wml.P;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * description
 * 租后检查计划
 * 每个季度月底检查 当月起租的合同不检查
 *
 * @author zhangdan 2022/05/27
 */
@Transactional(rollbackFor = Exception.class)
public class PrjCheckPlanReinitDataJob extends AbstractJob {


    @Autowired
    private ISelectAllConContractService selectAllConContractService;

    @Autowired
    private PrjCheckPlanMapper prjCheckPlanMapper;

    @Autowired
    private IPrjCheckPlanService prjCheckPlanService;
    @Autowired
    private SysUserAllocationMapper sysUserAllocationMapper;
    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;

    public List<String> getQuarter(int month) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        List<String> dates = new ArrayList<String>();
        for (int a = 1; a <= 4; a++) {
            Calendar calendar = Calendar.getInstance();
            int lastMonth = (int) MathUtil.sub(MathUtil.mul(a, 3), 2 - month);
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, lastMonth);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            Date dd = calendar.getTime();
            String dc = sdf.format(dd);
            dates.add(dc);
        }
        return dates;
    }

    public Date getQuarterLastDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date dd = calendar.getTime();
        return dd;
    }

    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {
        IRequest iRequest = (IRequest) jobExecutionContext.getMergedJobDataMap().get("requestContext");

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateNowStr = sdf.format(d);

        List<String> dates = getQuarter(0);
        Date checkDate = getQuarterLastDay();
        String checkDateStr = sdf.format(checkDate);

        if (dates.contains(dateNowStr)) {

            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setProjectStatus("APPROVED");
            hlsCusPrjProject.setDataClass("VIRTUAL_CON");
            List<HlsCusPrjProject> hlsCusPrjProjectList = hlsCusPrjProjectMapper.queryAllApproveContract(hlsCusPrjProject);

            for (int i = 0; i < hlsCusPrjProjectList.size(); i++) {
                HlsCusPrjProject hlsCusPrjProject1 = hlsCusPrjProjectList.get(i);
                PrjCheckPlan prjCheckPlan0 = new PrjCheckPlan();
                prjCheckPlan0.setContractId(hlsCusPrjProject1.getProjectId());
                prjCheckPlan0.setPlanCheckDate(checkDate);

                String planNumber = "";
                List<PrjCheckPlan> prjCheckPlan1 = prjCheckPlanMapper.selectPrjCheckPlanByContractId(prjCheckPlan0);

                if (prjCheckPlan1.size() == 0) {
                    PrjCheckPlan prjCheckPlan = new PrjCheckPlan();
                    prjCheckPlan.setContractId(hlsCusPrjProject1.getProjectId());
                    prjCheckPlan.setContractNumber(hlsCusPrjProject1.getContractNumber());
                    prjCheckPlan.setContractStartDate(hlsCusPrjProject1.getFirstPayDate());
                    prjCheckPlan.setEmployeeId(hlsCusPrjProject1.getHostProjectManager());
                    List<PrjCheckPlan> prjCheckPlanAll = prjCheckPlanMapper.selectPrjCheckPlan(new PrjCheckPlan());
                    if (prjCheckPlanAll.size() == 0) {
                        planNumber = "JH-" + checkDateStr + "-0001";
                    } else {
                        String tempNumber = prjCheckPlanAll.get(0).getPlanNumber();
                        int tmp = Integer.valueOf(tempNumber.substring(tempNumber.length() - 4)).intValue() + 1;
                        String code = String.format("%04d", tmp);
                        planNumber = "JH-" + checkDateStr + "-" + code;
                    }
                    prjCheckPlan.setPlanNumber(planNumber);
                    prjCheckPlan.setPrjRegularAssign("regular_check");

                    if (hlsCusPrjProject1.getFirstPayDate() != null) {
                        prjCheckPlan.setContractStartDate(hlsCusPrjProject1.getFirstPayDate());

                        prjCheckPlan.setPlanCheckDate(checkDate);
                        prjCheckPlan.setAssignStatus("unachieve");
                        PrjCheckPlan checkPlan = prjCheckPlanService.insert(iRequest, prjCheckPlan);
                    }
                }
            }
        }

        //季末15天前发通知
        List<String> dates2 = getQuarter(1);
        String dayNow = dateNowStr.substring(6);
        String dayNotice = checkDateStr.substring(6);
        if (dates2.contains(dateNowStr) && dayNotice.compareTo(dayNow) == 15) {
            PrjCheckPlan conCheckPlan = new PrjCheckPlan();
            conCheckPlan.setPlanCheckDate(checkDate);

            List<PrjCheckPlan> prjCheckPlanNotice = prjCheckPlanMapper.selectPrjCheckPlanByContractId(conCheckPlan);
            for (PrjCheckPlan check : prjCheckPlanNotice) {
                SysUserAllocation sysUserAllocation = new SysUserAllocation();
                sysUserAllocation.setUserId(check.getEmployeeId());
                List<SysUserAllocation> sysUserAllocations = sysUserAllocationMapper.select(sysUserAllocation);
                for (SysUserAllocation people : sysUserAllocations) {
                    Map<String, Object> evenParams = new HashMap();
                    evenParams.put("message", "需对编号为" + check.getContractNumber() + "的合同进行租后检查处理！");
                    evenParams.put("noticeTitle", "租后检查通知");
                    evenParams.put("url", "");
                    evenParams.put("level", 1L);
                    evenParams.put("noticeType", "NOTICE");
                    evenParams.put("allocation_id", people.getAllocationId());

                    String documentCategory = "RENT_CHECK_NOTICE";
                    String documentType = "RENT_CHECK_NOTICE";
                    iRequest.setUserId(people.getUserId());
                    this.sysEventService.eventSave(iRequest, check.getPlanId(), documentCategory, documentType, "RENT_CHECK_NOTICE", "RENT_CHECK", "P2D", evenParams);
                }
            }
        }
    }
}
