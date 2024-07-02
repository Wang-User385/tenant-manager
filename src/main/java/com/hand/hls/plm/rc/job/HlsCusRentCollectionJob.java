package com.hand.hls.plm.rc.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.plm.rc.dto.HlsCusRentCollectionRule;
import com.hand.hls.plm.rc.service.HlsCusRentCollectionRuleService;
import com.hand.hls.wfl.service.IActivitiStartService;
import lombok.val;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * description
 *  租金催收job 提起工作流
 * @author yuanyuan 2019/03/25 5:56 PM
 */
public class HlsCusRentCollectionJob extends AbstractJob {

    @Autowired
    private HlsCusRentCollectionRuleService rentCollectionRuleService;


    @Autowired
    private IActivitiStartService activitiStartService;

    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {
        //默认系统发起工作流
        IRequest iRequest= RequestHelper.newEmptyRequest();
        iRequest.setEmployeeCode("ADMIN");
        iRequest.setUserName("admin");
        iRequest.setCompanyId(248L);
        iRequest.setUserId(10001L);
        iRequest.setLocale("zh_CN");

        Long allocationId=144L;
        iRequest.setAttribute("allocationId",allocationId);

        iRequest.setAttribute("employeeCode","ADMIN");

        //开始流程
//        val params = new HashMap<String, Object>();


        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "PLM_RENT_COLLECTION_WFL");

          //当前日期 去掉时分秒
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date nowDate = sdf.parse(sdf.format(new Date()));
//        Calendar calendar = Calendar.getInstance();
        //查询出逾期的合同
        List<HlsCusRentCollectionRule> collectionRules = rentCollectionRuleService.selectOverDateContract()
                .stream().filter(item->item.getOverdueDate()!=null).collect(Collectors.toList());
        for(HlsCusRentCollectionRule collectionRule:collectionRules) {
           /* while (true) {
                calendar.add(Calendar.DATE,collectionRule.getCollectionDays().intValue());
                if (nowDate.compareTo(calendar.getTime())==0) {
                  //发起工作流
                    List<HlsCusRentCollectionRule> ruleList=new ArrayList<>();
                    ruleList.add(collectionRule);
                    activitiStartService.start(iRequest, ruleList, params);

                    collectionRule.setLastCollectionDate(new Date());
                    rentCollectionRuleService.updateLastCollectionDate(collectionRule);
                    break;
                }else if(nowDate.compareTo(calendar.getTime())<0){
                     break;
                }
            }*/
            //除得尽
            if (collectionRule.getCollectionDays()!=0&&
                collectionRule.getOverdueDays()%collectionRule.getCollectionDays()==0)
            {
                startWorkFlow(iRequest, params, collectionRule);
            }
        }

    }
    @Transactional
    public void startWorkFlow(IRequest iRequest, HashMap<String, Object> params, HlsCusRentCollectionRule collectionRule) {
        List<HlsCusRentCollectionRule> ruleList= Stream.of(collectionRule).collect(Collectors.toList());
        activitiStartService.start(iRequest, ruleList, params);
        collectionRule.setLastCollectionDate(new Date());
        rentCollectionRuleService.updateLastCollectionDate(collectionRule);
    }
}
