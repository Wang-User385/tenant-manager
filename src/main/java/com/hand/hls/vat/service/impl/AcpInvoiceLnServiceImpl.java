//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.vat.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.util.StringUtil;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractBp;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractBpMapper;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.vat.dto.AcpInvoiceLn;
import com.hand.hls.vat.dto.HlsCusAcpInvoiceLn;
import com.hand.hls.vat.exception.AcpInvoiceException;
import com.hand.hls.vat.mapper.HlsCusAcpInvoiceLnMapper;
import com.hand.hls.vat.service.IAcpInvoiceLnService;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(
        rollbackFor = {Exception.class}
)
public class AcpInvoiceLnServiceImpl extends BaseServiceImpl<HlsCusAcpInvoiceLn> implements IAcpInvoiceLnService {
    @Autowired
    private HlsCusAcpInvoiceLnMapper acpInvoiceLnMapper;
    @Autowired
    private HlsCusConContractMapper conContractMapper;
    @Autowired
    private HlsCusConContractCashflowMapper conContractCashflowMapper;
    @Autowired
    private HlsCusConContractBpMapper conContractBpMapper;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;
    @Autowired
    private JeTrxCommonService jeTrxCommonService;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private HlsCusAcpInvoiceLnMapper hlsCusAcpInvoiceLnMapper;
    public AcpInvoiceLnServiceImpl() {
    }

    protected boolean useSelectiveUpdate() {
        return false;
    }

    public List<HlsCusAcpInvoiceLn> queryAcpInvoiceDetail(HlsCusAcpInvoiceLn dto, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return this.acpInvoiceLnMapper.queryAcpInvoiceDetail(dto);
    }

    public List<HlsCusAcpInvoiceLn> searchInvoiceLnHomeQuery(IRequest iRequest, HlsCusAcpInvoiceLn dto, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return this.acpInvoiceLnMapper.searchInvoiceLnHomeQuery(dto);
    }

    public List<Map> selectInvoiceKind() {
        return this.acpInvoiceLnMapper.selectInvoiceKind();
    }

    private boolean isCodeExist(List<HlsCusAcpInvoiceLn> dto) {
        boolean existFlag = false;

        for(int i = 0; i < dto.size(); ++i) {
            HlsCusAcpInvoiceLn condition;
            AcpInvoiceLn result;
            if("insert".equals(((HlsCusAcpInvoiceLn)dto.get(i)).get__status())) {
                condition = new HlsCusAcpInvoiceLn();
                condition.setInvoiceCode(((HlsCusAcpInvoiceLn)dto.get(i)).getInvoiceCode());
                condition.setInvoiceNumber(((HlsCusAcpInvoiceLn)dto.get(i)).getInvoiceNumber());
                result = (AcpInvoiceLn)this.acpInvoiceLnMapper.selectOne(condition);
                if(result != null) {
                    existFlag = true;
                }
            } else {
                condition = new HlsCusAcpInvoiceLn();
                condition.setInvoiceCode(((HlsCusAcpInvoiceLn)dto.get(i)).getInvoiceCode());
                condition.setInvoiceNumber(((HlsCusAcpInvoiceLn)dto.get(i)).getInvoiceNumber());
                result = (AcpInvoiceLn)this.acpInvoiceLnMapper.selectOne(condition);
                if(result != null && !dto.get(i).getInvoiceLnId().toString().equals(result.getInvoiceLnId().toString())) {
                    existFlag = true;
                }
            }
        }

        return existFlag;
    }
    private boolean isAmount(List<HlsCusAcpInvoiceLn> dto) {
        boolean existFlag = false;

        for(int i = 0; i < dto.size(); ++i) {
            if (dto.get(i).getContractNumber() != null) {
                HlsCusAcpInvoiceLn condition;
                AcpInvoiceLn result;
                if("insert".equals(((HlsCusAcpInvoiceLn)dto.get(i)).get__status())) {
                    Double contractAmount= hlsCusAcpInvoiceLnMapper.queryContractAmount(dto.get(i).getContractId());
                    Double totalAmount = hlsCusAcpInvoiceLnMapper.queryTotalAmountNew(dto.get(i).getContractId());

                    if (totalAmount+dto.get(i).getTotalAmount()>contractAmount) {
                        existFlag = true;
                    }

                } else {

                    Double contractAmount= hlsCusAcpInvoiceLnMapper.queryContractAmount(dto.get(i).getContractId());
                    Double totalAmount = hlsCusAcpInvoiceLnMapper.queryTotalAmount(dto.get(i).getContractId(),dto.get(i).getInvoiceLnId());

                    if (totalAmount+dto.get(i).getTotalAmount()>contractAmount) {
                        existFlag = true;
                    }

                }
            }
        }

        return existFlag;
    }
    public List<HlsCusAcpInvoiceLn> selectImportTempList(Long headerId) {
        List<HlsCusAcpInvoiceLn> tempList = this.acpInvoiceLnMapper.selectImportTempList(headerId);
        tempList.forEach((item) -> {
            item.setErrorMsg(this.validImportRecord(item));
        });
        return tempList;
    }

    private String validImportRecord(HlsCusAcpInvoiceLn item) {
        if(!StringUtils.isAnyBlank(new CharSequence[]{item.getContractIdN(), item.getContractName(), item.getCfItemN(), item.getInvoiceTypeN(), item.getInvoiceCode(), item.getInvoiceNumber(), item.getBpName()}) && item.getRecordDate() != null  && item.getTotalAmount() != null) {
            StringBuffer errMsg = new StringBuffer();
            HlsCusConContract condition = new HlsCusConContract();
            condition.setContractNumber(item.getContractIdN());
            HlsCusConContract result = this.conContractMapper.selectOne(condition);
//            HlsCusPrjProject prj_project =new HlsCusPrjProject();
//            prj_project.setContractNumber(item.getContractIdN());
//            HlsCusPrjProject result = hlsCusPrjProjectMapper.selectOne(prj_project);

            if(result == null || result != null && !result.getContractName().equals(item.getContractName())) {
                errMsg.append("合同信息有误!;");
                return errMsg.toString();
            } else {
                HlsCusAcpInvoiceLn  hlsCusAcpInvoiceLn=new HlsCusAcpInvoiceLn();
                hlsCusAcpInvoiceLn.setContractId(result.getProjectId());
                List<HlsCusAcpInvoiceLn> prjList = acpInvoiceLnMapper.searchInvoiceLnByContractId(hlsCusAcpInvoiceLn);
                 Double totalAmount = 0d;
                 if(prjList.get(0)!=null){
                     totalAmount = prjList.get(0).getTotalAmount();
                 }
                 Double sumAmount=totalAmount+item.getTotalAmount().doubleValue();
                if(result.getContractAmount()-sumAmount <0) {
                   errMsg.append("单一合同的发票金额之和不能大于原合同的金额!;");
                    return errMsg.toString();
              }
               /* Long contractId = result.getContractId();
                List<HlsCusConContractCashflow> cfList = this.conContractCashflowMapper.queryContractOutCashflowByContractId(contractId);*/
/*
                boolean cfFlag = false;
*/

               /* for(int i = 0; i < cfList.size(); ++i) {
                    if(item.getCfItemN().equals(((HlsCusConContractCashflow)cfList.get(i)).getCfItemN())) {
                        cfFlag = true;
                    }
                }*/

              /*  if(!cfFlag) {
                    errMsg.append("应付项目有误!;");
                }*/

                List<Map> kindMap = this.acpInvoiceLnMapper.selectInvoiceKind();
                boolean kindFlag = false;
                Iterator var10 = kindMap.iterator();

                while(var10.hasNext()) {
                    Map map = (Map)var10.next();
                    if(item.getInvoiceTypeN().equals(map.get("meaning"))) {
                        kindFlag = true;
                    }
                }

                if(!kindFlag) {
                    errMsg.append("发票类型不存在!;");
                }
                item.set__status("insert");
                if(this.isCodeExist(Arrays.asList(new HlsCusAcpInvoiceLn[]{item}))) {
                    errMsg.append("该发票已录入!;");
                }

//                if(item.getNetAmount().doubleValue() + item.getTaxAmount().doubleValue() != item.getTotalAmount().doubleValue()) {
//                    errMsg.append("价税合计有误!;");
//                }

//                HlsCusBpMaster bpCondition = new HlsCusBpMaster();
//                bpCondition.setBpName(item.getBpName());
//                if(this.hlsCusBpMasterMapper.select(bpCondition).size() == 0) {
//                    errMsg.append("销售方信息有误;");
//                }
                if (item.getBpName() == null || item.getBpName().length() == 0) {
                    errMsg.append("销售方信息有误;");
                }
                if (item.getInvoiceCode().trim().length() < 10 || item.getInvoiceCode().trim().length() > 12) {
                    errMsg.append("请输入正确的发票代码(10~12位)");
                }
                if (item.getInvoiceNumber().trim().length() < 8 || item.getInvoiceNumber().trim().length() > 12){
                    errMsg.append("请输入正确的发票号码(8~12位)");
                }

                return errMsg.toString();
            }
        } else {
            return "发票信息不完整!请核实后再上传";
        }
    }

    private List<HlsCusAcpInvoiceLn> fillInfo(List<HlsCusAcpInvoiceLn> dto) {
        Iterator var2 = dto.iterator();

        while(var2.hasNext()) {
            HlsCusAcpInvoiceLn item = (HlsCusAcpInvoiceLn)var2.next();
            if(item.getDocumentNumber()==null){
                IRequest requestCtx = null;
                item.setDocumentNumber(fndCodingRuleValuesService.getCodeRuleValue(requestCtx,"AP_INVOICE", "ACP", "ACP", new HashMap()));
            }
            HlsCusPrjProject condition = new HlsCusPrjProject();
            condition.setContractNumber(item.getContractIdN());

//            HlsCusPrjProject result = this.hlsCusPrjProjectMapper.selectOne(condition);
//            item.setContractId(result.getProjectId());
/*
            HlsCusConContract result = (HlsCusConContract)this.conContractMapper.selectOne(condition);
*/
         /*   item.setContractId(result.getContractId());
            Long contractId = result.getContractId();
            List<HlsCusConContractCashflow> cfList = this.conContractCashflowMapper.queryContractOutCashflowByContractId(contractId);

            for(int i = 0; i < cfList.size(); ++i) {
                if(item.getCfItemN().equals(((HlsCusConContractCashflow)cfList.get(i)).getCfItemN())) {
                    item.setCashflowId(((HlsCusConContractCashflow)cfList.get(i)).getCashflowId());
                    item.setCfItem(((HlsCusConContractCashflow)cfList.get(i)).getCfItem());
                    item.setCfType(((HlsCusConContractCashflow)cfList.get(i)).getCfType());
                }
            }
*/
            List<Map> kindMap = this.acpInvoiceLnMapper.selectInvoiceKind();
            if(kindMap != null) {
                Iterator var9 = kindMap.iterator();

                while(var9.hasNext()) {
                    Map map = (Map)var9.next();
                    if(item.getInvoiceTypeN().equals(map.get("meaning"))) {
                        item.setInvoiceType(map.get("code").toString());
                    }
                }
            }

            String taxTypeRateN = item.getTaxTypeRateN();
//            switch (taxTypeRateN) {
//                case "3%":
//                    item.setTaxTypeRate(0.03D);
//                    break;
//                case "6%":
//                    item.setTaxTypeRate(0.06D);
//                    break;
//                case "9%":
//                    item.setTaxTypeRate(0.09D);
//                    break;
//                case "13%":
//                    item.setTaxTypeRate(0.13D);
//                    break;
//                case "5%":
//                    item.setTaxTypeRate(0.05D);
//                    break;
//                case "0%":
//                    item.setTaxTypeRate(0D);
//                    break;
//                default:
            item.setTaxTypeRate(Double.parseDouble(taxTypeRateN));
//            }


            HlsCusBpMaster bpCondition = new HlsCusBpMaster();
            bpCondition.setBpName(item.getBpName());
            if (this.hlsCusBpMasterMapper.select(bpCondition).size() > 0) {
                item.setBpId((this.hlsCusBpMasterMapper.select(bpCondition).get(0)).getBpId());
            }

            item.set__status("insert");
            item.setInvoiceStatus("NEW");
        }

        return dto;
    }

    private boolean hasConfirmRecord(List<HlsCusAcpInvoiceLn> dto) {
        boolean confirmFlag = false;
        Iterator var3 = dto.iterator();

        while(var3.hasNext()) {
            AcpInvoiceLn item = (AcpInvoiceLn)var3.next();
            if("CONFIRM".equals(item.getInvoiceStatus())) {
                confirmFlag = true;
            }
        }

        return confirmFlag;
    }

    public void submit(IRequest iRequest, List<HlsCusAcpInvoiceLn> dto) throws AcpInvoiceException {
        if(this.hasConfirmRecord(dto)) {
            throw new AcpInvoiceException("已确认的发票不能修改!");
        } else if(this.isCodeExist(dto)) {
            throw new AcpInvoiceException("发票号码重复，请重新录入!");
        } else if(this.isAmount(dto)) {
            throw new AcpInvoiceException("单一合同的发票金额之和不能大于原合同的金额!");
        } else {
            dto.forEach((item) -> {
                if("insert".equals(item.get__status())) {
                    item.setInvoiceStatus("NEW");
                }

            });
            this.batchUpdate(iRequest, dto);
        }
    }

    public void delete(List<HlsCusAcpInvoiceLn> dto) throws AcpInvoiceException {
        Iterator var2 = dto.iterator();

        HlsCusAcpInvoiceLn acpInvoiceLn;
        do {
            if(!var2.hasNext()) {
                this.batchDelete(dto);
                return;
            }

            acpInvoiceLn = (HlsCusAcpInvoiceLn)var2.next();
        } while(!"CONFIRM".equals(acpInvoiceLn.getInvoiceStatus()));

        throw new AcpInvoiceException("已确认的发票不能删除!");
    }

    public void reverse(IRequest iRequest, HlsCusAcpInvoiceLn dto) throws AcpInvoiceException {
        if(dto.getInvoiceLnId() != null && !StringUtil.isEmpty(dto.getReversedReason())) {
            HlsCusAcpInvoiceLn acp = (HlsCusAcpInvoiceLn)this.selectByPrimaryKey(iRequest, dto);
            if("Y".equals(acp.getReversedFlag())) {
                throw new AcpInvoiceException("该发票已被反冲，请勿再次反冲!");
            } else {
                dto.setInvoiceStatus("NEW");
                dto.setReversedFlag("Y");
                dto.setCheckStatus("N");
                dto.setReversedDate(new Date());
                this.acpInvoiceLnMapper.updateByPrimaryKeySelective(dto);
            }
        } else {
            throw new AcpInvoiceException("请填写反冲原因!");
        }
    }

    public List<HlsCusAcpInvoiceLn> confirm(IRequest iRequest, List<HlsCusAcpInvoiceLn> dto) throws AcpInvoiceException {
        Iterator var3 = dto.iterator();

        while(var3.hasNext()) {
            HlsCusAcpInvoiceLn acpInvoiceLn = (HlsCusAcpInvoiceLn)var3.next();
            if(!"NEW".equals(acpInvoiceLn.getInvoiceStatus())) {
                throw new AcpInvoiceException("发票状态错误!");
            }

            acpInvoiceLn.setCheckStatus("Y");
            acpInvoiceLn.setInvoiceStatus("CONFIRM");
            this.acpInvoiceLnMapper.updateByPrimaryKey(acpInvoiceLn);
//            JeTrxCommonService var10000 = this.jeTrxCommonService;
//            AbstractJeTrxService jeTrxService = (AbstractJeTrxService)JeTrxCommonService.map.get("INPUT_INVOICE");
//            Map params = new HashMap();
//            params.put("jeTrxId", acpInvoiceLn.getInvoiceLnId());
//            params.put("companyId", iRequest.getCompanyId());
//            params.put("contractId", acpInvoiceLn.getContractId());
//            params.put("sourceDoc", "CON_CONTRACT");
//            jeTrxService.process(iRequest, params);
        }

        return dto;
    }

    public void importConfirm(IRequest iRequest, List<HlsCusAcpInvoiceLn> dto) throws AcpInvoiceException {
        long errorCount = dto.stream().filter((o) -> {
            return StringUtils.isNoneBlank(new CharSequence[]{o.getErrorMsg()});
        }).count();
        if(errorCount > 0L) {
            throw new AcpInvoiceException("数据不合规范，请修改后再导入!");
        } else {
            List<HlsCusAcpInvoiceLn> list = this.fillInfo(dto);
            this.batchUpdate(iRequest, list);
        }
    }
}
