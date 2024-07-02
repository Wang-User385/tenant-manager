package com.hand.hls.hls.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;

import javax.persistence.Table;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "hls_credit_line_trx"
)
public class HlsCusHlsCreditLineTrx extends HlsCreditLineTrx {
    public HlsCusHlsCreditLineTrx() {
    }
}
