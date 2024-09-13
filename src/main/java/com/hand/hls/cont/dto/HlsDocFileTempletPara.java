//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hand.hls.cont.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.validator.constraints.NotEmpty;

@ExtensionAttribute(
    disable = true
)
@Table(
    name = "hls_doc_file_templet_para"
)
public class HlsDocFileTempletPara extends BaseDTO {
    @Id
    @GeneratedValue
    private Long templetParaId;
    @NotEmpty
    private String bookmark;
    private String bookmarkDescription;
    private String bookmarkType;
    private String enabledFlag;
    private String sqlSource;
    private Long dataSourceId;
    private String fontFamily;
    private Double fontSize;
    private Long tableWidth;
    private String underline;
    private String blod;
    @Transient
    private String description;
    @Transient
    private String queryCondition;

    public HlsDocFileTempletPara() {
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDataSourceId() {
        return this.dataSourceId;
    }

    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getQueryCondition() {
        return this.queryCondition;
    }

    public void setQueryCondition(String queryCondition) {
        this.queryCondition = queryCondition;
    }

    public Long getTempletParaId() {
        return this.templetParaId;
    }

    public void setTempletParaId(Long templetParaId) {
        this.templetParaId = templetParaId;
    }

    public String getBookmark() {
        return this.bookmark;
    }

    public void setBookmark(String bookmark) {
        this.bookmark = bookmark;
    }

    public String getBookmarkDescription() {
        return this.bookmarkDescription;
    }

    public void setBookmarkDescription(String bookmarkDescription) {
        this.bookmarkDescription = bookmarkDescription;
    }

    public String getBookmarkType() {
        return this.bookmarkType;
    }

    public void setBookmarkType(String bookmarkType) {
        this.bookmarkType = bookmarkType;
    }

    public String getEnabledFlag() {
        return this.enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getSqlSource() {
        return this.sqlSource;
    }

    public void setSqlSource(String sqlSource) {
        this.sqlSource = sqlSource;
    }

    public String getFontFamily() {
        return this.fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public Double getFontSize() {
        return this.fontSize;
    }

    public void setFontSize(Double fontSize) {
        this.fontSize = fontSize;
    }

    public Long getTableWidth() {
        return this.tableWidth;
    }

    public void setTableWidth(Long tableWidth) {
        this.tableWidth = tableWidth;
    }

    public String getUnderline() {
        return this.underline;
    }

    public void setUnderline(String underline) {
        this.underline = underline;
    }

    public String getBlod() {
        return this.blod;
    }

    public void setBlod(String blod) {
        this.blod = blod;
    }
}
