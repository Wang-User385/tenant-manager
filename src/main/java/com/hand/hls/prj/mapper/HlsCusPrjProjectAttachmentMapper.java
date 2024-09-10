package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCusFctProjectAttachment;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface HlsCusPrjProjectAttachmentMapper extends Mapper<HlsCusPrjProjectAttachment> {
    Map<String, Long> queryPrjAttachmentCurrentMonthAppend();

    Map<String, Long> queryFctAttachmentCurrentMonthAppend();

    Map<String, Long> queryAttachmentCountForArcHome();

    List<HlsCusPrjProjectAttachment> queryByProjectId(Long projectId);

    List<HlsCusPrjProjectAttachment> prjProjectAttachmentDetailQuery(HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment);

    List<HlsCusFctProjectAttachment> fctProjectAttachmentDetailQuery(HlsCusFctProjectAttachment hlsCusFctProjectAttachment);

    List<HlsCusPrjProjectAttachment> selectAttInfo(HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment);

    List<HlsCusPrjProjectAttachment> riskReportDetailQuery(HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment);

    List<HlsCusPrjProjectAttachment> selectByprojectIdAndCategory(HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment);

    List<HlsCusPrjProjectAttachment> queryAllFile(HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment);

    Long selectOrderNumberMax(HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment);

    int selectAttachmentCodeNullCount(@Param("projectId") Long projectId, @Param("projectAttachmentCategory") String projectAttachmentCategory);

    int selectAttachmentExistCount(@Param("projectId") Long projectId, @Param("projectAttachmentCategory") String projectAttachmentCategory);

    List<HlsCusPrjProjectAttachment>  selectProjectAttachmentData(HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment);

    List<HlsCusPrjProjectAttachment> prjProjectAttachmentChangePpDetailQuery(HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment);

    List<HlsCusPrjProjectAttachment> queryContentFileInfo(HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment);

    List<HlsCusPrjProjectAttachment> queryContentFileInfo2(HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment);

    List<HlsCusPrjProjectAttachment> queryPrjAttachmentList(HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment);

    List<Map> selectProjectAttachmentInfo();

    /**
     * 二期功能：签约审批通过复制签约附件至合同
     * @param hlsCusPrjProjectAttachment
     * @return
     */
    List<HlsCusPrjProjectAttachment> queryCopySignFileToContract(HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment);


    List<Map> selectPrjProjectAttachmentInfo(HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment);
    List<HlsCusPrjProjectAttachment> selectPrjProjectAttachmentInfo1(HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment);

    List<HlsCusPrjProjectAttachment> selectContractAttachmentInfo(HlsCusPrjProjectAttachment t);
    List<HlsCusPrjProjectAttachment> selectContractAttachmentList(HlsCusPrjProjectAttachment t);
    List<HlsCusPrjProjectAttachment> selectContractChangeAttachmentInfo(HlsCusPrjProjectAttachment t);
    List<Map> selectContractChangeAttachmentInfo1(HlsCusPrjProjectAttachment t);

    List<HlsCusPrjProjectAttachment> selectContractAttachmentPlan(HlsCusPrjProjectAttachment t);

    List<HlsCusPrjProjectAttachment> selectPrjProjectJdAttachmentInfo(HlsCusPrjProjectAttachment t);

    List<HlsCusPrjProjectAttachment> queryAbsProductAttachmentInfo(HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment);

    List<Map> selectPrjProjectAllAttachmentInfo(HlsCusPrjProjectAttachment attachment);

    void deleteFndAtmAttachmentMulti(HlsCusPrjProjectAttachment attachment);
    void deleteFndAtmAttachment(HlsCusPrjProjectAttachment attachment);
    List<Map> selectConRentAttachment();

    String queryNameByPath(String var1);

    List<HlsCusPrjProjectAttachment> checkWflAtt(HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment);

    List<HlsCusPrjProjectAttachment> selectPrjSignAttachmentInfo(HlsCusPrjProjectAttachment t);

    List<HlsCusPrjProjectAttachment> selectProjectDocxNew(@Param("projectAttachmentCategory") String projectAttachmentCategory,
                                                          @Param("projectId") Long projectId,@Param("manufacturerCode") String manufacturerCode);

    HlsCusPrjProjectAttachment selectProjectDocxById(@Param("projectAttachmentId") Long projectAttachmentId);


    List<HlsCusPrjProjectAttachment> prjProjectAttachQuery(HlsCusPrjProject hlsCusPrjProject);

    HlsCusPrjProjectAttachment selectAttachYl(@Param("projectId") Long projectId,
                                              @Param("attachmentCode") String attachmentCode);

    Integer selectAttachMultiYlByCode(@Param("projectId") Long projectId,
                                      @Param("attachmentCode") String attachmentCode,
                                      @Param("tableName") String tableName,
                                      @Param("attachmentCategory") String attachmentCategory);

    List<HlsCusPrjProjectAttachment> findListByHlsCusPrjProjectAttachment(HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment);

    /***
     * 保理立项审批附件信息
     * @param hlsCusPrjProjectAttachment
     * @return
     */
    List<HlsCusPrjProjectAttachment> findFactroingApprovalInfo(HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment);
    List<HlsCusPrjProjectAttachment> findProjectApprovalInfo(HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment);



}