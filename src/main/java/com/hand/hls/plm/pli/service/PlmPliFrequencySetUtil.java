package com.hand.hls.plm.pli.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.plm.pli.dto.PlmPliFrequencySet;

import java.util.Date;
import java.util.Map;

public interface PlmPliFrequencySetUtil extends IBaseService<PlmPliFrequencySet>, ProxySelf<PlmPliFrequencySetUtil> {
    void createUtil(String FREQUENCY_TYPE, String MODEL);

    Map<String, Date> getFrequencyDate(String SIGN, Date rentDate, Date appointedDate) throws Exception;

    boolean isRemind(Map<String, Date> maps);

    String getPriorityInspect();

    String getPriorityInspect(String defaultPriorityInspect);
}
