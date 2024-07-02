package com.hand.hls.plm.pli.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.plm.pli.dto.PlmPliNewSituation;
import com.hand.hls.plm.pli.service.PlmPliNewSituationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class PlmPliNewSituationServiceImpl extends BaseServiceImpl<PlmPliNewSituation> implements PlmPliNewSituationService {

}