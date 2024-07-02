package com.hand.hls.csh.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.ConContractMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.dto.HlsCusWriteOffMatch;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.mapper.HlsCusWriteOffMatchMapper;
import com.hand.hls.csh.service.HlsCusIWriteOffMatchService;
import com.hand.hls.fnd.dto.HlsCashflowItem;
import com.hand.hls.fnd.mapper.HlsCfItemMapper;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.mapper.SysUserMapper;
import com.hand.hls.utils.HlsCusConstant;
import hls.core.sys.event.service.SysEventService;
import hls.core.sys.event.utils.SysEventCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusWriteOffMatchServiceImpl extends BaseServiceImpl<HlsCusWriteOffMatch> implements HlsCusIWriteOffMatchService {


    @Autowired
    private HlsCusWriteOffMatchMapper hlsCusWriteOffMatchMapper;

    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;

    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;

    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private HlsCusCshTransactionMapper transactionMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private HlsCfItemMapper hlsCfItemMapper;

    private static String RECEIPT_CREDIT = "RECEIPT_CREDIT";
    private static String RECEIPT_ADVANCE_RECEIPT = "RECEIPT_ADVANCE_RECEIPT";
    private static String RECEIPT_DEPOSIT = "RECEIPT_DEPOSIT";
    private static String RECEIPT_DEPOSIT_POOL = "RECEIPT_DEPOSIT_POOL";


    @Override
    public void saveWriteOffMatch(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffList, HttpSession httpSession) throws BeyondAmountLimitException {
        Long companyId = (Long) httpSession.getAttribute("companyId");

        //cshTransaction.setCompanyId(companyId);
        List<HlsCusWriteOffMatch> writeOffMatchList = new ArrayList<>();

        for (HlsCusCshWriteOff cshWriteOff : cshWriteOffList) {
            HlsCusWriteOffMatch writeOffMatch = setHlsCusWriteOffMatch(iRequest, cshWriteOff);
            writeOffMatchList.add(writeOffMatch);
        }


        HlsCusCshTransaction cshTransaction = new HlsCusCshTransaction();
        cshTransaction.setTransactionId(cshWriteOffList.get(0).getCshTransactionId());
        cshTransaction.setBpId(cshWriteOffList.get(0).getBpId());
        transactionMapper.updateByPrimaryKeySelective(cshTransaction);

        cshTransaction = transactionMapper.selectByPrimaryKey(cshTransaction);

        hlsCusWriteOffMatchMapper.updateMatchFlag(cshTransaction.getTransactionId());
        HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
        hlsCusBpMaster.setBpId(cshTransaction.getBpId());
        hlsCusBpMaster = hlsCusBpMasterMapper.selectByPrimaryKey(hlsCusBpMaster);
        //插入匹配消息
        String dateStr = "";
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        if (cshTransaction.getWriteOffDate() != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            dateStr = format.format(cshTransaction.getWriteOffDate());
        }

        //总核销加匹配的金额
        Double sum = hlsCusWriteOffMatchMapper.selectMatchAmount(cshTransaction.getTransactionId());
        if (sum > cshTransaction.getTransactionAmount()) {
            throw new BeyondAmountLimitException();
        }
        DecimalFormat df = new DecimalFormat("###,##0.00");
        Double amount = hlsCusWriteOffMatchMapper.selectMatchAmountSum(cshTransaction.getTransactionId());
        for (HlsCusWriteOffMatch match : writeOffMatchList) {
            StringBuilder builder = new StringBuilder();
            if ("RECEIPT_ADVANCE_RECEIPT".equals(match.getWriteOffType())) {
                builder.append(hlsCusBpMaster.getBpName()).append(dateStr).append("的").append(df.format(cshTransaction.getTransactionAmount())).append("元收款,收款编号为")
                        .append(cshTransaction.getTransactionNum()).append("已匹配预收款类型")
                        .append("￥").append(df.format(match.getCshWriteOffAmount()))
                        .append("，请及时进行复核核销。");
            } else {
                builder.append(hlsCusBpMaster.getBpName()).append(dateStr).append("的")
                        .append(df.format(cshTransaction.getTransactionAmount())).append("元收款,收款编号为")
                        .append(cshTransaction.getTransactionNum()).append("，已匹配到合同")
                        .append(match.getContractNumber()).append("第").append(match.getTimes())
                        .append("期").append(match.getCfItemDesc()).append("￥").append(df.format(match.getCshWriteOffAmount()))
                        .append("，请及时进行复核核销。");
            }
            paramsEvent.put("message", builder.toString());
            paramsEvent.put("noticeTitle", "收款匹配");
            paramsEvent.put("noticeType", "NOTICE");
            paramsEvent.put("url", "");
            Map<String, Object> params = new HashMap<>();
            paramsEvent.put("eventCode", SysEventCodeUtil.PROPERTY_CSH_WRITE_OFF_MATCH_CONFIRM);
            paramsEvent.put("level", 1L);
            List<SysUser> sysUsers = sysUserMapper.selectContractChiefUnitUser(companyId, match.getContractId(), match.getWriteOffDocCategory());
            IRequest request = RequestHelper.newEmptyRequest();
            for (SysUser user : sysUsers) {
                request.setUserId(user.getUserId());
                sysEventService.eventSave(request, match.getWriteOffMatchId(), SysEventCodeUtil.PROPERTY_CSH_WRITE_OFF_MATCH_CONFIRM, SysEventCodeUtil.PROPERTY_CSH_WRITE_OFF_MATCH_CONFIRM, "BAC", SysEventCodeUtil.PROPERTY_CSH_WRITE_OFF_MATCH_CONFIRM, "P2D", paramsEvent);
            }
        }
    }

    private HlsCusWriteOffMatch setHlsCusWriteOffMatch(IRequest iRequest, HlsCusCshWriteOff cshWriteOff) {

        HlsCusWriteOffMatch hlsCusWriteOffMatch = new HlsCusWriteOffMatch();
        hlsCusWriteOffMatch.setWriteOffDate(cshWriteOff.getWriteOffDate());
        hlsCusWriteOffMatch.setWriteOffType(cshWriteOff.getWriteOffType());
        hlsCusWriteOffMatch.setCshTransactionId(cshWriteOff.getTransactionId());
        hlsCusWriteOffMatch.setCshWriteOffAmount(cshWriteOff.getWriteOffDueAmount());
        hlsCusWriteOffMatch.setWriteFlag("N");
        hlsCusWriteOffMatch.setCshTransactionId(cshWriteOff.getCshTransactionId());
        hlsCusWriteOffMatch.setCompanyId(cshWriteOff.getCompanyId());
        hlsCusWriteOffMatch.setBpId(cshWriteOff.getBpId());
        hlsCusWriteOffMatch.setBpName(cshWriteOff.getBpName());
        hlsCusWriteOffMatch.setCurrencyCode(cshWriteOff.getCurrencyCode());
        hlsCusWriteOffMatch.setCashflowId(cshWriteOff.getCashflowId());
        hlsCusWriteOffMatch.setContractId(cshWriteOff.getContractId());
        hlsCusWriteOffMatch.setTimes(cshWriteOff.getTimes());
        hlsCusWriteOffMatch.setCfItem(cshWriteOff.getCfItem());
        hlsCusWriteOffMatch.setCfType(cshWriteOff.getCfType());
        hlsCusWriteOffMatch.setWriteOffDueAmount(cshWriteOff.getWriteOffDueAmount());
        hlsCusWriteOffMatch.setWriteOffPrincipal(cshWriteOff.getWriteOffPrincipal());
        hlsCusWriteOffMatch.setWriteOffInterest(cshWriteOff.getWriteOffInterest());
        hlsCusWriteOffMatch.setWriteOffDocCategory(cshWriteOff.getWriteOffDocCategory());

        if (hlsCusWriteOffMatch.getWriteOffMatchId() != null) {
            HlsCusWriteOffMatch match = hlsCusWriteOffMatchMapper.selectByPrimaryKey(hlsCusWriteOffMatch.getWriteOffMatchId());
            if (match == null || HlsCusConstant.FLAG.Y.equals(match.getWriteFlag())) {
                throw new RuntimeException("有他人同时操作单据请刷新!");
            } else {
                self().updateByPrimaryKeySelective(iRequest, hlsCusWriteOffMatch);
            }
        } else {
            hlsCusWriteOffMatch.setImportFlag("N");
            self().insertSelective(iRequest, hlsCusWriteOffMatch);
        }
        hlsCusWriteOffMatch = self().selectByPrimaryKey(iRequest, hlsCusWriteOffMatch);

        if(cshWriteOff.getContractId() != null) {
            HlsCusConContract conContract = hlsCusConContractMapper.selectByPrimaryKey(cshWriteOff.getContractId());
            hlsCusWriteOffMatch.setContractNumber(conContract.getContractNumber());
        }

        if(cshWriteOff.getCfItem()!= null && cshWriteOff.getCfType() != null){
            HlsCashflowItem cfItem = new HlsCashflowItem();
            cfItem.setCfItem(cshWriteOff.getCfItem().toString());
            cfItem.setCfType(cshWriteOff.getCfType().toString());
            cfItem = hlsCfItemMapper.selectByPrimaryKey(cfItem);
            hlsCusWriteOffMatch.setCfItemDesc(cfItem.getDescription());
        }
        return hlsCusWriteOffMatch;
    }

    @Override
    public void updateWriteOffMatchBack(IRequest iRequest, HlsCusCshTransaction cshTransaction) {
        HlsCusWriteOffMatch writeOffMatch = new HlsCusWriteOffMatch();
        writeOffMatch.setTransactionId(cshTransaction.getTransactionId());
        writeOffMatch.setCshTransactionId(cshTransaction.getTransactionId());
        List<HlsCusWriteOffMatch> hlsCusWriteOffMatches = hlsCusWriteOffMatchMapper.selectWriteOffMatch(writeOffMatch);

        HlsCusCshTransaction transaction = transactionMapper.selectByPrimaryKey(cshTransaction.getTransactionId());
        //SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        DecimalFormat df = new DecimalFormat("###,##0.00");
        for (HlsCusWriteOffMatch hlsCusWriteOffMatch : hlsCusWriteOffMatches) {
            if (hlsCusWriteOffMatch.getWriteOffMatchId() != null) {
                //hlsCusWriteOffMatch.setWriteFlag("R");
                //self().updateByPrimaryKeySelective(iRequest, hlsCusWriteOffMatch);
                //根据兴业界面逻辑 退回的记录 直接在核销匹配表中删除
                self().deleteByPrimaryKey(hlsCusWriteOffMatch);

                Map<String, Object> paramsEvent = new HashMap<>();
                StringBuilder builder = new StringBuilder();
                if ("RECEIPT_ADVANCE_RECEIPT".equals(hlsCusWriteOffMatch.getWriteOffType())) {
                    builder.append("收款编号").append(transaction.getTransactionNum()).append("，匹配预收款款类型￥")
                            .append(df.format(hlsCusWriteOffMatch.getCshWriteOffAmount())).append("核销退回，请重新匹配确认。");
                } else {
                    builder.append("收款编号").append(transaction.getTransactionNum()).append(",匹配到合同").append(hlsCusWriteOffMatch.getContractNumber())
                            .append("第").append(hlsCusWriteOffMatch.getTimes()).append("期").append(hlsCusWriteOffMatch.getCfItemDesc()).append("￥")
                            .append(df.format(hlsCusWriteOffMatch.getCshWriteOffAmount())).append("，核销退回，请重新匹配确认。");
                }
                paramsEvent.put("message", builder.toString());
                paramsEvent.put("noticeTitle", "收款匹配退回");
                paramsEvent.put("noticeType", "NOTICE");
                paramsEvent.put("url", "");
                Map<String, Object> params = new HashMap<>();
                params.put("eventCode", SysEventCodeUtil.PROPERTY_CSH_WRITE_OFF_MATCH_REJECT);
                paramsEvent.put("level", 1L);
                //运营岗
                List<String> positionCodeList = new ArrayList<>();
                positionCodeList.add("05530");
                positionCodeList.add("00320");
                List<SysUser> sysUsers = sysUserMapper.selectUserByPositionCode(transaction.getCompanyId(), positionCodeList);
                IRequest request = RequestHelper.newEmptyRequest();
                //给所有的资金收付岗发送消息
                for (SysUser user : sysUsers) {
                    request.setUserId(user.getUserId());
                    sysEventService.eventSave(request, cshTransaction.getTransactionId(), SysEventCodeUtil.PROPERTY_CSH_WRITE_OFF_MATCH_REJECT, SysEventCodeUtil.PROPERTY_CSH_WRITE_OFF_MATCH_REJECT, "BAC", SysEventCodeUtil.PROPERTY_CSH_WRITE_OFF_MATCH_REJECT, "P2D", paramsEvent);
                }
            }
        }
    }

    @Override
    public List<HlsCusWriteOffMatch> selectWriteOffMatch(IRequest iRequest, HlsCusWriteOffMatch hlsCusWriteOffMatch, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusWriteOffMatchMapper.selectWriteOffMatch(hlsCusWriteOffMatch);
    }

    @Override
    public void updateWriteOffMatchCheck(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffs) {
        HlsCusWriteOffMatch hlsCusWriteOffMatch = new HlsCusWriteOffMatch();
        for (HlsCusCshWriteOff cusCshWriteOff : cshWriteOffs) {
            if (cusCshWriteOff.getWriteOffMatchId() != null) {
                hlsCusWriteOffMatch.setWriteOffMatchId(cusCshWriteOff.getWriteOffMatchId());
                hlsCusWriteOffMatch.setWriteFlag("Y");
                hlsCusWriteOffMatch.setWriteOffId(cusCshWriteOff.getWriteOffId());
                self().updateByPrimaryKeySelective(iRequest, hlsCusWriteOffMatch);
            }
        }
    }


    @Override
    public int selectMatchCount(Long cshTransactionId) {
        return hlsCusWriteOffMatchMapper.selectMatchCount(cshTransactionId);
    }


    @Override
    public void reversedWriteOffMatch(IRequest iRequest, HlsCusCshWriteOff cusCshWriteOff) {
        if (cusCshWriteOff.getWriteOffId() != null) {
            HlsCusWriteOffMatch hlsCusWriteOffMatch = new HlsCusWriteOffMatch();
            hlsCusWriteOffMatch.setWriteOffId(cusCshWriteOff.getWriteOffId());
            List<HlsCusWriteOffMatch> writeOffMatches = hlsCusWriteOffMatchMapper.select(hlsCusWriteOffMatch);
            if (writeOffMatches.size() == 1) {
                writeOffMatches.get(0).setWriteFlag("R");
                self().updateByPrimaryKeySelective(iRequest, writeOffMatches.get(0));
            }
        }

    }
}
