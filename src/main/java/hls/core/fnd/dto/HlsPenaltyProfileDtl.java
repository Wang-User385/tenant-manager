package hls.core.fnd.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "hls_penalty_profile_dtl"
)
public class HlsPenaltyProfileDtl extends BaseDTO implements Serializable {
    @Id
    private String penaltyProfile;
    @Id
    private Long cfItem;
    private Double penaltyRate;
    private Long gracePeriod;
    private String enabledFlag;
    @Transient
    private String queryCondition;

    public HlsPenaltyProfileDtl() {
    }

    public String getQueryCondition() {
        return this.queryCondition;
    }

    public void setQueryCondition(String queryCondition) {
        this.queryCondition = queryCondition;
    }

    public String getPenaltyProfile() {
        return this.penaltyProfile;
    }

    public void setPenaltyProfile(String penaltyProfile) {
        this.penaltyProfile = penaltyProfile;
    }

    public Long getCfItem() {
        return this.cfItem;
    }

    public void setCfItem(Long cfItem) {
        this.cfItem = cfItem;
    }

    public Double getPenaltyRate() {
        return this.penaltyRate;
    }

    public void setPenaltyRate(Double penaltyRate) {
        this.penaltyRate = penaltyRate;
    }

    public Long getGracePeriod() {
        return this.gracePeriod;
    }

    public void setGracePeriod(Long gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public String getEnabledFlag() {
        return this.enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }
}

