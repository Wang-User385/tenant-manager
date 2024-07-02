package com.hand.hls.cont.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@ExtensionAttribute(disable = true)
@Table(name = "con_contract_change_req")
@Getter
@Setter
public class HlsCusConContractChangeReq extends ConContractChangeReq {

    private Long changeTimes;
    private Long changeTerm;
    private Double changeIrr;

    /**
     * 大单提前还本：提前还本日
     */
    private Date changeStartDate;
    /**
     * 大单提前还本：提前还本本金金额
     */
    private Double changePrincipal;
    /**
     * 大单提前还本：变更后xirr
     */
    private Double changeXirr;
    @Transient
    private Double xirr;

    /**
     * 大单提前还本：提前还本起始期数
     */
    private Long ccrStartTimes;


    @Transient
    private String factoryId;
    @Transient
    private String factoryIdN;
    @Transient
    private String manufacturerId;
    @Transient
    private String manufacturerIdN;

    @Transient
    private Long bpIdVender;
    @Transient
    private String bpIdVenderN;
    @Transient
    private String currentOverdueStatus;
    @Transient
    private String division;
    @Transient
    private String divisionN;
    @Transient
    private String leaseChannel;
    @Transient
    private String leaseChannelN;
    @Transient
    private String businessType;
    @Transient
    private String businessTypeN;
    @Transient
    private Double irr;
    @Transient
    private Long times;

    @Transient
    private Long employeeId;
    @Transient
    private String employeeIdN;
    @Transient
    private String priceList;
    @Transient
    private String priceListN;
}
