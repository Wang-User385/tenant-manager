package com.hand.hls.hn.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import com.mysql.jdbc.Clob;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@ExtensionAttribute(disable = true)
@Table(name = "PRJ_CHECK_ITEMS_HD")
@Getter
@Setter
public class PrjCheckItemHd extends BaseDTO {

    @Id
    @GeneratedValue
    private Long hdId;

    private Long checkId;

    private String checkCategory;
    @Transient
    private String checkCategoryN;

    private Date checkDate;
    private String auditY;
    @Transient
    private String auditYN;
    private String auditN;
    @Transient
    private String auditNN;

    private String compareData;
    private Date financeReportDate;
    private String compareAuditY;
    private String compareAuditN;

    private String refinanceAnalyse;

    private String addComments;

    private String otherExplainSituation;
}
