//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.dto;

public class KeywordLocation {
    private String pageNo = "0";
    private String keyword;
    private String keywordPositionIndex = "0";
    private String offsetX = "0";
    private String offsetY = "0";

    public KeywordLocation() {
    }

    public KeywordLocation(String keyword) {
        this.keyword = keyword;
    }

    public KeywordLocation(String keyword, String offsetX, String offsetY) {
        this.keyword = keyword;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public KeywordLocation(String pageNo, String keyword) {
        this.pageNo = pageNo;
        this.keyword = keyword;
    }

    public KeywordLocation(String pageNo, String keyword, String offsetX, String offsetY) {
        this.pageNo = pageNo;
        this.keyword = keyword;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public KeywordLocation(String pageNo, String keyword, String offsetX, String offsetY, String keywordPositionIndex) {
        this.pageNo = pageNo;
        this.keyword = keyword;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.keywordPositionIndex = keywordPositionIndex;
    }

    public String getPageNo() {
        return this.pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeywordPositionIndex() {
        return this.keywordPositionIndex;
    }

    public void setKeywordPositionIndex(String keywordPositionIndex) {
        this.keywordPositionIndex = keywordPositionIndex;
    }

    public String getOffsetX() {
        return this.offsetX;
    }

    public void setOffsetX(String offsetX) {
        this.offsetX = offsetX;
    }

    public String getOffsetY() {
        return this.offsetY;
    }

    public void setOffsetY(String offsetY) {
        this.offsetY = offsetY;
    }
}
