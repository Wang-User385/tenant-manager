package com.hand.hls.pam.service.impl;

import com.alibaba.fastjson.JSON;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bill.dto.hlsBillRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.pam.dto.HlsCusLeaseItem;
import com.hand.hls.pam.dto.LeaseAssetLn;
import com.hand.hls.pam.mapper.LeaseAssetHdMapper;
import com.hand.hls.pam.mapper.LeaseAssetLnMapper;
import com.hand.hls.pam.service.HlsCusLeaseItemService;
import com.hand.hls.pam.service.IHlsCusLeaseItemListService;
import com.hand.hls.prj.dto.HlsLeaseItem;
import com.hand.hls.sys.mapper.FndCompanyMapper;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import leaf.service.validation.ParameterNullException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.pam.dto.LeaseAssetHd;
import com.hand.hls.pam.service.ILeaseAssetHdService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class LeaseAssetHdServiceImpl extends BaseServiceImpl<LeaseAssetHd> implements ILeaseAssetHdService{
    private static final String APPROVED = "APPROVED";
    private static final String APPROVING = "APPROVING";
    @Autowired
    private HlsEmployeeMapper employeeMapper;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private LeaseAssetHdMapper leaseAssetHdMapper;
    @Autowired
    private ILeaseAssetHdService leaseAssetHdService;
    @Autowired
    private LeaseAssetLnMapper leaseAssetLnMapper;
    @Autowired
    private HlsCusLeaseItemService hlsCusLeaseItemService;

    @Autowired
    FndCompanyMapper fndCompanyMapper;

    private void approveWfl(IRequest iRequest, LeaseAssetHd leaseAssetHd) throws ResMessageException {
        leaseAssetHd = leaseAssetHdMapper.selectByPrimaryKey(leaseAssetHd);
        if (APPROVED.equals(leaseAssetHd.getAssetStatus()) || APPROVING.equals(leaseAssetHd.getAssetStatus())) {
            throw new ResMessageException("当前单据状态不能提交申请！");
        }
        databaseLockProvider.lock(leaseAssetHd);

        List<LeaseAssetHd> leaseAssetHdList = new ArrayList<>();
        LeaseAssetLn leaseAssetLn = new LeaseAssetLn();
        leaseAssetLn.setAssetHdId(leaseAssetHd.getAssetHdId());
        leaseAssetHd = leaseAssetHdService.selectByPrimaryKey(iRequest, leaseAssetHd);
        leaseAssetHdList.add(leaseAssetHd);

        Map<String, Object> params = new HashMap<>();
        params.put("workFlowType", "PROPERTY_EVALUATE_WFL");
        params.put("assetHdId", leaseAssetHd.getAssetHdId());
        params.put("documentName", "资产价值重估流程审批"+leaseAssetHd.getAssetNumber());
        params.put("documentNumber", leaseAssetHd.getAssetNumber());

        params.put(IActivitiCommonService.WORK_FLOW_NAME, "PROPERTY_EVALUATE_WFL");
        params.put(IActivitiCommonService.DEMO_NAME, "PROPERTY_EVALUATE_WFL");
        params.put(IActivitiCommonService.BUSINESS_KEY, leaseAssetHd.getAssetHdId());
        params.put("documentCategory", "ASSET_NUMBER"); //
        params.put("leaseAssetHd", JSON.toJSONString(leaseAssetHd));
        params.put("startUserName", iRequest.getUserName());
        activitiStartService.start(iRequest, leaseAssetHdList, params);

        LeaseAssetHd contractInsure = new LeaseAssetHd();
        contractInsure.setAssetHdId(leaseAssetHd.getAssetHdId());
        contractInsure.setAssetStatus(APPROVING);
        leaseAssetHdService.updateByPrimaryKeySelective(iRequest, contractInsure);

    }

    @Override
    public List<LeaseAssetHd> conInceptSubmit(IRequest iRequest, LeaseAssetHd leaseAssetHd) throws ResMessageException, ParameterNullException {


        //启动工作流
        approveWfl(iRequest, leaseAssetHd);

        List<LeaseAssetHd> contractInsure = new ArrayList<>();
        contractInsure.add(leaseAssetHd);
        return contractInsure;
    }

    @Override
    public List<LeaseAssetHd> confirm(IRequest iRequest, LeaseAssetHd leaseAssetHd) {
        LeaseAssetLn leaseAssetLn = new LeaseAssetLn();
        leaseAssetLn.setAssetHdId(leaseAssetHd.getAssetHdId());
        List<LeaseAssetLn> leaseAssetLnList = leaseAssetLnMapper.select(leaseAssetLn);
        for(LeaseAssetLn leaseAssetLn1 : leaseAssetLnList){
            HlsCusLeaseItem hlsCusLeaseItem = new HlsCusLeaseItem();
            hlsCusLeaseItem.setLeaseItemId(leaseAssetLn1.getLeaseItemId());
            HlsCusLeaseItem leaseItem = hlsCusLeaseItemService.selectByPrimaryKey(iRequest,hlsCusLeaseItem);
            leaseItem.setFairValue(Double.valueOf(leaseAssetLn1.getFairValue()));
            hlsCusLeaseItemService.updateByPrimaryKeySelective(iRequest,leaseItem);

        }
        LeaseAssetHd leaseAssetHd1 = this.selectByPrimaryKey(iRequest,leaseAssetHd);
        //leaseAssetHd1.setAssetStatus();
        List<LeaseAssetHd> leaseAssetHdList = new ArrayList<>();
        leaseAssetHdList.add(leaseAssetHd1);

        return leaseAssetHdList;
    }


    /*@Override
    public void conInceptSubmit(IRequest iRequest, LeaseAssetHd leaseAssetHd) {
        List<LeaseAssetHd> leaseAssetHdList = new ArrayList<>();
        LeaseAssetLn leaseAssetLn = new LeaseAssetLn();
        leaseAssetLn.setAssetHdId(leaseAssetHd.getAssetHdId());
        leaseAssetHd = self().selectByPrimaryKey(iRequest, leaseAssetHd);
        leaseAssetHdList.add(leaseAssetHd);
        databaseLockProvider.lock(leaseAssetHd);
        if( "APPROVING".equalsIgnoreCase(leaseAssetHd.getAssetStatus())  ){
           //在审批中
        }
        //获取申请人
        if(Objects.isNull(iRequest.getUserId())){
            HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
            if (Objects.isNull(employee)) {

            }
            String employeeCode = employee.getEmployeeCode();
            iRequest.setEmployeeCode(employeeCode);
        }

        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "PROPERTY_EVALUATE_WFL");
        activitiStartService.start(iRequest, leaseAssetHdList, params);

       leaseAssetHd.setAssetStatus("APPROVING");
       self().updateByPrimaryKeySelective(iRequest, leaseAssetHd);
    }*/

    @Override
    public String generateAuthorityString(IRequest iRequest) {
        Long companyId = iRequest.getCompanyId();
        HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest().getSession();
        FndCompany company = fndCompanyMapper.selectByPrimaryKey(companyId);
        String unitCode = (String) session.getAttribute("unitCode");
        String positionCode = (String) session.getAttribute("positionCode");
        String empCode = iRequest.getEmployeeCode();
        String authorityString = '"' + company.getCompanyCode() + '"' + "." + '"' + unitCode + '"' + "." + '"' + positionCode + '"' + "." + '"' + empCode + '"';
        //String authorityString = '"' + company.getCompanyCode() + '"' + "." + '"' + unitCode + '"' + "." + '"' +  positionCode + '"' + "." + '"' + '"' + "." + '"' + '"' + "." + '"'  + positionCode + '"' + "." + '"'+  empCode + '"';
        return authorityString;
    }
}