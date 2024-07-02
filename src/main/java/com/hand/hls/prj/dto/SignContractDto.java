package com.hand.hls.prj.dto;

import java.util.List;
import lombok.Data;

/**
 * description
 *
 * @author shigure 2022/11/22 11:21
 */
@Data
public class SignContractDto {
    private Integer serialNumber;
    private String contractCode;
    private String contractName;
    private String signatureSubject;
    private String sealPerson;
    private String sealLocation;
    private String sealReason;
    private String contractCreationDate;
    private String contractPDF;
    private String signedPDF;
    private String contractSignType;
    private String digitalCertificateCode;
    private String legalCertificateCode;
    private String companyCode;
    private String companyName;
    private String contractStatus;
    private String documentSignedFile;
    private List<SignKeywordLocationDto> keywordLocationList;

}
