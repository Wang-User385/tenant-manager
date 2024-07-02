package com.hand.hls.fnd.dto;

/**
 * @version: 1.0
 * @name: ImpSegment
 * @description: 导入模板行表dto实体类
 * @date: 2017-08-07 10:44
 */

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@ExtensionAttribute(disable=true)
@Table(name = "FND_IMP_SEGMENT")
public class HlsCusImpSegment extends BaseDTO {

    public static final String FIELD_SEGMENT_ID = "segmentId";
    public static final String FIELD_TEMP_ID = "tempId";
    public static final String FIELD_SEQ_NUM = "seqNum";
    public static final String FIELD_SEGMENT_CODE = "segmentCode";
    public static final String FIELD_SEGMENT_DESC = "segmentDesc";
    public static final String FIELD_TYPE_CODE = "typeCode";
    public static final String FIELD_FORMAT = "format";
    public static final String FIELD_SEGMENT_WIDTH = "segmentWidth";
    public static final String FIELD_BAND_VALUE = "bandValue";
    public static final String FIELD_ENABLE_FLAG = "enableFlag";
    public static final String FIELD_REQUIRE_FLAG = "requireFlag";
    public static final String FIELD_DEFAULT_VALUE = "defaultValue";

    /**
     * 字段类型（N/S/T）
     */
    public static final String TYPE_CODE_NUMBER = "NUMBER";
    public static final String TYPE_CODE_STRING = "STRING";
    public static final String TYPE_CODE_TIME = "DATE";

    /**
     * 数字格式转换快码代码
     */
    public static final String NUMBER_FORMAT_CODE_NAME="FND_IMP_NUMBER_FORMAT";
    /**
     * 默认时间格式
     */
    public static final String DATE_DEFAULT_FORMAT="yyyy-MM-dd HH:mm:ss";
    /**
     * 默认数字格式
     */
    public static final String DECIMAL_DEFAULT_FORMAT="##.######";

    /**
     * 主键
     */
    @Id
    @GeneratedValue
    private Float segmentId;

    /**
     * 外键，头主键
     */
    private Float tempId;

    /**
     * 列序号
     */
    private Float seqNum;

    /**
     * 列代码
     */
    private String segmentCode;

    /**
     * 列描述
     */
    private String segmentDesc;

    /**
     * 字段类型（N/S/T）
     */
    private String typeCode;

    /**
     * 格式掩码
     */
    private String format;

    /**
     * 列宽
     */
    private Float segmentWidth;

    /**
     * 临时表字段
     */
    private String bandValue;

    /**
     * 有效标识
     */
    private String enableFlag;

    /**
     * 必输标识
     */
    private String requireFlag;

    /**
     * 默认值
     */
    private String defaultValue;


    public Float getSegmentId() {
        return segmentId;
    }

    public HlsCusImpSegment setSegmentId(Float segmentId) {
        this.segmentId = segmentId;
        return this;
    }

    public Float getTempId() {
        return tempId;
    }

    public HlsCusImpSegment setTempId(Float tempId) {
        this.tempId = tempId;
        return this;
    }

    public Float getSeqNum() {
        return seqNum;
    }

    public HlsCusImpSegment setSeqNum(Float seqNum) {
        this.seqNum = seqNum;
        return this;
    }

    public String getSegmentCode() {
        return segmentCode;
    }

    public HlsCusImpSegment setSegmentCode(String segmentCode) {
        this.segmentCode = segmentCode;
        return this;
    }

    public String getSegmentDesc() {
        return segmentDesc;
    }

    public HlsCusImpSegment setSegmentDesc(String segmentDesc) {
        this.segmentDesc = segmentDesc;
        return this;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public HlsCusImpSegment setTypeCode(String typeCode) {
        this.typeCode = typeCode;
        return this;
    }

    public String getFormat() {
        return format;
    }

    public HlsCusImpSegment setFormat(String format) {
        this.format = format;
        return this;
    }

    public Float getSegmentWidth() {
        return segmentWidth;
    }

    public HlsCusImpSegment setSegmentWidth(Float segmentWidth) {
        this.segmentWidth = segmentWidth;
        return this;
    }

    public String getBandValue() {
        return bandValue;
    }

    public HlsCusImpSegment setBandValue(String bandValue) {
        this.bandValue = bandValue;
        return this;
    }

    public String getEnableFlag() {
        return enableFlag;
    }

    public HlsCusImpSegment setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
        return this;
    }

    public String getRequireFlag() {
        return requireFlag;
    }

    public HlsCusImpSegment setRequireFlag(String requireFlag) {
        this.requireFlag = requireFlag;
        return this;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public HlsCusImpSegment setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
}
