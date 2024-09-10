package com.hand.hls.fct.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.fct.dto.HlsChanceBusinessAccessCompare;
import com.hand.hls.fct.dto.HlsCusCreditProject;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;
import com.hand.hls.hls.dto.HlsCusHlsMarketingReport;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.utils.ResMessageException;
import hls.core.utils.exception.HlsCusException;
import uncertain.composite.CompositeMap;

import java.text.ParseException;
import java.util.List;

public interface HlsCusHlsCreditLineChanceService extends IBaseService<HlsCusHlsCreditLineChance>, ProxySelf<HlsCusHlsCreditLineChanceService> {
    //合同文本
    public static final String REPORT_DOCX = "HLS_CREDIT_CHANCE_DOCX";
    public static final String DATA_ECXEPTION = "数据异常";
    //附件表名
    public static final String FND_ATM_ATTACHMENT_MULTI = "fnd_atm_attachment_multi";
    public static final String NOT_FOUND_CONTRACT_TEMPLATE  = "找不到对应的项目立项报告模板";
    public static final String TABLE_NAME = "table_name";
    public static final String CONTRACT_DOCX_DESCRIPTION = "项目立项报告";
    //综合授信项目查询（根据状态）
    List<HlsCusHlsCreditLineChance> selectCreditLineChanceByStatus(IRequest iRequest, HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance, Integer page, Integer pageSize,String sortName,String sortOrder);

    //授信立项扇形图查询
    List<HlsCusHlsCreditLineChance> selectCreditLineChanceStatusInfo(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance, IRequest iRequest);

    //授信立项首页条件查询
    List<HlsCusHlsCreditLineChance> selectCreditLineChanceByCondition(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance, IRequest iRequest);

    //授信立项提交启动工作流
    HlsCusHlsCreditLineChance submitApproval(IRequest requestCtx, HlsCusCreditProject dto);

    /**
     * 授项立项保存
     *
     * @param requestCtx
     * @param dto
     * @return
     */

    HlsCusCreditProject cascadeSubmit(IRequest requestCtx, HlsCusCreditProject dto);

    List<HlsCusHlsCreditLineChance> selectModelByCondition(IRequest iRequest, HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance, Integer page, Integer pageSize);

    /**
     * 授信立项关闭
     * @param requestCtx
     * @param hlsCusHlsCreditLineChances
     * @return
     */
    List<HlsCusHlsCreditLineChance> closeCreditChance(IRequest requestCtx, List<HlsCusHlsCreditLineChance> hlsCusHlsCreditLineChances);

    /*项目立项创建*/
    HlsCusHlsCreditLineChance hlsCreditCreate(IRequest requestCtx, HlsCusHlsCreditLineChance dto) throws HlsCusException;
    /*项目立项业务意向书生成*/
    List<FndAttachment> reportCreateDocx(IRequest iRequest, Long chanceId, String templateType) throws Exception;
    List<FndAttachment> reportCreateDocx(IRequest iRequest, HlsCusHlsCreditLineChance hlsCreditLineChance) throws Exception;

    //生成权限字符串
    String generateAuthorityString(IRequest iRequest);

    ResponseData queryChanceId(CompositeMap var1, String var2);

    //
    List<HlsCusHlsCreditLineChance> selectCreditLineChanceByCreditLineStatus();
    List<HlsCusHlsCreditLineChance> selectCreditLineChanceById1(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);

    void generateChanceCompare(IRequest requestCtx, HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);

    void chanceAccessCompareImport(IRequest iRequest, Long hdId , Long chanceId) throws ExcelException, Exception, ParseException;

    //保存chang_req
    HlsCusHlsCreditLineChance changeCreate(IRequest request, HlsCusHlsCreditLineChance hlsCusChance) throws com.hand.hls.exception.HlsCusException;
    //保存chang_req
    HlsCusHlsCreditLineChance reCreate(IRequest request, HlsCusHlsCreditLineChance hlsCusChance) ;


    //立项变更，备份从表数据
    void chanceBackUp(IRequest iRequest, HlsCusHlsCreditLineChance prjChance) throws com.hand.hls.exception.HlsCusException;


    //立项变更工作流
    HlsCusHlsCreditLineChance prjChangeSubmitWfl(IRequest iRequest, HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);
    //立项变更关闭
    boolean changeCancel(IRequest requestCt, HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);

    //授信关闭
    boolean creditCancel(IRequest requestCt, HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);

    void deleteOld(Long projectIdOld, IRequest request);
    void updateOld(IRequest requestCtx, Long projectIdOld, Long projectIdNew, IRequest request) throws com.hand.hls.exception.HlsCusException;

    void deleteRelaProject(IRequest requestCtx, List<HlsCusHlsCreditLineChanceBp> bpList);

    void checkCooperativeOrganization(IRequest iRequest, HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance) throws HlsCusException;

    List<HlsCusHlsCreditLineChance> createInfo(Long userId);

    List<HlsCusHlsCreditLineChance> submit(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance,IRequest requestCtx);

    List<HlsCusHlsCreditLineChance> submitCredit(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance, IRequest requestCtx) throws HlsCusException;

    void transfer(IRequest requestCtx,List<HlsCusHlsCreditLineChance> chanceList, String flag,Long userId) throws ResMessageException;
}