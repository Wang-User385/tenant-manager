package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fnd.dto.HlsCusEmployee;
import com.hand.hls.fnd.dto.HlsEmployeeAssigns;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.pam.dto.HlsWarrantStockHd;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.service.SysUserService;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Qian Yuanfeng
 * @date 2020/7/28 - 9:31
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WarrantStockInActivitiStartServiceImpl implements IActivitiCommonService {

    private static final String workFlowType = "WARRANT_IN_STOCK_WFL";

    private static final  String APPROVING = "APPROVING";
    private static final  String APPROVED = "APPROVED";
    private static final  String WARRANT_IN_STOCK_WFL = "WARRANT_IN_STOCK_WFL";

    private static final  String WARRANT_IN_STOCK = "WARRANT_IN_STOCK";
    private static final  String WARRANT_OUT_STOCK = "WARRANT_OUT_STOCK";




    @Autowired
    private IActivitiService activitiService;

    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest((HlsWarrantStockHd) list.get(0), iRequest);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }


    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(HlsWarrantStockHd hlsWarrantStockHd, IRequest iRequest) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        //获取最新的头部信息
        //第一个参数是工作流主键，第二个参数是流程命名空间
        ReProcdef reProcdefs = reProcdefService.queryReProcdef(WARRANT_IN_STOCK_WFL, WARRANT_IN_STOCK_WFL);
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(hlsWarrantStockHd.getWarrantStockId().toString());
        //设置基础参数
        List<RestVariable> variables = new ArrayList<RestVariable>();
        List<RestVariable> transientVariables = new ArrayList<RestVariable>();
        RestVariable restVariable1 = new RestVariable();
        restVariable1.setName("processDefinitionId");
        restVariable1.setValue(id);
        variables.add(restVariable1);
        RestVariable restVariable2 = new RestVariable();
        restVariable2.setName("startUserDescription");
        restVariable2.setValue(sysUser.getDescription());
        variables.add(restVariable2);
        RestVariable restVariable3 = new RestVariable();
        restVariable3.setName("iRequest");
        restVariable3.setValue(iRequest);
        variables.add(restVariable3);
        RestVariable restVariable4 = new RestVariable();
        restVariable4.setName("startUserName");
        restVariable4.setValue(iRequest.getEmployeeCode());
        variables.add(restVariable4);
        //传输的对象是主表信息，用于传输对象，将对象转成JSON格式，方便获取
        RestVariable restVariable5 = new RestVariable();
        restVariable5.setName("hlsWarrantStockHd");
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(hlsWarrantStockHd));
        restVariable5.setValue(jsonObject.toString());
        variables.add(restVariable5);

        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("workFlowType");
        restVariable6.setValue(workFlowType);
        variables.add(restVariable6);
        //流程的名称为pName
        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("pName");
        restVariable7.setValue(name);
        variables.add(restVariable7);


        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("unitId");
        restVariable8.setValue(hlsWarrantStockHd.getApplicantUnit());
        variables.add(restVariable8);

        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("companyId");
        restVariable9.setValue(iRequest.getCompanyId());
        variables.add(restVariable9);


        //这个参数必须，接收的是主表的ID
        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("documentId");
        restVariable10.setValue(hlsWarrantStockHd.getWarrantStockId());
        variables.add(restVariable10);


        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("BUSINESS_KEY");
        restVariable11.setValue(hlsWarrantStockHd.getWarrantStockId());
        variables.add(restVariable11);


        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("documentCategory");
        restVariable12.setValue(WARRANT_IN_STOCK);
        variables.add(restVariable12);

        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("documentType");
        restVariable13.setValue(WARRANT_IN_STOCK);
        variables.add(restVariable13);

        RestVariable restVariable14 = new RestVariable();
        restVariable14.setName("documentName");
        String documentName = hlsWarrantStockHd.getContractName() + "的权证入库申请";
        restVariable14.setValue( documentName );
        variables.add(restVariable14);

        RestVariable restVariable15 = new RestVariable();
        restVariable15.setName("warrantStockId");
        restVariable15.setValue(  hlsWarrantStockHd.getWarrantStockId() );
        variables.add(restVariable15);

        RestVariable restVariable16 = new RestVariable();
        restVariable16.setName("stockType");
        restVariable16.setValue(  hlsWarrantStockHd.getStockType() );
        variables.add(restVariable16);

        RestVariable restVariable17 = new RestVariable();
        restVariable17.setName("documentNumber");
        restVariable17.setValue(  hlsWarrantStockHd.getContractNumber() );
        variables.add(restVariable17);

        RestVariable restVariable18 = new RestVariable();
        restVariable18.setName("paymentFlag");
        restVariable18.setValue(  hlsWarrantStockHd.getPaymentFlag() );
        variables.add(restVariable18);

        //是否为航空事业部审批
        RestVariable restVariable19 = new RestVariable();
        restVariable19.setName("aviationFlag");
        Long unitId = hlsWarrantStockHd.getApplicantUnit();
        String aviationFlag = "N";
        if(unitId == 114L){
            //航空管理部
            aviationFlag = "Y";
        }
        restVariable19.setValue(aviationFlag);
        variables.add(restVariable19);


        HlsCusEmployee managerAssign = new HlsCusEmployee();
        managerAssign.setEmployeeId(hlsWarrantStockHd.getApplicantEmploy());
        managerAssign.setCompanyId(iRequest.getCompanyId());
        RestVariable restVariable20 = new RestVariable();
        restVariable20.setName("employeeManagerAssignsId");
        restVariable20.setValue(hlsCusEmployeeMapper.selectEmployeeAssignIdByEmployeeId(managerAssign).get(0).getEmployeeAssignId());
        variables.add(restVariable20);

        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);
        return createRequest;


    }


}
