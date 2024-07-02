package com.hand.hls.custom.demo.dto;

import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.Entity;

@Entity
public class SysPage extends BaseDTO {

    public static final Integer DEFAULT_PAGE_SIZE = 10;
    public static final Integer DEFAULT_PAGE_NUMBER = 1;

    private String pageName;
    private Integer pageSize;
    private Integer pageNumber;

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }
}
