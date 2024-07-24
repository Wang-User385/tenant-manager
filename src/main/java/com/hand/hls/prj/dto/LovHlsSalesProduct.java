package com.hand.hls.prj.dto;

import leaf.annotation.LovField;

/**
 * @author 3835
 * @description 核心批复产品列表LOV
 * @date 2019/7/9
 */
public class LovHlsSalesProduct extends HlsCusPrjProject {
//    @LovField(prompt = "批复产品编号", field = "reply_product_code", forQuery = true, forDisplay = true)
//    private String replyProductCode;
//    @LovField(prompt = "批复产品名称", field = "difinition_name", forQuery = true, forDisplay = true)
//    private String difinitionName;
//    @LovField(prompt = "厂商/合作方", field = "bp_name", forQuery = true, forDisplay = true)
//    private String bpName;
//    @LovField(prompt = "批复编号", field = "project_number", forQuery = true, forDisplay = true)
//    private String projectNumber;
//    @LovField(prompt = "项目名称", field = "project_name", forQuery = true, forDisplay = true)
//    private String projectName;

    @LovField(prompt = "业务经理", field = "employee_name", forQuery = true, forDisplay = true)
    private String employeeName;

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

/*@Override
    public String getProjectNumber() {
        return projectNumber;
    }

    @Override
    public void setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    @Override
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }*/

//    @Override
//    public String getReplyProductCode() {
//        return replyProductCode;
//    }
//
//    @Override
//    public void setReplyProductCode(String replyProductCode) {
//        this.replyProductCode = replyProductCode;
//    }
//
//    @Override
//    public String getDifinitionName() {
//        return difinitionName;
//    }
//
//    @Override
//    public void setDifinitionName(String difinitionName) {
//        this.difinitionName = difinitionName;
//    }
//
//    public String getBpName() {
//        return bpName;
//    }
//
//    public void setBpName(String bpName) {
//        this.bpName = bpName;
//    }
}
