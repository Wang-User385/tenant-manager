package com.hand.hls.wfl.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.wfl.dto.HlsCusWflAppointApprover;
import com.hand.hls.wfl.service.WflAppointApproverService;
import leaf.bm.components.RecordHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class WflAppointApproverServiceImpl extends BaseServiceImpl<HlsCusWflAppointApprover> implements WflAppointApproverService {

    @Override
    public void wxInsertAppint(IRequest iRequest, String processInstanceId, String designatedAllocationId, String currentSidCode, String businessKey, Long  userId) {
        //查询指派的数据
        Map map = new HashMap();
        map.put("proc_inst_id_", processInstanceId);
        List<Map> variableList = RecordHelper.select("act_ru_variable", map);

        String sourceTable = "";
        for (Map item :
                variableList) {
            if ("sourceTable".equals(item.get("name_"))) {
                sourceTable = (String) item.get("text_");
            }
        }

        String[] split = designatedAllocationId.split("\\|");

        for (int i = 0; i < split.length; i++) {
            HlsCusWflAppointApprover wflAppointApprover = new HlsCusWflAppointApprover();
            wflAppointApprover.setProcessInstanceId(Long.valueOf(processInstanceId));
            wflAppointApprover.setUserId(userId);
            wflAppointApprover.setUserAllocationId(Long.valueOf(split[i]));
            wflAppointApprover.setSourceId(businessKey);
            wflAppointApprover.setSourceTable(sourceTable);
            wflAppointApprover.setSidCode(currentSidCode);

            this.insertSelective(iRequest,wflAppointApprover);
        }

    }
}