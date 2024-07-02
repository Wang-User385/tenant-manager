//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hand.hap.activiti.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import org.activiti.rest.service.api.engine.variable.RestVariable;

import javax.persistence.*;
import java.util.List;

@Table(name = "act_hi_task_trans")
@Setter
@Getter
public class ActHiTaskTrans {

    @Id
    @GeneratedValue
    @Column(name = "RECORD_ID")
    private Long recordId;

    @Column(name = "PROC_INST_ID")
    private String procInstId;
    @Column(name = "ACTIVITY_ID")
    private String activityId;
    @Column(name = "TASK_ID")
    private Long taskId;
    @Column(name = "PARENT_TASK_ID_")
    private String parentTaskId;
}
