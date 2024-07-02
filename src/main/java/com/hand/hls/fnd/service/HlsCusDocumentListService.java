package com.hand.hls.fnd.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fnd.dto.HlsCusDocumentList;

import java.util.List;

/**
 * @Description:附件清单Service
 * @Author: wty
 * @Date: Created in 0:16 2018/4/16
 */
public interface HlsCusDocumentListService extends IBaseService<HlsCusDocumentList>, ProxySelf<HlsCusDocumentList> {

    List<HlsCusDocumentList> selectList(IRequest iRequest, HlsCusDocumentList hlsCusDocumentList, int page, int pagesize);//附件清单查询

    List<HlsCusDocumentList> BpMasterListInit(IRequest iRequest, HlsCusDocumentList hlsCusDocumentList, int page, int pagesize);//商业伙伴附件清单初始

    List<HlsCusDocumentList> selectByCategoryAndType(HlsCusDocumentList hlsCusDocumentList);

}
