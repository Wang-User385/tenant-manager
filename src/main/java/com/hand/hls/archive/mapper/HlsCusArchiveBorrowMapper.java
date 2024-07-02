package com.hand.hls.archive.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.archive.dto.HlsCusArchiveBorrow;

import java.util.List;

public interface HlsCusArchiveBorrowMapper extends Mapper<HlsCusArchiveBorrow>{

    /**
     * 查询即将到期档案借阅记录
     * @param hlsCusArchiveBorrow
     * @return
     */
    List<HlsCusArchiveBorrow> queryArchiveBorrowEndNotice(HlsCusArchiveBorrow hlsCusArchiveBorrow);
}