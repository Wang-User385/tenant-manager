package com.hand.hls.pam.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Qian Yuanfeng
 * @date 2020/7/27 - 16:49
 */
@Getter
@Setter
public class HlsWarrantStockTran  extends BaseDTO {

    private List<HlsWarrantStockLn> hlsWarrantStockLnList;

    private  HlsWarrantStockHd hlsWarrantStockHd;

    public List<HlsWarrantStockLn> getHlsWarrantStockLnList() {
        return hlsWarrantStockLnList;
    }

    public void setHlsWarrantStockLnList(List<HlsWarrantStockLn> hlsWarrantStockLnList) {
        this.hlsWarrantStockLnList = hlsWarrantStockLnList;
    }


    public HlsWarrantStockHd getHlsWarrantStockHd() {
        return hlsWarrantStockHd;
    }

    public void setHlsWarrantStockHd(HlsWarrantStockHd hlsWarrantStockHd) {
        this.hlsWarrantStockHd = hlsWarrantStockHd;
    }


}
