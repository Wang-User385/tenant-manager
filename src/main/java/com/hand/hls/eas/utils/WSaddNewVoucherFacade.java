package com.hand.hls.eas.utils;


import ws.eas.services.WSaddNewVoucherFacade.WSaddNewVoucherFacadeSrvProxy;
import ws.eas.services.WSaddNewVoucherFacade.WSaddNewVoucherFacadeSrvProxyServiceLocator;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;


/**
 * @Description:凭证传输接口
 * @Author: Wangchao
 * @Date: Created om 10:12 2020/5/7
 */

public class WSaddNewVoucherFacade {

    public String credentialsSynchronization(String url,String param)throws MalformedURLException,ServiceException,RemoteException {
        java.net.URL endpoint=null;
        WSaddNewVoucherFacadeSrvProxy proxy=null;
        String returnString="";
        try{
            endpoint=new java.net.URL(url);
            proxy=new WSaddNewVoucherFacadeSrvProxyServiceLocator().getWSaddNewVoucherFacade(endpoint);
            returnString = proxy.addNewVoucher(param);
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
