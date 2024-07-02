package com.hand.hls.sys.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Table;

@ExtensionAttribute(disable = true)
@Table(name = "SYS_DOCUMENT_HISTORY_BLOB_D")
@Getter
@Setter
public class DocumentHistoryBlobD extends BaseDTO {

    public static final String FIELD_CLOB_ID = "clobId";
    public static final String FIELD_HISTORY_DETAIL_ID = "historyDetailId";
    public static final String FIELD_TABLE_PK_VALUE = "tablePkValue";
    public static final String FIELD_TABLE_NAME = "tableName";
    public static final String FIELD_FIELD_VALUE = "fieldValue";
    public static final String FIELD_FIELD_NAME = "fieldName";

    private Long clobId;

    private Long historyDetailId;

    @Length(max = 20)
    private String tablePkValue;

    @Length(max = 100)
    private String tableName;

    @Length(max = 4000)
    private String fieldValue;

    @Length(max = 100)
    private String fieldName;

}
