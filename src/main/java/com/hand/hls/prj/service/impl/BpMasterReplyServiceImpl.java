package com.hand.hls.prj.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.BaseConstants;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.HlsProductDefDealer;
import com.hand.hls.fnd.dto.HlsProductDefinition;
import com.hand.hls.fnd.dto.HlsProductDefinitionPara;
import com.hand.hls.fnd.mapper.HlsProductDefDealerMapper;
import com.hand.hls.fnd.mapper.HlsProductDefinitionMapper;
import com.hand.hls.fnd.mapper.HlsProductDefinitionParaMapper;
import com.hand.hls.prj.dto.BpMasterReply;
import com.hand.hls.prj.mapper.BpMasterReplyMapper;
import com.hand.hls.prj.service.IBpMasterReplyService;
import com.hand.hls.utils.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class BpMasterReplyServiceImpl extends BaseServiceImpl<BpMasterReply> implements IBpMasterReplyService{

    @Autowired
    private BpMasterReplyMapper bpMasterReplyMapper;

    @Autowired
    private HlsProductDefinitionMapper hlsProductDefinitionMapper;
    @Autowired
    private HlsProductDefDealerMapper hlsProductDefDealerMapper;
    @Autowired
    private HlsProductDefinitionParaMapper hlsProductDefinitionParaMapper;


    @Override
    public List<BpMasterReply> selectReplyInfo(IRequest iRequest, String manufacturerId,String dealerId) {
        //如果经销商能查询到产品信息就是用经销商的产品如果不能就是用合作方
        List<HlsProductDefinition> productDefinitions = new ArrayList();
        if(!"null".equals(dealerId)){
            productDefinitions=hlsProductDefinitionMapper.queryProductDefDealerByDealerId(Long.valueOf(dealerId));
        }
        List<BpMasterReply> listBpMasterReply = new ArrayList<>();
        if(productDefinitions.size()==0){
            HlsProductDefinition hlsProductDefinition = new HlsProductDefinition();
            hlsProductDefinition.setBpId(manufacturerId);
            productDefinitions=hlsProductDefinitionMapper.selectHlsProductDefinitionList(hlsProductDefinition);
        }
        //查询产品信息
        BpMasterReply bpMasterReply=new BpMasterReply();
        for(HlsProductDefinition dto: productDefinitions){
            bpMasterReply.setReplyId(0L);
            List<Map> hlsProductDefinitions = hlsProductDefinitionMapper.selectHlsProductDefinitionInfo(dto);
            bpMasterReply.setHlsProductDefinitionList(hlsProductDefinitions);
        }
        if(productDefinitions.size()>0){
            listBpMasterReply.add(bpMasterReply);
        }
        return listBpMasterReply;
    }

    public List<BpMasterReply> manufacturerQuery(IRequest iRequest, BpMasterReply bpMasterReply, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return bpMasterReplyMapper.manufacturerQuery(bpMasterReply);
    }

    /**
     * 根据厂商ID 以及 批复参数名 获取对应的参数最大值
     *
     * @param bpId
     * @param replyPara
     * @return
     */
    @Override
    public String queryReplyParaValueToByBpIdAndReplyPara(Long bpId, String replyPara) {

        //当前日期
        Date now = new Date();
        List<BpMasterReply> bpMasterReplyList = bpMasterReplyMapper.queryReplyParaByBpIdAndReplyPara(bpId);

        /*如果厂商下有相应的批复信息，处理逻辑如下
        1、默认取厂商/合作方批复最新标志为Y的批复下对应参数，若该批复无对应参数，则视为空；
        2、若厂商合作方无最新标志为Y的批复，则取当前日期≥批复起始日，且创建日期最大的一条批复对应对应参数，若该批复无对应参数，则视为空
        */
        if(CollectionUtils.isNotEmpty(bpMasterReplyList)){
            //存在最新批复则取最新批复下的对应参数，若没有的话则取当前日期≥批复起始日，且创建日期最大的一条批复对应对应参数
            List<BpMasterReply> collect = bpMasterReplyList.stream().filter(item -> StringUtils.equals(item.getLatestFlag(), BaseConstants.YES)).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(collect)){
                Optional<BpMasterReply> any = collect.stream().filter(item -> StringUtils.equals(item.getReplyPara(), replyPara)).findAny();
                if(any.isPresent()){
                   return any.get().getVauleTo();
                }
            }else {
                Optional<BpMasterReply> max = bpMasterReplyList.stream().filter(item -> StringUtils.equals(item.getReplyPara(),replyPara)
                        && DateUtils.compare(now , item.getValidFrom()) != -1).max(Comparator.comparing(item -> item.getCreationDate()));
                if(max.isPresent() && max.get().getVauleTo() != null){
                    return max.get().getVauleTo();
                }
            }
        }

        return null;
    }
}