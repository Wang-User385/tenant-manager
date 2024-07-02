package com.hand.hls.bp.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;

import javax.persistence.Table;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "sys_attachment"
)
public class HlsCusSysAttachment extends HlsSysAttachment {
    public HlsCusSysAttachment() {
    }
}
