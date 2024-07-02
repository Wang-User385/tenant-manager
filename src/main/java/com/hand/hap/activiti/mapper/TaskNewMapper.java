//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hand.hap.activiti.mapper;

import com.hand.hap.activiti.dto.TaskNew;
import com.hand.hap.mybatis.common.Mapper;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface TaskNewMapper extends Mapper<TaskNew> {
    List<TaskNew> queryTasketail(TaskNew var1);

    TaskNew queryTaskByTaskId(@Param("taskId") String var1);

    /**
     * 查询单据名称
     * @param process_id
     * @return
     */
    String queryDocumentName(@Param("process_id")String process_id);
    String queryDocumentNameNew(@Param("process_id")String process_id,@Param("document_name")String document_name);

    void deleteParallel(@Param("process_id")String process_id);
    /**
     * 通过process_id来查询运行中的组件
     */
    List<TaskNew> queryTasks(@Param("process_id")String process_id);

    String queryTaskProcessName(@Param("process_id")String process_id);

    List<Map> queryWflHistoryInfo(Map<String, Object> taskInfo);
    String getProcessIdsByprojectId(@Param("projectId") Long projectId);
}
