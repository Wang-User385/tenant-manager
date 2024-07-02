package com.hand.hls.bp.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hls.sys.dto.SysDocumentHistoryBlob;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(
        name = "sys_document_history_blob"
)
@ExtensionAttribute(
        disable = true
)
@Getter
@Setter
public class HlsSysDocumentHistoryBlob extends SysDocumentHistoryBlob {
    public HlsSysDocumentHistoryBlob() {
    }
}
