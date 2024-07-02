package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hls.ast.dto.AssetClassHead;
import com.hand.hls.ast.dto.AssetClassLine;
import com.hand.hls.ast.service.IAssetClassHeadService;
import com.hand.hls.ast.service.IAssetClassLineService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: ximufeng
 * @version: v1.0
 * @description: 资产分类工作流结束任务监听器
 * @date:2019/8/7
 */
@Component
public class AssetClassHeadServiceTask implements JavaDelegate, IActivitiBean {

    private static final String APPROVED = "APPROVED";
    private static final String REJECTED = "REJECTED";

    @Autowired
    private IAssetClassHeadService assetClassHeadService;

    @Autowired
    private IAssetClassLineService assetClassLineService;

    static Map<String, String> FiveClassificationResult = new HashMap<String, String>(200) {
    };
    static {
        FiveClassificationResult.put("0", "A1");
        FiveClassificationResult.put("1", "A2");
        FiveClassificationResult.put("2", "B1");
        FiveClassificationResult.put("3", "B2");
        FiveClassificationResult.put("4", "C");
        FiveClassificationResult.put("5", "D");
        FiveClassificationResult.put("6", "E");
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String str = (String) delegateExecution.getVariable("assetClassHead");
        AssetClassHead assetClassHead = JSON.parseObject(str, AssetClassHead.class);
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String userId = String.valueOf(delegateExecution.getVariable("startUserId"));
        assetClassHead = this.assetClassHeadService.selectByPrimaryKey(requestCtx, assetClassHead);
        requestCtx.setUserId(Long.valueOf(userId));

        AssetClassLine assetClassLine = new AssetClassLine();
        assetClassLine.setClassHeadId(assetClassHead.getClassHeadId());
        List<AssetClassLine> assetClassLines = assetClassLineService.selectSelective(requestCtx, assetClassLine);
        if (StringUtils.equals(result, APPROVED)) {
            assetClassHead.setApproveStatus(APPROVED);
            assetClassHead.setApproveTime(new Date());
            for (AssetClassLine classLine :
                    assetClassLines) {
                classLine.setReviewLevel(classLine.getApproveReviewLevel());
                classLine.setInitialLevel(classLine.getApproveInitialLevel());
                //根据审批复核级别更新 五级分类结果
                classLine.setFiveClassificationResult(FiveClassificationResult.get(classLine.getApproveReviewLevel()));
                classLine.set__status(DTOStatus.UPDATE);
            }
        } else if (StringUtils.equals(result, REJECTED)) {
            assetClassHead.setApproveStatus(REJECTED);
            for (AssetClassLine classLine :
                    assetClassLines) {
                classLine.setApproveReviewLevel(classLine.getReviewLevel());
                classLine.setApproveInitialLevel(classLine.getInitialLevel());
                classLine.set__status(DTOStatus.UPDATE);
            }
        }
        assetClassLineService.batchUpdate(requestCtx,assetClassLines);
        this.assetClassHeadService.updateByPrimaryKeySelective(requestCtx, assetClassHead);
    }
}
