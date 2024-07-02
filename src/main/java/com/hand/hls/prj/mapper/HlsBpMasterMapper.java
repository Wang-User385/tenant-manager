//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.BpCategoryInfoLov;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.prj.dto.BpDealerInfoLov;
import com.hand.hls.prj.dto.BpManufacturerInfoLov;
import com.hand.hls.prj.dto.BpVenderInfoLov;
import com.hand.hls.prj.dto.HlsBpMaster;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface HlsBpMasterMapper <T extends HlsCusBpMaster> extends Mapper<HlsCusBpMaster> {
    List<HlsBpMaster> query();

    List<HlsBpMaster> bpMasterHomeQuery(HlsBpMaster var1);

    /**
     * 二期功能：保证金的付款对象查询
     * @param transactionId
     * @return
     */
    List<HlsBpMaster> queryPaymentBpMaster(String transactionId);


    List<HlsBpMaster> queryBpMasterLov(HlsBpMaster var1);

    List<HlsBpMaster> queryBpMasterLov2(HlsBpMaster var1);

    List<HlsBpMaster> queryDetails(Map var1);

    List<HlsBpMaster> queryForNp();

    List<HlsBpMaster> queryContractBpDetail(Map var1);

    List<Map> getBpCodeLov();

    List<Map> getYesNoCombo();

    List<Map> getBpClassCombo();

    List<Map> getZxBpTypeCombo();

    List<Map> getEcoTypeCombo();

    List<Map> getProfitCenterCombo();

    List<Map> getCompanyNatureCombo();

    List<Map> getActControlTypeCombo();

    List<Map> getZX_CertType();

    List<Map> getCurrencyCombo();

    List<Map> getCreditRatingCombo();

    List<Map> getMarketLocation();

    List<Map> getEnterpriseScale();

    List<Map> getScaleTypeCombo();

    List<Map> getHosQualityCombo();

    List<Map> getHosCategoryCombo();

    List<Map> getZxSubTypeCombo();

    List<Map> getZxOrgStatusCombo();

    List<Map> getEnterScopeCombo();

    List<Map> getAddrTypeCombo();

    List<Map> getHolderTypeCombo();

    List<Map> getRegnoTypeCombo();

    List<Map> getCertOrgTypeCombo();

    List<Map> getStockHolderTypeCombo();

    List<Map> getAffiliatedTypeCombo();

    List<Map> getMemType();

    List<Map> getCurrency();
    List<String> queryProjectStatusbyBPId( @Param("bpId")  Long bpId);

    List<Map> getBpTypeCombo();

    List<Map> getInvoiceKindCombo();

    List<Map> getTaxpayerTypeCombo();

    String getLatestBpCode();

    List<HlsBpMaster> selectForLovIf(HlsBpMaster var1);

    List<Map> industryTree();
    List<Map> businessTree();
    List<Map> organizationTree();
    List<Map> economicSectorTree();

    List<HlsBpMaster> creditOrgBpQuery(HlsBpMaster var1);

    List<Map> bpMasterAttachQuery();

    List<HlsBpMaster> lonCreditBpLovQuery(HlsBpMaster var1);

    List<HlsCusBpMaster> queryById(Long var1);

    List<HlsCusBpMaster> queryBpIds(Map<String, Object> var1);

    String selectBpClass(String var1);

    String validateCredit(String var1);

    String validateCardNo(String var1);

    List<HlsCusBpMaster> selectBpCount();

    List<HlsCusBpMaster> editQuery(Long var1);

    List<HlsCusBpMaster> masterDetailQuery(HlsCusBpMaster var1);

    List<HlsCusBpMaster> selectCreditLov(HlsCusBpMaster var1);

    List<HlsCusBpMaster> selectHlsBpMaster(HlsCusBpMaster var1);

    List<HlsCusBpMaster> queryByCodeOrName(HlsCusBpMaster var1);

    List<HlsCusBpMaster> homeFirstQuery(@Param("userId") Long var1);

    List<HlsCusBpMaster> homeFirstQuery2(@Param("userId") Long var1, @Param("bpCategory") String var2, @Param("bpType") String var3);

    List<HlsCusBpMaster> homeThirdQuery(Long var1);

    List<HlsCusBpMaster> homeThirdQuery2(@Param("userId") Long var1, @Param("bpCategory") String var2, @Param("bpType") String var3);

    List<HlsCusBpMaster> homeFourQuery();

    List<HlsCusBpMaster> homeSecordQuery();

    List<HlsCusBpMaster> homeQuery(@Param("inputValue") String var1);

    List<HlsCusBpMaster> homeQuery2(@Param("inputValue") String var1);

    List<HlsCusBpMaster> countQuery();

    List<Map> queryRecieivedPayments(Map var1);

    List<HlsCusBpMaster> selectAllBpMaster(HlsCusBpMaster var1);

    List<HlsCusBpMaster> selectGeneralBpMaster(HlsCusBpMaster var1);

    List<Map> invoicMessage(Map var1);

    List<HlsCusBpMaster> invoicDetail(HlsCusBpMaster var1);

    List<HashMap> getBpInvoiceInfoByGroup(HashMap var1);

    List<HlsCusBpMaster> selectHlsBpMasterById(HlsCusBpMaster var1);

    List<HlsCusBpMaster> getTenantBpInfo(HlsCusBpMaster var1);

    List<HlsCusBpMaster> getGuarantorInfo(HlsCusBpMaster var1);

    List<HlsCusBpMaster> contractBpQuery(@Param("contractId") Long var1);

    List<Map> getAllCreditCount();

    List<HlsCusBpMaster> queryBpLov(HlsCusBpMaster var1);

    List<HlsCusBpMaster> masterCreditDetailQuery(HlsCusBpMaster var1);

    List<HlsCusBpMaster> queryBpByBpType(HlsCusBpMaster var1);

    List<HlsCusBpMaster> queryIndustryLov(HlsCusBpMaster var1);

    List<HlsCusBpMaster> validBpName(@Param("bpName") String var1);

    List<BpCategoryInfoLov> bpTypeInfoQuery(BpCategoryInfoLov var1);
    List<Map> queryVenderInfo();


    List<BpVenderInfoLov> bpVenderInfoQuery(BpVenderInfoLov bpManufacturerInfoLov);
    List<HlsBpMaster> queryBpMasterVenderLov(HlsBpMaster hlsBpMaster);

    List<HlsBpMaster> queryBpMasterVenderLovNew(HlsBpMaster hlsBpMaster);

    /**
     * 查询商业伙伴
     */
    HlsBpMaster queryByBpId(@Param("bpId") Long bpId);
    List<BpManufacturerInfoLov> bpManufacturerInfoQuery(BpManufacturerInfoLov bpManufacturerInfoLov);
    List<HlsBpMaster> queryManufacturerPartnerInfo();
    List<HlsBpMaster> bpManufacturerPartnerLovQuery();

    List<BpDealerInfoLov> bpDealerInfoQuery(BpDealerInfoLov bpDealerInfoLov);
}
