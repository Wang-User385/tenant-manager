package com.hand.hls.bp.mapper;

import com.hand.hap.core.IRequest;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.partner.dto.QueryHlsBpMasterDTO;
import com.hand.hls.prj.dto.HlsBpMaster;
import com.hand.hls.prj.mapper.HlsBpMasterMapper;
import org.apache.ibatis.annotations.Param;
import uncertain.composite.CompositeMap;

import java.util.List;
import java.util.Map;

public interface HlsCusBpMasterMapper extends HlsBpMasterMapper<HlsCusBpMaster> {

    List<HlsCusBpMaster> hlsCusHomeSecondQuery(HlsCusBpMaster hlsCusBpMaster);

    List<HlsCusBpMaster> queryBpTenant(HlsCusBpMaster hlsCusBpMaster);

    List<HlsCusBpMaster> bpMasterCheck(HlsCusBpMaster hlsCusBpMaster);

    List<HlsCusBpMaster> bpTypeSelect(HlsCusBpMaster hlsCusBpMaster);

    List<HlsCusBpMaster> enterpriseUnifiedCreditCodeCheck(String enterpriseUnifiedCreditCode);

    List<HlsCusBpMaster> organizationCodeCheck(String organizationCode);

    List<HlsCusBpMaster> queryBpInfoAmount(HlsCusBpMaster hlsCusBpMaster);

    List<HlsCusBpMaster> homeThirdQuery2(@Param("userId") Long var1, @Param("bpCategory") String var2, @Param("bpType") String var3, @Param("bpTypeDesc") String var4, @Param("orgTypeDesc") String var5, @Param("bpName") String var6);

    List<Map> queryBpByBpTypeForExcel(String bpType);

    List<Map> queryBpProject(Map map);

    List<HlsCusBpMaster> queryIntegrate(IRequest requestContext, @Param("map") Map<String, String> map);

    List<HlsCusBpMaster> masterCreditDetailAndNullRoleQuery(HlsCusBpMaster hlsCusBpMaster);

    List<HlsCusBpMaster> queryBpIdByName(HlsCusBpMaster hlsCusBpMaster);

    List<HlsCusBpMaster> queryHlsBpMasterCreditInfo(@Param("creditLineId") Long creditLineId);

    List<HlsCusBpMaster> queryHlsBpMasterCreditInfoAll(HlsCusBpMaster hlsCusBpMaster);

    List<HlsCusBpMaster> creditExposureAmt(Long bpId);

    List<HlsCusBpMaster> creditMembershipGroup(Long bpId);

    int selectBpMasterCount(HlsCusBpMaster hlsCusBpMaster);

    List<HlsCusBpMaster> selectGroupInfo(HlsCusBpMaster hlsCusBpMaster);

    /**
     * 合同起贷状态的个数  用来决定客户信息编辑与否
     *
     * @param hlsCusBpMaster
     * @return
     */
    int selectBpMasterInceptContractCount(HlsCusBpMaster hlsCusBpMaster);


    List<HlsCusBpMaster> selectHistoryBpMaster(HlsCusBpMaster hlsCusBpMaster);

    //更新移交客户信息
    void updateTransforBp(HlsCusBpMaster hlsCusBpMaster);

    /**
     * 客户信息主界面查询 由 {@link #queryIntegrate(IRequest, Map<String, String>)}  方法修改
     *
     * @param hlsCusBpMaster
     * @return java.util.List<com.hand.hls.bp.dto.HlsCusBpMaster>
     * queryIntegrate
     */
    List<HlsCusBpMaster> queryHlsBpMasterInfo(HlsCusBpMaster hlsCusBpMaster);

    List<HlsCusBpMaster> queryHlsBpMasterInfoQuery(HlsCusBpMaster hlsCusBpMaster);

    List<HlsCusBpMaster> queryHlsBpMasterInfoQueryNew(HlsCusBpMaster hlsCusBpMaster);

    /**
     * 客户财务报表信息 由 {@link #queryIntegrate(IRequest, Map<String, String>)}  方法修改
     *
     * @param hlsCusBpMaster
     * @return java.util.List<com.hand.hls.bp.dto.HlsCusBpMaster>
     * queryIntegrate
     */
    List<HlsCusBpMaster> queryHlsBpMasterInfoFin(HlsCusBpMaster hlsCusBpMaster);

    /**
     * 客户信息明细查询
     *
     * @param hlsCusBpMaster
     * @return java.util.List<com.hand.hls.bp.dto.HlsCusBpMaster>
     */
    List<HlsCusBpMaster> queryCusBpMasterDetails(HlsCusBpMaster hlsCusBpMaster);

    List<HlsCusBpMaster> queryCusBpMasterDetails1(HlsCusBpMaster hlsCusBpMaster);

    /**
     * 客户信息LOV
     *
     * @param hlsCusBpMaster
     * @return java.util.List<com.hand.hls.bp.dto.HlsCusBpMaster>
     */
    List<HlsCusBpMaster> queryBpLov(HlsCusBpMaster hlsCusBpMaster);

    List<HlsCusBpMaster> queryBpMasterFinanceImport(HlsCusBpMaster hlsCusBpMaster);

    List<CompositeMap> queryCreatedName(CompositeMap var1, String var2);

    List<HlsCusBpMaster> queryCusBpMasterDetailsForPrj(HlsCusBpMaster hlsCusBpMaster);


    List<HlsCusBpMaster> paymentFundTenantInfo(HlsCusBpMaster hlsCusBpMaster);

    List<HlsCusBpMaster> queryForMarketing(HlsCusBpMaster hlsCusBpMaster);

    List<CompositeMap> queryCreatedId(CompositeMap var1, String var2);

    String selectSCodeByVName(String category);

    List<String> selectBpNameList(Long companyId);

    String selectCurrencyCode(String currencyName);

    Long selectBpId(String bpName);

    List<HlsCusBpMaster> selectEbsCodeBp(HlsCusBpMaster hlsCusBpMaster);


    List<HlsCusBpMaster> queryBpDetails(Map var1);

    List<HlsBpMaster> queryForNp(Map var1);
    List<String> queryProjectStatusbyBPId( @Param("bpId")  Long bpId);
    List<HlsCusBpMaster> queryVenderInfo(HlsCusBpMaster hlsCusBpMaster);

    HlsCusBpMaster selectMasterByIdCardNo(String idCardNo);

    String getCityIdAndProvinceIdByDistrictId(@Param("districtId") Long districtId);

    QueryHlsBpMasterDTO getQueryHlsBpMasterDTOByBpId(@Param("bpId")Long bpId);
}