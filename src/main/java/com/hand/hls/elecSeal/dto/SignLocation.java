//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.dto;

public class SignLocation {
    private String pageNo = "1";
    private String lx = "0";
    private String ly = "0";
    private String signFileName;

    public SignLocation() {
    }

    public SignLocation(String pageNo, String lx, String ly) {
        this.pageNo = pageNo;
        this.lx = lx;
        this.ly = ly;
    }

    public String getPageNo() {
        return this.pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getLx() {
        return this.lx;
    }

    public void setLx(String lx) {
        this.lx = lx;
    }

    public String getLy() {
        return this.ly;
    }

    public void setLy(String ly) {
        this.ly = ly;
    }

    public String getSignFileName() {
        return this.signFileName;
    }

    public void setSignFileName(String signFileName) {
        this.signFileName = signFileName;
    }
}
