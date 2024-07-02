package com.hand.hls.bp.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hls.prj.dto.HlsBpMasterShareholder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
@ExtensionAttribute(
        disable = true
)
@Table(
        name = "hls_bp_master_shareholder"
)
@Getter
@Setter
public class HlsCusBpMasterShareholder extends HlsBpMasterShareholder {
    public HlsCusBpMasterShareholder() {
    }
}
