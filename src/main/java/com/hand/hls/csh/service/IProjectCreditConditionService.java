package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.ProjectCreditCondition;
import hls.core.utils.exception.HlsCusException;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface IProjectCreditConditionService extends IBaseService<ProjectCreditCondition>, ProxySelf<IProjectCreditConditionService>{

    void submitWfl(HttpSession session,IRequest iRequest, List<String> creditConditionIds) throws HlsCusException;

    String getProjectHistoryInfo(IRequest iRequest,Long currentProjectId,Long historyProjectId);
}