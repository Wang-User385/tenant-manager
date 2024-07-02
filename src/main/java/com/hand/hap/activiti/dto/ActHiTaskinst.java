package com.hand.hap.activiti.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@ExtensionAttribute(disable = true)
@Table(name = "act_hi_taskinst")
@Getter
@Setter
public class ActHiTaskinst {
    @Id
    @GeneratedValue
    @Column(name = "id_")
    private String id;
    @Column(name = "proc_def_id_")
    private String procDefId;
    private String taskDefKey;
    @Column(name = "proc_inst_id_")
    private String procInstId;
    private String executionId;
    private String name;
    private String parentTaskId;
    private String description;
    private String owner;
    private String assignee;
    @Column(name = "start_time_")
    private Date startTime;
    private Date claimTime;
    @Column(name = "end_time_")
    private Date endTime;
    private Long duration;
    private String deleteReason;
    private Integer priority;
    private Date dueDate;
    @Column(name = "form_key_")
    private String formKey;
    private String category;
    private String tenantId;
}