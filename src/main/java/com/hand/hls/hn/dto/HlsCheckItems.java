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
@Table(name = "HLS_CHECK_ITEMS")
@Getter
@Setter
public class HlsCheckItems extends BaseDTO {

    @Id
    @GeneratedValue
    private Long itemId;
    private String checkCategory;
    @Transient
    private String checkCategoryN;
    private String checkType;
    @Transient
    private String checkTypeN;

    private String items;

    private String requiredFlag;
}
