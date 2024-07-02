//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusHlsBpMasterBankAccount;
import com.hand.hls.cont.service.HlsBpMasterBankAccountService;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.csh.service.CshTransactionService;
import com.hand.hls.csh.service.IMainCshTransactionService;
import com.hand.hls.csh.service.IMainCshWriteOffService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.utils.MathUtil;
import com.hand.hls.utils.ResMessageException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(
        rollbackFor = {Exception.class}
)
public class MainCshTransactionServiceImpl extends BaseServiceImpl<HlsCusCshTransaction> implements IMainCshTransactionService {
    public static final String PROPERTY_DOCUMENT_TYPE_FCT = "FCT";
    @Autowired
    HlsCusCshWriteOffMapper cshWriteOffMapper;
    @Autowired
    CshTransactionService cshTransactionService;
    @Autowired
    HlsCusCshTransactionMapper cshTransactionMapper;
    @Autowired
    private FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private IMainCshWriteOffService mainCshWriteOffService;
    @Autowired
    private JeTrxCommonService jeTrxCommonService;
    @Autowired(
            required = false
    )
    HlsBpMasterBankAccountService hlsBpMasterBankAccountService;

    public MainCshTransactionServiceImpl() {
    }

    public HlsCusCshTransaction reverseCshTransaction(IRequest iRequest, Long transactionId, Date reverseDate, String description) throws Exception {
        HlsCusCshTransaction cshTransaction = (HlsCusCshTransaction)this.cshTransactionMapper.selectByPrimaryKey(transactionId);
        this.databaseLockProvider.lock(cshTransaction);
        if (reverseDate == null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String cshDateStr = simpleDateFormat.format(new Date());
            cshTransaction.setTransactionDate(simpleDateFormat.parse(cshDateStr));
        } else {
            cshTransaction.setTransactionDate(reverseDate);
        }

        cshTransaction.setTransactionAmount(MathUtil.mul(-1.0D, cshTransaction.getTransactionAmount()));
        cshTransaction.setTransactionId((Long)null);
        cshTransaction.setReversedFlag("R");
        cshTransaction.setReversedTrxId((Long)null);
        cshTransaction.setReversedDate((Date)null);
        if (description != null) {
            cshTransaction.setDescription(description);
        } else {
            cshTransaction.setDescription("反冲现金事务：" + cshTransaction.getDescription());
        }

        cshTransaction.setWriteOffFlag("NOT");
        cshTransaction.setWriteOffAmount((Double)null);
        cshTransaction.setFullWriteOffDate((Date)null);
        Map<String, String> params = new HashMap();
        cshTransaction.setTransactionNum(this.codingRuleValuesService.getCodeRuleValue(iRequest, cshTransaction.getTransactionCategory(), cshTransaction.getTransactionType(), cshTransaction.getBusinessType(), params));
        cshTransaction = (HlsCusCshTransaction)this.cshTransactionService.insertSelective(iRequest, cshTransaction);
        return cshTransaction;
    }

    public void updateCshTrxAfterTrxReverse(Long cshTransactionId, Date reverseDate) throws Exception {
        HlsCusCshTransaction cshTransaction = new HlsCusCshTransaction();
        cshTransaction.setTransactionId(cshTransactionId);
        cshTransaction.setReversedDate(reverseDate);
        cshTransaction.setReversedFlag("W");
        this.cshTransactionMapper.updateCshTrByPrimaryKey(cshTransaction);
    }

    public void updateCshTrxAfterWriteOffReverse(Long cshTransactionId, Long cshWriteOffId) throws Exception {
        HlsCusCshTransaction cshTransaction = this.cshTransactionMapper.queryDetailById(cshTransactionId);
        //this.databaseLockProvider.lock(cshTransaction);
        HlsCusCshWriteOff cshWriteOff = (HlsCusCshWriteOff)this.cshWriteOffMapper.selectByPrimaryKey(cshWriteOffId);
        if (cshTransaction.getWriteOffAmount() == null) {
            cshTransaction.setWriteOffAmount(0.0D);
        }

        if (cshWriteOff.getWriteOffDueAmount() == null) {
            cshWriteOff.setWriteOffDueAmount(0.0D);
        }

        Double sumAmount = cshTransaction.getWriteOffAmount() - cshWriteOff.getWriteOffDueAmount();
        if ((new BigDecimal(sumAmount)).compareTo(new BigDecimal(0)) == 0) {
            cshTransaction.setWriteOffFlag("NOT");
            cshTransaction.setWriteOffAmount(sumAmount);
        } else if ((new BigDecimal(sumAmount)).compareTo(new BigDecimal(cshTransaction.getTransactionAmount())) < 0) {
            cshTransaction.setWriteOffFlag("PARTIAL");
            cshTransaction.setWriteOffAmount(sumAmount);
        } else {
            if ((new BigDecimal(sumAmount)).compareTo(new BigDecimal(cshTransaction.getTransactionAmount())) != 0) {
                throw new BeyondAmountLimitException();
            }

            cshTransaction.setWriteOffFlag("FULL");
            cshTransaction.setWriteOffAmount(sumAmount);
            cshTransaction.setFullWriteOffDate(cshWriteOff.getWriteOffDate());
        }

        this.cshTransactionMapper.updateCshTrByPrimaryKey(cshTransaction);
    }

    public void updateCshTrxAfterWriteOff(Long cshTransactionId, HlsCusCshWriteOff cshWriteOff) throws Exception {
        HlsCusCshTransaction cshTransaction = this.cshTransactionMapper.queryDetailById(cshTransactionId);
        //this.databaseLockProvider.lock(cshTransaction);
        if (cshTransaction.getWriteOffAmount() == null) {
            cshTransaction.setWriteOffAmount(0.0D);
        }

        if (cshWriteOff.getWriteOffDueAmount() == null) {
            cshWriteOff.setWriteOffDueAmount(0.0D);
        }

        Double sumAmount = cshTransaction.getWriteOffAmount() + cshWriteOff.getWriteOffDueAmount();
        if ((new BigDecimal(sumAmount)).compareTo(new BigDecimal(0)) == 0) {
            cshTransaction.setWriteOffFlag("NOT");
            cshTransaction.setWriteOffAmount(sumAmount);
        } else if ((new BigDecimal(sumAmount)).compareTo(new BigDecimal(cshTransaction.getTransactionAmount())) < 0) {
            cshTransaction.setWriteOffFlag("PARTIAL");
            cshTransaction.setWriteOffAmount(sumAmount);
        } else {
           /* if ((new BigDecimal(sumAmount)).compareTo(new BigDecimal(cshTransaction.getTransactionAmount())) != 0) {
                throw new BeyondAmountLimitException();
            }
*/
            cshTransaction.setWriteOffFlag("FULL");
            cshTransaction.setWriteOffAmount(sumAmount);
            cshTransaction.setFullWriteOffDate(cshWriteOff.getWriteOffDate());
        }

        this.cshTransactionMapper.updateCshTrByPrimaryKey(cshTransaction);
    }

    public void createCshTrxPostJe(IRequest iRequest, HlsCusCshTransaction cshTransaction) {
        Map transactionMap = new HashMap();
        AbstractJeTrxService JetrxService = (AbstractJeTrxService)JeTrxCommonService.map.get("CSH_TRANSACTION");
        if (JetrxService != null) {
            transactionMap.put("jeTrxId", cshTransaction.getTransactionId());
            transactionMap.put("companyId", iRequest.getCompanyId());
            transactionMap.put("contractId", cshTransaction.getContractId());
            if ("FCT".equals(cshTransaction.getDocumentType())) {
                transactionMap.put("sourceDoc", "FCT_CONTRACT");
            } else {
                transactionMap.put("sourceDoc", "CON_CONTRACT");
            }

            JetrxService.process(iRequest, transactionMap);
        }

    }

    public void createTrxWriteOffAllJe(IRequest iRequest, HlsCusCshTransaction cshTransaction) {
        Map transactionMap = new HashMap();
        JeTrxCommonService var10000 = this.jeTrxCommonService;
        AbstractJeTrxService JetrxService = (AbstractJeTrxService)JeTrxCommonService.map.get("CSH_CONSOLIDATION_REVERSE");
        if (JetrxService != null) {
            transactionMap.put("jeTrxId", cshTransaction.getTransactionId());
            transactionMap.put("companyId", iRequest.getCompanyId());
            transactionMap.put("contractId", cshTransaction.getContractId());
            transactionMap.put("sourceDoc", "CON_CONTRACT");
            JetrxService.process(iRequest, transactionMap);
        }

    }

    public void reverseCshTransactionMain(IRequest iRequest, List<HlsCusCshTransaction> cshTransactionList, Date reverseDate, String description) throws Exception {
        Iterator var5 = cshTransactionList.iterator();

        while(var5.hasNext()) {
            HlsCusCshTransaction hlsCusCshTransaction = (HlsCusCshTransaction)var5.next();
            this.databaseLockProvider.lock(hlsCusCshTransaction);
            if ("Y".equals(hlsCusCshTransaction.getReversedFlag())) {
                throw new ResMessageException("已反冲无需重复反冲！");
            }

            if (hlsCusCshTransaction.getReversedDate().compareTo(hlsCusCshTransaction.getTransactionDate()) == -1) {
                throw new ResMessageException("反冲日期不能早于现金事务的日期！");
            }

            HlsCusCshWriteOff hlsCusCshWriteOff = new HlsCusCshWriteOff();
            hlsCusCshWriteOff.setCshTransactionId(hlsCusCshTransaction.getTransactionId());
            List<HlsCusCshWriteOff> cshWriteOffs = this.cshWriteOffMapper.select(hlsCusCshWriteOff);
            if (reverseDate == null && hlsCusCshTransaction.getReversedDate() != null) {
                reverseDate = hlsCusCshTransaction.getReversedDate();
            }

            if (description == null && hlsCusCshTransaction.getDescription() != null) {
                description = hlsCusCshTransaction.getDescription();
            }

            HlsCusCshTransaction reverseCshTrx = this.reverseCshTransaction(iRequest, hlsCusCshTransaction.getTransactionId(), reverseDate, description);

            for(int i = 0; i < cshWriteOffs.size(); ++i) {
                HlsCusCshWriteOff cshWriteOff = (HlsCusCshWriteOff)cshWriteOffs.get(i);
                this.mainCshWriteOffService.reverseCshWriteOffNew(iRequest, cshWriteOff.getWriteOffId(), reverseDate, description, reverseCshTrx.getTransactionId());
            }

            if (CollectionUtils.isNotEmpty(cshWriteOffs) && ((HlsCusCshWriteOff)cshWriteOffs.get(0)).getWriteOffType().startsWith("FCT")) {
                reverseCshTrx.setDocumentType("FCT");
            }

            this.createCshTrxPostJe(iRequest, reverseCshTrx);
            this.updateCshTrxAfterTrxReverse(hlsCusCshTransaction.getTransactionId(), reverseDate);
            this.createTrxWriteOffAllJe(iRequest, hlsCusCshTransaction);
        }

    }

    public void checkCshTransaction(Long cshTransactionId, Double writeOffAmount) throws BeyondAmountLimitException {
        HlsCusCshTransaction transaction = this.cshTransactionMapper.queryDetailById(cshTransactionId);
        if (transaction.getWriteOffAmount() == null) {
            transaction.setWriteOffAmount(0.0D);
        }

        Double sum = MathUtil.add(writeOffAmount, transaction.getWriteOffAmount());
        if (sum.compareTo(transaction.getTransactionAmount()) == 1) {
            throw new BeyondAmountLimitException();
        }
    }

    public void cshTransactionMain(IRequest iRequest, List<HlsCusCshTransaction> cshTransactionList) throws Exception {
        if (!CollectionUtils.isEmpty(cshTransactionList)) {
            for(int i = 0; i < cshTransactionList.size(); ++i) {
                String postFlag = ((HlsCusCshTransaction)cshTransactionList.get(i)).getPostedFlag();
                HlsCusCshTransaction transaction = ((IMainCshTransactionService)this.self()).createCshTransaction(iRequest, (HlsCusCshTransaction)cshTransactionList.get(i));
                if ("Y".equals(postFlag)) {
                    ((IMainCshTransactionService)this.self()).postCshTransaction(iRequest, transaction);
                }
            }
        }

    }

    public HlsCusCshTransaction createCshTransaction(IRequest requestCtx, HlsCusCshTransaction hlsCusCshTransaction) {
        if (hlsCusCshTransaction.getTransactionId() != null) {
            ((IMainCshTransactionService)this.self()).updateByPrimaryKeySelective(requestCtx, hlsCusCshTransaction);
        } else {
            if (hlsCusCshTransaction.getBpBankAccountId() == null) {
                HlsCusHlsBpMasterBankAccount ba = new HlsCusHlsBpMasterBankAccount();
                ba.setBpId(hlsCusCshTransaction.getBpId());
                ba.setBankAccountName(hlsCusCshTransaction.getBpBankAccountName());
                ba.setBankAccountNum(hlsCusCshTransaction.getBpBankAccountNum());
                ba.setBankBranchName(hlsCusCshTransaction.getBpBankBranchName());
                ba.setBankFullName(hlsCusCshTransaction.getBpBankName());
//                this.hlsBpMasterBankAccountService.insertSelective(requestCtx, ba);
                hlsCusCshTransaction.setBpBankAccountId(ba.getBankAccountId());
            }

            Map<String, String> params = new HashMap();
            hlsCusCshTransaction.setTransactionCategory("CSH_TRANSACTION");
            hlsCusCshTransaction.setTransactionType("RECEIPT");
            hlsCusCshTransaction.setBusinessType("RECEIPT");
            hlsCusCshTransaction.setTransactionNum(this.codingRuleValuesService.getCodeRuleValue(requestCtx, hlsCusCshTransaction.getTransactionCategory(), hlsCusCshTransaction.getTransactionType(), hlsCusCshTransaction.getBusinessType(), params));
            hlsCusCshTransaction.setPenaltyCalcDate(hlsCusCshTransaction.getTransactionDate());
            hlsCusCshTransaction.setCompanyId(hlsCusCshTransaction.getCompanyId());
            hlsCusCshTransaction.setReversedFlag("N");
            hlsCusCshTransaction.setPostedFlag("N");
            hlsCusCshTransaction.setWriteOffFlag("NOT");
            if (hlsCusCshTransaction.getWriteOffAmount() == null) {
                hlsCusCshTransaction.setWriteOffAmount(0.0D);
            }

            ((IMainCshTransactionService)this.self()).insertSelective(requestCtx, hlsCusCshTransaction);
        }

        return hlsCusCshTransaction;
    }

    public void postCshTransaction(IRequest requestCtx, HlsCusCshTransaction transaction) throws ResMessageException {
        this.databaseLockProvider.lock(transaction);
        transaction = (HlsCusCshTransaction)((IMainCshTransactionService)this.self()).selectByPrimaryKey(requestCtx, transaction);
        if ("Y".equals(transaction.getPostedFlag())) {
            throw new ResMessageException("已过账无需重复过账！");
        } else {
            transaction.setPostedFlag("Y");
            ((IMainCshTransactionService)this.self()).updateByPrimaryKeySelective(requestCtx, transaction);
            ((IMainCshTransactionService)this.self()).createCshTrxPostJe(requestCtx, transaction);
        }
    }
}
