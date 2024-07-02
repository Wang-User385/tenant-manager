package com.hand.hls.utils.service;

import com.hand.hap.core.IRequest;
import com.hand.hls.utils.ResMessageException;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2019/10/9
 * @description:
 */
public interface FakeRequestService {
    /**
     *
     * @return
     * @throws ResMessageException
     */
    IRequest createFakeRequest();

    /**
     *
     * @param userId
     * @return
     * @throws ResMessageException
     */
    IRequest createFakeRequest(Long userId);
}
