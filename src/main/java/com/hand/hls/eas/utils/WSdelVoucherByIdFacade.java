package com.hand.hls.eas.utils;


import ws.eas.services.WSaddNewVoucherFacade.WSaddNewVoucherFacadeSrvProxy;
import ws.eas.services.WSaddNewVoucherFacade.WSaddNewVoucherFacadeSrvProxyServiceLocator;
import ws.eas.services.WSdelVoucherByIdFacade.WSdelVoucherByIdFacadeSrvProxy;
import ws.eas.services.WSdelVoucherByIdFacade.WSdelVoucherByIdFacadeSrvProxyServiceLocator;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;


/**
 * @Description:凭证删除接口
 * @Author: Wangchao
 * @Date: Created om 10:12 2020/5/7
 */

public class WSdelVoucherByIdFacade {

    public String credentialsDelete(String url,String voucherId, String comOrgNum)throws MalformedURLException,ServiceException,RemoteException {
        java.net.URL endpoint=null;
        WSdelVoucherByIdFacadeSrvProxy proxy=null;
        String returnString="";
        try{
            endpoint=new java.net.URL(url);
            proxy=new WSdelVoucherByIdFacadeSrvProxyServiceLocator().getWSdelVoucherByIdFacade(endpoint);
            returnString = proxy.delVoucherById(voucherId,comOrgNum);
        }
        catch(MalformedURLException e){
            e.printStackTrace();
            throw new MalformedURLException(e.toString());
        }
        catch(ServiceException e){
            e.printStackTrace();
            throw new ServiceException(e);
        }
        catch (RemoteException e) {
            e.printStackTrace();
            throw new RemoteException(e.toString());

        }
        return returnString;
    }
}
