package com.hand.hls.plm.pli.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.plm.pli.dto.PlmPliCheckList;
import com.hand.hls.plm.pli.service.PlmPliCheckListService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class PlmPliCheckListServiceImpl extends BaseServiceImpl<PlmPliCheckList> implements PlmPliCheckListService {

}