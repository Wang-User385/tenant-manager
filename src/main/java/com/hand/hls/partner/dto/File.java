package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

@Data
public class File extends BaseDTO {
    private String fileType;
    private String fileIndex;
    private String fileId;
    private String fileName;
}
