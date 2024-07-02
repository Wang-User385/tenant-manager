package com.hand.hls.cont.dto;

import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqLn;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectBp;
import com.hand.hls.prj.dto.HlsCusPrjProjectInsure;

import java.util.List;

public class HlsCusContractPkg {

    public List<HlsCusPrjProjectBp> getHlsCusPrjProjectBpList() {
        return hlsCusPrjProjectBpList;
    }

    public void setHlsCusPrjProjectBpList(List<HlsCusPrjProjectBp> hlsCusPrjProjectBpList) {
        this.hlsCusPrjProjectBpList = hlsCusPrjProjectBpList;
    }

    /**
     * 商业伙伴信息
     */
    private List<HlsCusPrjProjectBp> hlsCusPrjProjectBpList;
    /**
     * 放款信息头行表
     */
    private HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd;
    private HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn;
    /**
     * 项目表对象
     */
    private HlsCusPrjProject hlsCusPrjProject;


    public HlsCusCshPaymentReqHd getHlsCusCshPaymentReqHd() {
        return hlsCusCshPaymentReqHd;
    }

    public void setHlsCusCshPaymentReqHd(HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd) {
        this.hlsCusCshPaymentReqHd = hlsCusCshPaymentReqHd;
    }

    public HlsCusCshPaymentReqLn getHlsCusCshPaymentReqLn() {
        return hlsCusCshPaymentReqLn;
    }

    public void setHlsCusCshPaymentReqLn(HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn) {
        this.hlsCusCshPaymentReqLn = hlsCusCshPaymentReqLn;
    }

    public HlsCusPrjProject getHlsCusPrjProject() {
        return hlsCusPrjProject;
    }

    public void setHlsCusPrjProject(HlsCusPrjProject hlsCusPrjProject) {
        this.hlsCusPrjProject = hlsCusPrjProject;
    }

    public List<HlsCusCshPaymentReqLn> getHlsCusCshPaymentReqLnList() {
        return hlsCusCshPaymentReqLnList;
    }

    public void setHlsCusCshPaymentReqLnList(List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLnList) {
        this.hlsCusCshPaymentReqLnList = hlsCusCshPaymentReqLnList;
    }


    public List<HlsCusConContractPaymentPt> getConContractPaymentPtList() {
        return conContractPaymentPtList;
    }

    public List<HlsCusContractAttachment> getHlsCusContractAttachmentList() {
        return hlsCusContractAttachmentList;
    }

    public void setHlsCusContractAttachmentList(List<HlsCusContractAttachment> hlsCusContractAttachmentList) {
        this.hlsCusContractAttachmentList = hlsCusContractAttachmentList;
    }

    public void setConContractPaymentPtList(List<HlsCusConContractPaymentPt> conContractPaymentPtList) {
        this.conContractPaymentPtList = conContractPaymentPtList;
    }

    //结算方式
    private List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLnList;
    //合同附件
    private List<HlsCusContractAttachment> hlsCusContractAttachmentList;
    //付款前提条件
    private List<HlsCusConContractPaymentPt> conContractPaymentPtList;
    //租前息
    private List<HlsCusConContractBeforeRentH> hlsCusConContractBeforeRentHList;
    //现金流信息
    private List<HlsCusConContractCashflow> hlsCusConContractCashflowList;
    public HlsCusConContract getHlsCusConContract() {
        return hlsCusConContract;
    }

    public void setHlsCusConContract(HlsCusConContract hlsCusConContract) {
        this.hlsCusConContract = hlsCusConContract;
    }

    //合同信息
    private HlsCusConContract hlsCusConContract;

    public HlsCusContractTermination getHlsCusContractTermination() {
        return hlsCusContractTermination;
    }

    public void setHlsCusContractTermination(HlsCusContractTermination hlsCusContractTermination) {
        this.hlsCusContractTermination = hlsCusContractTermination;
    }

    //合同结束信息
    private HlsCusContractTermination hlsCusContractTermination;
    private List<HlsCusPrjProjectInsure> hlsCusPrjProjectInsureList;//保险信息

    public List<HlsCusPrjProjectInsure> getHlsCusPrjProjectInsureList() {
        return hlsCusPrjProjectInsureList;
    }

    public void setHlsCusPrjProjectInsureList(List<HlsCusPrjProjectInsure> hlsCusPrjProjectInsureList) {
        this.hlsCusPrjProjectInsureList = hlsCusPrjProjectInsureList;
    }

    public List<HlsCusConContractBeforeRentH> getHlsCusConContractBeforeRentHList() {
        return hlsCusConContractBeforeRentHList;
    }

    public void setHlsCusConContractBeforeRentHList(List<HlsCusConContractBeforeRentH> hlsCusConContractBeforeRentHList) {
        this.hlsCusConContractBeforeRentHList = hlsCusConContractBeforeRentHList;
    }

    public List<HlsCusConContractCashflow> getHlsCusConContractCashflowList() {
        return hlsCusConContractCashflowList;
    }

    public void setHlsCusConContractCashflowList(List<HlsCusConContractCashflow> hlsCusConContractCashflowList) {
        this.hlsCusConContractCashflowList = hlsCusConContractCashflowList;
    }
}
