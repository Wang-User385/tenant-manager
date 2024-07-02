package com.hand.hls.plm.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.plm.dto.HlsCusShips;
import com.hand.hls.plm.mapper.HlsCusShipsMapper;
import com.hand.hls.plm.service.HlsCusShipsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
*
*@author : Vincent(wenzheng.shao@hand-china.com)
*@date : 2019/9/20 ,09:28
*
**/
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusShipsServiceImpl extends BaseServiceImpl<HlsCusShips> implements HlsCusShipsService {
    @Autowired
    private HlsCusShipsMapper shipsMapper;
    @Override
    public List<HlsCusShips> selectShip(IRequest requestContext, HlsCusShips dto, int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        return shipsMapper.selectShip(dto);
    }

    /**
     * 根据合同ID删除船舶信息
     *
     * @param contractId
     * @return
     */
    @Override
    public int deleteByPliContractId(Long contractId) {
        HlsCusShips  ships =new HlsCusShips();
        ships.setPliContractId(contractId);
        return shipsMapper.delete(ships);
    }
}
