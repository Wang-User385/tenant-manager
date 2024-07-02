package com.hand.hls.prj.dto;

import java.util.List;
import lombok.Data;

/**
 * description
 *
 * @author shigure 2022/11/22 11:19
 */
@Data
public class SignRequestDto {
    private String uniqueIdentification;
    private String businessType;
    private String businessScene;
    private String signatureMethod;
    private Integer verifyCount;
    private String signerVerifyFlag;
    private String orderNum;
    private SignPersonDto verifyPerson;
    private SignPersonDto signLegalPerson;
    private List<SignContractDto> signatureContracts;

}
