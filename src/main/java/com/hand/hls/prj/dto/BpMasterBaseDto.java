package com.hand.hls.prj.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: ximufeng
 * @version: v1.0
 * @description:
 * @date:2019/11/27
 */
@Data
public class BpMasterBaseDto {

    /**
     * 商业伙伴基本信息 + 征信信息
     */

    private HlsBpMaster hlsBpMaster;

    /**
     * 商业伙伴地址信息
     */

    private List<HlsBpMasterAddress> hlsBpMasterAddressList;

    /**
     * 角色信息
     */

    private List<HlsBpMasterRole> hlsBpMasterRoleList;

    /**
     * 银行账号信息
     */

    private List<HlsBpMasterBankAccount> hlsBpMasterBankAccountList;

    /**
     * 主要组成人员信息
     */
    private List<HlsBpMasterMainMembers> hlsBpMasterMainMembersList;
}
