package com.hand.hls.fnd.dto;

import org.apache.poi.ss.usermodel.Workbook;

/**
 * Created by 王也 on 2017/8/1.
 * 文件解析后的返回数据
 */
public class HlsCusImportParData {
    private boolean status;
    private Workbook workbook;

    public boolean isStatus() {
        return status;
    }

    public HlsCusImportParData setStatus(boolean status) {
        this.status = status;
        return this;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public HlsCusImportParData setWorkbook(Workbook workbook) {
        this.workbook = workbook;
        return this;
    }
}
