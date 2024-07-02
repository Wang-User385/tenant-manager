package com.hand.hls.plm.pli.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.service.ICodeService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.plm.dto.HlsCusShips;
import com.hand.hls.plm.mapper.HlsCusShipsMapper;
import com.hand.hls.plm.pli.dto.HlsCusPostloanInspection;
import com.hand.hls.plm.pli.dto.PliLeaseItem;
import com.hand.hls.plm.pli.dto.PlmPliContract;
import com.hand.hls.plm.pli.mapper.HlsCusPostloanInspectionMapper;
import com.hand.hls.plm.pli.mapper.PlmPliContractMapper;
import com.hand.hls.plm.pli.service.IPliLeaseItemService;
import com.hand.hls.plm.pli.service.IPlmPliContractService;
import com.hand.hls.prj.dto.HlsCusPrjProjectLeaseItem;
import com.hand.hls.prj.service.HlsCusPrjProjectLeaseItemService;
import com.hand.hls.utils.ExportExcelUtil;
import com.hand.hls.utils.HlsCusConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class PlmPliContractServiceImpl extends BaseServiceImpl<PlmPliContract> implements IPlmPliContractService {
    @Autowired
    private PlmPliContractMapper mapper;
    @Autowired
    private ICodeService codeService;
    @Autowired
    private HlsCusPostloanInspectionMapper hlsCusPostloanInspectionMapper;
    @Autowired
    private HlsCusPrjProjectLeaseItemService hlsCusPrjProjectLeaseItemService;
    @Autowired
    private IPliLeaseItemService pliLeaseItemService;
    @Autowired
    private HlsCusShipsMapper shipsMapper;
    @Override
    public List<PlmPliContract> selectPlmPliContractData(IRequest iRequest, PlmPliContract dto, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return mapper.selectPlmPliContractData(dto);
    }

    @Override
    public List<PlmPliContract> selectOtherAll(PlmPliContract dto, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return mapper.selectOtherAll(dto);
    }

    @Override
    public List<PlmPliContract> selectOtherAllProject(PlmPliContract dto, int page, int pageSize){
        PageHelper.startPage(page, pageSize);
        return mapper.selectOtherAllProject(dto);
    }

    @Override
    public List<PlmPliContract> addOtherAll(List<PlmPliContract> dtos) {
        for (PlmPliContract dt : dtos) {
            if (HlsCusConstant.FLAG.Y.equalsIgnoreCase(dt.getIsSaved())) {
                PlmPliContract newDt = new PlmPliContract();
                newDt.setBpId(dt.getBpId());
                newDt.setProjectId(dt.getProjectId());
                newDt.setPostloanInspectionId(dt.getPostloanInspectionId());
                List<PlmPliContract> list = mapper.selectOtherAll(newDt);
                if (list.size() > 0) {
                    list.get(0).set__status(HlsCusConstant.DATA_STATUS.STATUS_ADD);
                    list.get(0).setPostloanInspectionId(dtos.get(0).getPostloanInspectionId());
                    mapper.insertSelective(list.get(0));
                }
            }
        }
        return dtos;
    }

    @Override
    public List<PlmPliContract> selectMeetingRiskDescription(PlmPliContract dto, int page, int pageSize) {
        List<PlmPliContract> resList = new ArrayList<>(0);
        if (null!=dto&&dto.getPostloanInspectionId()!=null)
        {
            mapper.selectPlmPliContractData(dto).stream()
                    .collect(Collectors.collectingAndThen(
                                Collectors.toCollection(
                                        () -> new TreeSet<>(Comparator.comparing(o -> o.getPostloanInspectionId() + ";" + o.getContractType()))),
                                ArrayList::new))
                    .forEach(contract-> resList.addAll(mapper.selectMeetingRiskDescription(contract)));
        }
        return resList;
    }

    @Override
    public void exportFivePlmPliContract(IRequest iRequest, HttpServletRequest request, HttpServletResponse response, PlmPliContract dto) {
        List<String>  colNameList = Lists.newArrayList("业务合同编号", "合同查询编号","客户名称", "业务类型","所属部门",
                "投放金额", "当前剩余本金", "当前资产分类", "建议资产分类");
        List<String> colGetMethods = Lists.newArrayList("approvalNumber","contractNumber","bpName","businessType","unitName",
                "financeAmount","remainAmount","fiveClassificationResult","suggestedClassification");

        XSSFWorkbook xwork = new XSSFWorkbook();
        XSSFSheet sheet = xwork.createSheet("sheet1");
        int dataRowNum = ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
        List<PlmPliContract> plmPliContracts = mapper.selectOtherAllProject(dto);
        for (PlmPliContract pliContract : plmPliContracts) {
            XSSFRow row = sheet.createRow(dataRowNum++);
            pliContract.setFiveClassificationResult(codeService.getCodeMeaningByValue(RequestHelper.getCurrentRequest(),"PLM.FC_RESULT",pliContract.getFiveClassificationResult()));
            pliContract.setSuggestedClassification(codeService.getCodeMeaningByValue(RequestHelper.getCurrentRequest(),"PLM.FC_RESULT",pliContract.getSuggestedClassification()));
            switch(pliContract.getBusinessType()){
                case "FACTORING" :
                    pliContract.setBusinessType("正向保理");
                    break;
                case "REVERSE_FACTORING" :
                    pliContract.setBusinessType("反向保理");
                    break;
                case "LEASE" :
                    pliContract.setBusinessType("直接租赁");
                    break;
                case "LEASEBACK" :
                    pliContract.setBusinessType("售后回租");
                    break;
                case "OPERATING_LEASE" :
                    pliContract.setBusinessType("经营性租赁");
                    break;
                default :
                    pliContract.setBusinessType("");
            }
            try {
                ExportExcelUtil.setData(xwork, sheet, row, pliContract, colGetMethods);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        try {
            ExportExcelUtil.IOWrite(xwork, null, request, response, "五级分类信息");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PlmPliContract> contractSelect(IRequest requestCtx, List<PlmPliContract> dto) {
        List<PlmPliContract> list = batchUpdate(requestCtx, dto);
        HlsCusPostloanInspection hlsCusPostloanInspection = new HlsCusPostloanInspection();
        hlsCusPostloanInspection.setPostloanInspectionId((dto.get(0).getPostloanInspectionId()));
        List<HlsCusPostloanInspection> hlsCusPostloanInspectionList = hlsCusPostloanInspectionMapper.select(hlsCusPostloanInspection);
        String type = hlsCusPostloanInspectionList.get(0).getInspectionType();
        if (PlmPliFrequencySetUtilImpl.OFF_SITE_INSPECT.equalsIgnoreCase(type)
                ||PlmPliFrequencySetUtilImpl.OFF_SITE_INSPECT_PLAN.equalsIgnoreCase(type)) {
            for (PlmPliContract con : dto) {
                if ("Y".equalsIgnoreCase(con.getIsPlaneCheck())) {
                    //插入租赁物
                    HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = new HlsCusPrjProjectLeaseItem();
                    hlsCusPrjProjectLeaseItem.setProjectId(con.getProjectId());
                    List<HlsCusPrjProjectLeaseItem> leaseItemList = hlsCusPrjProjectLeaseItemService.select(requestCtx, hlsCusPrjProjectLeaseItem, 1, 99999);
                    if (PlmPliFrequencySetUtilImpl.OFF_SITE_INSPECT_PLAN.equalsIgnoreCase(type))
                    {
                        leaseItemList=leaseItemList.stream().filter(item->"PLANE".equalsIgnoreCase(item.getItemType())).collect(Collectors.toList());
                    }
                    List<PliLeaseItem> pliLeaseItemList = new ArrayList<>();
                    for (HlsCusPrjProjectLeaseItem leaseItem : leaseItemList) {
                        PliLeaseItem pliLeaseItem = new PliLeaseItem();
                        pliLeaseItem.setContractId(con.getContractId());
                        pliLeaseItem.setProjectId(con.getProjectId());
                        pliLeaseItem.setPostLoanInspectionId(dto.get(0).getPostloanInspectionId());
                        pliLeaseItem.setProjectLeaseItemId(leaseItem.getProjectLeaseItemId());
                        pliLeaseItem.set__status("insert");
                        pliLeaseItemList.add(pliLeaseItem);
                    }
                    pliLeaseItemService.batchUpdate(requestCtx,pliLeaseItemList);
                }
            }
        }
        insertShipInfo(dto);
        return list;
    }

    /**
     * 将合同信息插入船舶表
     * @param dto
     */
    private void insertShipInfo(List<PlmPliContract> dto)  {
        if ( CollectionUtils.isNotEmpty(dto))
        {
            for (PlmPliContract con : dto) {
                Assert.notNull(con,"contract info cant be null!");
                //获取项目ID
                if (null!=con.getProjectId())
                {
                    //根据项目ID查询相关船舶信息
                    List<Float> ids= shipsMapper.selectShipInfoIdsByProjectId(con.getProjectId());
                    if (!ids.isEmpty())
                    {
                        ids.forEach(itemId->{
                            HlsCusShips hlsCusShips =new HlsCusShips();
                            hlsCusShips.setItemId(itemId);
                            hlsCusShips.setPliContractId(con.getPliContractId());
                            hlsCusShips.setPostloanInspectionId(Long.parseLong((String) ObjectUtils.defaultIfNull(con.getAttribute1(),"0")));
                            shipsMapper.insertSelective(hlsCusShips);
                        });
                    }
                }
            }
        }
        else {
            throw new IllegalArgumentException("contract info cant be null !" );
        }
    }
}
