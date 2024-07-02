package com.hand.hls.ws.service;

import com.hand.hls.ws.dto.HlsSysUserData;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.List;

/**
 * Created by 63171 on 2020/3/17.
 */
@WebService
/*@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)*/
public interface SoapSysUserQueryService {
    @WebMethod
    public List<HlsSysUserData> querySysUser(String ar);
}
