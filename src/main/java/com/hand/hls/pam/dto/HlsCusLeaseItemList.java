package com.hand.hls.pam.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author kalvin
 * @version 1.0
 * @Date 2021/3/8 20:09
 * @Description
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ExtensionAttribute(disable = true)
@Table(name = "hls_lease_item_list")
public class HlsCusLeaseItemList  extends BaseDTO {
    @Id
    @GeneratedValue
    private Integer hlsLeaseItemListId;
    private Long leaseItemId;
    private Long seq;
    private String assetNum;
    private String assetType;
    private String assetName;
    private Date postedDate;
    private String specification;
    private String vender;
    private String manufacturer;
    private Double quantity;
    private String uom;
    private Double originalAssetValue;
    private Long netAssetValue;
    private Double price;
    private Long totalAmount;
    private Long accumulatedDepreciation;
    private String currency;
    private String installationSite;
    private Long invoiceAmt;
    private Long invoiceAmtAfterTax;
    private String invoiceNum;
    private Date invoiceDate;
    private String imgNum;
    private String description;
    private String leaseType;
    private Double valuation;
    private String fileName;
    private Double totalPrice;


}
