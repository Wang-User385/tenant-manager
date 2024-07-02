package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.plm.pli.dto.HlsCusPostloanInspection;
import com.hand.hls.plm.pli.dto.PlmPliUpcoming;
import com.hand.hls.plm.pli.mapper.PlmPliUpcomingMapper;
import com.hand.hls.plm.pli.service.HlsCusIPostloanInspectionService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description:贷后检查工作流结束保存事件
 * @Author: wty
 * @Date: Created in 16:51 2018/5/21
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPlmPostloanInspectionSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private HlsCusIPostloanInspectionService postloanInspectionService;

    @Autowired
    private PlmPliUpcomingMapper plmPliUpcomingMapper;

    private static final String REGULAR = "REGULAR";
    private static final String IRREGULAR = "IRREGULAR";

    private static final String ON_SITE_INSPECT = "ON_SITE_INSPECT";
    private static final String OFF_SITE_INSPECT = "OFF_SITE_INSPECT";
    private static final String ALL = "ALL";

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest iRequest = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String employeeCode = (String) delegateExecution.getVariable("startUserName");
        String postloanInspection = (String) delegateExecution.getVariable("postloanInspection");
        HlsCusPostloanInspection dto = JSON.parseObject(postloanInspection, HlsCusPostloanInspection.class);
        HlsCusPostloanInspection newDto = new HlsCusPostloanInspection();
        newDto.setPostloanInspectionId(dto.getPostloanInspectionId());
        //更新对应的五级分类
        /*HlsCusFiveClassification fiveClassification = new HlsCusFiveClassification();
        fiveClassification.setBelongsToId(dto.getPostloanInspectionId());
        fiveClassification.setFiveClassificationType("PLI");
        List<HlsCusFiveClassification> fcList = fiveClassificationMapper.selectFcByBelongsToId(fiveClassification);*/


        String flag = null;
        if ("APPROVED".equalsIgnoreCase(result)) {
            flag = "APPROVED";
            newDto.setStatus("APPROVED");
        } else if ("APPROVED_RETURN".equalsIgnoreCase(result)) {
            flag = "APPROVED_RETURN";
            newDto.setStatus("APPROVED_RETURN");
        }
        postloanInspectionService.updateByPrimaryKeySelective(iRequest, newDto);

        //如果是定期检查需要释放待检查清单
        if (REGULAR.equalsIgnoreCase(newDto.getInspectionMethod())) {
            PlmPliUpcoming plmPliUpcoming = new PlmPliUpcoming();
            plmPliUpcoming.setBpId(newDto.getBpId());
            List<PlmPliUpcoming> list = plmPliUpcomingMapper.select(plmPliUpcoming);
            if (ON_SITE_INSPECT.equalsIgnoreCase(newDto.getInspectionType())) {
                for(PlmPliUpcoming dt : list){
                    if (ON_SITE_INSPECT.equalsIgnoreCase(dt.getInspectionType())){
                        dt.setEnableFlag("Y");
                        plmPliUpcomingMapper.updateByPrimaryKeySelective(dt);
                    }
                }
            } else if (OFF_SITE_INSPECT.equalsIgnoreCase(newDto.getInspectionType())) {
                for(PlmPliUpcoming dt : list){
                    if (OFF_SITE_INSPECT.equalsIgnoreCase(dt.getInspectionType())){
                        dt.setEnableFlag("Y");
                        plmPliUpcomingMapper.updateByPrimaryKeySelective(dt);
                    }
                }
            }else if (ALL.equalsIgnoreCase(newDto.getInspectionType())) {
                for(PlmPliUpcoming dt : list){
                    dt.setEnableFlag("Y");
                    plmPliUpcomingMapper.updateByPrimaryKeySelective(dt);
                }
            }
        }

        //更新五级分类
        /*if (CollectionUtils.isNotEmpty(fcList)) {
            HlsCusFiveClassification cusFiveClassification = new HlsCusFiveClassification();
            cusFiveClassification.setFiveClassificationId(fcList.get(0).getFiveClassificationId());
            cusFiveClassification.setStatus(flag);
            fiveClassificationService.updateByPrimaryKeySelective(iRequest, cusFiveClassification);
            if ("APPROVED".equals(flag)) {
                HlsCusFiveClassificationContract fiveClassificationContract = new HlsCusFiveClassificationContract();
                fiveClassificationContract.setFiveClassificationId(fcList.get(0).getFiveClassificationId());
                fiveClassificationContract.setChangeIq("NORMAL");
                List<String> approvalNodeList = fcContractMapper.selectLatestApprovalNode(fiveClassificationContract);
                if (CollectionUtils.isNotEmpty(approvalNodeList)) {
                    fiveClassificationContract.setApprovalNode(approvalNodeList.get(0));
                    List<HlsCusFiveClassificationContract> contracts = fcContractMapper.selectFiveClassificationContracts(fiveClassificationContract);
                    if (CollectionUtils.isNotEmpty(contracts)) {
                        if (CollectionUtils.isNotEmpty(contracts)) {
                            for (HlsCusFiveClassificationContract c : contracts) {
                                c.setFiveClassifyConId(null);
                                c.setApprovalNode("END_NODE");
                                c.set__status("add");
                                Map map = new HashMap();
                                map.put("contractNumber", c.getContractNumber());
                                map.put("fiveClassificationResult",c.getFirstClassificationResult());
                                if ("CON".equals(c.getContractType())) {
                                    fcContractMapper.updateConFiveClassificationResult(map);
                                } else if ("FCT".equals(c.getContractType())) {
                                    fcContractMapper.updateFctFiveClassificationResult(map);
                                }
                            }
                            fcContractService.batchUpdate(iRequest, contracts);
                        }
                    }
                }

            } else {
                HlsCusFiveClassificationContract fiveClassificationContract = new HlsCusFiveClassificationContract();
                fiveClassificationContract.setFiveClassificationId(fcList.get(0).getFiveClassificationId());
                fiveClassificationContract.setApprovalNode("FIRST_NODE");
                fiveClassificationContract.setChangeIq("NORMAL");
                fcContractMapper.deleteContractsOpinions(fiveClassificationContract);
            }
        }*/
    }
}
