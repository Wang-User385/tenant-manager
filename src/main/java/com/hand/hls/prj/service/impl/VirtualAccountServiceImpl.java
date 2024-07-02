package com.hand.hls.prj.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.BaseConstants;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.IConContractService;
import com.hand.hls.csh.dto.HlsCusCshBank;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.mapper.HlsCusCshBankMapper;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.service.CshWriteOffService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.VirtualAccount;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.VirtualAccountMapper;
import com.hand.hls.prj.service.IPrjProjectService;
import com.hand.hls.prj.service.IVirtualAccountService;
import com.hand.hls.utils.DateUtils;
import com.hand.hls.utils.service.HlsConstantUtil;
import com.hand.hls.utils.MathUtil;
import com.hand.hls.wsdl.utils.SapConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class VirtualAccountServiceImpl extends BaseServiceImpl<VirtualAccount> implements IVirtualAccountService{

    private static final String PARAM_NOT_FOUND = "参数未找到！";
    private static final String MANUFACTURER_NOT_FOUND = "未找到相关厂商！";
    private static final String MANUFACTURER_MATCH_MORE = "匹配到多个厂商，请检查数据！";
    private static final String NO_VIRTUAL_ACCOUNT_ERROR_MSG = "虚拟账号已不足，请导入虚拟账号！";
    private static final String VIRTUAL_ACCOUNT_EXISTS_ERROR_MSG = "虚拟账号已存在，且虚拟账号对应合同还未结束不能释放！";

    private static final String MAIN = "MAIN";
    private static final String NEW = "NEW";
    private static final String NOT = "NOT";
    private static final String CANCEL = "CANCEL";
    private static final String PENDING = "PENDING";
    private static final String YES = "Y";
    private static final String NO = "N";

    private static final Long READ_LINE = 0L;

    private static final Long LONG_ZERO = 0L;
    private static final Long CF_ITEM_9 = 9L;
    private static final Double DOUBLE_ZERO = 0D;

    private static final String TERMINATE = "TERMINATE";


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;

    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;

    @Autowired
    private VirtualAccountMapper virtualAccountMapper;

    @Autowired
    private HlsCusPrjProjectMapper prjProjectMapper;

    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;

    @Autowired
    private CshWriteOffService cshWriteOffService;

    @Autowired
    private HlsCusCshTransactionMapper hlsCusCshTransactionMapper;
    @Autowired
    private IConContractService conContractService;
    @Autowired
    private HlsCusCshBankMapper hlsCusCshBankMapper;

    String getExceptionInfo(int rownum, int colnum, String message) {
        StringBuffer sb = new StringBuffer();
        sb.append(IPrjProjectService.ORDER_DESC);
        sb.append(rownum);
        sb.append(IPrjProjectService.ROW_DESC);
        sb.append("，");
        sb.append(IPrjProjectService.ORDER_DESC);
        sb.append(colnum);
        sb.append(IPrjProjectService.COL_DESC);
        sb.append(message);
        return sb.toString();
    }


    private VirtualAccount checkImportInfo(FndInterfaceLines fndInterfaceLines, int rowNum) throws HlsCusException{

        VirtualAccount virtualAccount  = new VirtualAccount();

        if(StringUtils.isEmpty(fndInterfaceLines.getAttributes_1())){
            throw new HlsCusException(getExceptionInfo(rowNum,1,IPrjProjectService.NOT_NULL));
        }else{
            virtualAccount.setAccountName(fndInterfaceLines.getAttributes_1());
        }

        if(StringUtils.isEmpty(fndInterfaceLines.getAttributes_2())){
            throw new HlsCusException(getExceptionInfo(rowNum,2,IPrjProjectService.NOT_NULL));
        }else{
            virtualAccount.setAccountFullName(fndInterfaceLines.getAttributes_2());
        }

        if(StringUtils.isEmpty(fndInterfaceLines.getAttributes_3())){
            throw new HlsCusException(getExceptionInfo(rowNum,3,IPrjProjectService.NOT_NULL));
        }else{
            virtualAccount.setAccountNum(fndInterfaceLines.getAttributes_3());
        }

        if(StringUtils.isEmpty(fndInterfaceLines.getAttributes_4())){
            throw new HlsCusException(getExceptionInfo(rowNum,4,IPrjProjectService.NOT_NULL));
        }else {
            //先判断合同编号是否正确
            HlsCusConContract contract = new HlsCusConContract();
            contract.setContractNumber(fndInterfaceLines.getAttributes_4());
            List<HlsCusConContract> contractList = hlsCusConContractMapper.select(contract);
            if(CollectionUtils.size(contractList) == 0){
                throw new HlsCusException(getExceptionInfo(rowNum,4, "未在系统中找到相应的合同，请核对数据！"));
            }else if(CollectionUtils.size(contractList) > 1){
                throw new HlsCusException(getExceptionInfo(rowNum,4, "合同号在系统中匹配到多条合同，请联系管理员！"));
            }

            //然后判断合同号是否已经分配过虚拟账号
            VirtualAccount queryAccount = new VirtualAccount();
            queryAccount.setContractId(contractList.get(0).getContractId());
            List<VirtualAccount> virtualAccountList = virtualAccountMapper.select(queryAccount);
            if(CollectionUtils.isNotEmpty(virtualAccountList)){
                throw new HlsCusException(getExceptionInfo(rowNum,4, "合同号重复，请核对后导入！"));
            }

            //通过校验后将合同ID维护至虚拟账号信息中
            virtualAccount.setContractId(contractList.get(0).getContractId());
        }

        if(StringUtils.isEmpty(fndInterfaceLines.getAttributes_5())){
            throw new HlsCusException(getExceptionInfo(rowNum,5,IPrjProjectService.NOT_NULL));
        }else {
            //根据系统银行定义功能检查导入银行全称是否正确
            HlsCusCshBank bank = new HlsCusCshBank();
            bank.setBankName(fndInterfaceLines.getAttributes_5());
            List<HlsCusCshBank> hlsCusCshBanks = hlsCusCshBankMapper.select(bank);

            //判断系统中银行定义是否定义相应银行
            if(CollectionUtils.size(hlsCusCshBanks) != 1){
                throw new HlsCusException(getExceptionInfo(rowNum,5,"银行全称有误，请核对后导入！"));
            }

            virtualAccount.setBankCode(hlsCusCshBanks.get(0).getBankCode());

        }

        if(StringUtils.isEmpty(fndInterfaceLines.getAttributes_6())) {
            throw new HlsCusException(getExceptionInfo(rowNum,6,IPrjProjectService.NOT_NULL));
        } else {
            if(StringUtils.equals(fndInterfaceLines.getAttributes_6(),"是")) {
                virtualAccount.setIsAutoWriteoffFlag(YES);
            } else if (StringUtils.equals(fndInterfaceLines.getAttributes_6(),"否")) {
                virtualAccount.setIsAutoWriteoffFlag(NO);
            } else {
                throw new HlsCusException(getExceptionInfo(rowNum,6,"填写有误，请核对后导入！"));
            }
        }

        return virtualAccount;
    }

    @Override
    public void batchImportVirtualAccount(IRequest iRequest, Long headerId) throws HlsCusException {
        iRequest.setAttribute("wflRuleControlFlag", "N");
        if (headerId == null) {
            throw new HlsCusException(PARAM_NOT_FOUND);
        }

        FndInterfaceLines fndInterfaceLines = new FndInterfaceLines();
        fndInterfaceLines.setHeaderId(headerId);
        fndInterfaceLines.setReadLine(READ_LINE);
        fndInterfaceLines.setSheetName("Sheet1");
        List<FndInterfaceLines> accountList = fndInterfaceLinesMapper.fndInterfaceLinesDetailQuery(fndInterfaceLines);

        for (int i = 0; i <accountList.size(); i++) {
            VirtualAccount virtualAccount = checkImportInfo(accountList.get(i), i + 1);
            virtualAccount = self().insertSelective(iRequest, virtualAccount);
            //将虚拟账号信息反写到相应的合同上
            HlsCusConContract contract = new HlsCusConContract();
            contract.setContractId(virtualAccount.getContractId());
            contract.setVirtualAccountId(virtualAccount.getAccountId());
            contract.setVirtualAccountNum(virtualAccount.getAccountNum());
            HlsCusConContract contractNew = conContractService.selectByPrimaryKey(iRequest,contract);
            if(contractNew.getDownloadVersion()==null){
                contract.setDownloadVersion(1L);
            }else{
                contract.setDownloadVersion(contractNew.getDownloadVersion() + 1);
            }
            conContractService.updateByPrimaryKeySelective(iRequest,contract);
        }

    }

    @Override
    public VirtualAccount assignVirtualAccount (HlsCusPrjProject prjProject){

        VirtualAccount virtualAccount = new VirtualAccount();

        switch (prjProject.getVirtualAccount()){
            case MAIN:
                virtualAccount.setManufacturerId(prjProject.getManufacturerId());
                virtualAccount.setTenantId(prjProject.getTenantId());
                virtualAccount.setPrimaryFlag(BaseConstants.YES);
                virtualAccount = virtualAccountMapper.selectOne(virtualAccount);
                //如果该厂商与承租人下没有主账号，则在该厂商对应虚拟账号下分配一个虚拟账号给对应的承租人，并且将该虚拟账号的主账号更新为启用
                if(Objects.isNull(virtualAccount)){
                    VirtualAccount queryVirtual = new VirtualAccount();
                    queryVirtual.setManufacturerId(prjProject.getManufacturerId());
                    List<VirtualAccount> virtualAccounts = virtualAccountMapper.queryUnusedVirtualAccount(queryVirtual);

                    if(CollectionUtils.isEmpty(virtualAccounts)){
                        throw new IllegalArgumentException(NO_VIRTUAL_ACCOUNT_ERROR_MSG);
                    }else{
                        virtualAccount = virtualAccounts.get(0);
                        virtualAccount.setTenantId(prjProject.getTenantId());
                        virtualAccount.setPrimaryFlag(BaseConstants.YES);
                        virtualAccountMapper.updateByPrimaryKeySelective(virtualAccount);
                    }
                }
                break;
            case NEW:
                virtualAccount.setManufacturerId(prjProject.getManufacturerId());
                List<VirtualAccount> virtualAccounts = virtualAccountMapper.queryUnusedVirtualAccount(virtualAccount);
                if(CollectionUtils.isEmpty(virtualAccounts)){
                    throw new IllegalArgumentException(NO_VIRTUAL_ACCOUNT_ERROR_MSG);
                }else{
                    //判断对应厂商承租人是否存在为主账号的虚拟账号，如果不存在的话将新分配的虚拟账号更新为主账号
                    VirtualAccount queryVirtual = new VirtualAccount();
                    queryVirtual.setManufacturerId(prjProject.getManufacturerId());
                    queryVirtual.setTenantId(prjProject.getTenantId());
                    queryVirtual.setPrimaryFlag(BaseConstants.YES);
                    queryVirtual = virtualAccountMapper.selectOne(queryVirtual);

                    virtualAccount = virtualAccounts.get(0);
                    virtualAccount.setTenantId(prjProject.getTenantId());
                    if(Objects.isNull(queryVirtual)){
                        virtualAccount.setPrimaryFlag(BaseConstants.YES);
                    }
                    virtualAccountMapper.updateByPrimaryKeySelective(virtualAccount);
                }
                break;
        }
        return virtualAccount;
    }

    /**
     * 投放审查提交审批时校验虚拟账号库存是否足够
     * @param project
     * @throws HlsCusException
     */
    @Override
    public void checkVirtualAccountReserve(HlsCusPrjProject project) throws HlsCusException{
        VirtualAccount virtualAccount = new VirtualAccount();
        virtualAccount.setManufacturerId(project.getManufacturerId());
        List<VirtualAccount> virtualAccounts = virtualAccountMapper.queryUnusedVirtualAccount(virtualAccount);
        switch (project.getVirtualAccount()){
            case MAIN:
                virtualAccount.setTenantId(project.getTenantId());
                virtualAccount.setPrimaryFlag(BaseConstants.YES);
                virtualAccount = virtualAccountMapper.selectOne(virtualAccount);
                if(Objects.isNull(virtualAccount)){
                    if (CollectionUtils.isEmpty(virtualAccounts)){
                        throw new HlsCusException(NO_VIRTUAL_ACCOUNT_ERROR_MSG);
                    }else {
                        HlsCusPrjProject queryPrjProject = new HlsCusPrjProject();
                        queryPrjProject.setManufacturerId(project.getManufacturerId());
                        queryPrjProject.setSignStatus(HlsConstantUtil.WorkFlowStatus.APPROVING);
                        queryPrjProject.setVirtualAccount(NEW);
                        List<HlsCusPrjProject> hlsCusPrjProjects = prjProjectMapper.select(queryPrjProject);
                        if(CollectionUtils.isNotEmpty(hlsCusPrjProjects)){
                            if(hlsCusPrjProjects.size()>=virtualAccounts.size()){
                                throw new HlsCusException(NO_VIRTUAL_ACCOUNT_ERROR_MSG);
                            }
                        }
                    }

                }
                break;
            case NEW:
                if (CollectionUtils.isEmpty(virtualAccounts)){
                    throw new HlsCusException(NO_VIRTUAL_ACCOUNT_ERROR_MSG);
                }else {
                    HlsCusPrjProject queryPrjProject = new HlsCusPrjProject();
                    queryPrjProject.setManufacturerId(project.getManufacturerId());
                    queryPrjProject.setSignStatus(HlsConstantUtil.WorkFlowStatus.APPROVING);
                    List<HlsCusPrjProject> hlsCusPrjProjects = prjProjectMapper.select(queryPrjProject);
                    if(CollectionUtils.isNotEmpty(hlsCusPrjProjects)){
                        if(hlsCusPrjProjects.size()>=virtualAccounts.size()){
                            throw new HlsCusException(NO_VIRTUAL_ACCOUNT_ERROR_MSG);
                        }
                    }
                }
                break;
            case NOT:
                break;
        }

    }


    private void cashflowListToWriteOff(List<HlsCusConContractCashflow> hlsCusConContractCashflows, List<HlsCusCshWriteOff> cshWriteOffs,HlsCusCshTransaction cshTransaction,Date date){
        for (HlsCusConContractCashflow conContractCashflow:
                hlsCusConContractCashflows) {
            HlsCusCshWriteOff hlsCusCshWriteOff = new HlsCusCshWriteOff();
            hlsCusCshWriteOff.setArriveModality("TENANT");
            hlsCusCshWriteOff.setWriteOffType("RECEIPT_CREDIT");
            hlsCusCshWriteOff.setWriteOffDate(date);
            hlsCusCshWriteOff.setTransactionId(cshTransaction.getTransactionId());
            hlsCusCshWriteOff.setCshTransactionId(cshTransaction.getTransactionId());
            hlsCusCshWriteOff.setWriteOffDueAmount(conContractCashflow.getSurplusAmount());
            hlsCusCshWriteOff.setWriteOffPrincipal(conContractCashflow.getSurplusPrincipal());
            hlsCusCshWriteOff.setWriteOffInterest(conContractCashflow.getSurplusInterest());
            hlsCusCshWriteOff.setCashflowId(conContractCashflow.getCashflowId());
            hlsCusCshWriteOff.setContractId(conContractCashflow.getContractId());
            hlsCusCshWriteOff.setTimes(conContractCashflow.getTimes());
            hlsCusCshWriteOff.setCfItem(conContractCashflow.getCfItem());
            hlsCusCshWriteOff.setCfType(conContractCashflow.getCfType());
            hlsCusCshWriteOff.setSurplusAmount(conContractCashflow.getSurplusAmount());
            hlsCusCshWriteOff.setDueAmount(conContractCashflow.getDueAmount());
            hlsCusCshWriteOff.setDueDate(conContractCashflow.getDueDate());
            hlsCusCshWriteOff.setCf_direction(conContractCashflow.getCfDirection());
            hlsCusCshWriteOff.setContractNumber(conContractCashflow.getContractNumber());
            cshWriteOffs.add(hlsCusCshWriteOff);
        }
    }


    private void cashflowToWriteOff(HlsCusConContractCashflow conContractCashflow, List<HlsCusCshWriteOff> cshWriteOffs,HlsCusCshTransaction cshTransaction,Date date){
            HlsCusCshWriteOff hlsCusCshWriteOff = new HlsCusCshWriteOff();
            hlsCusCshWriteOff.setArriveModality("TENANT");
            hlsCusCshWriteOff.setWriteOffType("RECEIPT_CREDIT");
            hlsCusCshWriteOff.setWriteOffDate(date);
            hlsCusCshWriteOff.setTransactionId(cshTransaction.getTransactionId());
            hlsCusCshWriteOff.setCshTransactionId(cshTransaction.getTransactionId());
            hlsCusCshWriteOff.setWriteOffPrincipal(conContractCashflow.getSurplusPrincipal());
            hlsCusCshWriteOff.setWriteOffInterest(conContractCashflow.getSurplusInterest());
            hlsCusCshWriteOff.setCashflowId(conContractCashflow.getCashflowId());
            hlsCusCshWriteOff.setContractId(conContractCashflow.getContractId());
            hlsCusCshWriteOff.setTimes(conContractCashflow.getTimes());
            hlsCusCshWriteOff.setCfItem(conContractCashflow.getCfItem());
            hlsCusCshWriteOff.setCfType(conContractCashflow.getCfType());
            hlsCusCshWriteOff.setSurplusAmount(conContractCashflow.getSurplusAmount());
            hlsCusCshWriteOff.setDueAmount(conContractCashflow.getDueAmount());
            hlsCusCshWriteOff.setDueDate(conContractCashflow.getDueDate());
            hlsCusCshWriteOff.setCf_direction(conContractCashflow.getCfDirection());
            hlsCusCshWriteOff.setContractNumber(conContractCashflow.getContractNumber());
            if(conContractCashflow.getCfItem() == SapConstants._8 || conContractCashflow.getCfItem() == SapConstants._9){
                hlsCusCshWriteOff.setWriteOffDueAmount(conContractCashflow.getVirtualWriteOffAmount());
            }else{
                hlsCusCshWriteOff.setWriteOffDueAmount(MathUtil.add(conContractCashflow.getSurplusPrincipal(),conContractCashflow.getSurplusInterest()));
            }
            cshWriteOffs.add(hlsCusCshWriteOff);
    }


    //相同应收日下的不同现金流类型的认领
    private Double claimByCfItem (List<HlsCusConContractCashflow> dateContractCashflows,Long cfItem,Double remainingAmount,List<HlsCusCshWriteOff> cshWriteOffs,HlsCusCshTransaction cshTransaction,Date date){

        //能确认每种现金流类型在对应的应收日只会有一条记录，所以后面筛选出来的对应的现金流都按一条来处理
        List<HlsCusConContractCashflow> conCashflows = dateContractCashflows.stream()
                .filter(item -> item.getCfItem() == cfItem).collect(Collectors.toList());

        if(CollectionUtils.isNotEmpty(conCashflows)){
            if(remainingAmount.compareTo(conCashflows.get(0).getSurplusAmount()) >=0){
                cashflowListToWriteOff(conCashflows,cshWriteOffs,cshTransaction,date);
                remainingAmount = MathUtil.sub(remainingAmount,conCashflows.get(0).getSurplusAmount());
            }else{
                //不足额的情况下 按照先息后本的顺序进行认领
                if(cfItem == SapConstants._8 || cfItem == SapConstants._9){
                    conCashflows.get(0).setVirtualWriteOffAmount(remainingAmount);
                    cashflowToWriteOff(conCashflows.get(0),cshWriteOffs,cshTransaction,date);
                }else{
                    if(remainingAmount.compareTo(conCashflows.get(0).getSurplusInterest())>0){
                        conCashflows.get(0).setSurplusPrincipal(MathUtil.sub(remainingAmount,conCashflows.get(0).getSurplusInterest()));
                        cashflowToWriteOff(conCashflows.get(0),cshWriteOffs,cshTransaction,date);
                    }else if(remainingAmount.compareTo(conCashflows.get(0).getSurplusInterest()) == 0){
                        conCashflows.get(0).setSurplusPrincipal(0D);
                        cashflowToWriteOff(conCashflows.get(0),cshWriteOffs,cshTransaction,date);
                    }else{
                        conCashflows.get(0).setSurplusInterest(remainingAmount);
                        conCashflows.get(0).setSurplusPrincipal(0D);
                        cashflowToWriteOff(conCashflows.get(0),cshWriteOffs,cshTransaction,date);
                    }
                }
                remainingAmount = 0D;
            }
        }

        return remainingAmount;
    }


    private static Date nextMonthFirstDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, 1);
        return calendar.getTime();
    }

    /**
     * 收款自动认领
     * @param iRequest
     * @param cshTransactionId
     */
    @Override
    public void transactionAutoClaim(IRequest iRequest , Long cshTransactionId) throws Exception{

        HlsCusCshTransaction cshTransaction = new HlsCusCshTransaction();
        cshTransaction.setTransactionId(cshTransactionId);
        cshTransaction = hlsCusCshTransactionMapper.selectByPrimaryKey(cshTransaction);

        if(!Objects.isNull(cshTransaction)){
            //判断现金事务打款账号是否为虚拟账号
            if(StringUtils.equals(cshTransaction.getVirtualAccountFlag(),BaseConstants.YES)) {
                //根据虚拟账号来匹配对应的合同
                VirtualAccount virtualAccountQuery = new VirtualAccount();
                virtualAccountQuery.setAccountNum(cshTransaction.getRefV01());
                List<VirtualAccount> virtualAccountList = virtualAccountMapper.select(virtualAccountQuery);
                if (StringUtils.equals(virtualAccountList.get(0).getIsAutoWriteoffFlag(), BaseConstants.YES)) {
                    if (virtualAccountList.size() == 1L) {
                        for (VirtualAccount virtualAccount : virtualAccountList) {
                            HlsCusConContract conContract = new HlsCusConContract();
                            conContract.setVirtualAccountId(virtualAccount.getAccountId());
                            conContract.setSapStatus(SapConstants.SUCCESS);
                            List<HlsCusConContract> conContractList = hlsCusConContractMapper.select(conContract);

                            if (CollectionUtils.isNotEmpty(conContractList)) {
                                //筛选出不是结束、取消和暂挂的合同
                                conContractList = conContractList.stream().filter(item -> !StringUtils.equals(item.getContractStatus(), CANCEL) && !StringUtils.equals(item.getContractStatus(), PENDING)
                                        && !StringUtils.equals(item.getContractStatus(), TERMINATE)).collect(Collectors.toList());


                                HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
                                hlsCusConContractCashflow.setCfDirection("INFLOW");
                                hlsCusConContractCashflow.setnCfItem(SapConstants._61);

                                Date now = new Date();
                                Date nextMonthFirstDate = nextMonthFirstDate();

                                //认领需要的参数集合
                                List<HlsCusCshWriteOff> cshWriteOffs = new ArrayList<>();

                                if (conContractList.size() == 1) {
                                    hlsCusConContractCashflow.setContractId(conContractList.get(0).getContractId());
                                    List<HlsCusConContractCashflow> hlsCusConContractCashflows = hlsCusConContractCashflowMapper.queryContractCashflowWriteoffLov(hlsCusConContractCashflow);

                                    //筛选出本月及之前月份的现金流(承租人罚息为负移除)
                                    hlsCusConContractCashflows = hlsCusConContractCashflows.stream().filter(item -> DateUtils.compare(nextMonthFirstDate, item.getDueDate()) > 0
                                    && !(CF_ITEM_9.equals(item.getCfItem()) && item.getSurplusAmount() < 0)).collect(Collectors.toList());

                                    //筛选出本月及之前月份的现金流应收款项总金额
                                    Double sumSurplusAmount = hlsCusConContractCashflows.stream()
                                            .mapToDouble(HlsCusConContractCashflow::getSurplusAmount).sum();

                                    //判断承租人此次还款是否足额
                                    if (cshTransaction.getTransactionAmount().compareTo(sumSurplusAmount) > 0) {
                                        //如果足额，则自动认领并将多余款项挂为该承租人名下的预收款
                                        cashflowListToWriteOff(hlsCusConContractCashflows, cshWriteOffs, cshTransaction, now);

                                        //多余款项认领为预收款
                                        HlsCusCshWriteOff hlsCusCshWriteOff = new HlsCusCshWriteOff();
                                        hlsCusCshWriteOff.setAdvanceBpId(conContractList.get(0).getTenantId());
                                        hlsCusCshWriteOff.setWriteOffType("RECEIPT_ADVANCE_RECEIPT");
                                        hlsCusCshWriteOff.setWriteOffDate(now);
                                        hlsCusCshWriteOff.setTransactionId(cshTransaction.getTransactionId());
                                        hlsCusCshWriteOff.setCshTransactionId(cshTransaction.getTransactionId());
                                        hlsCusCshWriteOff.setWriteOffDueAmount(MathUtil.sub(cshTransaction.getTransactionAmount(), sumSurplusAmount));
                                        cshWriteOffs.add(hlsCusCshWriteOff);
                                    } else if (cshTransaction.getTransactionAmount().compareTo(sumSurplusAmount) == 0) {

                                        cashflowListToWriteOff(hlsCusConContractCashflows, cshWriteOffs, cshTransaction, now);

                                    } else {
                                        //如果不足额 日期从前往后排序，依次认领租金（租金、提前结清租金)、承租人罚息、留购金（名义货价）
                                        Map<Long, List<HlsCusConContractCashflow>> map = hlsCusConContractCashflows.stream()
                                                .collect(Collectors.groupingBy(HlsCusConContractCashflow::getTimes));

                                        List<Long> timesList = new ArrayList<>(map.size());
                                        for (Map.Entry<Long, List<HlsCusConContractCashflow>> entry : map.entrySet()) {
                                            timesList.add(entry.getKey());
                                        }

                                        //将应收日期排序，再依次认领租金（租金、提前结清租金)、承租人罚息、留购金（名义货价）
                                        timesList.sort(Comparator.comparing(Long::longValue));
                                        //剩余未认领金额
                                        Double remainingAmount = cshTransaction.getTransactionAmount();

                                        for (Long times : timesList) {
                                            List<HlsCusConContractCashflow> dateContractCashflows = map.get(times);

                                            Double dateSumSurplusAmount = dateContractCashflows.stream()
                                                    .mapToDouble(HlsCusConContractCashflow::getSurplusAmount).sum();
                                            if (remainingAmount.compareTo(dateSumSurplusAmount) >= 0) {
                                                cashflowListToWriteOff(dateContractCashflows, cshWriteOffs, cshTransaction, now);
                                                //更新未认领金额
                                                remainingAmount = MathUtil.sub(remainingAmount, dateSumSurplusAmount);
                                            } else {
                                                if (remainingAmount.compareTo(DOUBLE_ZERO) > 0) {
                                                    //租金
                                                    remainingAmount = claimByCfItem(dateContractCashflows, SapConstants._1, remainingAmount, cshWriteOffs, cshTransaction, now);
                                                    if (remainingAmount.compareTo(DOUBLE_ZERO) > 0) {
                                                        //提前结清租金
                                                        remainingAmount = claimByCfItem(dateContractCashflows, SapConstants._11, remainingAmount, cshWriteOffs, cshTransaction, now);
                                                        if (remainingAmount.compareTo(DOUBLE_ZERO) > 0) {
                                                            //承租人罚息
                                                            remainingAmount = claimByCfItem(dateContractCashflows, SapConstants._9, remainingAmount, cshWriteOffs, cshTransaction, now);
                                                            if (remainingAmount.compareTo(DOUBLE_ZERO) > 0) {
                                                                //留购金
                                                                remainingAmount = claimByCfItem(dateContractCashflows, SapConstants._8, remainingAmount, cshWriteOffs, cshTransaction, now);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else if (conContractList.size() > 1) {
                                    //计算所有合同本月及之前月份应收款项总金额，判断此次还款是否足额
                                    List<HlsCusConContractCashflow> hlsCusConContractCashflows = new ArrayList<>();
                                    for (HlsCusConContract contract :
                                            conContractList) {
                                        hlsCusConContractCashflow.setContractId(contract.getContractId());
                                        List<HlsCusConContractCashflow> conContractCashflows = hlsCusConContractCashflowMapper.queryContractCashflowWriteoffLov(hlsCusConContractCashflow);
                                        hlsCusConContractCashflows.addAll(conContractCashflows);
                                    }

                                    //筛选出本月及之前月份的现金流
                                    hlsCusConContractCashflows = hlsCusConContractCashflows.stream().filter(item -> DateUtils.compare(nextMonthFirstDate, item.getDueDate()) > 0)
                                            .collect(Collectors.toList());

                                    //筛选出本月及之前月份的现金流应收款项总金额
                                    Double sumSurplusAmount = hlsCusConContractCashflows.stream()
                                            .mapToDouble(HlsCusConContractCashflow::getSurplusAmount).sum();

                                    //判断承租人此次还款是否足额
                                    if (cshTransaction.getTransactionAmount().compareTo(sumSurplusAmount) > 0) {
                                        //如果足额，则自动认领并将多余款项挂为该承租人名下的预收款
                                        cashflowListToWriteOff(hlsCusConContractCashflows, cshWriteOffs, cshTransaction, now);

                                        //多余款项认领为预收款
                                        HlsCusCshWriteOff hlsCusCshWriteOff = new HlsCusCshWriteOff();
                                        hlsCusCshWriteOff.setAdvanceBpId(conContractList.get(0).getTenantId());
                                        hlsCusCshWriteOff.setWriteOffType("RECEIPT_ADVANCE_RECEIPT");
                                        hlsCusCshWriteOff.setWriteOffDate(now);
                                        hlsCusCshWriteOff.setTransactionId(cshTransaction.getTransactionId());
                                        hlsCusCshWriteOff.setCshTransactionId(cshTransaction.getTransactionId());
                                        hlsCusCshWriteOff.setWriteOffDueAmount(MathUtil.sub(cshTransaction.getTransactionAmount(), sumSurplusAmount));
                                        cshWriteOffs.add(hlsCusCshWriteOff);
                                    } else if (cshTransaction.getTransactionAmount().compareTo(sumSurplusAmount) == 0) {
                                        cashflowListToWriteOff(hlsCusConContractCashflows, cshWriteOffs, cshTransaction, now);
                                    }
                                }
                                if (CollectionUtils.isNotEmpty(cshWriteOffs)) {
                                    cshWriteOffService.cshWriteOffSendSap(iRequest, cshWriteOffs, null, false);
                                }
                            }
                        }
                    } else if (virtualAccountList.size() > 1L) {
                        List<HlsCusConContractCashflow> hlsCusConContractCashflows = new ArrayList<>();
                        //认领需要的参数集合
                        List<HlsCusCshWriteOff> cshWriteOffs = new ArrayList<>();
                        Date now = new Date();
                        Date nextMonthFirstDate = nextMonthFirstDate();
                        Long TenantId = null;
                        for (VirtualAccount virtualAccount : virtualAccountList) {
                            HlsCusConContract conContract = new HlsCusConContract();
                            conContract.setVirtualAccountId(virtualAccount.getAccountId());
                            conContract.setSapStatus(SapConstants.SUCCESS);
                            List<HlsCusConContract> conContractList = hlsCusConContractMapper.select(conContract);

                            if (CollectionUtils.isNotEmpty(conContractList)) {
                                //筛选出不是结束、取消和暂挂的合同
                                conContractList = conContractList.stream().filter(item -> !StringUtils.equals(item.getContractStatus(), CANCEL) && !StringUtils.equals(item.getContractStatus(), PENDING)
                                        && !StringUtils.equals(item.getContractStatus(), TERMINATE)).collect(Collectors.toList());


                                HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
                                hlsCusConContractCashflow.setCfDirection("INFLOW");
                                hlsCusConContractCashflow.setnCfItem(SapConstants._61);


                                //计算所有合同本月及之前月份应收款项总金额，判断此次还款是否足额

                                for (HlsCusConContract contract :
                                        conContractList) {
                                    TenantId = contract.getTenantId();
                                    hlsCusConContractCashflow.setContractId(contract.getContractId());
                                    List<HlsCusConContractCashflow> conContractCashflows = hlsCusConContractCashflowMapper.queryContractCashflowWriteoffLov(hlsCusConContractCashflow);
                                    hlsCusConContractCashflows.addAll(conContractCashflows);
                                }
                            }
                        }

                        if (CollectionUtils.isNotEmpty(hlsCusConContractCashflows)) {
                            //筛选出本月及之前月份的现金流
                            hlsCusConContractCashflows = hlsCusConContractCashflows.stream().filter(item -> DateUtils.compare(nextMonthFirstDate, item.getDueDate()) > 0
                                    && !(CF_ITEM_9.equals(item.getCfItem()) && item.getSurplusAmount() < 0)).collect(Collectors.toList());

                            //筛选出本月及之前月份的现金流应收款项总金额
                            Double sumSurplusAmount = hlsCusConContractCashflows.stream()
                                    .mapToDouble(HlsCusConContractCashflow::getSurplusAmount).sum();

                            //判断承租人此次还款是否足额
                            if (cshTransaction.getTransactionAmount().compareTo(sumSurplusAmount) > 0) {
                                //如果足额，则自动认领并将多余款项挂为该承租人名下的预收款
                                cashflowListToWriteOff(hlsCusConContractCashflows, cshWriteOffs, cshTransaction, now);

                                //多余款项认领为预收款
                                HlsCusCshWriteOff hlsCusCshWriteOff = new HlsCusCshWriteOff();
                                hlsCusCshWriteOff.setAdvanceBpId(TenantId);
                                hlsCusCshWriteOff.setWriteOffType("RECEIPT_ADVANCE_RECEIPT");
                                hlsCusCshWriteOff.setWriteOffDate(now);
                                hlsCusCshWriteOff.setTransactionId(cshTransaction.getTransactionId());
                                hlsCusCshWriteOff.setCshTransactionId(cshTransaction.getTransactionId());
                                hlsCusCshWriteOff.setWriteOffDueAmount(MathUtil.sub(cshTransaction.getTransactionAmount(), sumSurplusAmount));
                                cshWriteOffs.add(hlsCusCshWriteOff);
                            } else if (cshTransaction.getTransactionAmount().compareTo(sumSurplusAmount) == 0) {
                                cashflowListToWriteOff(hlsCusConContractCashflows, cshWriteOffs, cshTransaction, now);
                            }
                        }
                        if (CollectionUtils.isNotEmpty(cshWriteOffs)) {
                            cshWriteOffService.cshWriteOffSendSap(iRequest, cshWriteOffs, null, false);
                        }
                    }
                }
            }
        }
    }



    @Override
    public void batchDeleteVirtualAccount(IRequest iRequest ,List<VirtualAccount> virtualAccounts){
        String deleteList = JSON.toJSONString(virtualAccounts);
        logger.info("batchDeleteVirtualAccount:   {}", deleteList);
        for(VirtualAccount virtualAccount : virtualAccounts){
            Long contractId = virtualAccount.getContractId();
            logger.info("delete contractId: {}",contractId);
            //如果与合同有关联，先删除合同上的关联并且更新打印版本
            if(contractId != null){
                HlsCusConContract hlsCusConContract = new HlsCusConContract();
                hlsCusConContract.setContractId(contractId);
                conContractService.updateByPrimaryKeySelective(iRequest,hlsCusConContract);
                HlsCusConContract contractNew = conContractService.selectByPrimaryKey(iRequest,hlsCusConContract);
                contractNew.setVirtualAccountId(null);
                contractNew.setVirtualAccountNum(null);
                if(contractNew.getDownloadVersion()==null){
                    contractNew.setDownloadVersion(1L);
                }else{
                    contractNew.setDownloadVersion(contractNew.getDownloadVersion() + 1);
                }
                conContractService.updateByPrimaryKey(iRequest,contractNew);
            }
            Long accountId = virtualAccount.getAccountId();
            this.deleteByPrimaryKey(virtualAccount);
            logger.info("delete accountId: {}",accountId);
        }
    }

    @Override
    public String virtualAccountCheckBeforeSubmit(IRequest iRequest, Map params) {
        JSONObject paramJson = JSONObject.parseObject(params.get("_request_data").toString()).getJSONObject("parameter");
        JSONArray checkData = paramJson.getJSONArray("checkData");
        for(int i=0;i < checkData.size();i++){
            //判断合同号是否重复
            Map map = new HashMap();
            map.put("accountId",checkData.getJSONObject(i).get("account_id"));
            map.put("contractId",checkData.getJSONObject(i).get("contract_id"));
            Long count = virtualAccountMapper.queryRepeatAccount(map);
            if(count > 0L){
                if(StringUtils.isEmpty(checkData.getJSONObject(i).getString("account_id"))){
                    return "NEW";
                }
                return checkData.getJSONObject(i).getString("contract_id_n");
            }
        }
        return "NONE";
    }

    @Override
    public void virtualAccountContractUpdate(IRequest iRequest, Map params) {
        JSONObject paramJson = JSONObject.parseObject(params.get("_request_data").toString()).getJSONObject("parameter");
        JSONArray changeList = paramJson.getJSONArray("changeList");
        for(int i=0;i < changeList.size();i++){
            VirtualAccount virtualAccount = JSONObject.parseObject(changeList.getJSONObject(i).toJSONString(),VirtualAccount.class);
            //将虚拟账号信息反写到相应的合同上
            HlsCusConContract contract = new HlsCusConContract();
            contract.setContractId(virtualAccount.getContractId());
            contract.setVirtualAccountId(virtualAccount.getAccountId());
            contract.setVirtualAccountNum(virtualAccount.getAccountNum());
            HlsCusConContract contractNew = conContractService.selectByPrimaryKey(iRequest,contract);
            if(contractNew.getDownloadVersion()==null){
                contract.setDownloadVersion(1L);
            }else{
                contract.setDownloadVersion(contractNew.getDownloadVersion() + 1);
            }
            conContractService.updateByPrimaryKeySelective(iRequest,contract);
        }
    }

}
