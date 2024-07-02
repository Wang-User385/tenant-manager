package com.hand.hls.bp.service;

import java.util.List;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.HlsBpMasterRole;

public interface HlsBpMasterRoleService extends IBaseService<HlsBpMasterRole>,ProxySelf<HlsBpMasterRoleService>{

    List<HlsBpMasterRole> queryAll(IRequest requestContext, HlsBpMasterRole bpMasterRole, int page, int pagesize);

    /**
     * Created with IntelliJ IDEA.
     * User: Jeffery
     * Date: 2017/4/28
     * Time: 12:24
     * To change this template use File | Settings | File Templates.
     * Con:商业伙伴正在创建的时候（还没保存生成bpId的时候查询商业伙伴类型）
     */
    List<HlsBpMasterRole> queryRoleType(IRequest requestContext, HlsBpMasterRole bpMasterRole, int page, int pagesize);

    List<String> queryType(Long bpId);

    List<HlsBpMasterRole> selectType();

    List<HlsBpMasterRole> selectAllType();

    String selectBbpCategory(String bpType);

    List<String> selectRoleById(Long bpId);

    int updateRoleBpType(HlsBpMasterRole bpMasterRole);
}
