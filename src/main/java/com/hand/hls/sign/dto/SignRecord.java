package com.hand.hls.sign.dto;

import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
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
@Table(name = "prj_sign_record")
public class SignRecord extends BaseDTO {
    @Id
    @GeneratedValue
    private Long signId;

    private String sourceDocCategory;

    private Long sourceDocId;

    private String signUniqueId;

    private String signStatus;

    private String signResult;

    private String signUrl;

    private Long verifyBpId;

    private Long legalBpId;

    private Date signDate;

    private String signObject;

    private String signType;

}
