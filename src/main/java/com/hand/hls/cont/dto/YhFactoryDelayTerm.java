package com.hand.hls.cont.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * @program:
 * @description: 主机厂最长期限（月）维护
 * @author: ZHANGDAN
 * @create: 2022-12
 **/

@Data
@ExtensionAttribute(disable = true)
@Table(name = "yh_factory_delay_term")
@Getter
@Setter
public class YhFactoryDelayTerm {
    @Id
    @GeneratedValue
    private Long termId;

    private Long manufacturerId;

    private Long factoryId;

    private Long maxTerm;

    private String note;

    private Long createdBy;

    private Date creationDate;

    private Long lastUpdatedBy;

    private Date lastUpdateDate;

    @Transient
    private String manufacturerIdN;

    @Transient
    private String factoryIdN;

}
