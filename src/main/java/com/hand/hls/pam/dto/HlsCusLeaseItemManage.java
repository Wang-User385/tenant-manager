package com.hand.hls.pam.dto;

import com.hand.hap.system.dto.BaseDTO;

import java.util.List;

/**
 * @author Qian Yuanfeng
 * @date 2020/4/14 - 14:07
 */
public class HlsCusLeaseItemManage extends BaseDTO {

    private HlsCusLeaseItem hlsCusLeaseItem;

    private List<HlsCusLeaseItemTaxes> hlsCusLeaseItemTaxesList;

    private List<HlsLeaseItemDetail>  hlsLeaseItemDetailList;

    private List<HlsLeaseItemTransfer> hlsLeaseItemTransferList;

    private List<HlsCusHlsLeaseItemAttachment> hlsLeaseItemAttachmentList;

    private List<HlsCusLeaseItemList> hlsCusLeaseItemList;

    public List<HlsCusLeaseItemList> getHlsCusLeaseItemList() {
        return hlsCusLeaseItemList;
    }

    public void setHlsCusLeaseItemList(List<HlsCusLeaseItemList> hlsCusLeaseItemList) {
        this.hlsCusLeaseItemList = hlsCusLeaseItemList;
    }

    public HlsCusLeaseItem getHlsCusLeaseItem() {
        return hlsCusLeaseItem;
    }

    public void setHlsCusLeaseItem(HlsCusLeaseItem hlsCusLeaseItem) {
        this.hlsCusLeaseItem = hlsCusLeaseItem;
    }

    public List<HlsCusLeaseItemTaxes> getHlsCusLeaseItemTaxesList() {
        return hlsCusLeaseItemTaxesList;
    }

    public void setHlsCusLeaseItemTaxesList(List<HlsCusLeaseItemTaxes> hlsCusLeaseItemTaxesList) {
        this.hlsCusLeaseItemTaxesList = hlsCusLeaseItemTaxesList;
    }

    public List<HlsLeaseItemDetail> getHlsLeaseItemDetailList() {
        return hlsLeaseItemDetailList;
    }

    public void setHlsLeaseItemDetailList(List<HlsLeaseItemDetail> hlsLeaseItemDetailList) {
        this.hlsLeaseItemDetailList = hlsLeaseItemDetailList;
    }

    public List<HlsLeaseItemTransfer> getHlsLeaseItemTransferList() {
        return hlsLeaseItemTransferList;
    }

    public void setHlsLeaseItemTransferList(List<HlsLeaseItemTransfer> hlsLeaseItemTransferList) {
        this.hlsLeaseItemTransferList = hlsLeaseItemTransferList;
    }

    public List<HlsCusHlsLeaseItemAttachment> getHlsLeaseItemAttachmentList() {
        return hlsLeaseItemAttachmentList;
    }

    public void setHlsLeaseItemAttachmentList(List<HlsCusHlsLeaseItemAttachment> hlsLeaseItemAttachmentList) {
        this.hlsLeaseItemAttachmentList = hlsLeaseItemAttachmentList;
    }
}
