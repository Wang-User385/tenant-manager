package com.hand.hls.excel.formbean;

import lombok.Getter;
import lombok.Setter;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/5/11
 * @description:
 */
@Getter
@Setter
public class ExcelExportBean {
    /**
     * 导出数据ids
     */
    private Long[] ids;
    /**
     * 模版名
     */
    private String fileName;
    /**
     * sheet名 逗号隔开
     */
    private String[] sheetNames;
    /**
     * 下载文件名
     */
    private String downFileName;
    /**
     * excel 类型
     */
    private String excelType;
    /**
     * excel 加密密码
     */
    private String password;
}
