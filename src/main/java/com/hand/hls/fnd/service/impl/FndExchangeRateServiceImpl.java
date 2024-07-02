package com.hand.hls.fnd.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.mapper.FndExchangeRateMapper;
import hls.core.utils.exception.HlsCusException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.fnd.dto.FndExchangeRate;
import com.hand.hls.fnd.service.IFndExchangeRateService;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class FndExchangeRateServiceImpl extends BaseServiceImpl<FndExchangeRate> implements IFndExchangeRateService{

    @Autowired
    private FndExchangeRateMapper fndExchangeRateMapper;

    @Override
    public Double selectExchangeRate(IRequest iRequest, FndExchangeRate fndExchangeRate) throws HlsCusException {

        String currency = null ;
        Double dailyRate = 1D;
        //根据 凭证所属公司 找到 本位币 , 该币种即为外币
        List<FndExchangeRate> fndExchangeRateList =  fndExchangeRateMapper.queryCompanyCurrency(fndExchangeRate);
        if(fndExchangeRateList.size() > 0 ){
            currency =  fndExchangeRateList.get(0).getCurrency();
        }
        if(currency == null){
            throw new HlsCusException("该公司未维护本位币，请在公司定义维护！");
        }

        //本位币 = 外币 ， 汇率为  1
        if(currency.equalsIgnoreCase(fndExchangeRate.getForeignCurrency())){
            dailyRate = 1.0000D;
            return dailyRate;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD");
        if(fndExchangeRate.getExchangeDate() == null ){
            throw new HlsCusException("请维护日期再查询汇率！");
        }

        List<FndExchangeRate>  fndExchangeRates = fndExchangeRateMapper.queryDailyRate(fndExchangeRate);
        if( CollectionUtils.isEmpty(fndExchangeRates)){
            throw new HlsCusException("根据当前日期 和 凭证所属公司 ，未查询到汇率 ！");
        }else if(fndExchangeRates.get(0).getDailyRate() == null){
            throw new HlsCusException("根据当前日期 和 凭证所属公司 ，未查询到汇率 ！");
        }else{
            dailyRate =  fndExchangeRates.get(0).getDailyRate();
        }

        return dailyRate;

    }

}