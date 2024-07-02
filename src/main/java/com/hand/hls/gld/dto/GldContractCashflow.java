//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.gld.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "GLD_CONTRACT_CASHFLOW"
)
@Data
public class GldContractCashflow extends BaseDTO {
    @Id
    @GeneratedValue
    private Long gldCashflowId;
    private Long cashflowId;
    private Long contractId;
    private Double times;
    private Date finIncomeDate;
    private Double financeIncome;
    private String cfItem;
    private Double vatIncome;
    private Long quotationId;
    private Double netIncome;
    private Double reduction;
    private Double currentNetAmount;
    private Double dueAmount;

    public GldContractCashflow() {
    }


}
