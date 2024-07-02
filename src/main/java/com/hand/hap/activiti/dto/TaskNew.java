//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hand.hap.activiti.dto;

import java.util.List;
import javax.persistence.Transient;
import org.activiti.rest.service.api.engine.variable.RestVariable;

public class TaskNew {
    private String id;
    private String process_instance_id;
    private String operate;
    private String process_name;
    private String name;
    private String start_user_name;
    private String create_time;
    private String create_time_before;
    private String create_time_after;
    private String priority;
    private String assignee;
    private String document_name;
    private String document_number;
    @Transient
    private Long userId;
    @Transient
    private String procInstId;
    @Transient
    private String businessKey;

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }


    @Transient
    private List<RestVariable> executionVariables;

    public String getDocument_name() {
        return document_name;
    }

    public void setDocument_name(String document_name) {
        this.document_name = document_name;
    }

    public String getDocument_number() {
        return document_number;
    }

    public void setDocument_number(String document_number) {
        this.document_number = document_number;
    }

    public TaskNew() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProcess_instance_id() {
        return this.process_instance_id;
    }

    public void setProcess_instance_id(String process_instance_id) {
        this.process_instance_id = process_instance_id;
    }

    public String getOperate() {
        return this.operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public String getProcess_name() {
        return this.process_name;
    }

    public void setProcess_name(String process_name) {
        this.process_name = process_name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart_user_name() {
        return this.start_user_name;
    }

    public void setStart_user_name(String start_user_name) {
        this.start_user_name = start_user_name;
    }

    public String getCreate_time() {
        return this.create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getCreate_time_before() {
        return this.create_time_before;
    }

    public void setCreate_time_before(String create_time_before) {
        this.create_time_before = create_time_before;
    }

    public String getCreate_time_after() {
        return this.create_time_after;
    }

    public void setCreate_time_after(String create_time_after) {
        this.create_time_after = create_time_after;
    }

    public String getPriority() {
        return this.priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getAssignee() {
        return this.assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public List<RestVariable> getExecutionVariables() {
        return this.executionVariables;
    }

    public void setExecutionVariables(List<RestVariable> executionVariables) {
        this.executionVariables = executionVariables;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
