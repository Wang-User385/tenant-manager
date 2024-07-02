//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.abs.dto.HlsCusAbsProduct;
import com.hand.hls.abs.service.AbsProductService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.service.SysUserService;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CtAbsProductSubmitActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = "FZ_PROJECT_DOS_WFL";
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private AbsProductService absProductService;
    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private SysUserService sysUserService;

    public CtAbsProductSubmitActivitiStartServiceImpl() {
    }

    @Override
    public String getWorkFlowType() {
        return "FZ_PROJECT_DOS_WFL";
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest((HlsCusAbsProduct)list.get(0),iRequest);
        this.activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(HlsCusAbsProduct hlsCusAbsProduct, IRequest iRequest) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs = reProcdefService.queryReProcdef("FZ_PROJECT_DOS_WFL","FZ_PROJECT_DOS_WFL");
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(hlsCusAbsProduct.getProductId().toString());

        //设置参数
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

        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("documentCategory");
        restVariable6.setValue(hlsCusAbsProduct.getDocumentCategory());
        variables.add(restVariable6);
        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("documentType");
        restVariable7.setValue(hlsCusAbsProduct.getDocumentType());
        variables.add(restVariable7);
        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("documentId");
        restVariable8.setValue(hlsCusAbsProduct.getProductId());
        variables.add(restVariable8);
        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("pName");
        restVariable9.setValue(name);
        variables.add(restVariable9);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("workFlowType");
        restVariable11.setValue(workFlowType);
        variables.add(restVariable11);

        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("documentName");
        restVariable12.setValue(hlsCusAbsProduct.getProductName());
        variables.add(restVariable12);

        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("documentNumber");
        restVariable13.setValue(hlsCusAbsProduct.getProductNumber());
        variables.add(restVariable13);


        RestVariable restVariable0 = new RestVariable();
        restVariable0.setName("BUSINESS_KEY");
        restVariable0.setValue(hlsCusAbsProduct.getProductId());
        variables.add(restVariable0);


        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);

        return createRequest;
    }


    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String)params.get("businessKey");
        long id = Long.parseLong(businessKey);
        HlsCusAbsProduct hlsCusAbsProduct = new HlsCusAbsProduct();
        hlsCusAbsProduct.setProductId(id);
        hlsCusAbsProduct.setProductStatus("CANCEL");
        this.absProductService.updateByPrimaryKeySelective(iRequest, hlsCusAbsProduct);
    }
}
