package com.hand.hls.fnd.dto;

import javax.persistence.Transient;

public class HlsCusFndCountry extends FndCountry {

    private String ecifCode;

    @Transient
    private Long attachmentId;

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getEcifCode() {
        return ecifCode;
    }

    public void setEcifCode(String ecifCode) {
        this.ecifCode = ecifCode;
    }
}
