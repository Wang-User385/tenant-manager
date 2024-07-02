//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hand.hap.activiti.mapper;

import com.hand.hap.activiti.dto.ActHiTaskTrans;
import com.hand.hap.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ActHiTaskTransMapper extends Mapper<ActHiTaskTrans> {

    List<ActHiTaskTrans> selectLastTaskInactive(@Param("process_id")String process_id);
}
