//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;

@ExtensionAttribute(
    disable = true
)
@Table(
    name = "hls_bp_master_relation"
)
@Setter
@Getter
public class HlsCusBpMasterRelation extends HlsBpMasterRelation {
    public HlsCusBpMasterRelation() {
    }
    private Long relationBpId;
}
