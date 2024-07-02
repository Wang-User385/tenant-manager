//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;

import javax.persistence.Table;
import javax.persistence.Transient;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "csh_write_off"
)
public class HlsCusCshDeduction extends CshDeduction {

    @Transient
    private String deductionDocCategory;


    @Transient
    private String  orderFlag;


    public HlsCusCshDeduction() {
    }

    public String getDeductionDocCategory() {
        return deductionDocCategory;
    }

    public void setDeductionDocCategory(String deductionDocCategory) {
        this.deductionDocCategory = deductionDocCategory;
    }

    public String getOrderFlag() {
        return orderFlag;
    }

    public void setOrderFlag(String orderFlag) {
        this.orderFlag = orderFlag;
    }
}
