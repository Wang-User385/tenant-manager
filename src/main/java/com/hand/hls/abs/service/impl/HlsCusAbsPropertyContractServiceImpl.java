package com.hand.hls.abs.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.HlsCusAbsPropertyContract;
import com.hand.hls.abs.mapper.HlsCusAbsPropertyContractMapper;
import com.hand.hls.abs.service.HlsCusAbsPropertyContractService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.fct.dto.HlsCusFctContract;
import com.hand.hls.fct.dto.HlsCusFctQuotationCashflow;
import com.hand.hls.fct.service.HlsCusFctQuotationCashflowService;
import com.hand.hls.fin.service.HlsCusFctContractService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.utils.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsPropertyContractServiceImpl extends BaseServiceImpl<HlsCusAbsPropertyContract> implements HlsCusAbsPropertyContractService {
    @Autowired
    private HlsCusAbsPropertyContractMapper hlsCusAbsPropertyContractMapper;
    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;
    @Autowired
    private HlsCusFctQuotationCashflowService hlsCusFctQuotationCashflowService;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;


    @Override
    public List<HlsCusAbsPropertyContract> query(IRequest request, HlsCusAbsPropertyContract hlsCusAbsPropertyContract, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusAbsPropertyContractMapper.query(hlsCusAbsPropertyContract);
    }

    @Override
    public List<HlsCusAbsPropertyContract> queryHome(IRequest request, HlsCusAbsPropertyContract hlsCusAbsPropertyContract, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusAbsPropertyContractMapper.queryHome(hlsCusAbsPropertyContract);
    }

    @SuppressWarnings("all")
    @Override
    public List<Map<String, Object>> queryContract(IRequest request, HlsCusAbsPropertyContract hlsCusAbsPropertyContract, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        if(StringUtils.isNotEmpty(hlsCusAbsPropertyContract.getParam())){
            String[] ss = hlsCusAbsPropertyContract.getParam().split(",");
            List<Long> list = new ArrayList<>();
            for(String s : ss){
                if(StringUtils.isNotEmpty(s)){
                    list.add(Long.valueOf(s));
                }
            }
            Long[] ls = new Long[list.size()];
            hlsCusAbsPropertyContract.setContractIds(list.toArray(ls));
        }
        // 先查出该产品对应的项目下所有合同
        List<HlsCusAbsPropertyContract> list = hlsCusAbsPropertyContractMapper.queryContract(hlsCusAbsPropertyContract);
        List<Map<String, Object>> maps = new ArrayList<>(list.size());
        if(CollectionUtils.isNotEmpty(list)){
            for (HlsCusAbsPropertyContract contract : list) {
                contract.setDateStr(hlsCusAbsPropertyContract.getDateStr());
                List<Map<String, Object>> map = hlsCusAbsPropertyContractMapper.queryContractForProductContract(contract);
                if (CollectionUtils.isNotEmpty(map)) {
                    maps.add(map.get(0));
                }
            }
        }
        LocalDate baseDate = LocalDate.parse(hlsCusAbsPropertyContract.getDateStr());  // 封包日
        LocalDate dueDateBegin = LocalDate.parse(hlsCusAbsPropertyContract.getDateStr2());  // 发行日
        return hlsCusConContractService.calcContractAmount(maps, baseDate, dueDateBegin, request);
//        return calcContractAmount(maps, baseDate, request);
        // 在这里将下面代码段封装成一个方法,若出现问题则使用注释中的代码
        /*for (Map<String, Object> contract : maps) {
            if(StringUtils.equalsIgnoreCase("CON_CONTRACT", (String)contract.get("documentType"))){
                HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
                hlsCusConContractCashflow.setContractId((Long)contract.get("contractId"));
                List<HlsCusConContractCashflow> cashflowList = hlsCusConContractCashflowService.select(request, hlsCusConContractCashflow, 1, 99999);
                cashflowList = cashflowList.stream().filter(cashflow -> cashflow.getCfItem() == 1L)
                        .sorted(Comparator.comparing(HlsCusConContractCashflow::getDueDate)).collect(Collectors.toList());
                for (HlsCusConContractCashflow cashflow : cashflowList) {
                    if(baseDate.isEqual(DateUtils.format(cashflow.getDueDate()))){
                        // 如果封包日正好等于某一期的应收日期,那么上面查询数据不需要修正
                        break;
                    }
                    // 封包日小于某一期应收日期时, 因为集合是根据应收日期顺序排序后的,所以第一次进入判断的就是距离封包日最近的未收的应收日期
                    if(baseDate.isBefore(DateUtils.format(cashflow.getDueDate()))){
                        int index = cashflowList.indexOf(cashflow);
                        if(index > 0){
                            // 计算出上一期与这一期的天数差
                            long days = DateUtils.between(cashflowList.get(index-1).getDueDate(),cashflow.getDueDate());
                            // 计算出封包日与这一期的天数差
                            long days2 = DateUtils.between(DateUtils.format(baseDate), cashflow.getDueDate());

                            BigDecimal uncollectedInterest = new BigDecimal(cashflow.getInterest()-cashflow.getReceivedInterest());
                            uncollectedInterest = uncollectedInterest.multiply(new BigDecimal(days2)).divide(new BigDecimal(days),2,BigDecimal.ROUND_HALF_EVEN).add((BigDecimal)contract.get("uncollectedInterest"));
                            contract.put("uncollectedInterest", uncollectedInterest);
                            break;
                        }else{
                            // 封包日在第一期之前那么结束循环
                            break;
                        }
                    }
                }
            }
            if(StringUtils.equalsIgnoreCase("FCT_CONTRACT", (String)contract.get("documentType"))){
                HlsCusFctQuotationCashflow hlsCusFctQuotationCashflow = new HlsCusFctQuotationCashflow();
                hlsCusFctQuotationCashflow.setContractId((Long)contract.get("contractId"));
                List<HlsCusFctQuotationCashflow> cashflowList = hlsCusFctQuotationCashflowService.select(request, hlsCusFctQuotationCashflow, 1, 99999);
                cashflowList = cashflowList.stream().filter(cashflow -> (cashflow.getCfItem() == 55L))
                        .sorted(Comparator.comparing(HlsCusFctQuotationCashflow::getDueDate)).collect(Collectors.toList());
                for (HlsCusFctQuotationCashflow cashflow : cashflowList) {
                    if(baseDate.isEqual(DateUtils.format(cashflow.getDueDate()))){
                        // 如果封包日正好等于某一期的应收日期,那么上面查询数据不需要修正
                        break;
                    }
                    // 封包日小于某一期应收日期时, 因为集合是根据应收日期顺序排序后的,所以第一次进入判断的就是距离封包日最近的未收的应收日期
                    if(baseDate.isBefore(DateUtils.format(cashflow.getDueDate()))){
                        int index = cashflowList.indexOf(cashflow);
                        if(index > 0){
                            // 计算出上一期与这一期的天数差
                            HlsCusFctQuotationCashflow quotationCashflow = new HlsCusFctQuotationCashflow();
                            quotationCashflow.setContractId(cashflow.getContractId());
                            quotationCashflow.setTimes(cashflow.getTimes()-1);
                            List<HlsCusFctQuotationCashflow> cl = hlsCusFctQuotationCashflowService.select(request, quotationCashflow, 1, 99999);
                            long days = DateUtils.between(cl.get(0).getDueDate(),cashflow.getDueDate());
                            // 计算出封包日与这一期的天数差
                            long days2 = DateUtils.between(DateUtils.format(baseDate), cashflow.getDueDate());

                            BigDecimal uncollectedInterest = new BigDecimal(cashflow.getDueAmount());
                            uncollectedInterest = uncollectedInterest.multiply(new BigDecimal(days2)).divide(new BigDecimal(days)).add((BigDecimal)contract.get("uncollectedInterest"));
                            contract.put("uncollectedInterest", uncollectedInterest);
                            break;
                        }
                    }
                }
            }
        }*/
    }

    @Override
    public List<Map<String, Object>> queryChangeContract(IRequest request, HlsCusAbsPropertyContract hlsCusAbsPropertyContract, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<HlsCusAbsPropertyContract> list = hlsCusAbsPropertyContractMapper.queryChangeContract(hlsCusAbsPropertyContract);
        List<Map<String, Object>> maps = new ArrayList<>(list.size());
        if (CollectionUtils.isNotEmpty(list)) {
            for (HlsCusAbsPropertyContract contract : list) {
                contract.setDateStr(hlsCusAbsPropertyContract.getDateStr());
                maps.add(hlsCusAbsPropertyContractMapper.queryContractForProductContract(contract).get(0));
            }
        }
        LocalDate baseDate = LocalDate.parse(hlsCusAbsPropertyContract.getDateStr());  // 封包日
        LocalDate dueDateBegin = LocalDate.parse(hlsCusAbsPropertyContract.getDateStr2());  // 封包日
        return hlsCusConContractService.calcContractAmount(maps, baseDate, dueDateBegin, request);
    }

    @SuppressWarnings("all")
    @Override
    public List<HlsCusAbsPropertyContract> queryAssetChangeContract(IRequest request, HlsCusAbsPropertyContract hlsCusAbsPropertyContract, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        if(StringUtils.isNotEmpty(hlsCusAbsPropertyContract.getParam())){
            String[] ss = hlsCusAbsPropertyContract.getParam().split(",");
            List<Long> list = new ArrayList<>();
            for(String s : ss){
                if(StringUtils.isNotEmpty(s)){
                    list.add(Long.valueOf(s));
                }
            }
            Long[] ls = new Long[list.size()];
            hlsCusAbsPropertyContract.setPropertyContractIds(list.toArray(ls));
        }
        return hlsCusAbsPropertyContractMapper.queryAssetChangeContract(hlsCusAbsPropertyContract);
    }

    @SuppressWarnings("all")
    @Override
    public List<Map<String, Object>> queryFinishContract(IRequest request, HlsCusAbsPropertyContract hlsCusAbsPropertyContract, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        if (StringUtils.isNotEmpty(hlsCusAbsPropertyContract.getParam())) {
            String[] ss = hlsCusAbsPropertyContract.getParam().split(",");
            List<Long> list = new ArrayList<>();
            for (String s : ss) {
                if (StringUtils.isNotEmpty(s)) {
                    list.add(Long.valueOf(s));
                }
            }
            Long[] ls = new Long[list.size()];
            hlsCusAbsPropertyContract.setPropertyContractIds(list.toArray(ls));
        }
        List<HlsCusAbsPropertyContract> list = hlsCusAbsPropertyContractMapper.queryFinishContract(hlsCusAbsPropertyContract);


        List<Map<String, Object>> maps = new ArrayList<>(list.size());
        if(CollectionUtils.isNotEmpty(list)){
            for (HlsCusAbsPropertyContract contract : list) {
                contract.setDateStr(hlsCusAbsPropertyContract.getDateStr());
                Map<String, Object> map = hlsCusAbsPropertyContractMapper.queryContractForProductContract(contract).get(0);
                map.put("propertyContractId", contract.getPropertyContractId());
                map.put("contractName", contract.getContractName());
                map.put("contractNumber", contract.getContractNumber());
                maps.add(map);
            }
        }
        LocalDate baseDate = LocalDate.parse(hlsCusAbsPropertyContract.getDateStr());  // 封包日
        LocalDate dueDateBegin = LocalDate.parse(hlsCusAbsPropertyContract.getDateStr2());  // 封包日
        maps = hlsCusConContractService.calcContractAmount(maps, baseDate, dueDateBegin, request);
        for (Map<String, Object> map : maps) {
            map.put("uncollectionAmount", ((BigDecimal)map.get("uncollectedPrincipal")).add((BigDecimal)map.get("uncollectedInterest")));
        }
        return maps;
    }

    @Override
    public List<Map<String, Object>> calcContractAmount(List<Map<String, Object>> maps, LocalDate baseDate, IRequest request){
        for (Map<String, Object> contract : maps) {
            if (StringUtils.equalsIgnoreCase("CON_CONTRACT", (String) contract.get("documentType"))) {
                HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
                hlsCusConContractCashflow.setContractId((Long) contract.get("contractId"));
                List<HlsCusConContractCashflow> cashflowList = hlsCusConContractCashflowService.select(request, hlsCusConContractCashflow, 1, 99999);
                cashflowList = cashflowList.stream().filter(cashflow -> cashflow.getCfItem() == 1L)
                        .sorted(Comparator.comparing(HlsCusConContractCashflow::getDueDate)).collect(Collectors.toList());
                for (HlsCusConContractCashflow cashflow : cashflowList) {
                    if (baseDate.isEqual(DateUtils.format(cashflow.getDueDate()))) {
                        // 如果封包日正好等于某一期的应收日期,那么上面查询数据不需要修正
                        break;
                    }
                    // 封包日小于某一期应收日期时, 因为集合是根据应收日期顺序排序后的,所以第一次进入判断的就是距离封包日最近的未收的应收日期
                    if (baseDate.isBefore(DateUtils.format(cashflow.getDueDate()))) {
                        int index = cashflowList.indexOf(cashflow);
                        if (index > 0) {
                            // 计算出上一期与这一期的天数差
                            long days = DateUtils.between(cashflowList.get(index - 1).getDueDate(), cashflow.getDueDate());
                            // 计算出封包日与这一期的天数差
                            long days2 = DateUtils.between(DateUtils.format(baseDate), cashflow.getDueDate());

                            BigDecimal uncollectedInterest = new BigDecimal(cashflow.getInterest() - cashflow.getReceivedInterest());
                            uncollectedInterest = uncollectedInterest.multiply(new BigDecimal(days2)).divide(new BigDecimal(days), 2, BigDecimal.ROUND_HALF_EVEN).add((BigDecimal) contract.get("uncollectedInterest"));
                            contract.put("uncollectedInterest", uncollectedInterest);
                            break;
                        } else {
                            // 封包日在第一期之前那么结束循环
                            break;
                        }
                    }
                }
            }
            if (StringUtils.equalsIgnoreCase("FCT_CONTRACT", (String) contract.get("documentType"))) {
                HlsCusFctQuotationCashflow hlsCusFctQuotationCashflow = new HlsCusFctQuotationCashflow();
                hlsCusFctQuotationCashflow.setContractId((Long) contract.get("contractId"));
                List<HlsCusFctQuotationCashflow> cashflowList = hlsCusFctQuotationCashflowService.select(request, hlsCusFctQuotationCashflow, 1, 99999);
                cashflowList = cashflowList.stream().filter(cashflow -> (cashflow.getCfItem() == 55L))
                        .sorted(Comparator.comparing(HlsCusFctQuotationCashflow::getDueDate)).collect(Collectors.toList());
                for (HlsCusFctQuotationCashflow cashflow : cashflowList) {
                    if (baseDate.isEqual(DateUtils.format(cashflow.getDueDate()))) {
                        // 如果封包日正好等于某一期的应收日期,那么上面查询数据不需要修正
                        break;
                    }
                    // 封包日小于某一期应收日期时, 因为集合是根据应收日期顺序排序后的,所以第一次进入判断的就是距离封包日最近的未收的应收日期
                    if (baseDate.isBefore(DateUtils.format(cashflow.getDueDate()))) {
                        int index = cashflowList.indexOf(cashflow);
                        if (index > 0) {
                            // 计算出上一期与这一期的天数差
                            HlsCusFctQuotationCashflow quotationCashflow = new HlsCusFctQuotationCashflow();
                            quotationCashflow.setContractId(cashflow.getContractId());
                            quotationCashflow.setTimes(cashflow.getTimes() - 1);
                            List<HlsCusFctQuotationCashflow> cl = hlsCusFctQuotationCashflowService.select(request, quotationCashflow, 1, 99999);
                            long days = DateUtils.between(cl.get(0).getDueDate(), cashflow.getDueDate());
                            // 计算出封包日与这一期的天数差
                            long days2 = DateUtils.between(DateUtils.format(baseDate), cashflow.getDueDate());

                            BigDecimal uncollectedInterest = new BigDecimal(cashflow.getDueAmount());
                            uncollectedInterest = uncollectedInterest.multiply(new BigDecimal(days2)).divide(new BigDecimal(days)).add((BigDecimal) contract.get("uncollectedInterest"));
                            contract.put("uncollectedInterest", uncollectedInterest);
                            break;
                        }
                    }
                }
            }
        }
        return maps;
    }
    @Autowired
    private HlsCusFctContractService hlsCusFctContractService;


    @SuppressWarnings("all")
    @Override
    public void delete(IRequest request, List<HlsCusAbsPropertyContract> list) {
        for (HlsCusAbsPropertyContract hlsCusAbsPropertyContract : list) {
            if(StringUtils.equalsIgnoreCase("CON_CONTRACT", hlsCusAbsPropertyContract.getDocumentType())){
                HlsCusConContract hlsCusConContract = new HlsCusConContract();
                hlsCusConContract.setContractId(hlsCusAbsPropertyContract.getContractId());
                hlsCusConContract.setAbsShowFlag("N");
                hlsCusConContractService.updateByPrimaryKeySelective(request, hlsCusConContract);
            }else if(StringUtils.equalsIgnoreCase("FCT_CONTRACT", hlsCusAbsPropertyContract.getDocumentType())){
                HlsCusFctContract hlsCusFctContract = new HlsCusFctContract();
                hlsCusFctContract.setContractId(hlsCusAbsPropertyContract.getContractId());
                hlsCusFctContract.setAbsShowFlag("N");
                hlsCusFctContractService.updateByPrimaryKeySelective(request, hlsCusFctContract);
            }
        }
        self().batchDelete(list);
    }
}

