package com.hand.hls.prj.dto;

import lombok.Data;

/**
 * description
 *
 * @author shigure 2022/11/22 11:20
 */
@Data
public class SignPersonDto {
    private String elementType;
    private String personName;
    private String documentType;
    private String idNumber;
    private String bankCardNumber;
    private String phoneNumber;
    private String receivePhoneNumber;
    private String codeFlag;
    private String faceFlag;
    private Integer faceCount;
    private Integer elementVerifyCount;
}
