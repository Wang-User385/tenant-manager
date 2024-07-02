package com.hand.hls.plm.pli.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.plm.pli.dto.PlmPliUpcoming;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface PlmPliUpcomingService extends IBaseService<PlmPliUpcoming>, ProxySelf<PlmPliUpcomingService> {

    List<PlmPliUpcoming> upcomingList(IRequest iRequest, PlmPliUpcoming dto, int page, int pageSize);

    void upcomingListDownloadExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, InvocationTargetException, IllegalAccessException;

    public List<PlmPliUpcoming> selectUpcomingListNotAnyCondition(IRequest iRequest,PlmPliUpcoming dto,int page,int pageSize);

}
