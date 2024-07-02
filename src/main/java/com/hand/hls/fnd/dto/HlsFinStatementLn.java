package com.hand.hls.fnd.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by haibin on 2017/6/27.
 */
@SuppressWarnings("serial")
@ExtensionAttribute(disable=true)
@Table(name="hls_fin_statement_ln")
public class HlsFinStatementLn extends BaseDTO {
    @Id
    @GeneratedValue
    Long finStatementLnId;
    Long finStatementHdId;
    String lineType;
    Long lineSeqNumber;
    String finStatementItemName;
    String finStatementItemType;
    Double amount;
    Double adjAmount;
    String adjReason;
    Double finalAmount;
    private String accountCode;//新增 科目代码

    private Long headerId;

    private String attribute_1;

    private String attribute_2;

    private String attribute_3;

    private String attribute_4;

    private String attribute_5;

    private String attribute_6;

    private String attribute_7;

    private String attribute_8;

    private String attribute_9;

    private String attribute_10;

    private String attribute_11;

    private String attribute_12;

    private String attribute_13;

    private String attribute_14;
    private String attribute_15;

    private String attribute_16;

    private String attribute_17;

    private String attribute_18;




    @Transient
    private String finStatementHdIds;//ids

    @Transient
    private Long total;//总数

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public String getFinStatementHdIds() {
        return finStatementHdIds;
    }

    public void setFinStatementHdIds(String finStatementHdIds) {
        this.finStatementHdIds = finStatementHdIds;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public Long getFinStatementLnId() {
        return finStatementLnId;
    }

    public void setFinStatementLnId(Long finStatementLnId) {
        this.finStatementLnId = finStatementLnId;
    }

    public Long getFinStatementHdId() {
        return finStatementHdId;
    }

    public void setFinStatementHdId(Long finStatementHdId) {
        this.finStatementHdId = finStatementHdId;
    }

    public String getLineType() {
        return lineType;
    }

    public void setLineType(String lineType) {
        this.lineType = lineType;
    }

    public Long getLineSeqNumber() {
        return lineSeqNumber;
    }

    public void setLineSeqNumber(Long lineSeqNumber) {
        this.lineSeqNumber = lineSeqNumber;
    }

    public String getFinStatementItemName() {
        return finStatementItemName;
    }

    public void setFinStatementItemName(String finStatementItemName) {
        this.finStatementItemName = finStatementItemName;
    }

    public String getFinStatementItemType() {
        return finStatementItemType;
    }

    public void setFinStatementItemType(String finStatementItemType) {
        this.finStatementItemType = finStatementItemType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getAdjAmount() {
        return adjAmount;
    }

    public void setAdjAmount(Double adjAmount) {
        this.adjAmount = adjAmount;
    }

    public String getAdjReason() {
        return adjReason;
    }

    public void setAdjReason(String adjReason) {
        this.adjReason = adjReason;
    }

    public Double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(Double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public Long getHeaderId() {
        return headerId;
    }

    public void setHeaderId(Long headerId) {
        this.headerId = headerId;
    }

    public String getAttribute_1() {
        return attribute_1;
    }

    public void setAttribute_1(String attribute_1) {
        this.attribute_1 = attribute_1;
    }

    public String getAttribute_2() {
        return attribute_2;
    }

    public void setAttribute_2(String attribute_2) {
        this.attribute_2 = attribute_2;
    }

    public String getAttribute_3() {
        return attribute_3;
    }

    public void setAttribute_3(String attribute_3) {
        this.attribute_3 = attribute_3;
    }

    public String getAttribute_4() {
        return attribute_4;
    }

    public void setAttribute_4(String attribute_4) {
        this.attribute_4 = attribute_4;
    }

    public String getAttribute_5() {
        return attribute_5;
    }

    public void setAttribute_5(String attribute_5) {
        this.attribute_5 = attribute_5;
    }

    public String getAttribute_6() {
        return attribute_6;
    }

    public void setAttribute_6(String attribute_6) {
        this.attribute_6 = attribute_6;
    }

    public String getAttribute_7() {
        return attribute_7;
    }

    public void setAttribute_7(String attribute_7) {
        this.attribute_7 = attribute_7;
    }

    public String getAttribute_8() {
        return attribute_8;
    }

    public void setAttribute_8(String attribute_8) {
        this.attribute_8 = attribute_8;
    }

    public String getAttribute_9() {
        return attribute_9;
    }

    public void setAttribute_9(String attribute_9) {
        this.attribute_9 = attribute_9;
    }

    public String getAttribute_10() {
        return attribute_10;
    }

    public void setAttribute_10(String attribute_10) {
        this.attribute_10 = attribute_10;
    }

    public String getAttribute_11() {
        return attribute_11;
    }

    public void setAttribute_11(String attribute_11) {
        this.attribute_11 = attribute_11;
    }

    public String getAttribute_12() {
        return attribute_12;
    }

    public void setAttribute_12(String attribute_12) {
        this.attribute_12 = attribute_12;
    }

    public String getAttribute_13() {
        return attribute_13;
    }

    public void setAttribute_13(String attribute_13) {
        this.attribute_13 = attribute_13;
    }

    public String getAttribute_14() {
        return attribute_14;
    }

    public void setAttribute_14(String attribute_14) {
        this.attribute_14 = attribute_14;
    }

    public String getAttribute_15() {
        return attribute_15;
    }

    public void setAttribute_15(String attribute_15) {
        this.attribute_15 = attribute_15;
    }

    public String getAttribute_16() {
        return attribute_16;
    }

    public void setAttribute_16(String attribute_16) {
        this.attribute_16 = attribute_16;
    }

    public String getAttribute_17() {
        return attribute_17;
    }

    public void setAttribute_17(String attribute_17) {
        this.attribute_17 = attribute_17;
    }

    public String getAttribute_18() {
        return attribute_18;
    }

    public void setAttribute_18(String attribute_18) {
        this.attribute_18 = attribute_18;
    }
}
