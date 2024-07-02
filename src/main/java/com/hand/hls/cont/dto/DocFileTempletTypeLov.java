//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.cont.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import leaf.annotation.LovField;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Id;
import javax.persistence.Table;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "HLS_DOC_FILE_TEMPLET_TYPE"
)
public class DocFileTempletTypeLov {
    @LovField(
        prompt = "模板类型",
        field = "templet_type",
        forQuery = true,
        forDisplay = true,
        displayWidth = 220
    )
    private String templetType;
    @LovField(
        prompt = "类型描述",
        field = "description",
        forQuery = true,
        forDisplay = true,
        displayWidth = 220
    )
    private String description;

    public DocFileTempletTypeLov() {
    }


    public String getTempletType() {
        return this.templetType;
    }

    public void setTempletType(String templetType) {
        this.templetType = templetType;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
