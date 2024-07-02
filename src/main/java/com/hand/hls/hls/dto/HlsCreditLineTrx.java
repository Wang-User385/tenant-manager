package com.hand.hls.hls.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "hls_credit_line_trx"
)
@Getter
@Setter
public class HlsCreditLineTrx extends BaseDTO {
    public static final String FIELD_TRX_ID = "trxId";
    public static final String FIELD_COMPANY_ID = "companyId";
    public static final String FIELD_CREDIT_LINE_ID = "creditLineId";
    public static final String FIELD_TRX_DATE = "trxDate";
    public static final String FIELD_TRX_CODE = "trxCode";
    public static final String FIELD_TRX_AMOUNT = "trxAmount";
    public static final String FIELD_SOURCE_DOCUMENT_CATEGORY = "sourceDocumentCategory";
    public static final String FIELD_SOURCE_DOCUMENT_ID = "sourceDocumentId";
    public static final String FIELD_DESCRIPTION = "description";
    @Id
    @GeneratedValue
    private Long trxId;
    private Long companyId;
    private Long creditLineId;
    private Date trxDate;
    @Length(
            max = 100
    )
    private String trxCode;
    private Double trxAmount;
    @Length(
            max = 100
    )
    private String sourceDocumentCategory;
    private Long sourceDocumentId;
    @Length(
            max = 1000
    )
    private String description;
    @Transient
    private String trxName;
    @Transient
    private String creditLineName;
    @Transient
    private String trxType;
    @Transient
    private String trxTypeDesc;
    @Transient
    private String bpName;
    @Transient
    private String eventMessage;
}
