//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "hls_score_target"
)
public class HlsScoreTarget extends BaseDTO {
    @Id
    @GeneratedValue
    private Long scoreTargetId;
    private Long companyId;
    private String scoreTargetCode;
    private String scoreTargetName;
    private String targetValueType;
    private String enabledFlag;




    public HlsScoreTarget() {
    }

    public void setScoreTargetId(Long scoreTargetId) {
        this.scoreTargetId = scoreTargetId;
    }

    public Long getScoreTargetId() {
        return this.scoreTargetId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getCompanyId() {
        return this.companyId;
    }

    public void setScoreTargetCode(String scoreTargetCode) {
        this.scoreTargetCode = scoreTargetCode;
    }

    public String getScoreTargetCode() {
        return this.scoreTargetCode;
    }

    public void setScoreTargetName(String scoreTargetName) {
        this.scoreTargetName = scoreTargetName;
    }

    public String getScoreTargetName() {
        return this.scoreTargetName;
    }

    public void setTargetValueType(String targetValueType) {
        this.targetValueType = targetValueType;
    }

    public String getTargetValueType() {
        return this.targetValueType;
    }

    public String getEnabledFlag() {
        return this.enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }
}
