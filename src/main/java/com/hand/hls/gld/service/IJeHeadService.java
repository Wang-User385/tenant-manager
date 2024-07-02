package com.hand.hls.gld.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.gld.dto.HlsCusJeHead;
import com.hand.hls.gld.dto.JeLine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface IJeHeadService extends IBaseService<HlsCusJeHead>, ProxySelf<IJeHeadService> {
    List<HlsCusJeHead> jeListQuery(Map<String, Object> var1, int var2, int var3);

    List<JeLine> jeConfirm(List<HlsCusJeHead> var1, IRequest var2, Long var3, Long var4);

    List<Map> selectHeadInfo(IRequest iRequest,HlsCusJeHead hlsCusJeHead,int page,int pagesize);

    List<HlsCusJeHead> gldJeDataQuery(IRequest iRequest,HlsCusJeHead hlsCusJeHead,int page,int pagesize);

    void jeLineExport(HttpServletRequest iRequest, HttpServletResponse response, HlsCusJeHead dto) throws IOException, InvocationTargetException, IllegalAccessException;
}
