package com.hand.hls.wsdl.dto;

import java.io.Serializable;
import java.util.List;

/**
 * <p>放款信息
 *
 * @author ferry ferry_sy@163.com
 * created by 2019/12/02 16:16
 */

public class PaymentReq implements Serializable {
    /**
     * 合作方
     */
    private String partners;

    /**
     * 进件序号
     */
    private String partnersContractNumber;

    /**
     * 租赁物信息
     */
    private List<LeaseItem> leaseItems;

    public String getPartners() {
        return partners;
    }

    public void setPartners(String partners) {
        this.partners = partners;
    }

    public String getPartnersContractNumber() {
        return partnersContractNumber;
    }

    public void setPartnersContractNumber(String partnersContractNumber) {
        this.partnersContractNumber = partnersContractNumber;
    }

    public List<LeaseItem> getLeaseItems() {
        return leaseItems;
    }

    public void setLeaseItems(List<LeaseItem> leaseItems) {
        this.leaseItems = leaseItems;
    }
}
