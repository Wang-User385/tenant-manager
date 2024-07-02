package com.hand.hls.fct.dto;

/**
 * @author 胡兴恒
 * @Time 2020-03-16 10:33
 */
public class FctProjectBpInfo {
    private Long bpId;
    private String bpRoleType;
    private String bpCode;
    private String bpName;
    private String creditGrantorParty;

    public FctProjectBpInfo() {
    }

    public Long getBpId() {
        return this.bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public String getBpRoleType() {
        return this.bpRoleType;
    }

    public void setBpRoleType(String bpRoleType) {
        this.bpRoleType = bpRoleType;
    }

    public String getBpCode() {
        return this.bpCode;
    }

    public void setBpCode(String bpCode) {
        this.bpCode = bpCode;
    }

    public String getBpName() {
        return this.bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    public String getCreditGrantorParty() {
        return this.creditGrantorParty;
    }

    public void setCreditGrantorParty(String creditGrantorParty) {
        this.creditGrantorParty = creditGrantorParty;
    }

}
