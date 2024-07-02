package com.hand.hls.widget.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.*;
import java.util.Date;

@ExtensionAttribute(disable=true)
@Table(name = "sys_widgets_prop")
public class SysWidgetsProp extends BaseDTO {
    /**
     * 微件模型属性主键
     */
    @Id
    @Column(name = "prop_id")
    @GeneratedValue
    private Long propId;

    /**
     * 微件模型主键
     */
    @Column(name = "widgets_id")
    private Long widgetsId;

    /**
     * 属性名
     */
    @Column(name = "prop_field")
    private String propField;

    /**
     * 描述
     */
    @Column(name = "prop_name")
    private String propName;

    /**
     * 属性类型
     */
    @Column(name = "prop_type")
    private String propType;

    /**
     * 是否必需
     */
    @Column(name = "required")
    private String required;

    @Column(name = "REF_V01")
    private String refV01;

    @Column(name = "REF_V02")
    private String refV02;

    @Column(name = "REF_V03")
    private String refV03;

    @Column(name = "REF_V04")
    private String refV04;

    @Column(name = "REF_V05")
    private String refV05;

    @Column(name = "REF_V06")
    private String refV06;

    @Column(name = "REF_V07")
    private String refV07;

    @Column(name = "REF_V08")
    private String refV08;

    @Column(name = "REF_V09")
    private String refV09;

    @Column(name = "REF_V10")
    private String refV10;

    @Column(name = "REF_V11")
    private String refV11;

    @Column(name = "REF_V12")
    private String refV12;

    @Column(name = "REF_V13")
    private String refV13;

    @Column(name = "REF_V14")
    private String refV14;

    @Column(name = "REF_V15")
    private String refV15;

    @Column(name = "REF_N01")
    private Long refN01;

    @Column(name = "REF_N02")
    private Long refN02;

    @Column(name = "REF_N03")
    private Long refN03;

    @Column(name = "REF_N04")
    private Long refN04;

    @Column(name = "REF_N05")
    private Long refN05;

    @Column(name = "REF_N06")
    private Long refN06;

    @Column(name = "REF_N07")
    private Long refN07;

    @Column(name = "REF_N08")
    private Long refN08;

    @Column(name = "REF_N09")
    private Long refN09;

    @Column(name = "REF_N10")
    private Long refN10;

    @Column(name = "REF_D01")
    private Date refD01;

    @Column(name = "REF_D02")
    private Date refD02;

    @Column(name = "REF_D03")
    private Date refD03;

    @Column(name = "REF_D04")
    private Date refD04;

    @Column(name = "REF_D05")
    private Date refD05;

    @Column(name = "REF_D06")
    private Date refD06;

    @Column(name = "REF_D07")
    private Date refD07;

    @Column(name = "REF_D08")
    private Date refD08;

    @Column(name = "REF_D09")
    private Date refD09;

    @Column(name = "REF_D10")
    private Date refD10;

    @Transient
    private String dataField;

    @Transient
    private String widgetsPropValue;

    @Transient
    private Double width;

    @Transient
    private Double orderSeq;

    @Transient
    private String linkFieldFlag;

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(Double orderSeq) {
        this.orderSeq = orderSeq;
    }

    public String getWidgetsPropValue() {
        return widgetsPropValue;
    }

    public void setWidgetsPropValue(String widgetsPropValue) {
        this.widgetsPropValue = widgetsPropValue;
    }

    public String getDataField() {
        return dataField;
    }

    public void setDataField(String dataField) {
        this.dataField = dataField;
    }

    /**
     * 获取微件模型属性主键
     *
     * @return prop_id - 微件模型属性主键
     */
    public Long getPropId() {
        return propId;
    }

    /**
     * 设置微件模型属性主键
     *
     * @param propId 微件模型属性主键
     */
    public void setPropId(Long propId) {
        this.propId = propId;
    }

    /**
     * 获取微件模型主键
     *
     * @return widgets_id - 微件模型主键
     */
    public Long getWidgetsId() {
        return widgetsId;
    }

    /**
     * 设置微件模型主键
     *
     * @param widgetsId 微件模型主键
     */
    public void setWidgetsId(Long widgetsId) {
        this.widgetsId = widgetsId;
    }

    /**
     * 获取字段名
     *
     * @return prop_field - 字段名
     */
    public String getPropField() {
        return propField;
    }

    /**
     * 设置字段名
     *
     * @param propField 字段名
     */
    public void setPropField(String propField) {
        this.propField = propField;
    }

    /**
     * 获取描述
     *
     * @return prop_name - 描述
     */
    public String getPropName() {
        return propName;
    }

    /**
     * 设置描述
     *
     * @param propName 描述
     */
    public void setPropName(String propName) {
        this.propName = propName;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getPropType() {
        return propType;
    }

    public void setPropType(String propType) {
        this.propType = propType;
    }

    /**
     * @return REF_V01
     */
    public String getRefV01() {
        return refV01;
    }

    /**
     * @param refV01
     */
    public void setRefV01(String refV01) {
        this.refV01 = refV01;
    }

    /**
     * @return REF_V02
     */
    public String getRefV02() {
        return refV02;
    }

    /**
     * @param refV02
     */
    public void setRefV02(String refV02) {
        this.refV02 = refV02;
    }

    /**
     * @return REF_V03
     */
    public String getRefV03() {
        return refV03;
    }

    /**
     * @param refV03
     */
    public void setRefV03(String refV03) {
        this.refV03 = refV03;
    }

    /**
     * @return REF_V04
     */
    public String getRefV04() {
        return refV04;
    }

    /**
     * @param refV04
     */
    public void setRefV04(String refV04) {
        this.refV04 = refV04;
    }

    /**
     * @return REF_V05
     */
    public String getRefV05() {
        return refV05;
    }

    /**
     * @param refV05
     */
    public void setRefV05(String refV05) {
        this.refV05 = refV05;
    }

    /**
     * @return REF_V06
     */
    public String getRefV06() {
        return refV06;
    }

    /**
     * @param refV06
     */
    public void setRefV06(String refV06) {
        this.refV06 = refV06;
    }

    /**
     * @return REF_V07
     */
    public String getRefV07() {
        return refV07;
    }

    /**
     * @param refV07
     */
    public void setRefV07(String refV07) {
        this.refV07 = refV07;
    }

    /**
     * @return REF_V08
     */
    public String getRefV08() {
        return refV08;
    }

    /**
     * @param refV08
     */
    public void setRefV08(String refV08) {
        this.refV08 = refV08;
    }

    /**
     * @return REF_V09
     */
    public String getRefV09() {
        return refV09;
    }

    /**
     * @param refV09
     */
    public void setRefV09(String refV09) {
        this.refV09 = refV09;
    }

    /**
     * @return REF_V10
     */
    public String getRefV10() {
        return refV10;
    }

    /**
     * @param refV10
     */
    public void setRefV10(String refV10) {
        this.refV10 = refV10;
    }

    /**
     * @return REF_V11
     */
    public String getRefV11() {
        return refV11;
    }

    /**
     * @param refV11
     */
    public void setRefV11(String refV11) {
        this.refV11 = refV11;
    }

    /**
     * @return REF_V12
     */
    public String getRefV12() {
        return refV12;
    }

    /**
     * @param refV12
     */
    public void setRefV12(String refV12) {
        this.refV12 = refV12;
    }

    /**
     * @return REF_V13
     */
    public String getRefV13() {
        return refV13;
    }

    /**
     * @param refV13
     */
    public void setRefV13(String refV13) {
        this.refV13 = refV13;
    }

    /**
     * @return REF_V14
     */
    public String getRefV14() {
        return refV14;
    }

    /**
     * @param refV14
     */
    public void setRefV14(String refV14) {
        this.refV14 = refV14;
    }

    /**
     * @return REF_V15
     */
    public String getRefV15() {
        return refV15;
    }

    /**
     * @param refV15
     */
    public void setRefV15(String refV15) {
        this.refV15 = refV15;
    }

    /**
     * @return REF_N01
     */
    public Long getRefN01() {
        return refN01;
    }

    /**
     * @param refN01
     */
    public void setRefN01(Long refN01) {
        this.refN01 = refN01;
    }

    /**
     * @return REF_N02
     */
    public Long getRefN02() {
        return refN02;
    }

    /**
     * @param refN02
     */
    public void setRefN02(Long refN02) {
        this.refN02 = refN02;
    }

    /**
     * @return REF_N03
     */
    public Long getRefN03() {
        return refN03;
    }

    /**
     * @param refN03
     */
    public void setRefN03(Long refN03) {
        this.refN03 = refN03;
    }

    /**
     * @return REF_N04
     */
    public Long getRefN04() {
        return refN04;
    }

    /**
     * @param refN04
     */
    public void setRefN04(Long refN04) {
        this.refN04 = refN04;
    }

    /**
     * @return REF_N05
     */
    public Long getRefN05() {
        return refN05;
    }

    /**
     * @param refN05
     */
    public void setRefN05(Long refN05) {
        this.refN05 = refN05;
    }

    /**
     * @return REF_N06
     */
    public Long getRefN06() {
        return refN06;
    }

    /**
     * @param refN06
     */
    public void setRefN06(Long refN06) {
        this.refN06 = refN06;
    }

    /**
     * @return REF_N07
     */
    public Long getRefN07() {
        return refN07;
    }

    /**
     * @param refN07
     */
    public void setRefN07(Long refN07) {
        this.refN07 = refN07;
    }

    /**
     * @return REF_N08
     */
    public Long getRefN08() {
        return refN08;
    }

    /**
     * @param refN08
     */
    public void setRefN08(Long refN08) {
        this.refN08 = refN08;
    }

    /**
     * @return REF_N09
     */
    public Long getRefN09() {
        return refN09;
    }

    /**
     * @param refN09
     */
    public void setRefN09(Long refN09) {
        this.refN09 = refN09;
    }

    /**
     * @return REF_N10
     */
    public Long getRefN10() {
        return refN10;
    }

    /**
     * @param refN10
     */
    public void setRefN10(Long refN10) {
        this.refN10 = refN10;
    }

    /**
     * @return REF_D01
     */
    public Date getRefD01() {
        return refD01;
    }

    /**
     * @param refD01
     */
    public void setRefD01(Date refD01) {
        this.refD01 = refD01;
    }

    /**
     * @return REF_D02
     */
    public Date getRefD02() {
        return refD02;
    }

    /**
     * @param refD02
     */
    public void setRefD02(Date refD02) {
        this.refD02 = refD02;
    }

    /**
     * @return REF_D03
     */
    public Date getRefD03() {
        return refD03;
    }

    /**
     * @param refD03
     */
    public void setRefD03(Date refD03) {
        this.refD03 = refD03;
    }

    /**
     * @return REF_D04
     */
    public Date getRefD04() {
        return refD04;
    }

    /**
     * @param refD04
     */
    public void setRefD04(Date refD04) {
        this.refD04 = refD04;
    }

    /**
     * @return REF_D05
     */
    public Date getRefD05() {
        return refD05;
    }

    /**
     * @param refD05
     */
    public void setRefD05(Date refD05) {
        this.refD05 = refD05;
    }

    /**
     * @return REF_D06
     */
    public Date getRefD06() {
        return refD06;
    }

    /**
     * @param refD06
     */
    public void setRefD06(Date refD06) {
        this.refD06 = refD06;
    }

    /**
     * @return REF_D07
     */
    public Date getRefD07() {
        return refD07;
    }

    /**
     * @param refD07
     */
    public void setRefD07(Date refD07) {
        this.refD07 = refD07;
    }

    /**
     * @return REF_D08
     */
    public Date getRefD08() {
        return refD08;
    }

    /**
     * @param refD08
     */
    public void setRefD08(Date refD08) {
        this.refD08 = refD08;
    }

    /**
     * @return REF_D09
     */
    public Date getRefD09() {
        return refD09;
    }

    /**
     * @param refD09
     */
    public void setRefD09(Date refD09) {
        this.refD09 = refD09;
    }

    /**
     * @return REF_D10
     */
    public Date getRefD10() {
        return refD10;
    }

    /**
     * @param refD10
     */
    public void setRefD10(Date refD10) {
        this.refD10 = refD10;
    }

    public String getLinkFieldFlag() {
        return linkFieldFlag;
    }

    public void setLinkFieldFlag(String linkFieldFlag) {
        this.linkFieldFlag = linkFieldFlag;
    }
}