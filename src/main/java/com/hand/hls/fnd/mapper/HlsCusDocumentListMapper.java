package com.hand.hls.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.HlsCusDocumentList;

import java.util.List;

/**
 * @Description:
 * @Author: wty
 * @Date: Created in 0:16 2018/4/16
 */
public interface HlsCusDocumentListMapper extends Mapper<HlsCusDocumentList> {

    List<HlsCusDocumentList> selectList(HlsCusDocumentList hlsCusDocumentList);//附件清单页面查询

    List<HlsCusDocumentList> BpMasterListInit(HlsCusDocumentList hlsCusDocumentList);//商业伙伴附件清单初始化

    List<HlsCusDocumentList> selectByCategoryAndType(HlsCusDocumentList hlsCusDocumentList);//附件初始化

}