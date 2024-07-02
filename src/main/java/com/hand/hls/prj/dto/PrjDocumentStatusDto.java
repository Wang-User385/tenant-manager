package com.hand.hls.prj.dto;

import com.hand.hls.wsdl.dto.FinanceInterfaceHead;
import lombok.Data;

/**
 * @author: ximufeng
 * @version: v1.0
 * @description: 单据状态查询接口DTO
 * @date:2020/03/17
 */
@Data
public class PrjDocumentStatusDto extends FinanceInterfaceHead {
    /**
     * 进件序号
     */
    private String partnersContractNumber;

    /**
     * 证件号码
     */
    private String idCardNo;

    /**
     * 备用字段
     */

    private String ref56;
    private String ref57;
    private String ref58;
    private String ref59;
    private String ref60;
}
