package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

import java.util.List;

@Data
public class ImageSyncDTO  extends BaseDTO {
    private String orderNo;
    private List<File> files;
}
