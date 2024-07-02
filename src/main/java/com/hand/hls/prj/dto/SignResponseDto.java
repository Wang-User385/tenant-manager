package com.hand.hls.prj.dto;

import java.util.List;
import lombok.Data;

/**
 * description
 *
 * @author shigure 2022/11/23 18:17
 */
@Data
public class SignResponseDto {

    private String uniqueIdentification;
    private String businessType;
    private String businessScene;
    private String resultStatus;
    private String resultDesc;
    private List<SignContractDto> contractList;
}
