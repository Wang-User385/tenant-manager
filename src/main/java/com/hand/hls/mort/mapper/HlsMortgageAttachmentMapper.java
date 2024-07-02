package com.hand.hls.mort.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.mort.dto.HlsMortgageAttachment;

import java.util.List;

public interface HlsMortgageAttachmentMapper extends Mapper<HlsMortgageAttachment>{
    List<HlsMortgageAttachment> queryMortgageAttachment(HlsMortgageAttachment hlsMortgageAttachment);
}