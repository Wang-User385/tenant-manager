package com.hand.hls.layout.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.layout.dto.DocLayoutScreen;
import com.hand.hls.layout.service.IDocLayoutScreenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class DocLayoutScreenServiceImpl extends BaseServiceImpl<DocLayoutScreen> implements IDocLayoutScreenService {

}