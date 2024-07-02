package com.hand.hls.gld.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "gld_je_head"
)
@Getter
@Setter
public class HlsCusJeHead extends JeHead {
    public HlsCusJeHead() {
    }
    @Transient
    private Long jeTrxId;

    private Long trialedBy;

    private Long retrialedBy;

    private Double exchangeRate;

    @Transient
    private String companyName;
    @Transient
    private String currencyName;

    @Transient
    private String jeTrxN;

    @Transient
    private Date jeDateFrom;

    @Transient
    private Date jeDateTo;
    @Transient
    private String currencyN;
    @Transient
    private String trialFlag;
    @Transient
    private String createdDate;
    @Transient
    private String businessTypeN;
    @Transient
    private String retrialedByN;
    @Transient
    private String trialedByN;

    @Transient
    private String paymentNumber;

}
