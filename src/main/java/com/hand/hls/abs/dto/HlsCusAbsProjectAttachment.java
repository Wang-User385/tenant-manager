//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.abs.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import javax.persistence.Transient;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "ct_abs_project_attachment"
)
@Getter
@Setter
public class HlsCusAbsProjectAttachment extends AbsProjectAttachment {
    @Transient
    private String fileNames;
    @Transient
    private String fileName;
    @Transient
    private String uploadDate;
    @Transient
    private String uploadPerson;
    @Transient
    private String sourceKey;
    public HlsCusAbsProjectAttachment() {
    }

    public String getFileNames() {
        return this.fileNames;
    }

    public void setFileNames(String fileNames) {
        this.fileNames = fileNames;
    }
}
