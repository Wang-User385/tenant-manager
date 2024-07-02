package com.hand.hls.fct.mapper;

import com.hand.hls.fct.dto.HlsCusHlsCreditLineGuarantor;

import java.util.List;

/**
 * @Author: ever
 * @DATE: 2020-02-19 17:56
 */
public interface HlsCusHlsCreditLineGuarantorMapper extends HlsCreditLineGuarantorMapper<HlsCusHlsCreditLineGuarantor>  {

    List<HlsCusHlsCreditLineGuarantor> queryCreditLineGuarantor(HlsCusHlsCreditLineGuarantor hlsCusHlsCreditLineGuarantor);
}
