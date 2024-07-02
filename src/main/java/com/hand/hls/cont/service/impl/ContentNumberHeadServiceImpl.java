package com.hand.hls.cont.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.ContentNumberLine;
import com.hand.hls.cont.mapper.ContentNumberLineMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusProjectCreditNotice;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusProjectCreditNoticeMapper;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.utils.exception.HlsCusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.cont.dto.ContentNumberHead;
import com.hand.hls.cont.service.IContentNumberHeadService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class ContentNumberHeadServiceImpl extends BaseServiceImpl<ContentNumberHead> implements IContentNumberHeadService{

    private static final String APPROVING = "APPROVING";
    private static final String WORK_FLOW_TYPE = "CON_CONTENT_NUM_WFL";

    @Autowired
    private IActivitiStartService activitiStartService;

    @Autowired
    private ContentNumberLineMapper contentNumberLineMapper;

    @Autowired
    private HlsCusProjectCreditNoticeMapper hlsCusProjectCreditNoticeMapper;

    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;

    @Override
    public void submitWfl(IRequest iRequest, ContentNumberHead contentNumberHead) throws HlsCusException {
        List<ContentNumberHead> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();

        ContentNumberLine contentNumberLine = new ContentNumberLine();
        contentNumberLine.setHeadId(contentNumberHead.getHeadId());
        List<ContentNumberLine> lineList = contentNumberLineMapper.select(contentNumberLine);
        if(lineList == null || lineList.size() == 0){
            throw new HlsCusException("请维护文本信息!");
        }

        //更改单据状态
        contentNumberHead.setStatus(APPROVING);
        self().updateByPrimaryKey(iRequest, contentNumberHead);
        list.add(contentNumberHead);

        //是否存在授信审批通知书
        HlsCusProjectCreditNotice hlsCusProjectCreditNotice = new HlsCusProjectCreditNotice();
        hlsCusProjectCreditNotice.setProjectId(contentNumberHead.getProjectId());
        List<HlsCusProjectCreditNotice> creditNoticeList = hlsCusProjectCreditNoticeMapper.quertNoticeList(hlsCusProjectCreditNotice);

        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(contentNumberHead.getProjectId());
        hlsCusPrjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(hlsCusPrjProject);

        //设置工作流参数
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(contentNumberHead));
        map.put("contentNumberHead", jsonObject.toString());
        map.put("headId", contentNumberHead.getHeadId());
        map.put("projectId", contentNumberHead.getProjectId());
        map.put("documentCategory", WORK_FLOW_TYPE);
        map.put("documentType", WORK_FLOW_TYPE);
        map.put("reqNumber", contentNumberHead.getReqNumber());
        map.put("headDesc", contentNumberHead.getHeadDesc());
        map.put("workFlowType", WORK_FLOW_TYPE);
        map.put("documentName", hlsCusPrjProject.getProjectName());
        map.put("documentNumber", contentNumberHead.getReqNumber());
        if(creditNoticeList != null && creditNoticeList.size() > 0){
            map.put("creditFileFlag", "exist");
        }else{
            map.put("creditFileFlag", "not_exist");
        }
        activitiStartService.start(iRequest, list, map);
    }
}