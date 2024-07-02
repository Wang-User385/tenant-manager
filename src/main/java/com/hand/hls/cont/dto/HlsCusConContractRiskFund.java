package com.hand.hls.cont.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@ExtensionAttribute(disable = true)
@Table(name = "con_contract_risk_fund")
@Getter
@Setter
public class HlsCusConContractRiskFund extends BaseDTO {

    @Id
    @GeneratedValue
    private Long riskId;
    private Long contractId;
    private Double withdrawalAmount;
    private Double receivedAmount;
    private Date inceptionOfLease;
    private String postFlag;
    private String periodName;

    private Double riskReserveRatio;
    private Long riskReserveRatioId;
}
