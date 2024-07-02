package com.hand.hls.fnd.dto;

/**
 * @version: 1.0
 * @name: ImpBatch
 * @description: 导入批次表dto实体类
 * @date: 2017-08-07 10:44
 */

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@ExtensionAttribute(disable=true)
@Table(name = "FND_IMP_BATCH")
public class HlsCusImpBatch extends BaseDTO {

    public static final String FIELD_BATCH_ID = "batchId";
    public static final String FIELD_TEMP_CODE = "tempCode";
    public static final String FIELD_FILE_NAME = "fileName";
    public static final String FIELD_FILE_PATH = "filePath";
    public static final String FIELD_FILE_SIZE = "fileSize";
    public static final String FIELD_IMP_STATUS = "impStatus";
    public static final String FIELD_IMP_DATE = "impDate";
    public static final String FIELD_PARSE_ERR_COUNT = "parseErrCount";
    public static final String FIELD_IMP_ERR_COUNT = "impErrCount";
    public static final String FIELD_JOB_ID = "jobId";

    /**
     * 批次状态 上传失败
     */
    public static final String IMP_STATUS_UPLOAD_FAILURE = "UPLOAD FAIL";

    /**
     * 批次状态 解析失败
     */
    public static final String IMP_STATUS_PARSE_FAILURE = "PARSE FAIL";

    /**
     * 批次状态 导入失败
     */
    public static final String IMP_STATUS_IMPORT_FAILURE = "IMPORT FAIL";

    /**
     * 批次状态 解析成功
     */
    public static final String IMP_STATUS_PARSE_SUCCESS = "PARSE SUCCESS";

    /**
     * 批次状态 导入成功
     */
    public static final String IMP_STATUS_IMPORT_SUCCESS = "IMPORT SUCCESS";

    /**
     * 主键
     */
    @Id
    @GeneratedValue
    private Float batchId;

    /**
     * 模板编码
     */
    private String tempCode;


    /**
     *  模板导入主键
     */
    private Long tempKey;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小
     */
    private String fileSize;

    /**
     * 导入状态
     */
    private String impStatus;

    /**
     * 导入时间
     */
    private Date impDate;

    /**
     * 导入起始时间
     */
    @Transient
    private String impDateStart;

    /**
     * 导入结束时间
     */
    @Transient
    private String impDateEnd;

    /**
     * 解析失败数
     */
    private Float parseErrCount;

    /**
     * 导入失败数
     */
    private Float impErrCount;;

    /**
     * JOB 组
     */
    private String jobGroup;

    /**
     * JOB 名称
     */
    private String jobName;

    /**
     * 模板主键
     */
    @Transient
    private Float tempId;

    /**
     * 多语言标识（Y/N）
     */
    @Transient
    private String multLang;

    /**
     * 执行模式（API/JOB）
     */
    @Transient
    private String executeMode;

    /**
     * 语言
     */
    private String lang;

    /**
     * 数据总数
     */
    private Long dataCount;


    private String param;


    public HlsCusImpBatch() {
    }

    public Float getBatchId() {
        return batchId;
    }

    public HlsCusImpBatch setBatchId(Float batchId) {
        this.batchId = batchId;
        return this;
    }

    public String getTempCode() {
        return tempCode;
    }

    public HlsCusImpBatch setTempCode(String tempCode) {
        this.tempCode = tempCode;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public HlsCusImpBatch setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public HlsCusImpBatch setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public String getImpStatus() {
        return impStatus;
    }

    public HlsCusImpBatch setImpStatus(String impStatus) {
        this.impStatus = impStatus;
        return this;
    }

    public Date getImpDate() {
        return impDate;
    }

    public HlsCusImpBatch setImpDate(Date impDate) {
        this.impDate = impDate;
        return this;
    }

    public Float getParseErrCount() {
        return parseErrCount;
    }

    public HlsCusImpBatch setParseErrCount(Float parseErrCount) {
        this.parseErrCount = parseErrCount;
        return this;
    }

    public Float getImpErrCount() {
        return impErrCount;
    }

    public HlsCusImpBatch setImpErrCount(Float impErrCount) {
        this.impErrCount = impErrCount;
        return this;
    }

    public String getImpDateStart() {
        return impDateStart;
    }

    public void setImpDateStart(String impDateStart) {
        this.impDateStart = impDateStart;
    }

    public String getImpDateEnd() {
        return impDateEnd;
    }

    public void setImpDateEnd(String impDateEnd) {
        this.impDateEnd = impDateEnd;
    }

    public Float getTempId() {
        return tempId;
    }

    public void setTempId(Float tempId) {
        this.tempId = tempId;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public HlsCusImpBatch setFileSize(String fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getMultLang() {
        return multLang;
    }

    public void setMultLang(String multLang) {
        this.multLang = multLang;
    }

    public Long getDataCount() {
        return dataCount;
    }

    public void setDataCount(Long dataCount) {
        this.dataCount = dataCount;
    }

    public String getExecuteMode() {
        return executeMode;
    }

    public void setExecuteMode(String executeMode) {
        this.executeMode = executeMode;
    }

    public Long getTempKey() {
        return tempKey;
    }

    public void setTempKey(Long tempKey) {
        this.tempKey = tempKey;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}
