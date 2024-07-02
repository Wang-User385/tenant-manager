//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.dto;

import java.util.List;

public abstract class BaseStrategy {
    private String serialNo = "";
    private String sealCode = "";
    private String sealPassword = "";
    private String sealPerson = "";
    private String sealLocation = "";
    private String sealReason = "";
    private String sealText = "";
    private String fontSize = "12";
    private String width = "100";
    private String height = "100";
    private String displaySize = "";
    private String fontColor = "FF0000";
    private String family = "宋体";
    private String fillOpacity = "1.0";
    private String visible = "1";
    private boolean isTimestampUsed = false;
    private String hashAlg = "";
    private String sealType = "";
    private String signatureFieldName = "";
    private String pageNo = "0";
    private String isCenterCoordinate = "0";
    private String businessCode = "";
    private String textRectHeightPercent = "0.5";
    private String businessColor = "FF0000";
    private String businessFamily = "宋体";
    private String businessFontSize = "12";
    private String isAddDateText = "0";
    private String addDateText = "";
    private String dateFontColor = "FF0000";
    private String dateFontFamily = "宋体";
    private String dateFontSize = "12";
    private String dateRectHeight = "40";
    private String lx = "0";
    private String ly = "0";
    private String pdfIndex = "0";
    private String keyword;
    private String offsetX = "0";
    private String offsetY = "0";
    private String keywordPositionIndex = "0";
    private byte[] sealImageData = null;
    private List<String> signatureFieldNameList;
    private List<SignLocation> signLocationList;
    private List<KeywordLocation> keywordLocationList;

    public BaseStrategy() {
    }

    public String getSerialNo() {
        return this.serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getSealPerson() {
        return this.sealPerson;
    }

    public void setSealPerson(String sealPerson) {
        this.sealPerson = sealPerson;
    }

    public String getSealLocation() {
        return this.sealLocation;
    }

    public void setSealLocation(String sealLocation) {
        this.sealLocation = sealLocation;
    }

    public String getSealReason() {
        return this.sealReason;
    }

    public void setSealReason(String sealReason) {
        this.sealReason = sealReason;
    }

    public String getSealText() {
        return this.sealText;
    }

    public void setSealText(String sealText) {
        this.sealText = sealText;
    }

    public String getFamily() {
        return this.family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getSealType() {
        return this.sealType;
    }

    public void setSealType(String sealType) {
        this.sealType = sealType;
    }

    public String getSignatureFieldName() {
        return this.signatureFieldName;
    }

    public void setSignatureFieldName(String signatureFieldName) {
        this.signatureFieldName = signatureFieldName;
    }

    public String getVisible() {
        return this.visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getIsCenterCoordinate() {
        return this.isCenterCoordinate;
    }

    public void setIsCenterCoordinate(String isCenterCoordinate) {
        this.isCenterCoordinate = isCenterCoordinate;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getFontColor() {
        return this.fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public boolean isTimestampUsed() {
        return this.isTimestampUsed;
    }

    public void setTimestampUsed(boolean isTimestampUsed) {
        this.isTimestampUsed = isTimestampUsed;
    }

    public String getHashAlg() {
        return this.hashAlg;
    }

    public void setHashAlg(String hashAlg) {
        this.hashAlg = hashAlg;
    }

    public String getSealCode() {
        return this.sealCode;
    }

    public void setSealCode(String sealCode) {
        this.sealCode = sealCode;
    }

    public String getSealPassword() {
        return this.sealPassword;
    }

    public void setSealPassword(String sealPassword) {
        this.sealPassword = sealPassword;
    }

    public byte[] getSealImageData() {
        return this.sealImageData;
    }

    public void setSealImageData(byte[] sealImageData) {
        this.sealImageData = sealImageData;
    }

    public String getBusinessCode() {
        return this.businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getBusinessColor() {
        return this.businessColor;
    }

    public void setBusinessColor(String businessColor) {
        this.businessColor = businessColor;
    }

    public String getBusinessFamily() {
        return this.businessFamily;
    }

    public void setBusinessFamily(String businessFamily) {
        this.businessFamily = businessFamily;
    }

    public boolean isAddDateText() {
        return "1".equals(this.isAddDateText);
    }

    public String getIsAddDateText() {
        return this.isAddDateText;
    }

    public void setIsAddDateText(String isAddDateText) {
        this.isAddDateText = isAddDateText;
    }

    public String getAddDateText() {
        return this.addDateText;
    }

    public void setAddDateText(String addDateText) {
        this.addDateText = addDateText;
    }

    public String getDateFontColor() {
        return this.dateFontColor;
    }

    public void setDateFontColor(String dateFontColor) {
        this.dateFontColor = dateFontColor;
    }

    public String getDateFontFamily() {
        return this.dateFontFamily;
    }

    public void setDateFontFamily(String dateFontFamily) {
        this.dateFontFamily = dateFontFamily;
    }

    public String getDateFontSize() {
        return this.dateFontSize;
    }

    public void setDateFontSize(String dateFontSize) {
        this.dateFontSize = dateFontSize;
    }

    public String getDateRectHeight() {
        return this.dateRectHeight;
    }

    public void setDateRectHeight(String dateRectHeight) {
        this.dateRectHeight = dateRectHeight;
    }

    public String getFontSize() {
        return this.fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getWidth() {
        return this.width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getFillOpacity() {
        return this.fillOpacity;
    }

    public void setFillOpacity(String fillOpacity) {
        this.fillOpacity = fillOpacity;
    }

    public String getPageNo() {
        return this.pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getTextRectHeightPercent() {
        return this.textRectHeightPercent;
    }

    public void setTextRectHeightPercent(String textRectHeightPercent) {
        this.textRectHeightPercent = textRectHeightPercent;
    }

    public String getBusinessFontSize() {
        return this.businessFontSize;
    }

    public void setBusinessFontSize(String businessFontSize) {
        this.businessFontSize = businessFontSize;
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

    public String getKeywordPositionIndex() {
        return this.keywordPositionIndex;
    }

    public void setKeywordPositionIndex(String keywordPositionIndex) {
        this.keywordPositionIndex = keywordPositionIndex;
    }

    public String getPdfIndex() {
        return this.pdfIndex;
    }

    public void setPdfIndex(String pdfIndex) {
        this.pdfIndex = pdfIndex;
    }

    public String getDisplaySize() {
        return this.displaySize;
    }

    public void setDisplaySize(String displaySize) {
        this.displaySize = displaySize;
    }

    public List<String> getSignatureFieldNameList() {
        return this.signatureFieldNameList;
    }

    public void setSignatureFieldNameList(List<String> signatureFieldNameList) {
        this.setSealType("1");
        this.signatureFieldNameList = signatureFieldNameList;
    }

    public List<SignLocation> getSignLocationList() {
        return this.signLocationList;
    }

    public void setSignLocation(SignLocation signLocation) {
        this.pageNo = signLocation.getPageNo();
        this.lx = signLocation.getLx();
        this.ly = signLocation.getLy();
    }

    public void setSignLocationList(List<SignLocation> signLocationList) {
        this.setSealType("2");
        this.signLocationList = signLocationList;
    }

    public List<KeywordLocation> getKeywordLocationList() {
        return this.keywordLocationList;
    }

    public void setKeywordLocationList(List<KeywordLocation> keywordLocationList) {
        this.setSealType("3");
        this.keywordLocationList = keywordLocationList;
    }

    public void setKeywordLocation(KeywordLocation keywordLocation) {
        this.keyword = keywordLocation.getKeyword();
        this.pageNo = keywordLocation.getPageNo();
        this.offsetX = keywordLocation.getOffsetX();
        this.offsetY = keywordLocation.getOffsetY();
        this.keywordPositionIndex = keywordLocation.getKeywordPositionIndex();
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("BaseStrategy");
        int length = this.sealImageData == null ? 0 : this.sealImageData.length;
        return stringBuffer.append(" [serialNo=").append(this.serialNo).append(", sealCode=").append(this.sealCode).append(", sealPassword=").append(this.sealPassword).append(", sealPerson=").append(this.sealPerson).append(", sealLocation=").append(this.sealLocation).append(", sealReason=").append(this.sealReason).append(", sealText=").append(this.sealText).append(", fontSize=").append(this.fontSize).append(", width=").append(this.width).append(", height=").append(this.height).append(", fontColor=").append(this.fontColor).append(", family=").append(this.family).append(", fillOpacity=").append(this.fillOpacity).append(", visible=").append(this.visible).append(", isTimestampUsed=").append(this.isTimestampUsed).append(", hashAlg=").append(this.hashAlg).append(", sealType=").append(this.sealType).append(", signatureFieldName=").append(this.signatureFieldName).append(", pageNo=").append(this.pageNo).append(", isCenterCoordinate=").append(this.isCenterCoordinate).append(", businessCode=").append(this.businessCode).append(", textRectHeightPercent=").append(this.textRectHeightPercent).append(", businessColor=").append(this.businessColor).append(", businessFamily=").append(this.businessFamily).append(", businessFontSize=").append(this.businessFontSize).append(", isAddDateText=").append(this.isAddDateText).append(", addDateText=").append(this.addDateText).append(", dateFontColor=").append(this.dateFontColor).append(", dateFontFamily=").append(this.dateFontFamily).append(", dateFontSize=").append(this.dateFontSize).append(", dateRectHeight=").append(this.dateRectHeight).append(", lx=").append(this.lx).append(", ly=").append(this.ly).append(", pdfIndex=").append(this.pdfIndex).append(", keyword=").append(this.keyword).append(", offsetX=").append(this.offsetX).append(", offsetY=").append(this.offsetY).append(", keywordPositionIndex=").append(this.keywordPositionIndex).append(", sealImageData.length=").append(length).append(", toString()=").append(super.toString()).append("]").toString();
    }
}
