//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "fnd_score_tplt_db_source"
)
public class FndScoreTemplateDbSource extends BaseDTO {
    @Id
    @GeneratedValue
    private Long scoreTemplateDbSourceId;
    private Long scoreTemplateHdId;
    private Long dbDataSourceId;
    @Transient
    private String dbDataSourceName;
    private String enabledFlag;

    public FndScoreTemplateDbSource() {
    }

    public void setScoreTemplateDbSourceId(Long scoreTemplateDbSourceId) {
        this.scoreTemplateDbSourceId = scoreTemplateDbSourceId;
    }

    public Long getScoreTemplateDbSourceId() {
        return this.scoreTemplateDbSourceId;
    }

    public void setScoreTemplateHdId(Long scoreTemplateHdId) {
        this.scoreTemplateHdId = scoreTemplateHdId;
    }

    public Long getScoreTemplateHdId() {
        return this.scoreTemplateHdId;
    }

    public void setDbDataSourceId(Long dbDataSourceId) {
        this.dbDataSourceId = dbDataSourceId;
    }

    public Long getDbDataSourceId() {
        return this.dbDataSourceId;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getEnabledFlag() {
        return this.enabledFlag;
    }

    public String getDbDataSourceName() {
        return this.dbDataSourceName;
    }

    public void setDbDataSourceName(String dbDataSourceName) {
        this.dbDataSourceName = dbDataSourceName;
    }
}
