package com.hand.hls.excel.formbean;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.util.List;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2019/8/14
 * @description:
 */
@Getter
@Setter
public class ExcelBean {
    private Integer row;
    private Integer cell;
    private String value;
    private Double valueD;
    private String dataType;

    /**
     * 单元格线条
     */
    private BorderStyle border;
    private BorderStyle borderTop;
    private BorderStyle borderBottom;
    private BorderStyle borderLeft;
    private BorderStyle borderRight;

    /**
     * 单元格颜色
     */
    private Short borderColor;
    private Short topBorderColor;
    private Short bottomBorderColor;
    private Short leftBorderColor;
    private Short rightBorderColor;

    /**
     * 水平对齐方式
     */
    private HorizontalAlignment alignment;

    /**
     * 垂直对齐方式
     */
    private VerticalAlignment verticalAlignment;

    /**
     * 长宽自适应 自动伸缩
     */
    private Boolean shrinkToFit;

    /**
     * 字体
     */

    /**
     * 设置字体名称
     */
    private String fontName;
    /**
     * 设置字号
     */
    private Short fontHeightInPotins;
    /**
     * 设置字体颜色
     */
    private Short color;
    /**
     * 设置下划线
     */
    private Byte underline;
    /**
     * 设置上标下标
     */
    private Short typeOffset;
    /**
     * 设置删除线
     */
    private Boolean strikeout;
    /**
     * 设置加粗
     */
    private Boolean bold;

    /**
     * 锁定单元格
     */
    private Boolean lockCell;

    /**
     * 合并的行
     */
    private List<Long> mergeRow;


}

