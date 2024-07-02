package com.hand.hls.abs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.abs.dto.HlsCusAbsPkg;
import com.hand.hls.abs.dto.HlsCusAbsProject;
import com.hand.hls.exception.HlsCusException;

import java.util.List;

public interface HlsCusAbsProjectService extends IBaseService<HlsCusAbsProject>, ProxySelf<HlsCusAbsProjectService> {
    HlsCusAbsPkg absProjectSave(IRequest request, HlsCusAbsPkg hlsCusAbsPkg);

    List<HlsCusAbsProject> queryProjectDetail(IRequest request, HlsCusAbsProject hlsCusAbsProject);

    List<HlsCusAbsProject> absPieChartQuery(IRequest request, HlsCusAbsProject hlsCusAbsProject);

    List<HlsCusAbsProject> absProjectSubmit(IRequest request, HlsCusAbsProject hlsCusAbsProject) throws HlsCusException;

    HlsCusAbsPkg assetTransferSave(IRequest request, HlsCusAbsPkg hlsCusAbsPkg);

    void submitWflApproval(IRequest request, HlsCusAbsProject hlsCusAbsProject);

//    HlsCusAbsPkg absTransferSubmit(IRequest request, HlsCusAbsPkg hlsCusAbsPkg);



    /**
     * 资产包管理
     * @param hlsCusAbsProject
     * @return
     */
    List<HlsCusAbsProject> selectProjectPackageData(IRequest request, HlsCusAbsProject hlsCusAbsProject, int page, int pageSize);


    /**
     * 作废
     * @param request
     * @param hlsCusAbsProject
     * @throws HlsCusException
     */
    void cancelAbsProject(IRequest request, HlsCusAbsProject hlsCusAbsProject) throws HlsCusException;
}

