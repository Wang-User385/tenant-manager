package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.abs.dto.HlsCusAbsProduct;
import com.hand.hls.abs.service.HlsCusAbsProductService;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.service.HlsCusChangeReqInfoService;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.service.SysUserService;
import com.hand.hls.utils.HlsCusConstant;
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
 * 产品变更
 *
 * @author yuanyuan 2019/06/27 7:35 PM
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsProductChangeActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = HlsCusConstant.ABS_WFL.ABS_ABN_CHANGE_WFL;
    private final String NAME_SPACE = "BOND_ISSUE_PRODUCT_CHANGE";
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private HlsCusAbsProductService hlsCusAbsProductService;
    @Autowired
    private HlsCusChangeReqInfoService hlsCusChangeReqInfoService;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest request, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest((HlsCusAbsProduct) list.get(0), request);
        activitiService.startProcess(request, processInstanceCreateRequest);
    }


    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(HlsCusAbsProduct hlsCusAbsProduct, IRequest request) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs = reProcdefService.queryReProcdef(HlsCusConstant.ABS_WFL.ABS_ABN_CHANGE_WFL,NAME_SPACE);
        SysUser sysUser = sysUserService.selectUserById(request.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(hlsCusAbsProduct.getProductId().toString());

        //设置参数
        List<RestVariable> variables = new ArrayList<>();
        List<RestVariable> transientVariables = new ArrayList<>();

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
        restVariable3.setValue(request);
        variables.add(restVariable3);

        RestVariable restVariable4 = new RestVariable();
        restVariable4.setName("startUserName");
        restVariable4.setValue(request.getEmployeeCode());
        variables.add(restVariable4);

        RestVariable restVariable5 = new RestVariable();
        restVariable5.setName("documentCategory");
        restVariable5.setValue(hlsCusAbsProduct.getDocumentCategory());
        variables.add(restVariable5);

        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("documentType");
        restVariable6.setValue(hlsCusAbsProduct.getDocumentType());
        variables.add(restVariable6);

        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("workflowType");
        restVariable7.setValue(workFlowType);
        variables.add(restVariable7);

        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("businessType");
        restVariable8.setValue("FACTOR");
        variables.add(restVariable8);

        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("companyId");
        restVariable9.setValue(hlsCusAbsProduct.getCompanyId());
        variables.add(restVariable9);

        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("pName");
        restVariable10.setValue(name);
        variables.add(restVariable10);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("documentName");
        restVariable11.setValue(hlsCusAbsProduct.getProductName());
        variables.add(restVariable11);

        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("documentNumber");
        restVariable12.setValue(hlsCusAbsProduct.getProductNumber());
        variables.add(restVariable12);

        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("documentId");
        restVariable13.setValue(hlsCusAbsProduct.getProductId());
        variables.add(restVariable13);

        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);
        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        Long productId = Long.valueOf(businessKey);
        HlsCusAbsProduct hlsCusAbsProduct = new HlsCusAbsProduct();
        hlsCusAbsProduct.setProductId(productId);
        hlsCusAbsProduct=hlsCusAbsProductService.selectByPrimaryKey(iRequest, hlsCusAbsProduct);

        hlsCusAbsProduct.setProductStatus("APPROVED");
        hlsCusAbsProductService.updateByPrimaryKeySelective(iRequest,hlsCusAbsProduct);
        HlsCusChangeReqInfo reqInfo=new HlsCusChangeReqInfo();
        reqInfo.setStatus("NEW");
        reqInfo.setChangeReqId(hlsCusAbsProduct.getChangeReqId());
        hlsCusChangeReqInfoService.updateByPrimaryKeySelective(iRequest,reqInfo);
    }

}
