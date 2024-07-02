//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hap.activiti.custom.process;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.impl.HistoricProcessInstanceQueryProperty;
import org.activiti.engine.impl.QueryOperator;
import org.activiti.engine.impl.QueryVariableValue;
import org.activiti.engine.query.QueryProperty;
import org.activiti.rest.common.api.DataResponse;
import org.activiti.rest.service.api.history.HistoricProcessInstancePaginateList;
import org.activiti.rest.service.api.history.HistoricProcessInstanceQueryRequest;
import org.activiti.rest.service.api.history.HistoricProcessInstanceQueryResource;

public class CustomHistoricProcessInstanceQueryResource extends HistoricProcessInstanceQueryResource {
    private static Map<String, QueryProperty> allowedSortProperties = new HashMap();

    public CustomHistoricProcessInstanceQueryResource() {
    }

    protected DataResponse getQueryResponse(HistoricProcessInstanceQueryRequest queryRequest, Map<String, String> allRequestParams) {
        HistoricProcessInstanceQuery query = this.historyService.createHistoricProcessInstanceQuery();
        if (queryRequest instanceof CustomHistoricProcessInstanceQueryRequest) {
            CustomHistoricProcessInstanceQueryRequest customHistoricProcessInstanceQueryRequest = (CustomHistoricProcessInstanceQueryRequest)queryRequest;
            String processDefinitionName = customHistoricProcessInstanceQueryRequest.getProcessDefinitionNameLike();
            String documentName = customHistoricProcessInstanceQueryRequest.getDocumentName();
            if (processDefinitionName != null) {
                query.processDefinitionName(processDefinitionName);
            }

            String startUserName = customHistoricProcessInstanceQueryRequest.getStartUserName();
            String carbonCopyUser = customHistoricProcessInstanceQueryRequest.getCarbonCopyUser();
            String readFlag = customHistoricProcessInstanceQueryRequest.getReadFlag();
            if (query instanceof CustomHistoricProcessInstanceQuery) {
                CustomHistoricProcessInstanceQuery customHistoricProcessInstanceQuery = (CustomHistoricProcessInstanceQuery)query;
                if (startUserName != null) {
                    customHistoricProcessInstanceQuery.setStartUserName(startUserName);
                }

                if (carbonCopyUser != null) {
                    customHistoricProcessInstanceQuery.setCarbonCopyUser(carbonCopyUser);
                }

                if (readFlag != null) {
                    customHistoricProcessInstanceQuery.setReadFlag(readFlag);
                }
                if(documentName != null){
                    QueryVariableValue queryVariableValue = new QueryVariableValue("documentName", "%"+documentName+"%", QueryOperator.LIKE_IGNORE_CASE, true);
                    customHistoricProcessInstanceQuery.getQueryVariableValues().add(queryVariableValue);
                }
                customHistoricProcessInstanceQuery.setSuspended(customHistoricProcessInstanceQueryRequest.isSuspended());
            }
        }

        if (queryRequest.getProcessInstanceId() != null) {
            query.processInstanceId(queryRequest.getProcessInstanceId());
        }

        if (queryRequest.getProcessInstanceIds() != null && !queryRequest.getProcessInstanceIds().isEmpty()) {
            query.processInstanceIds(new HashSet(queryRequest.getProcessInstanceIds()));
        }

        if (queryRequest.getProcessDefinitionKey() != null) {
            query.processDefinitionKey(queryRequest.getProcessDefinitionKey());
        }

        if (queryRequest.getProcessDefinitionId() != null) {
            query.processDefinitionId(queryRequest.getProcessDefinitionId());
        }

        if (queryRequest.getProcessBusinessKey() != null) {
            query.processInstanceBusinessKey(queryRequest.getProcessBusinessKey());
        }

        if (queryRequest.getInvolvedUser() != null) {
            query.involvedUser(queryRequest.getInvolvedUser());
        }

        if (queryRequest.getSuperProcessInstanceId() != null) {
            query.superProcessInstanceId(queryRequest.getSuperProcessInstanceId());
        }

        if (queryRequest.getExcludeSubprocesses() != null) {
            query.excludeSubprocesses(queryRequest.getExcludeSubprocesses());
        }

        if (queryRequest.getFinishedAfter() != null) {
            query.finishedAfter(queryRequest.getFinishedAfter());
        }

        if (queryRequest.getFinishedBefore() != null) {
            query.finishedBefore(queryRequest.getFinishedBefore());
        }

        if (queryRequest.getStartedAfter() != null) {
            query.startedAfter(queryRequest.getStartedAfter());
        }

        if (queryRequest.getStartedBefore() != null) {
            query.startedBefore(queryRequest.getStartedBefore());
        }

        if (queryRequest.getStartedBy() != null) {
            query.startedBy(queryRequest.getStartedBy());
        }

        if (queryRequest.getFinished() != null) {
            if (queryRequest.getFinished()) {
                query.finished();
            } else {
                query.unfinished();
            }
        }

        if (queryRequest.getIncludeProcessVariables() != null && queryRequest.getIncludeProcessVariables()) {
            query.includeProcessVariables();
        }

        if (queryRequest.getVariables() != null) {
            this.addVariables(query, queryRequest.getVariables());
        }

        if (queryRequest.getTenantId() != null) {
            query.processInstanceTenantId(queryRequest.getTenantId());
        }

        if (queryRequest.getTenantIdLike() != null) {
            query.processInstanceTenantIdLike(queryRequest.getTenantIdLike());
        }

        if (Boolean.TRUE.equals(queryRequest.getWithoutTenantId())) {
            query.processInstanceWithoutTenantId();
        }

        return (new HistoricProcessInstancePaginateList(this.restResponseFactory)).paginateList(allRequestParams, queryRequest, query, "processInstanceId", allowedSortProperties);
    }

    public DataResponse getQueryResponse(boolean custom, HistoricProcessInstanceQueryRequest queryRequest, Map<String, String> allRequestParams) {
        return this.getQueryResponse(queryRequest, allRequestParams);
    }

    static {
        allowedSortProperties.put("processInstanceId", HistoricProcessInstanceQueryProperty.PROCESS_INSTANCE_ID_);
        allowedSortProperties.put("processDefinitionId", HistoricProcessInstanceQueryProperty.PROCESS_DEFINITION_ID);
        allowedSortProperties.put("businessKey", HistoricProcessInstanceQueryProperty.BUSINESS_KEY);
        allowedSortProperties.put("startTime", HistoricProcessInstanceQueryProperty.START_TIME);
        allowedSortProperties.put("endTime", HistoricProcessInstanceQueryProperty.END_TIME);
        allowedSortProperties.put("duration", HistoricProcessInstanceQueryProperty.DURATION);
        allowedSortProperties.put("tenantId", HistoricProcessInstanceQueryProperty.TENANT_ID);
        allowedSortProperties.put("readFlag", new HistoricProcessInstanceQueryProperty("READ_FLAG_"));
    }
}
