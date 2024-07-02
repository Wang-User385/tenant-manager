package com.hand.hls.wsdl.dto;

import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectLeaseItem;
import lombok.Data;

import java.util.List;

/**
 * <p>租后信息同步接口实体类
 *
 * @author yangjupeng
 * created by 2020/06/30
 */

@Data
public class RentInfo {
    /**
     * 进件基本信息
     */
    private HlsCusPrjProject prjProject;

    /**
     * 租赁物信息
     */

    private List<HlsCusPrjProjectLeaseItem> prjProjectLeaseItemList;
}
