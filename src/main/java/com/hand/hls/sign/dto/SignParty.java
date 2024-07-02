package com.hand.hls.sign.dto;

import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

/**
 * description
 *
 * @author shigure 2022/11/25 16:43
 */
@ExtensionAttribute(disable=true)
@Data
@Table(name = "prj_sign_party")
public class SignParty extends BaseDTO {
    @Id
    @GeneratedValue
    private Long objectId;

    private Long projectAttachmentId;

    private Long bpId;

    private String keyword;

    private String sealCode;

    private String sealPerson;

    private String idCardNo;

    private String phone;

    private String signStatus;

    private Date signTime;

    private String signType;

    private Long parentId;

    @Transient
    private String signStatusN;

    @Transient
    private String signTypeN;

    @Transient
    private String bpName;

    @Transient
    private String bpIdN;

}
