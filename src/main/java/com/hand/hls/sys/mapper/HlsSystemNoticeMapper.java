//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.sys.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.sys.dto.HlsSystemNotice;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface HlsSystemNoticeMapper extends Mapper<HlsSystemNotice> {
    List<HlsSystemNotice> selectUserNotice(@Param("allocation_id") Long var1, @Param("dto") HlsSystemNotice var2);
    List<HlsSystemNotice> selectUserUnNotice(@Param("allocation_id") Long var1, @Param("dto") HlsSystemNotice var2);
    List<HlsSystemNotice> selectUserAllNotice(@Param("allocation_id") Long var1, @Param("dto") HlsSystemNotice var2);

    List<HlsSystemNotice> selectUserTodo(@Param("allocation_id") Long var1, @Param("dto") HlsSystemNotice var2);

    int setAllRead(@Param("allocation_id") Long var1);
    List<HlsSystemNotice>  noticeDelete(@Param("noticeId") Long var1, @Param("dto") HlsSystemNotice var2);

    List<HlsSystemNotice> queryNoticeByTodoAndDocId(Long var1);

    List<HlsSystemNotice> userUnreadCount(@Param("allocation_id") Long var1, @Param("dto") HlsSystemNotice var2);

    List<Map> queryPrjByid(@Param("contractId") String var1, @Param("projectId") String var2);

    List<Map> queryInceptByid(@Param("inceptId") String var1);

    List<Map> querySignNoticeByid(@Param("contractId") String var1);
}
