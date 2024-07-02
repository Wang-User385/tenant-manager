package com.hand.hls.ast.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.ast.dto.NoticeManage;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;

import java.util.List;

public interface NoticeManageMapper extends Mapper<NoticeManage>{

    List<NoticeManage> queryAll(NoticeManage dto);

    List<NoticeManage> queryContractCashflowLov(NoticeManage noticeManage);

    List<NoticeManage> queryNoticeTempLov(NoticeManage noticeManage);

    List<NoticeManage> queryContractCashflowItemLov(NoticeManage noticeManage);

}