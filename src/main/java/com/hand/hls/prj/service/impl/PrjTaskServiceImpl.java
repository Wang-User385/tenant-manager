//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hand.hls.prj.service.impl;

import com.hand.hls.cont.dto.ConContractIncept;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.ConContractInceptMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.IConContractInceptService;
import com.hand.hls.prj.mapper.PrjTaskMapper;
import com.hand.hls.prj.service.PrjTaskService;
import com.hand.hls.sys.mapper.HlsSystemNoticeMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.persistence.entity.data.UserDataManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PrjTaskServiceImpl implements PrjTaskService {
    @Autowired
    private PrjTaskMapper prjTaskMapper;
    @Autowired
    private HlsCusConContractMapper conContractMapper;
    @Autowired
    private HlsSystemNoticeMapper hlsSystemNoticeMapper;
    @Autowired
    private UserDataManager userDataManager;
    @Autowired
    private ConContractInceptMapper conContractInceptMapper;
    @Autowired
    private IConContractInceptService conContractInceptService;

    public PrjTaskServiceImpl() {
    }

    public List<Map> queryPrjTask(String contractId, String projectId) {
        return this.prjTaskMapper.queryPrjTask(contractId, projectId);
    }

    public List<Map> queryConTask(String contractId) {
        return this.prjTaskMapper.queryConTask(contractId);
    }

    public Map<String, List<Map>> queryAllPrjConById(String contractId, String projectId) {
        Map<String, List<Map>> listMap = new HashMap();
        if (projectId == null) {
            HlsCusConContract cc = new HlsCusConContract();
            cc.setContractId(Long.parseLong(contractId));
            Long projectIdByCc = ((HlsCusConContract)this.conContractMapper.select(cc).get(0)).getProjectId();
            projectId = projectIdByCc.toString();
        }

        listMap.put("PRJ", this.prjTaskMapper.queryPrj(contractId, projectId));
        listMap.put("CON", this.prjTaskMapper.queryCon(contractId, projectId));
        listMap.put("PRJ_MEET", this.prjTaskMapper.queryPrjMeet(contractId, projectId));
        listMap.put("CON_CANCEL", this.prjTaskMapper.queryConCancel(contractId, projectId));
        listMap.put("CON_CHANGE", this.prjTaskMapper.queryConChange(contractId, projectId));
        listMap.put("CON_INCEPT", this.prjTaskMapper.queryConIncept(contractId, projectId));
        listMap.put("CON_CHANGE_CF", this.prjTaskMapper.queryConChangeCf(contractId, projectId));
        listMap.put("CON_SIGN", this.prjTaskMapper.queryConSign(contractId, projectId));
        listMap.put("CON_PAY", this.prjTaskMapper.queryConPay(contractId, projectId));
        listMap.put("CON_END", this.prjTaskMapper.queryConEnd(contractId, projectId));
        listMap.put("CON_CSH", this.prjTaskMapper.queryConCsh(contractId, projectId));
        listMap.put("CON_CSH_PAY", this.prjTaskMapper.queryCshPaid(contractId, projectId));
        listMap.put("NOTICE", this.hlsSystemNoticeMapper.queryPrjByid(contractId, projectId));
        return listMap;
    }

    public List<Map> queryInceptById(String inceptIds) {
        List<Map> resultList = new ArrayList();
        if (inceptIds == null) {
            return new ArrayList();
        } else {
            List<String> ids = Arrays.asList(inceptIds.split(","));
            Iterator<String> it = ids.iterator();

            ConContractIncept incept;
            for(LinkedList reqIdList = new LinkedList(); it.hasNext(); reqIdList.add(incept.getContractInceptReqId())) {
                String inceptId = (String)it.next();
                incept = (ConContractIncept)this.conContractInceptMapper.selectByPrimaryKey(inceptId);
                if (!reqIdList.contains(incept.getContractInceptReqId())) {
                    resultList.addAll(this.hlsSystemNoticeMapper.queryInceptByid(inceptId));
                }
            }

            return resultList;
        }
    }

    public List<Map> querySignNoticeById(String contractId) {
        List<Map> resultList = new ArrayList();
        if (contractId == null) {
            return new ArrayList();
        } else {
            List<String> ids = Arrays.asList(contractId.split(","));
            Iterator it = ids.iterator();

            while(it.hasNext()) {
                resultList.addAll(this.hlsSystemNoticeMapper.querySignNoticeByid((String)it.next()));
            }

            return resultList;
        }
    }

    public String getEmployeeName(String userId) {
        UserEntity userEntity = (UserEntity)this.userDataManager.findById(userId);
        return userEntity != null && StringUtils.isNotEmpty(userEntity.getFirstName()) ? userEntity.getFirstName() : userId;
    }

    public List<Map> queryTaskInfo(String processInstanceId) {
        List<Map> listMap = this.prjTaskMapper.queryTaskInfo(processInstanceId);
        if (listMap.size() > 0) {
            for(int i = 0; i < listMap.size(); ++i) {
                if (((Map)listMap.get(i)).get("assignee") != null) {
                    ((Map)listMap.get(i)).put("assigneeName", this.getEmployeeName(((Map)listMap.get(i)).get("assignee").toString()));
                }

                if (((Map)listMap.get(i)).get("realAssignee") != null) {
                    ((Map)listMap.get(i)).put("realAssigneeName", this.getEmployeeName(((Map)listMap.get(i)).get("realAssignee").toString()));
                }
            }
        }

        return listMap;
    }

    public List<Map> queryConPayInfo(String contractId, String projectId) {
        List<Map> listMap = this.prjTaskMapper.queryConPayInfo(contractId, projectId);
        if (listMap.size() > 0) {
            for(int i = 0; i < listMap.size(); ++i) {
                if (((Map)listMap.get(i)).get("taskUser") != null) {
                    ((Map)listMap.get(i)).put("taskUserName", this.getEmployeeName(((Map)listMap.get(i)).get("taskUser").toString()));
                }
                Object paymentFlag = ((Map) listMap.get(i)).get("paymentFlag");
                if (null == paymentFlag) {
                    ((Map)listMap.get(i)).put("paymentFlagName", "未核销");
                } else if (paymentFlag.equals("PAID")) {
                    ((Map)listMap.get(i)).put("paymentFlagName", "完全核销");
                } else {
                    ((Map)listMap.get(i)).put("paymentFlagName", "部分核销");
                }
            }
        }

        return listMap;
    }

    public List<Map> getprocInstIdByPrjId(Long projectId) {
        return this.prjTaskMapper.getprocInstIdByPrjId(projectId);
    }
}
