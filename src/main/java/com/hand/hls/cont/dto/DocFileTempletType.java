//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.cont.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import javax.persistence.Id;
import javax.persistence.Table;

import leaf.annotation.LovField;
import org.hibernate.validator.constraints.NotEmpty;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "HLS_DOC_FILE_TEMPLET_TYPE"
)
public class DocFileTempletType extends BaseDTO {

    @Id
    private String templetType;
    @NotEmpty
    private String description;
    private String enabledFlag;
    private String systemFlag;

    public DocFileTempletType() {
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

    public String getEnabledFlag() {
        return this.enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getSystemFlag() {
        return this.systemFlag;
    }

    public void setSystemFlag(String systemFlag) {
        this.systemFlag = systemFlag;
    }
}
