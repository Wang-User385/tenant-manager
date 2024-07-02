package com.hand.hls.app.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import com.hand.hls.cont.dto.ConContract;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@ExtensionAttribute(disable = true)
@Table(name = "yh_itf_cashflow_aync")
@Getter
@Setter
public class HlsCashflowAyncDto extends BaseDTO {
    @Id
    @GeneratedValue
    private Long itfId;

    private Long contractId;

    private Long type;

    private Date inceptionDate;

    private String postFlag;

    private Date postedDate;
}
