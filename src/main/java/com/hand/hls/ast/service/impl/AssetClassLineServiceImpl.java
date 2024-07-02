package com.hand.hls.ast.service.impl;

import com.github.pagehelper.StringUtil;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.mapper.CodeValueMapper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.ast.dto.AssetClassHead;
import com.hand.hls.ast.dto.AssetClassLine;
import com.hand.hls.ast.mapper.AssetClassHeadMapper;
import com.hand.hls.ast.mapper.AssetClassLineMapper;
import com.hand.hls.ast.service.IAssetClassLineService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.service.IConContractService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.prj.service.IBpMasterReplyService;
import com.hand.hls.utils.MathUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.hand.hls.ast.service.impl.AssetClassHeadServiceImpl.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class AssetClassLineServiceImpl extends BaseServiceImpl<AssetClassLine> implements IAssetClassLineService {

    @Autowired
    private AssetClassLineMapper assetClassLineMapper;
    @Autowired
    private IBpMasterReplyService bpMasterReplyService;
    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;
    @Autowired
    private CodeValueMapper codeValueMapper;
    @Autowired
    private AssetClassHeadMapper assetClassHeadMapper;

    private static final String PARAM_NOT_FOUND = "参数未找到";
    private static final Long READ_LINE = 0L;
    private static final String ASSET_CLASS_LINE_SHEET = "sheet";
    private static final String ASSET_CLASS = "ASSET_CLASS";
    private static final String AST020F2 = "AST020F2";
    private static final String AST020F3 = "AST020F3";
    private static final String AST020S2 = "AST020S2";

    public ResponseData updateInitLevel(IRequest iRequest, List<AssetClassLine> lineList) {
        ResponseData responseData = new ResponseData(false);

        List<AssetClassLine> endList = new ArrayList<>();

        for (AssetClassLine assetClassLine :
                lineList) {
            List<AssetClassLine> assetClassLines = assetClassLineMapper.queryLineInfoByIdAndReviewLevel(assetClassLine);
            for (AssetClassLine line :
                    assetClassLines) {
                //将初分级别和复核级别都更新为页面中更新的初分级别
                line.setInitialLevel(assetClassLine.getInitialLevel());
                line.setReviewLevel(assetClassLine.getInitialLevel());
                line.setApproveReviewLevel(assetClassLine.getInitialLevel());
                line.set__status(DTOStatus.UPDATE);
                endList.add(line);
            }
        }

        self().batchUpdate(iRequest, endList);
        responseData.setSuccess(true);
        return responseData;
    }

    public ResponseData updateApproveReviewLevel(IRequest iRequest, List<AssetClassLine> lineList) {
        ResponseData responseData = new ResponseData(false);

        List<AssetClassLine> endList = new ArrayList<>();

        for (AssetClassLine assetClassLine :
                lineList) {
            List<AssetClassLine> assetClassLines = assetClassLineMapper.queryLineInfoByIdAndInitLevel(assetClassLine);
            for (AssetClassLine line :
                    assetClassLines) {
                //修改合同明细页面的审批复核级别
                line.setApproveReviewLevel(assetClassLine.getApproveReviewLevel());
                line.set__status(DTOStatus.UPDATE);
                endList.add(line);
            }
        }

        self().batchUpdate(iRequest, endList);
        responseData.setSuccess(true);
        return responseData;
    }

    /**
     * 保存资产分类行数据
     * @param classHeadId
     * @param assetClassLineList
     */
    public void insertClassLineInfo(IRequest iRequest,Long classHeadId,List<AssetClassLine> assetClassLineList){
        //回购期
//        Long counterpurchase;
//        //获取回购期
//        String repurchasePeriod = bpMasterReplyService.queryReplyParaValueToByBpIdAndReplyPara(assetClassLineList.get(0).getManufacturerId(), "REPURCHASE_PERIOD");
//
//        if(StringUtils.isEmpty(repurchasePeriod)){
//            counterpurchase = 0L;
//        }else {
//            counterpurchase = Long.valueOf(repurchasePeriod);
//        }

        for (AssetClassLine assetClassLine : assetClassLineList) {
            assetClassLine.set__status(DTOStatus.ADD);
            assetClassLine.setClassHeadId(classHeadId);
            //剩余敞口
//            double sub = MathUtil.sub(assetClassLine.getRemainingPrincipal(), assetClassLine.getRemainingMargin(), 2);
//            if(Double.doubleToLongBits(sub) < 0L){
//                sub = 0.0;
//            }
//            assetClassLine.setRemainingExposure(sub);
            //逾期天数(租金)
            Long overdueDays = assetClassLine.getOverdueDays();
            //逾期天数(罚息)
            Long fxOverdueDays = assetClassLine.getFxOverdueDays();
            //按照合同罚息或租金最大逾期天数进行资产分类等级判断
            overdueDays = (overdueDays > fxOverdueDays ? overdueDays : fxOverdueDays);

            if (overdueDays == 0L) {
                assetClassLine.setInitialLevel("0");
                assetClassLine.setReviewLevel("0");
                assetClassLine.setApproveReviewLevel("0");
                assetClassLine.setApproveInitialLevel("0");
            } else if (overdueDays >= 1L && overdueDays <= 90L) {
                assetClassLine.setInitialLevel("1");
                assetClassLine.setReviewLevel("1");
                assetClassLine.setApproveReviewLevel("1");
                assetClassLine.setApproveInitialLevel("1");
            }
//            } else if (overdueDays > 60L && overdueDays <= 90L) {
//                assetClassLine.setInitialLevel(SPECIAL_MENTION_TWO);
//                assetClassLine.setReviewLevel(SPECIAL_MENTION_TWO);
//                assetClassLine.setApproveReviewLevel(SPECIAL_MENTION_TWO);
//                assetClassLine.setApproveInitialLevel(SPECIAL_MENTION_TWO);
//            } else if (overdueDays > 90L && overdueDays <= 180L) {
//                assetClassLine.setInitialLevel(SUBSTANDARD_ONE);
//                assetClassLine.setReviewLevel(SUBSTANDARD_ONE);
//                assetClassLine.setApproveReviewLevel(SUBSTANDARD_ONE);
//                assetClassLine.setApproveInitialLevel(SUBSTANDARD_ONE);
//            } else if (overdueDays > 180L && overdueDays <= 270L) {
//                assetClassLine.setInitialLevel(SUBSTANDARD_TWO);
//                assetClassLine.setReviewLevel(SUBSTANDARD_TWO);
//                assetClassLine.setApproveReviewLevel(SUBSTANDARD_TWO);
//                assetClassLine.setApproveInitialLevel(SUBSTANDARD_TWO);
//            } else if (overdueDays > 270L && overdueDays < 360L) {
//                assetClassLine.setInitialLevel(DOUBTFUL_ONE);
//                assetClassLine.setReviewLevel(DOUBTFUL_ONE);
//                assetClassLine.setApproveReviewLevel(DOUBTFUL_ONE);
//                assetClassLine.setApproveInitialLevel(DOUBTFUL_ONE);
//            } else {
//                assetClassLine.setInitialLevel(LOSS);
//                assetClassLine.setReviewLevel(LOSS);
//                assetClassLine.setApproveReviewLevel(LOSS);
//                assetClassLine.setApproveInitialLevel(LOSS);
//            }

//            if (counterpurchase != null && overdueDays <= counterpurchase && overdueDays > 0L) {
//                assetClassLine.setInitialLevel(NORMAL_FOUR);
//                assetClassLine.setReviewLevel(NORMAL_FOUR);
//                assetClassLine.setApproveReviewLevel(NORMAL_FOUR);
//                assetClassLine.setApproveInitialLevel(NORMAL_FOUR);
//            }
//            if (overdueDays > 0L) {
//                assetClassLine.setInitialLevel(NORMAL_FOUR);
//                assetClassLine.setReviewLevel(NORMAL_FOUR);
//                assetClassLine.setApproveReviewLevel(NORMAL_FOUR);
//                assetClassLine.setApproveInitialLevel(NORMAL_FOUR);
//            }
        }
        self().batchUpdate(iRequest, assetClassLineList);
    }

//    @Override
//    public void excelBatchImport(IRequest iRequest, Long headerId, Long classHeadId, String layoutCode) throws HlsCusException {
//        if (Objects.isNull(headerId) || Objects.isNull(classHeadId)) {
//            throw new HlsCusException(PARAM_NOT_FOUND);
//        }
//        //获取批次信息
//        AssetClassHead assetClassHead = assetClassHeadMapper.selectByPrimaryKey(classHeadId);
//        //资产分类详情信息
//        FndInterfaceLines fndInterfaceLines = new FndInterfaceLines();
//        fndInterfaceLines.setHeaderId(headerId);
//        fndInterfaceLines.setReadLine(READ_LINE);
//        fndInterfaceLines.setSheetName(ASSET_CLASS_LINE_SHEET);
//        List<FndInterfaceLines> assetClassLines = fndInterfaceLinesMapper.fndInterfaceLinesDetailQuery(fndInterfaceLines);
//        if (CollectionUtils.isEmpty(assetClassLines)) {
//            throw new HlsCusException(new StringBuffer(ASSET_CLASS_LINE_SHEET).append("不能为空！").toString());
//        }
//        for (int i = 0; i < assetClassLines.size(); i++) {
//            String contractNumber;
//            int lineNumber = i+2;
//            List<AssetClassLine> assetClassLineList;
//            AssetClassLine classLine = new AssetClassLine();
//            classLine.setClassHeadId(classHeadId);
//            FndInterfaceLines assetClassLine = assetClassLines.get(i);
//            if (StringUtils.isEmpty(assetClassLine.getAttributes_1())) {
//                throw new HlsCusException(ASSET_CLASS_LINE_SHEET+"：sheet页第"+lineNumber+"行第1列不能为空！");
//            }else {
//                contractNumber = assetClassLine.getAttributes_1();
//            }
//            //根据classHeadId、contractNumber获取分类明细数据并进行导入数据一致性校验
//            classLine.setContractNumber(contractNumber);
//            //校验数据一致性
//            if (StringUtils.equals(AST020F2,layoutCode) || StringUtils.equals(AST020F3,layoutCode)) {
//                assetClassLineList = assetClassLineMapper.queryClassContractDetail(classLine);
//            } else {
//                assetClassLineList = assetClassLineMapper.queryClassContractDetailApproval(classLine);
//            }
//            if (CollectionUtils.isEmpty(assetClassLineList) || assetClassLineList.size()>1) {
//                throw new HlsCusException(ASSET_CLASS_LINE_SHEET + ":sheet页第"+lineNumber+"行第1列值有误！"+assetClassHead.getBatchNum()+"批次下不存在或存在多条："+contractNumber);
//            } else {
//                classLine = assetClassLineList.get(0);
//                //检验 合同编号 一致性
//                if (!StringUtils.equals(assetClassLine.getAttributes_1(),classLine.getContractNumber())) {
//                    throw new HlsCusException(ASSET_CLASS_LINE_SHEET + "sheet页第"+lineNumber+"行第1列值与"+assetClassHead.getBatchNum()+"批次下："+contractNumber+"不一致！");
//                }
//                //校验 业务模式 一致性
//                if (!StringUtils.equals(assetClassLine.getAttributes_2(),classLine.getBusinessTypeN())) {
//                    throw new HlsCusException(ASSET_CLASS_LINE_SHEET + "sheet页第"+lineNumber+"行第2列值与"+assetClassHead.getBatchNum()+"批次下："+contractNumber+"不一致！");
//                }
//                //校验 初分级别 一致性
//                if (StringUtils.equals(AST020F2,layoutCode) || StringUtils.equals(AST020F3,layoutCode)) {
//                    if (!StringUtils.equals(assetClassLine.getAttributes_3(),classLine.getInitialLevelN())) {
//                        throw new HlsCusException(ASSET_CLASS_LINE_SHEET + "sheet页第"+lineNumber+"行第3列值与"+assetClassHead.getBatchNum()+"批次下："+contractNumber+"不一致！");
//                    }
//                } else {
//                    if (!StringUtils.equals(assetClassLine.getAttributes_3(),classLine.getApproveInitialLevelN())) {
//                        throw new HlsCusException(ASSET_CLASS_LINE_SHEET + "sheet页第"+lineNumber+"行第3列值与"+assetClassHead.getBatchNum()+"批次下："+contractNumber+"不一致！");
//                    }
//                }
//                //校验 逾期天数（租金） 一致性
//                if (!StringUtils.equals(assetClassLine.getAttributes_5(),classLine.getOverdueDays().toString())) {
//                    throw new HlsCusException(ASSET_CLASS_LINE_SHEET + "sheet页第"+lineNumber+"行第5列值与"+assetClassHead.getBatchNum()+"批次下："+contractNumber+"不一致！");
//                }
//                //校验 逾期租金金额 一致性
//                if (new BigDecimal(assetClassLine.getAttributes_6()).compareTo(BigDecimal.valueOf(classLine.getOverdueRent())) != 0) {
//                    throw new HlsCusException(ASSET_CLASS_LINE_SHEET + "sheet页第"+lineNumber+"行第6列值与"+assetClassHead.getBatchNum()+"批次下："+contractNumber+"不一致！");
//                }
//                //校验 逾期天数（罚息） 一致性
//                if (!StringUtils.equals(assetClassLine.getAttributes_7(),classLine.getFxOverdueDays().toString())) {
//                    throw new HlsCusException(ASSET_CLASS_LINE_SHEET + "sheet页第"+lineNumber+"行第7列值与"+assetClassHead.getBatchNum()+"批次下："+contractNumber+"不一致！");
//                }
//                //校验 逾期罚息金额 一致性
//                if (new BigDecimal(assetClassLine.getAttributes_8()).compareTo(BigDecimal.valueOf(classLine.getFxOverdueRent())) != 0) {
//                    throw new HlsCusException(ASSET_CLASS_LINE_SHEET + "sheet页第"+lineNumber+"行第8列值与"+assetClassHead.getBatchNum()+"批次下："+contractNumber+"不一致！");
//                }
//                //校验 合同金额 一致性
//                if (new BigDecimal(assetClassLine.getAttributes_9()).compareTo(BigDecimal.valueOf(classLine.getContractAmount())) != 0) {
//                    throw new HlsCusException(ASSET_CLASS_LINE_SHEET + "sheet页第"+lineNumber+"行第9列值与"+assetClassHead.getBatchNum()+"批次下："+contractNumber+"不一致！");
//                }
//                //校验 投放时间 一致性
//                if (!StringUtils.equals(assetClassLine.getAttributes_10(),classLine.getDeliveryTimeN())) {
//                    throw new HlsCusException(ASSET_CLASS_LINE_SHEET + "sheet页第"+lineNumber+"行第10列值与"+assetClassHead.getBatchNum()+"批次下："+contractNumber+"不一致！");
//                }
//                //校验 租赁期限(月) 一致性
//                if (!StringUtils.equals(assetClassLine.getAttributes_11(),classLine.getLeaseTerm().toString())) {
//                    throw new HlsCusException(ASSET_CLASS_LINE_SHEET + "sheet页第"+lineNumber+"行第11列值与"+assetClassHead.getBatchNum()+"批次下："+contractNumber+"不一致！");
//                }
//
//            }
//            //解析复核级别
//            if (StringUtil.isNotEmpty(assetClassLine.getAttributes_4())) {
//                String value = codeValueMapper.selectCodeValuesByCodeNameAndValue(ASSET_CLASS, assetClassLine.getAttributes_4());
//                if (StringUtils.isEmpty(value)) {
//                    throw new HlsCusException(ASSET_CLASS_LINE_SHEET+"：sheet页第"+lineNumber+"行第4列值有误！");
//                }
//                classLine.set__status("update");
//                if (StringUtils.equals(AST020F2,layoutCode) || StringUtils.equals(AST020F3,layoutCode)) {
//                    classLine.setReviewLevel(value);
//                    assetClassLineMapper.updateReviewLevel(classLine);
//                } else {
//                    classLine.setApproveReviewLevel(value);
//                    assetClassLineMapper.updateApproveReviewLevel(classLine);
//                }
//            } else {
//                throw new HlsCusException(ASSET_CLASS_LINE_SHEET+"：sheet页第"+lineNumber+"行第4列不能为空！");
//            }
//
//        }
//    }

    @Autowired
    private IConContractService contractService;
    @Override
    public void transforByContracts(IRequest iRequest, List<HlsCusConContract> contracts,Long classHeadId){
        //合同表转化为资产分类行表
        List<AssetClassLine> assetClassLineList = assetClassLineMapper.queryOverdueContractInfoByContractIds(contracts,null);
        //保存资产分类行数据
        self().insertClassLineInfo(iRequest,classHeadId,assetClassLineList);

    }
}