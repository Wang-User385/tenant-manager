package com.hand.hls.prj.dto;

/**
 *
 * 批复(授信尽调)参数
 **/

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;

@ExtensionAttribute(disable = true)
@Table(name = "PRJ_CREDIT_REPLY_PARA")
@Getter
@Setter
public class PrjCreditReplyPara extends BaseDTO {

    public static final String FIELD_REPLY_PARA_ID = "replyParaId";
    public static final String FIELD_REPLY_ID = "replyId";
    public static final String FIELD_REPLY_PARA = "replyPara";
    public static final String FIELD_DEFAULT_VALUE = "defaultValue";
    public static final String FIELD_VAULE_FROM = "vauleFrom";
    public static final String FIELD_VAULE_TO = "vauleTo";
    public static final String FIELD_SYSTEM_CONTROL_FLAG = "systemControlFlag";

    @Id
    @GeneratedValue
    private Long replyParaId;

    private Long replyId;

    @Length(max = 200)
    private String replyPara;

    private BigDecimal defaultValue;

    private BigDecimal vauleFrom;

    private BigDecimal vauleTo;

    @Length(max = 50)
    private String systemControlFlag;

    @Transient
    private String replyParaN;

}
