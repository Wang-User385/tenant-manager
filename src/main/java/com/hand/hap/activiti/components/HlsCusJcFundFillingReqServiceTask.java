package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.fp.dto.*;
import com.hand.hls.fp.mapper.*;
import com.hand.hls.fp.service.FundFillingReqDetailService;
import com.hand.hls.fp.service.JcFundFillingDetailService;
import com.hand.hls.fp.service.JcFundFillingLnService;
import com.hand.hls.fp.service.JcFundFillingService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : qzk
 * @date : 2020/4/11
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusJcFundFillingReqServiceTask implements JavaDelegate, IActivitiBean {

    public static final String APPROVED = "APPROVED";
    public static final String REJECTED = "REJECTED";
    public static final String APPROVING = "APPROVING";
    @Autowired
    private JcFundFillingMapper fundFillingMapper;
    @Autowired
    JcFundFillingService fillingService;
    @Autowired
    JcFundFillingLnService fillingLnService;
    @Autowired
    private JcFundFillingLnMapper lnMapper;
    @Autowired
    private FundFillingReqLnMapper reqLnMapper;
    @Autowired
    private JcFundFillingDetailMapper detailMapper;
    @Autowired
    private FundFillingReqDetailMapper reqDetailMapper;
    @Autowired
    private JcFundFillingDetailService detailService;
    @Autowired
    private JcFundPlanScheduleMapper scheduleMapper;
    private static final String SUMMARY_FLAG = "N";

    private Logger logger = LoggerFactory.getLogger(getClass());

    //覆盖原单据数据
    public void copyTofilling(IRequest request, JcFundFilling fillingReq) {
        JcFundFilling req = new JcFundFilling();
        req.setFillingId(fillingReq.getFillingId());
        List<JcFundFilling> reqList = fundFillingMapper.queryAll(req);
        if (reqList.size() > 0) {
            for (JcFundFilling fundFillingReq : reqList) {
                //行复制
                JcFundFillingLn ln = new JcFundFillingLn();
                ln.setFillingId(fundFillingReq.getFillingId());
                FundFillingReqLn reqLn = new FundFillingReqLn();
                //删除原行明细
                lnMapper.deleteFillingLn(ln);
                List<FundFillingReqLn> lnList = reqLnMapper.queryAllLn(reqLn);
                if (lnList.size() > 0) {
                    int i = 0;
                    for (FundFillingReqLn fillingReqLn : lnList) {
                        JcFundFillingLn fundFillingLn = new JcFundFillingLn();
                        BeanUtils.copyProperties(fillingReqLn, fundFillingLn);
                        fundFillingLn.setFillingLnId(null);
                        fillingLnService.insertSelective(request, fundFillingLn);
                        //明细复制
                        JcFundFillingDetail detail = new JcFundFillingDetail();
                        detail.setFillingId(fundFillingLn.getFillingId());
                        //删除原金额明细 首次需要删除原数据
                        if (i == 0) {
                            detailMapper.deleteFillingDetail(detail);
                        }
                        i = i + 1;

                        FundFillingReqDetail reqDetail = new FundFillingReqDetail();
                        reqDetail.setFillingLnId(fillingReqLn.getFillingLnId());
                        List<FundFillingReqDetail> detailList = reqDetailMapper.queryAllByLnId(reqDetail);
                        if (detailList.size() > 0) {
                            for (FundFillingReqDetail fillingReqDetail : detailList) {
                                JcFundFillingDetail fundFillingDetail = new JcFundFillingDetail();
                                BeanUtils.copyProperties(fillingReqDetail, fundFillingDetail);
                                fundFillingDetail.setFillingLnId(fundFillingLn.getFillingLnId());
                                fundFillingDetail.setFillingDetailId(null);
                                detailService.insertSelective(request, fundFillingDetail);
                            }
                        }
                    }

                }
            }

        }
    }

    public void updateSummaryLn(IRequest request, JcFundFilling fillingReq, JcFundFilling fillingNor) {
        //先删除对应的明细
        JcFundFillingDetail detail = new JcFundFillingDetail();
        detail.setFillingId(fillingNor.getFillingId());
        detail.setHostUnitId(fillingReq.getUnitId());
        //查询记录员单据数据
        List<JcFundFillingDetail> detailList = detailMapper.queryAllSummary(detail);
        if (detailList.size() > 0) {
            detailMapper.deleteFillingDetailSummary(detail);
            //遍历
            FundFillingReqDetail detailReq = new FundFillingReqDetail();
            detailReq.setFillingId(fillingReq.getFillingId());
            detailReq.setRefFillingId(fillingNor.getFillingId());
            List<FundFillingReqDetail> reqDetailList = reqDetailMapper.queryAllReaDetail(detailReq);
            if (reqDetailList.size() > 0) {
                for(FundFillingReqDetail fillingReqDetail : reqDetailList){
                    JcFundFillingDetail fillingDetail = new JcFundFillingDetail();
                    BeanUtils.copyProperties(fillingReqDetail, fillingDetail);
                    fillingDetail.setFillingId(fillingNor.getFillingId());
                    fillingDetail.setFillingLnId(fillingReqDetail.getFillingLnIdNor());
                    fillingDetail.setHostUnitId(fillingReq.getUnitId());
                    fillingDetail.setFillingDetailId(null);
                    detailService.insertSelective(request,fillingDetail);
                }
            }

        }

    }

    //更新汇总数据
    public void updateSummaryWeek(IRequest request, JcFundFilling fillingReq) {
        JcFundFilling filling = new JcFundFilling();
        filling.setFillType("WEEK");
        filling.setFillYear(fillingReq.getFillYear());
        filling.setFillMon(fillingReq.getFillMon());
        filling.setFillWeek(fillingReq.getFillWeek());
        List<JcFundFilling> fillingList = fundFillingMapper.queryNormal(filling);
        if (fillingList.size() > 0) {
            //汇总数据存在 则更新汇总detial金额数据
            updateSummaryLn(request, fillingReq, fillingList.get(0));
            //更新汇总 行合计金额
            JcFundFillingDetail detail = new JcFundFillingDetail();
            detail.setFillingId(fillingList.get(0).getFillingId());
            detail.setFillYear(fillingList.get(0).getFillYear());
            detail.setFillType("WEEK");
            detail.setFillMon(fillingList.get(0).getFillMon());
            detail.setFillWeek(fillingList.get(0).getFillWeek());
            detail.setHostUnitId(fillingList.get(0).getUnitId());
            detail.setFillQuarter(fillingList.get(0).getFillQuarter());

            detailService.updateSummaryAmount(request, detail);
        }
    }


    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag;
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = delegateExecution.getVariable("approveResult").toString();
        String jcFundFilling = (String) delegateExecution.getVariable("jcFundFilling");
        JcFundFilling fundFilling = JSON.parseObject(jcFundFilling, JcFundFilling.class);
        JcFundFilling filling = fundFillingMapper.selectByPrimaryKey(fundFilling);

        if (APPROVING.equalsIgnoreCase(filling.getReqStatus())) {
            if (APPROVED.equalsIgnoreCase(result)) {
                flag = APPROVED;
            } else {
                flag = REJECTED;
            }
            filling.setReqStatus(flag);
            fillingService.updateByPrimaryKeySelective(requestCtx, filling);
            //生成报表汇总数据
            if (APPROVED.equalsIgnoreCase(flag)) {
                //通过需要覆盖原单据 并更新汇总计划
                copyTofilling(requestCtx, filling);
                updateSummaryWeek(requestCtx, filling);
            }
        }
    }
}
