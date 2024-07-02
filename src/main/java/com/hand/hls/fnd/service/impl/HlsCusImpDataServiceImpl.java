package com.hand.hls.fnd.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.FieldRequiredException;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.service.ICodeService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.*;
import com.hand.hls.fnd.mapper.HlsCusImpDataMapper;
import com.hand.hls.fnd.service.HlsCusImpBatchService;
import com.hand.hls.fnd.service.HlsCusImpDataService;
import com.hand.hls.fnd.service.HlsCusImpSegmentService;
import com.hand.hls.fnd.service.HlsCusImpTemplateService;
import com.hand.hls.utils.HlsCusImportDataUtil;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.xmlbeans.impl.piccolo.io.FileFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusImpDataServiceImpl extends BaseServiceImpl<HlsCusImpData> implements HlsCusImpDataService {

    @Autowired
    private HlsCusImpBatchService batchService;
    @Autowired
    private HlsCusImpTemplateService templateService;
    @Autowired
    private HlsCusImpSegmentService segmentService;
    @Autowired
    private HlsCusImpDataMapper mapper;
    @Autowired
    private ICodeService codeService;
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 传入的Excel的最大列数
     */
    private static final int MAX_COLUMN = 60;

    /**
     * EXCEL 将时间转换为数字后得到的是 距离公元元年的天数（date）
     * java.util.Date getTime 获得的毫秒数十 距离1900-01-01（计算机元年）的毫秒数
     * 下面值是 1900-01-01 到公元元年之间的毫秒数
     */
    private static final long EXCEL_TIME_INTERVAL = 2209190400000L;



    /**
     * 获取批次对应的模板头
     * @param iRequest
     * @param batch 批次
     * @return
     */
    public HlsCusImpTemplate getTemplate(IRequest iRequest, HlsCusImpBatch batch){
        return templateService.selectTemplateByCode(iRequest, new HlsCusImpTemplate().setTempCode(batch.getTempCode()));
    }

    /**
     * 获取批次对应的模板行
     * @param iRequest
     * @param batch 批次
     * @return
     */
    @Override
    public List<HlsCusImpSegment> getSegment(IRequest iRequest, HlsCusImpBatch batch){
        return segmentService.selectSegmentByTempId(iRequest, new HlsCusImpSegment().setTempId(getTemplate(iRequest,batch).getTempId()));
    }

    /**
     * 获取模板对应的模板行
     * @param iRequest
     * @param template 模板
     * @return
     */
    public List<HlsCusImpSegment> getSegment(IRequest iRequest, HlsCusImpSegment template){
        return segmentService.selectSegmentByTempId(iRequest,new HlsCusImpSegment().setTempId(template.getTempId()));
    }

    /**
     * 获取当前批次下临时表中的数据
     * Map<[attributesName],[value]>
     * 每个map对应一条数据，每条数据对应一个业务DTO
     * @param iRequest
     * @param batchId 批次编号
     * @return
     */
    @Override
    public List<Map<String,String>> getDataMap(IRequest iRequest, float batchId){
        List<Map<String, String>> dataMap = mapper.getDataMap(batchId);
        if(dataMap!=null){
            HlsCusImpBatch impBatch = batchService.selectByPrimaryKey(iRequest, new HlsCusImpBatch().setBatchId(batchId));
            List<HlsCusImpSegment> segments = getSegment(iRequest, impBatch);
            if(segments!=null){
                Map<String,HlsCusImpSegment> segMap=new HashMap<>();
                /**
                 * 包装成一个Map<bandValue,segment>
                 * 方便后面获取对象
                 */
                for(HlsCusImpSegment s:segments){
                    segMap.put(s.getBandValue(),s);
                }
                /**
                 * 封装成一个可转换为Dto的map
                 */
                List<Map<String,String>> resultList=new ArrayList<>();
                int total=segMap.size();
                for(Map<String,String> m:dataMap){
                    Map<String,String> resultMap=new HashMap<>();
                    /**
                     * m
                     * key=v1,v2,v3.....
                     * value=应该注入到dto里的值
                     */
                    /*for(String key:m.keySet()){
                        if(m.get(key)==null){//为空，跳出循环
                            break;
                        }
                        resultMap.put(segMap.get(key).getSegmentDesc(),m.get(key));
                    }*/
                    //使用Entry循环代替 keySet循环 意味Map通过key获取Value是极耗时操作
                    resultMap.put("dataId",m.get("DATA_ID"));
                    resultMap.put("tempCode",impBatch.getTempCode());
                    resultMap.put("tempParam",impBatch.getParam());
                    if(impBatch.getTempKey()!=null){
                        resultMap.put("tempKey",impBatch.getTempKey().toString());
                    }

                    for(Map.Entry<String,String> e:m.entrySet()){
                        /*if(e.getValue()==null){//为空，跳出循环
                            continue;
                        }*/
                        if(("DATA_ID").equals(e.getKey())){
                            continue;
                        }
                        HlsCusImpSegment impSegment = segMap.get(e.getKey().toLowerCase());
                        if(impSegment==null){
                            break;
                        }
                        resultMap.put(impSegment.getSegmentCode(),e.getValue());
                    }
                    resultList.add(resultMap);
                }
                return resultList;
            }
        }
        return null;
    }

    /**
     * 业务对象转换成DTO
     * @return
    public List<Object> dataToBussiness(ImportInterface importInterface,IRequest iRequest, float batchId){
    return importInterface.dataToDto(getDataMap(iRequest,batchId));
    }*/

    /**
     * 批量插入临时数据方法
     * @param iRequest
     * @param datas
     * @return
     */
    @Override
    public int batchInsert(IRequest iRequest, List<HlsCusImpData> datas){
        int result=0;
        if(datas!=null){
            for(HlsCusImpData data:datas){
                data.setLastUpdatedBy(iRequest.getUserId());
                data.setCreatedBy(iRequest.getUserId());
                mapper.insertSelective(data);
            }
        }
        return result;
    }

    /**
     * 判断一个列是否为空
     * @param row
     * @param cellNum
     * @return
     */
    public boolean rowNotEmpty(Row row, int cellNum){
        for(int i=0;i<cellNum;i++){
            Cell cell = row.getCell(i);
            if(cell!=null&&cell.getCellType()!= Cell.CELL_TYPE_BLANK){
                cell.setCellType(Cell.CELL_TYPE_STRING);
                if(cell.getCellType()== Cell.CELL_TYPE_STRING){
                    if(StringUtils.isNotBlank(cell.getRichStringCellValue().getString().trim())){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void updateErrMessage(Map<String,String> map,String... errMessage){
        float dataId = Float.parseFloat(map.get("dataId").toString());
        List<String> err=Arrays.asList(errMessage);
        mapper.updateByPrimaryKeySelective(new HlsCusImpData()
                .setDataId(dataId)
                .setImpMsg(err)
                .setImpStatus(HlsCusImpData.IMP_STATUS_IMP_FAILURE));
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void updateErrMessage(List<Map<String,String>> dataMap,int index,String... errMessage){
        updateErrMessage(dataMap.get(index),errMessage);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void batchUpdateImpStatus(IRequest iRequest, float batchId, String beforeStatus, String afterStatus){
        mapper.batchUpdateImpStatus(batchId,beforeStatus,afterStatus);
    }
}
