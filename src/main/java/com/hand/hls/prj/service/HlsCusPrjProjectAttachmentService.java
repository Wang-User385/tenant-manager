package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;

import java.util.List;

public interface HlsCusPrjProjectAttachmentService extends IBaseService<HlsCusPrjProjectAttachment>, ProxySelf<HlsCusPrjProjectAttachmentService> {

    List<HlsCusPrjProjectAttachment> prjProjectAttachmentDetailQuery(IRequest iRequest, HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment, int page, int pageSize);

    List<HlsCusPrjProjectAttachment> selectInProjectAttachmentService(IRequest iRequest, HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment, int page, int pageSize);
    /*
    List<HlsCusPrjProjectAttachment> cloneAttachment(IRequest request, HlsCusConContract sourceContract, HlsCusConContract targetContract, String category);

    int deleteAttachment(IRequest request, HlsCusConContract targetContract, String attachmentCategory);

    List<Map<String, Object>> queryPrjAttachmentCountForArcHome(IRequest request);

    List<Map<String, Object>> queryFctAttachmentCountForArcHome(IRequest request);

    List<HlsCusPrjProjectAttachment> queryByProjectId(IRequest request, Long projectId);



    List<HlsCusFctProjectAttachment> fctProjectAttachmentDetailQuery(IRequest iRequest, HlsCusFctProjectAttachment hlsCusFctProjectAttachment, int page, int pageSize);

    */
/*风险评估报告*//*

    List<HlsCusPrjProjectAttachment> riskReportDetailQuery(IRequest iRequest, HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment, int page, int pageSize);

    List<HlsCusPrjProjectAttachment> selectByprojectIdAndCategory(IRequest iRequest, HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment, int page, int pageSize);



    List<HlsCusPrjProjectAttachment> selectPrjProjectAttachmentService(IRequest iRequest, HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment, int page, int pageSize);

    List<HlsCusPrjProjectAttachment> queryAllFile(IRequest requestContext, HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment, int page, int pagesize);

    void batchUpdateAttachemnt(IRequest requestContext, List<HlsCusPrjProjectAttachment> list);

    int selectAttachmentCodeNullCount(Long projectId, String projectAttachmentCategory);

    List<HlsCusPrjProjectAttachment>  selectProjectAttachmentData(IRequest iRequest, HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment, int page, int pagesize);


    void downloadZipFile(Long projectAttachmentId, String fileIds, IRequest iRequest, HttpServletRequest request, HttpServletResponse response)  throws IOException;

    void downloadZipFileContractSign(Long projectAttachmentId, String fileIds, IRequest iRequest, HttpServletRequest request, HttpServletResponse response) throws IOException;

    HlsCusPrjProjectAttachment changeSave(IRequest requestCtx, Long contractId);


    List<HlsCusPrjProjectAttachment> prjProjectAttachmentChangePpDetailQuery(IRequest iRequest, HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment, int page, int pageSize);


    void downloadZipAttachmentFile(HlsCusPrjProjectAttachment attachment, IRequest iRequest, HttpServletResponse response) throws IOException;

    List<HlsCusPrjProjectAttachment> queryContentFileInfo(IRequest iRequest, HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment, int page, int pageSize, int mode);
*/

    /**
     * 二期功能：进件投放审查通过后，复制附件
     * @param iRequest
     * @param contractId
     * @param projectId
     * @return
     */
    List<HlsCusPrjProjectAttachment> saveAttachmentFromPrj(IRequest iRequest, Long contractId, Long projectId);

    int selectAttachmentCodeNullCount(Long projectId, String projectAttachmentCategory);

    int selectAttachmentExistCount(Long projectId, String projectAttachmentCategory);
    int saveProjectAttachment(IRequest requestContext,HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment) throws HlsCusException;

    void batchUpdateAttachemnt(IRequest requestContext, List<HlsCusPrjProjectAttachment> list);

    boolean deleteAttachment(Long attachmentId);

    List<HlsCusPrjProjectAttachment> selectContractAttachmentInfo(IRequest iRequest, HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment, int page, int pageSize);

}
