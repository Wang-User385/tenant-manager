package com.hand.hls.hls.dto;


import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "hls_credit_line_trx_master"
)
@Getter
@Setter
public class HlsCreditLineTrxMaster extends BaseDTO {
    public static final String FIELD_TRX_CODE = "trxCode";
    public static final String FIELD_TRX_NAME = "trxName";
    public static final String FIELD_TRX_TYPE = "trxType";
    @Id
    private String trxCode;
    @Length(
            max = 1000
    )
    private String trxName;
    @Length(
            max = 100
    )
    private String trxType;
    @Transient
    private String queryCondition;
}
