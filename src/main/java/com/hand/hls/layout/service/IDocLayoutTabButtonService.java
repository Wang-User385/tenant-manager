package com.hand.hls.layout.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.layout.dto.DocLayoutTabButton;

import java.util.List;
import java.util.Map;

public interface IDocLayoutTabButtonService extends IBaseService<DocLayoutTabButton>, ProxySelf<IDocLayoutTabButtonService> {

    List<DocLayoutTabButton> selectDocLayoutTabButton(Map map, int page, int pageSize);

    List<DocLayoutTabButton> queryDocLayoutTabButton(DocLayoutTabButton param, int page, int pageSize);

    /**
     * package -> java
     * hls_doc_layout_button_pkg.tab_button_config_load
     */
    ResponseData tabButtonConfigLoad(DocLayoutTabButton docLayoutTabButton);

    /**
     * package -> java
     * hls_doc_layout_button_pkg.tab_button_config_reload
     *
     * @return
     */
    ResponseData tabButtonConfigReload(List<DocLayoutTabButton> docLayoutTabButton);
}