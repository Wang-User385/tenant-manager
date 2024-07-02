package com.hand.hls.fnd.dto;

/**
 * @author: wenqiang.zhang@hand-china.com
 * @version: 1.0
 * @name: ImpTemplate
 * @description: 导入模板头表dto实体类
 * @date: 2017-08-07 10:44
 */

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@ExtensionAttribute(disable=true)
@Table(name = "FND_IMP_TEMPLATE")
public class HlsCusImpTemplate extends BaseDTO {

    public static final String FIELD_TEMP_ID = "tempId";
    public static final String FIELD_TEMP_CODE = "tempCode";
    public static final String FIELD_TEMP_NAME = "tempName";
    public static final String FIELD_API_TYPE_CODE = "apiTypeCode";
    public static final String FIELD_API_INSTANCE = "apiInstance";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_EXECUTE_MODE = "executeMode";

    /**
     * API类型 JAVA
     */
    public static final String API_TYPE_JAVA="JAVA";

    /**
     * API类型 PKG
     */
    public static final String API_TYPE_ORACLE="PKG";

    /**
     * 执行方式 API
     */
    public static final String MODE_TYPE_API="API";

    /**
     * 执行方式 JOB
     */
    public static final String MODE_TYPE_JOB="JOB";

    /**
     * 主键
     */
    @Id
    @GeneratedValue
    private Float tempId;

    /**
     * 模板代码
     */
    private String tempCode;

    /**
     * 模板名称
     */
    private String tempName;

    /**
     * API类型（PKG/JAVA）
     */
    private String apiTypeCode;

    /**
     * API实例
     */
    private String apiInstance;

    /**
     * 模板状态（N/E/U/D）
     */
    private String status;

    /**
     * 多语言标识（Y/N）
     */
    private String multLang;

    /**
     * 执行方式（JOB/API）
     */
    private String executeMode;

    /**
     * 模板行数
     */
    private Float lineCount;

    public Float getTempId() {
        return tempId;
    }

    public HlsCusImpTemplate setTempId(Float tempId) {
        this.tempId = tempId;
        return this;
    }

    public String getTempCode() {
        return tempCode;
    }

    public HlsCusImpTemplate setTempCode(String tempCode) {
        this.tempCode = tempCode;
        return this;
    }

    public String getTempName() {
        return tempName;
    }

    public HlsCusImpTemplate setTempName(String tempName) {
        this.tempName = tempName;
        return this;
    }

    public String getApiTypeCode() {
        return apiTypeCode;
    }

    public HlsCusImpTemplate setApiTypeCode(String apiTypeCode) {
        this.apiTypeCode = apiTypeCode;
        return this;
    }

    public String getApiInstance() {
        return apiInstance;
    }

    public HlsCusImpTemplate setApiInstance(String apiInstance) {
        this.apiInstance = apiInstance;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public HlsCusImpTemplate setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getExecuteMode() {
        return executeMode;
    }

    public HlsCusImpTemplate setExecuteMode(String executeMode) {
        this.executeMode = executeMode;
        return this;
    }

    public Float getLineCount() {
        return lineCount;
    }

    public HlsCusImpTemplate setLineCount(Float lineCount) {
        this.lineCount = lineCount;
        return this;
    }

    public String getMultLang() {
        return multLang;
    }

    public void setMultLang(String multLang) {
        this.multLang = multLang;
    }
}
