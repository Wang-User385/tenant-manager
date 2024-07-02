package com.hand.hls.hn.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@ExtensionAttribute(disable = true)
@Table(name = "PRJ_CHECK_ITEMS_LN")
@Getter
@Setter
public class PrjCheckItemLn extends BaseDTO {

    @Id
    @GeneratedValue
    private Long lnId;

    private Long hdId;
    private Long checkId;

    private String checkType;
    @Transient
    private String checkTypeN;

    private String items;
    private String description;

    private String checkY;
    @Transient
    private String checkYN;
    private String checkN;
    @Transient
    private String checkNN;

    private String requiredFlag;
}
