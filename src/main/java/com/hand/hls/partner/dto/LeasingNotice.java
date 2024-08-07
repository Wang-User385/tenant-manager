package com.hand.hls.partner.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Data
@ExtensionAttribute(disable = true)
@Table(name = "leasing_notice")
public class LeasingNotice extends BaseDTO {
    public static final String FIELD_NOTICE_ID = "noticeId";
    public static final String FIELD_SOURCE_ID = "sourceId";
    public static final String FIELD_SOURCE_TYPE = "sourceType";
    public static final String FIELD_SCENE = "scene";
    public static final String FIELD_NOTICE_STATUS = "noticeStatus";
    public static final String FIELD_ERROR_MESSAGE = "errorMessage";
    public static  final String FIELD_NOTICE_BODY = "noticeBody";
    public static  final String FIELD_DESCRIPTION = "description";
    public static  final String FIELD_RESEND_FLAG = "resendFlag";

    @Id
    @GeneratedValue
    private Long noticeId;

    private Long sourceId;  // 来源ID,一般是projectId

    private String sourceType;    //来源类型：审核结果通知、放款结果通知等等

    private String scene;   //审核场景：只有审核结果通知使用：PRE_RISK 人工风险审核LOAN_AUDIT 放款审核MORTGAGE_MATERIAL_AUDIT 抵押材料审核

    private String noticeStatus;  //发送状态

    private String errorMessage;  // MSG

    private String description; //备注

    private String resendFlag;  //重推标志

    //CLOB字段
    private String noticeBody;   //推送的内容

    @Transient
    private String sourceTypeN;
    @Transient
    private String sceneN;

}

