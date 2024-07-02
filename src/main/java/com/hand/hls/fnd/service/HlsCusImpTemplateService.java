package com.hand.hls.fnd.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fnd.dto.HlsCusImpTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface HlsCusImpTemplateService extends IBaseService<HlsCusImpTemplate>, ProxySelf<HlsCusImpTemplateService> {

    /**
     * 查询模板通过编码
     * @param iRequest
     * @param template
     * @return
     */
    HlsCusImpTemplate selectTemplateByCode(IRequest iRequest, HlsCusImpTemplate template);

    /**
     * 查询模板编码
     * @return
     */
    List<HlsCusImpTemplate> selectTempCode();

    /**
     * 下载模板
     * @param response
     * @param template
     */
    void downloadFile(HttpServletResponse response, HlsCusImpTemplate template);
}