package com.hand.hls.prj.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.prj.dto.HlsCusPrjProjectLeaseItem;
import com.hand.hls.prj.mapper.HlsCusPrjProjectLeaseItemMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectLeaseItemService;
import com.hand.hls.utils.HlsCusGridExcelImportUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectLeaseItemServiceImpl extends BaseServiceImpl<HlsCusPrjProjectLeaseItem> implements HlsCusPrjProjectLeaseItemService {
    @Autowired
    private HlsCusPrjProjectLeaseItemMapper mapper;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;

    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;

    private static final String DEVICE = "DEVICE";
    private static final String INSTALLATION = "INSTALLATION";
    private static final String VESSEL = "VESSEL";
    private static final String PLANE = "PLANE";

    private static final String[] fileds = new String[]{
            "seqNumber", "leaseItemName", "quantity", "uom",
            "evaluationValue", "patternSpecification", "manufacturer", "leaseItemLocation",
            "evaluationBpName", "isregiste", "description"};

    private static Logger logger = LoggerFactory.getLogger(HlsCusPrjProjectLeaseItemServiceImpl.class);


    @Override
    public void excelImport(IRequest iRequest, Long headerId, Long projectId) {
        //先删除原来的数据
        mapper.deleteLeaseItemByProjectId(projectId);
        FndInterfaceLines interfaceLine = new FndInterfaceLines();
        interfaceLine.setHeaderId(headerId);
        List<FndInterfaceLines> fndInterfaceLinesList = fndInterfaceLinesMapper.select(interfaceLine);
        List<HlsCusPrjProjectLeaseItem> list = new ArrayList<>();
        List<Map> evaluatorMaps = hlsCusBpMasterMapper.queryBpByBpTypeForExcel("EVALUATOR");//评估机构
        Map<String, List<Map>> map = new HashMap<>();//map的第一个字段为 对应的属性，第二个字段为这个属性对应的syscode
        try {
            HlsCusGridExcelImportUtil.excelImport(HlsCusPrjProjectLeaseItem.class, list, fndInterfaceLinesList, "projectId", projectId, map);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new IllegalArgumentException("导入失败,请检查模版数据");
        }
        if (CollectionUtils.isNotEmpty(list)) {
            String evaluationName = "评估机构:";
            boolean dataRight = true;
            for (HlsCusPrjProjectLeaseItem dt : list) {
                evaluatorMaps.forEach((m) -> {
                    String meaning = (String) m.get("meaning");
                    String value = m.get("value").toString();
                    if (meaning.equals(dt.getEvaluationBpName())) {
                        dt.setEvaluationBpId(Long.parseLong(value));
                    }
                });
                if (dt.getEvaluationBpId() == null) {
                    evaluationName = evaluationName + dt.getEvaluationBpName() + ",";
                    dataRight = false;
                }
            }
            if (!dataRight) {
                throw new IllegalArgumentException("导入失败，" + evaluationName + "不存在");
            } else {
                self().batchUpdate(iRequest, list);
            }
        }
    }

    @Override
    public void prjLeaseItemExcelImport(IRequest iRequest, Long headerId, Long projectId, String sheetName) {

        String itemType = "";

        if (sheetName.equals("设备") || sheetName.equals(DEVICE)) {
            itemType = DEVICE;
        } else if (sheetName.equals("设施") || sheetName.equals(INSTALLATION)) {
            itemType = INSTALLATION;
        } else if (sheetName.equals("轮船") || sheetName.equals(VESSEL)) {
            itemType = VESSEL;
        } else if (sheetName.equals("飞机") || sheetName.equals(PLANE)) {
            itemType = PLANE;
        }

        if (itemType.isEmpty()) {
            throw new IllegalArgumentException("导入失败,请检查sheet页名称");
        }

        //获取接口表数据
        Example example = new Example(FndInterfaceLines.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("headerId", headerId).andEqualTo("sheetName", sheetName).andGreaterThan("lineNumber", 1);
        List<FndInterfaceLines> fndInterfaceLinesList = fndInterfaceLinesMapper.selectByExample(example);

        //插入租赁物表
        excelToPrjLeaseItem(iRequest, fndInterfaceLinesList, projectId, itemType);
    }

    public void excelToPrjLeaseItem(IRequest iRequest, List<FndInterfaceLines> fndInterfaceLines, Long projectId, String itemType) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Long seqNumber = 1L;
        for (FndInterfaceLines fndInterfaceLine : fndInterfaceLines) {
            HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = new HlsCusPrjProjectLeaseItem();
            hlsCusPrjProjectLeaseItem.setProjectId(projectId);
            hlsCusPrjProjectLeaseItem.setItemType(itemType);
            hlsCusPrjProjectLeaseItem.setSeqNumber(seqNumber);
            hlsCusPrjProjectLeaseItem.set__status("add");
            if (itemType.equals(DEVICE)) {
                //固定资产编号
                hlsCusPrjProjectLeaseItem.setFixedAssetsNum(fndInterfaceLine.getAttributes_1());
                //设备名称
                hlsCusPrjProjectLeaseItem.setLeaseItemName(fndInterfaceLine.getAttributes_2());
                validate("isNull", "租赁物设备-第" + fndInterfaceLine.getLineNumber() + "行:设备名称不能为空", hlsCusPrjProjectLeaseItem.getLeaseItemName());
                //设备序列号
                hlsCusPrjProjectLeaseItem.setSerialNumber(fndInterfaceLine.getAttributes_3());
                //规格型号
                hlsCusPrjProjectLeaseItem.setPatternSpecification(fndInterfaceLine.getAttributes_4());
                validate("isNull", "租赁物设备-第" + fndInterfaceLine.getLineNumber() + "行:规格型号不能为空", hlsCusPrjProjectLeaseItem.getPatternSpecification());
                //数量
                // validate("isInteger","租赁物设备-第" + fndInterfaceLine.getLineNumber() + "行:数量必须为整数",fndInterfaceLine.getAttributes_5());
                //  hlsCusPrjProjectLeaseItem.setQuantity(Long.parseLong(fndInterfaceLine.getAttributes_5().split("\\.")[0]));
                if (!StringUtils.isEmpty(fndInterfaceLine.getAttributes_5())) {
                    hlsCusPrjProjectLeaseItem.setQuantity(Double.valueOf(fndInterfaceLine.getAttributes_5()));
                }
                //单价
                if (!StringUtils.isEmpty(fndInterfaceLine.getAttributes_6().trim())) {
                    hlsCusPrjProjectLeaseItem.setPrice(Double.valueOf(fndInterfaceLine.getAttributes_6()));
                }

                //总价
                if (!StringUtils.isEmpty(fndInterfaceLine.getAttributes_7().trim())) {
                    hlsCusPrjProjectLeaseItem.setTotalPrice(Double.valueOf(fndInterfaceLine.getAttributes_7()));
                }

                //净值
                if (!StringUtils.isEmpty(fndInterfaceLine.getAttributes_8().trim())) {
                    hlsCusPrjProjectLeaseItem.setLeaseItemAmount(Double.valueOf(fndInterfaceLine.getAttributes_8()));
                }
                validate("isNull", "租赁物设备-第" + fndInterfaceLine.getLineNumber() + "行:净值不能为空", hlsCusPrjProjectLeaseItem.getLeaseItemAmount());
                //出厂时间
                try {
                    hlsCusPrjProjectLeaseItem.setProductDate(df.parse(fndInterfaceLine.getAttributes_9()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //经济使用年限
                hlsCusPrjProjectLeaseItem.setEconomicLife(fndInterfaceLine.getAttributes_10());
                //生产厂商
                hlsCusPrjProjectLeaseItem.setManufacturer(fndInterfaceLine.getAttributes_11());
                //存放地点
                hlsCusPrjProjectLeaseItem.setLeaseItemLocation(fndInterfaceLine.getAttributes_12());
            } else if (itemType.equals(INSTALLATION)) {
                //资产名称
                hlsCusPrjProjectLeaseItem.setLeaseItemName(fndInterfaceLine.getAttributes_1());
                validate("isNull", "租赁物资产-第" + fndInterfaceLine.getLineNumber() + "行:资产名称不能为空", hlsCusPrjProjectLeaseItem.getLeaseItemName());
                //坐落地址
                hlsCusPrjProjectLeaseItem.setLeaseItemLocation(fndInterfaceLine.getAttributes_2());
                //出厂时间
                try {
                    if (!StringUtils.isEmpty(fndInterfaceLine.getAttributes_3())) {
                        hlsCusPrjProjectLeaseItem.setProductDate(df.parse(fndInterfaceLine.getAttributes_3()));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //使用单位
                hlsCusPrjProjectLeaseItem.setOperator(fndInterfaceLine.getAttributes_4());
                //建造方
                hlsCusPrjProjectLeaseItem.setManufacturer(fndInterfaceLine.getAttributes_5());
                //净值
                hlsCusPrjProjectLeaseItem.setLeaseItemAmount(Double.valueOf(fndInterfaceLine.getAttributes_6()));
                validate("isNull", "租赁物资产-第" + fndInterfaceLine.getLineNumber() + "行:净值不能为空", hlsCusPrjProjectLeaseItem.getLeaseItemAmount());
            } else if (itemType.equals(VESSEL)) {
                //船名
                hlsCusPrjProjectLeaseItem.setLeaseItemName(fndInterfaceLine.getAttributes_1());
                validate("isNull", "租赁物船舶-第" + fndInterfaceLine.getLineNumber() + "行:船名不能为空", hlsCusPrjProjectLeaseItem.getLeaseItemName());
                //船舶识别号
                hlsCusPrjProjectLeaseItem.setSerialNumber(fndInterfaceLine.getAttributes_2());
                validate("isNull", "租赁物船舶-第" + fndInterfaceLine.getLineNumber() + "行:船舶识别号不能为空", hlsCusPrjProjectLeaseItem.getSerialNumber());
                //船籍
                hlsCusPrjProjectLeaseItem.setNationality(fndInterfaceLine.getAttributes_3());
                validate("isNull", "租赁物船舶-第" + fndInterfaceLine.getLineNumber() + "行:船籍不能为空", hlsCusPrjProjectLeaseItem.getNationality());
                //船籍港
                hlsCusPrjProjectLeaseItem.setLeaseItemLocation(fndInterfaceLine.getAttributes_4());
                validate("isNull", "租赁物船舶-第" + fndInterfaceLine.getLineNumber() + "行:船籍港不能为空", hlsCusPrjProjectLeaseItem.getLeaseItemLocation());
                //船舶种类
                hlsCusPrjProjectLeaseItem.setVesselType(fndInterfaceLine.getAttributes_5());
                validate("isNull", "租赁物船舶-第" + fndInterfaceLine.getLineNumber() + "行:船舶种类不能为空", hlsCusPrjProjectLeaseItem.getVesselType());
                //吨位
                hlsCusPrjProjectLeaseItem.setTonnage(fndInterfaceLine.getAttributes_6());
                validate("isNull", "租赁物船舶-第" + fndInterfaceLine.getLineNumber() + "行:吨位不能为空", hlsCusPrjProjectLeaseItem.getTonnage());
                //建成日期
                try {
                    if (!StringUtils.isEmpty(fndInterfaceLine.getAttributes_7())) {
                        hlsCusPrjProjectLeaseItem.setProductDate(df.parse(fndInterfaceLine.getAttributes_7()));
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                validate("isNull", "isNull", "租赁物船舶-第" + fndInterfaceLine.getLineNumber() + "行:建成日期不能为空", hlsCusPrjProjectLeaseItem.getProductDate());
                //租赁物价值
                hlsCusPrjProjectLeaseItem.setLeaseItemAmount(Double.valueOf(fndInterfaceLine.getAttributes_8()));
                validate("isNull", "租赁物船舶-第" + fndInterfaceLine.getLineNumber() + "行:租赁物价值不能为空", hlsCusPrjProjectLeaseItem.getLeaseItemAmount());
            } else if (itemType.equals(PLANE)) {
                //飞机制造商
                hlsCusPrjProjectLeaseItem.setManufacturer(fndInterfaceLine.getAttributes_1());
                validate("isNull", "租赁物飞机-第" + fndInterfaceLine.getLineNumber() + "行:飞机制造商不能为空", hlsCusPrjProjectLeaseItem.getManufacturer());
                //飞机型号
                hlsCusPrjProjectLeaseItem.setPatternSpecification(fndInterfaceLine.getAttributes_2());
                validate("isNull", "租赁物飞机-第" + fndInterfaceLine.getLineNumber() + "行:飞机型号不能为空", hlsCusPrjProjectLeaseItem.getPatternSpecification());
                //出厂序号
                hlsCusPrjProjectLeaseItem.setSerialNumber(fndInterfaceLine.getAttributes_3());
                validate("isNull", "租赁物飞机-第" + fndInterfaceLine.getLineNumber() + "行:出厂序号不能为空", hlsCusPrjProjectLeaseItem.getSerialNumber());
                //发动机制造商
                hlsCusPrjProjectLeaseItem.setEngineManufacture(fndInterfaceLine.getAttributes_4());
                validate("isNull", "租赁物飞机-第" + fndInterfaceLine.getLineNumber() + "行:发动机制造商不能为空", hlsCusPrjProjectLeaseItem.getEngineManufacture());
                //发动机型号
                hlsCusPrjProjectLeaseItem.setEngineSpecification(fndInterfaceLine.getAttributes_5());
                validate("isNull", "租赁物飞机-第" + fndInterfaceLine.getLineNumber() + "行:发动机型号不能为空", hlsCusPrjProjectLeaseItem.getEngineSpecification());

                //发动机序列号
                hlsCusPrjProjectLeaseItem.setEngineSerialNumber(fndInterfaceLine.getAttributes_6());
                validate("isNull", "租赁物飞机-第" + fndInterfaceLine.getLineNumber() + "行:发动机序列号不能为空", hlsCusPrjProjectLeaseItem.getEngineSerialNumber());
                //租赁物价值
                validate("isNull", "租赁物飞机-第" + fndInterfaceLine.getLineNumber() + "行:租赁物价值不能为空", fndInterfaceLine.getAttributes_7());
                validate("isNumber", "租赁物飞机-第" + fndInterfaceLine.getLineNumber() + "行:租赁物价值为非数字", fndInterfaceLine.getAttributes_7());

                hlsCusPrjProjectLeaseItem.setLeaseItemAmount(Double.valueOf(fndInterfaceLine.getAttributes_7()));
                //出厂时间
                validate("isNull", "租赁物飞机-第" + fndInterfaceLine.getLineNumber() + "行:出厂时间不能为空", fndInterfaceLine.getAttributes_7());
                try {
                    if (!StringUtils.isEmpty(fndInterfaceLine.getAttributes_8())) {
                        hlsCusPrjProjectLeaseItem.setProductDate(df.parse(fndInterfaceLine.getAttributes_8()));
                    }


                } catch (ParseException e) {
                    throw new RuntimeException("租赁物飞机-第" + fndInterfaceLine.getLineNumber() + "行:出厂时间日期格式有误");
                }
                //已飞行里程
                hlsCusPrjProjectLeaseItem.setFlyDistance(fndInterfaceLine.getAttributes_9());
                validate("isNull", "租赁物飞机-第" + fndInterfaceLine.getLineNumber() + "行:已飞行里程不能为空", hlsCusPrjProjectLeaseItem.getFlyDistance());
            }

            self().insert(iRequest, hlsCusPrjProjectLeaseItem);

            seqNumber++;
        }
    }

    // 校验字段是否有值
    public static void validate(String validType, String message, Object... objects) {
        if (validType.equalsIgnoreCase("isNull")) {
            for (int i = 0; i < objects.length; i++) {
                if (objects[i] == null || "".equals(objects[i]) || "null".equals(objects[i])) {
                    throw new RuntimeException(message);
                }
            }
        }

        if (validType.equalsIgnoreCase("isNumber")) {
            for (int i = 0; i < objects.length; i++) {
                String value = String.valueOf(objects[i]);
                try {
                    Double.parseDouble(value);
                } catch (Exception ed) {
                    try {
                        Float.parseFloat(value);
                    } catch (Exception ef) {
                        try {
                            Long.parseLong(value);
                        } catch (Exception el) {
                            try {
                                Integer.parseInt(value);
                            } catch (Exception ei) {
                                try {
                                    Short.parseShort(value);
                                } catch (Exception es) {
                                    throw new RuntimeException(message);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (validType.equalsIgnoreCase("isInteger")) {
            for (int i = 0; i < objects.length; i++) {
                String value = String.valueOf(objects[i]);
                try {
                    Double.parseDouble(value);
                } catch (Exception ed) {
                    try {
                        Float.parseFloat(value);
                    } catch (Exception ef) {
                        throw new RuntimeException(message);
                    }
                }
                String[] num = value.split("\\.");
                String decimalPart = num[1];
                if(Integer.parseInt(decimalPart) > 0){
                    throw new RuntimeException(message);
                }

            }
        }

    }
}