package com.hand.hls.gld.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.gld.dto.JeLine;
import java.util.List;
import java.util.Map;

public interface IJeLineService extends IBaseService<JeLine>, ProxySelf<IJeLineService> {
    List<JeLine> query(JeLine var1, int var2, int var3);

    List<JeLine> queryNoPage(Map<String, Object> var1);

    List<JeLine> queryCompany(JeLine var1);

    List<Map> selectJeLineInfoGroupAccount(IRequest iRequest,JeLine line,int pagenum,int pagesize);

    List<Map> selectJeLineInfo(IRequest iRequest,JeLine line,int pagenum,int pagesize);

}
